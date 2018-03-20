package com.nameof.web.custom.component.factory;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.nameof.web.custom.component.session.HttpSessionWrapper;

/**
 * 实例化自定义HttpSession
 * @author ChengPan
 */
@Component
public class CacheHttpSessionFactory implements ApplicationContextAware {

	private ApplicationContext applicationCtx;

	@Value("${cache.httpsession.bean.name}")
	private String sessionBeanName;
	
	public HttpSessionWrapper newSessionInstance(HttpSession session, String token) {
		HttpSessionWrapper wrapper = (HttpSessionWrapper) 
				applicationCtx.getBean(sessionBeanName, session, token);
		return wrapper;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationCtx)
			throws BeansException {
		this.applicationCtx = applicationCtx;
	}
}
