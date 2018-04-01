package com.nameof.web.filter.strategy;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nameof.common.constant.SessionAccessor;

/**
 * 根据{@link SessionAccessor}选择的不同，处理不同的session替换逻辑
 * @author ChengPan
 */
public interface SessionFilterStrategy {
	
	void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) 
			throws IOException, ServletException;
}
