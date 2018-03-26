package com.nameof.web.custom.component.session;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.nameof.cache.CacheDao;

/**
 * {@link cas.custom.component.session.BufferedCacheHttpSession}实例会在构造时
 * 尝试从缓存中加载所有的用户会话数据（包括所有属性和maxInactiveInterval） * 缓存到本地的
 * {@link cas.custom.component.session.BufferedCacheHttpSession#attributes}中.<br>
 * 
 * 在当前会话期间，每一次对Session中Attribute的操作都是对于{@link cas.custom.component.session.BufferedCacheHttpSession}
 * 对象ConcurrentHashMap属性缓存的attributes操作.<br>
 * 
 * 当前请求完成之后，所有attributes通过{@link cas.filter.CacheSessionFilter}调用
 * {@link cas.custom.component.session.BufferedCacheHttpSession}的commit方法提交到缓存中，同时设置expire过期时间.
 * 
 * @author ChengPan
 */
@Deprecated
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BufferedCacheHttpSession extends AbstractCacheHttpSession
			implements CustomSessionProcessor {
	
	private static final long serialVersionUID = -248646772305855733L;

    /** 本地属性集合 */
    private Map<String,Object> attributes = new ConcurrentHashMap<>();
    
    @Autowired
    private CacheDao cacheDao;
    
	public BufferedCacheHttpSession(HttpSession session, String token) {
		super(session, token);
	}

	/**
	 * 初始化session属性信息
	 */
	@Override
	@PostConstruct
	public void initialize() {
		
		setNew(!cacheDao.exists(token));

		//获取缓存中所有"Attribute"，缓存到本地
		attributes.putAll(cacheDao.getAllAttribute(token));
		
		initCreateTime();
		
		initLastAccessedTime();
		
		initMaxInactiveInterval();
		
	}
	
	private void initCreateTime() {
		if (isNew()) {
			attributes.put(CACHE_CREATE_TIME_KEY, getCreationTime());
			return;
		}
		setCreationTime((Long) attributes.get(CACHE_CREATE_TIME_KEY));
	}
	
	private void initLastAccessedTime() {
		Long lat = (Long) attributes.get(CACHE_LAST_ACCESSED_TIME_KEY);
		
		if (lat == null) {
			//在首次访问情况下，LastAccessedTime为空，则返回当前会话创建时间，即此时LastAccessedTime=ThisAccessedTime=CreationTime
			setLastAccessedTime(getCreationTime());
			return;
		}
		setLastAccessedTime(lat);
	}
	
	private void initMaxInactiveInterval() {
		// 从缓存中读取maxInactiveInterval信息
		Integer originalExpire = (Integer) attributes.get(CACHE_INTERVAL_KEY);
		if (originalExpire != null) {
			setMaxInactiveInterval(originalExpire);
		}
	}

	@Override
	public void commit() {
		
		storeLastAccessedTime();
		
		//提交Session属性到缓存中
		cacheDao.setAllAttributes(token, attributes);
		
		//设置expire
		setExpireToCache();
	}

	private void storeLastAccessedTime() {
		attributes.put(CACHE_LAST_ACCESSED_TIME_KEY, getAccessedTime());
	}

	@Override
    public void setMaxInactiveInterval(int maxInactiveInterval) {
		super.setMaxInactiveInterval(maxInactiveInterval);
    	attributes.put(CACHE_INTERVAL_KEY, maxInactiveInterval);
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
	protected Object getAttributeInterval(String name) {
		return attributes.get(name);
	}

	@Override
	protected Enumeration<String> getAttributeNamesInterval() {
		return new Vector<String>(attributes.keySet()).elements();
	}

	@Override
	protected String[] getValueNamesInterval() {
		Set<String> keys = attributes.keySet();
		return keys.toArray(new String[keys.size()]);
	}

	@Override
	protected void setAttributeInterval(String name, Object value) {
		attributes.put(name, value);
	}

	@Override
	protected void removeAttributeInterval(String name) {
		attributes.remove(name);
	}

	@Override
	protected void invalidateInterval() {
		attributes.clear();
		cacheDao.del(token);
	}
}
