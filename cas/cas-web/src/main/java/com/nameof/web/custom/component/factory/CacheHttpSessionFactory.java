package com.nameof.web.custom.component.factory;

import java.lang.reflect.Constructor;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.nameof.cache.CacheDao;
import com.nameof.common.config.ConfigLoader;
import com.nameof.web.custom.component.session.DefaultCacheHttpSession;
import com.nameof.web.custom.component.session.HttpSessionWrapper;

/**
 * 根据{@link cas.support.ConfigLoader}获取自定义HttpSession实现类配置，实例化自定义HttpSession
 * @author ChengPan
 */
@Component
public class CacheHttpSessionFactory implements ApplicationContextAware {

	private ApplicationContext applicationCtx;

	private CacheHttpSessionFactory() {}

	private static final Class<?> clazz;

	private static final String SESSION_IMPL_CLASS_KEY = "cache.httpsession.impl.class";

	private static final String HttpSessionWrapper = null;
	
	static {
		try {
			clazz = Class
					.forName(ConfigLoader.getConfig(SESSION_IMPL_CLASS_KEY));
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("can not found session class", e);
		}
	}

	public HttpSessionWrapper newSessionInstance(HttpSession session, String token) {
		HttpSessionWrapper wrapper = (HttpSessionWrapper) applicationCtx.getBean("defaultCacheHttpSession", session, token);
		return wrapper;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationCtx)
			throws BeansException {
		this.applicationCtx = applicationCtx;
	}
}
