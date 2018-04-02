package com.nameof.common.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SpringUtils {
	
	public static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	}
	
	public static void setAttribute(String k, Object v) {
		getRequest().setAttribute(k, v);
	}
	
	public static Object getAttribute(String k) {
		return getAttribute(k);
	}
}
