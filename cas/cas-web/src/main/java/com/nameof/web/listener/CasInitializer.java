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

@Component
public class CasInitializer implements ServletContextAware, ApplicationContextAware {
	
	@Value("${session.monitor.url}")
	private String monitorUrl;

	private static final Logger LOG = LoggerFactory.getLogger(CasInitializer.class);

	@Override
	public void setServletContext(ServletContext servletContext) {
		servletContext.setAttribute("monitorUrl", monitorUrl);
		
		String activeProfile = servletContext.getInitParameter("spring.profiles.active");
		servletContext.setAttribute("sessionAccessor", activeProfile);
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
