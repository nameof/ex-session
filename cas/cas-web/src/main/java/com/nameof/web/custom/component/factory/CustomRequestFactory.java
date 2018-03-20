package com.nameof.web.custom.component.factory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.nameof.web.custom.component.request.CustomHttpServletRequest;

@Component
public class CustomRequestFactory implements ApplicationContextAware {

	private ApplicationContext applicationCtx;
	
	private String requestBeanName = "customHttpServletRequest";

	@Override
	public void setApplicationContext(ApplicationContext applicationCtx)
			throws BeansException {
		this.applicationCtx = applicationCtx;
	}
	
	public CustomHttpServletRequest getWrapperedRequest(HttpServletRequest req, HttpServletResponse resp) {
		CustomHttpServletRequest wrapper = (CustomHttpServletRequest) 
				applicationCtx.getBean(requestBeanName, req, resp);
		return wrapper;
	}

}
