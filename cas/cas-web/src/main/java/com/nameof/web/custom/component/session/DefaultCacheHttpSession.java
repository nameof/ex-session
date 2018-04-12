package com.nameof.web.custom.component.session;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.nameof.cache.CacheDao;
/**
 * 默认情况下，{@link cas.custom.component.session.DefaultCacheHttpSession}实例会在构造时尝试从缓存中
 * 加载maxInactiveInterval信息（如果有的话）.<br>
 * 
 * 每一次对Session中Attribute都会直接导致{@link cas.custom.component.session.DefaultCacheHttpSession}
 * 与缓存进行直接交互.<br>
 * 
 * 当前请求完成之后，通过{@link cas.filter.CacheSessionFilter}调用{@link cas.custom.component.session.DefaultCacheHttpSession}
 * 的commit方法将expire（maxInactiveInterval）提交到缓存中.<br>
 * 
 * @author ChengPan
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DefaultCacheHttpSession extends AbstractCacheHttpSession implements
		CustomSessionProcessor {

	private static final long serialVersionUID = 3977740308601865675L;

	@Autowired
	private CacheDao cacheDao;
	
	public DefaultCacheHttpSession(HttpSession session, String token) {
		super(session, token);
	}

	@Override
	@PostConstruct
	public void initialize() {
		
		setNew(!cacheDao.exists(token));
		
		initCreateTime();
		
		initLastAccessedTime();

		initMaxInactiveInterval();
	}

	private void initCreateTime() {
		if (isNew()) {
			cacheDao.setAttribute(token, CACHE_CREATE_TIME_KEY, super.getCreationTime());
			return;
		}
		//非新的Session，应当已存在createTime属性，但避免手动操作缓存，将属性清空，造成空指针和createTime丢失
		Long createTime = (Long) cacheDao.getAttribute(token, CACHE_CREATE_TIME_KEY);
		if (createTime == null) {
			cacheDao.setAttribute(token, CACHE_CREATE_TIME_KEY, super.getCreationTime());
		}
		setCreationTime(createTime != null ? createTime : super.getCreationTime());
	}
	

	private void initLastAccessedTime() {
		Long lat = (Long) cacheDao.getAttribute(token, CACHE_LAST_ACCESSED_TIME_KEY);
		if (lat == null) {
			//在首次访问情况下，LastAccessedTime为空，则返回当前会话创建时间，即此时LastAccessedTime=ThisAccessedTime=CreationTime
			setLastAccessedTime(getCreationTime());
			return;
		}
		setLastAccessedTime(lat);
	}

	private void initMaxInactiveInterval() {
		// 从缓存中读取maxInactiveInterval信息
		Integer originalExpire = (Integer) cacheDao.getAttribute(token, CACHE_INTERVAL_KEY);
		if (originalExpire != null) {
			setMaxInactiveInterval(originalExpire);
		}
	}

	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		super.setMaxInactiveInterval(maxInactiveInterval);
		cacheDao.setAttribute(token, CACHE_INTERVAL_KEY, maxInactiveInterval);
	}

	@Override
	public void commit() {
		storeLastAccessedTime();
		setExpireToCache();
	}

	private void storeLastAccessedTime() {
		cacheDao.setAttribute(token, CACHE_LAST_ACCESSED_TIME_KEY, getAccessedTime());
	}
	
	private void setExpireToCache() {
		if (isPersist()) {
			cacheDao.setPersist(token);
		}
		else {
			cacheDao.setExpire(token, getMaxInactiveInterval());
		}
	}

	@Override
	protected Enumeration<String> getAttributeNamesInterval() {
		Collection<String> keys = cacheDao.getAttributeKeys(token);
		return new Vector<String>(keys).elements();
	}

	@Override
	protected String[] getValueNamesInterval() {
		Collection<String> keys = cacheDao.getAttributeKeys(token);
		return keys.toArray(new String[keys.size()]);
	}

	@Override
	protected void setAttributeInterval(String name, Object value) {
		cacheDao.setAttribute(token, name, value);
	}

	@Override
	protected void removeAttributeInterval(String name) {
		cacheDao.removeAttribute(token, name);
	}

	@Override
	protected void invalidateInterval() {
		cacheDao.del(token);
	}

	@Override
	protected Object getAttributeInterval(String name) {
		return cacheDao.getAttribute(token, name);
	}
}