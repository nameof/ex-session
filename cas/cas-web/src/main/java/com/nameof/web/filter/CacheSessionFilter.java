package com.nameof.web.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nameof.common.domain.Constants;
import com.nameof.common.domain.SessionAccessor;
import com.nameof.common.utils.CookieUtil;
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
	
	private static final Logger LOG = LoggerFactory.getLogger(CacheSessionFilter.class);
	
	@Autowired
	private CustomRequestFactory requestFactory;
	
	/** 是否开启spring-session */
	private boolean userSpringSession = false;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;  
	    HttpServletResponse resp = (HttpServletResponse)response; 
	    CustomHttpServletRequest wrapper = null;
		try {
			if (userSpringSession) {
				chain.doFilter(request, response);
			} else { 
			    wrapper = requestFactory.getWrapperedRequest(req, resp);
			    chain.doFilter(wrapper, response);
			}
		} finally {
			if (!userSpringSession) {
				commitSessionToCache(wrapper);
				checkTokenCookie(wrapper, resp);
			}
			RedisUtil.returnResource();//release redis
		}
	}
	
	private void commitSessionToCache(CustomHttpServletRequest wrapper) {
		try {
			if (wrapper != null && wrapper.getSession(false) instanceof CustomSessionProcessor) {
				((CustomSessionProcessor) wrapper.getSession()).commit();
			}
		} catch (Throwable e) {
			LOG.error("commitSessionToCache fail {}", e);
		}
	}

	private void checkTokenCookie(HttpServletRequest req, HttpServletResponse resp) {
		String token = CookieUtil.getCookieValue(req, CustomHttpServletRequest.COOKIE_SESSION_KEY);
        if (StringUtils.isBlank(token) && req.getSession(false) == null) {
        	token = UUID.randomUUID().toString();
        	CookieUtil.addCookie(resp, CustomHttpServletRequest.COOKIE_SESSION_KEY, token);
        }
	}
	
	@Override
	public void destroy() { }

	@Override
	public void init(FilterConfig config) throws ServletException {
		String sessionAccessor = (String) config.getServletContext()
				.getAttribute(Constants.SESSION_ACCESSOR);
		if (SessionAccessor.SPRING_SESSION.equalsIgnoreCase(sessionAccessor)) {
			this.userSpringSession = true;
		}
	}

}
