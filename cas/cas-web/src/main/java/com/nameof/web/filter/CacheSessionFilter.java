package com.nameof.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nameof.common.utils.RedisUtil;
import com.nameof.web.custom.component.request.CustomHttpServletRequest;
import com.nameof.web.custom.component.session.CustomSessionProcessor;

/**
 * 处理请求之前，对HttpServletRequest包装类的实例化
 * 请求完成之后，提交自定义Session数据到缓存中，并释放缓存连接资源
 * @author ChengPan
 */
public class CacheSessionFilter implements Filter{

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;  
        HttpServletResponse resp = (HttpServletResponse)response;  
        CustomHttpServletRequest wrapper = new CustomHttpServletRequest(req, resp);
        try {
        	chain.doFilter(wrapper, response);
        } finally {
	        if (wrapper.getSession(false) instanceof CustomSessionProcessor) {
	        	((CustomSessionProcessor) wrapper.getSession()).commit();
	        }
	        RedisUtil.returnResource();//release redis
        }
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
