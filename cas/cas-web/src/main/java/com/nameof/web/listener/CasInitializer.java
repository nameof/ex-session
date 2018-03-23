package com.nameof.web.listener;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class CasInitializer implements ServletContextAware {
	
	@Value("${session.monitor.url}")
	private String monitorUrl;

	@Override
	public void setServletContext(ServletContext servletContext) {
		servletContext.setAttribute("monitorUrl", monitorUrl);
	}

}
