package com.nameof.web.listener;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.nameof.common.domain.Constants;
import com.nameof.common.domain.SessionAccessor;

@Component
public class CasInitializer implements ServletContextAware, ApplicationContextAware {
	
	@Value("${session.monitor.url}")
	private String monitorUrl;
	
	@Value("${login.websocket.enable}")
	private boolean loginWithWebSocket;

	private static final Logger LOG = LoggerFactory.getLogger(CasInitializer.class);

	@Override
	public void setServletContext(ServletContext servletContext) {
		servletContext.setAttribute("monitorUrl", monitorUrl);
		LOG.debug("monitorUrl : {}" , monitorUrl);
		
		servletContext.setAttribute("loginWithWebSocket", loginWithWebSocket);
		LOG.debug("loginWithWebSocket : {}" , loginWithWebSocket);
		
		String activeProfile = servletContext.getInitParameter(Constants.SPRING_PROFILES_ACTIVE);
		if (!SessionAccessor.ALL.contains(activeProfile)) {
			throw new IllegalStateException("不合法的profile配置，不存在对应的SessionAccessor: " + activeProfile);
		}
		servletContext.setAttribute(Constants.SESSION_ACCESSOR, activeProfile);
		LOG.debug("sessionAccessor : {}" , activeProfile);
		
		
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		//can also get sessionAccessor from  ApplicationContext
		String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
		LOG.debug("all activeProfile : {}" , activeProfiles);
	}
}
