package com.nameof.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nameof.common.utils.RedisUtil;
import com.nameof.web.filter.strategy.SessionFilterStrategy;

/**
 * 自定义session核心过滤器
 * @author ChengPan
 */
@Component("sessionFilter")
public class CacheSessionFilter implements Filter {
	
	@Autowired
	private SessionFilterStrategy filterStrategy;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;  
	    HttpServletResponse resp = (HttpServletResponse)response; 
		try {
			filterStrategy.doFilter(req, resp, chain);
		} finally {
			RedisUtil.returnResource();//release redis
		}
	}
}
