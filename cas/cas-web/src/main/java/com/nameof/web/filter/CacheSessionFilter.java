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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nameof.common.domain.Constants;
import com.nameof.common.domain.SessionAccessor;
import com.nameof.common.utils.RedisUtil;
import com.nameof.web.custom.component.factory.CustomRequestFactory;
import com.nameof.web.custom.component.request.CustomHttpServletRequest;
import com.nameof.web.custom.component.session.CustomSessionProcessor;

/**
 * 处理请求之前，对HttpServletRequest包装类的实例化
 * 请求完成之后，提交自定义Session数据到缓存中，并释放缓存连接资源
 * @author ChengPan
 */
@Component("sessionFilter")
public class CacheSessionFilter implements Filter {
	
	@Autowired
	private CustomRequestFactory requestFactory;
	
	/** 是否开启spring-session */
	private boolean userSpringSession = false;

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
        if (userSpringSession) {
        	chain.doFilter(request, response);
        	RedisUtil.returnResource();//release redis
        	return;
        }
		HttpServletRequest req = (HttpServletRequest)request;  
        HttpServletResponse resp = (HttpServletResponse)response;  
        CustomHttpServletRequest wrapper = requestFactory.getWrapperedRequest(req, resp);
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
	public void init(FilterConfig config) throws ServletException {
		String sessionAccessor = (String) config.getServletContext()
				.getAttribute(Constants.SESSION_ACCESSOR);
		if (SessionAccessor.SPRING_SESSION.equalsIgnoreCase(sessionAccessor)) {
			this.userSpringSession = true;
		}
	}

}
