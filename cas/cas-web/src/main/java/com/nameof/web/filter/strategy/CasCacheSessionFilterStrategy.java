package com.nameof.web.filter.strategy;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.nameof.common.domain.Constants;
import com.nameof.common.domain.SessionAccessor;
import com.nameof.common.utils.CookieUtil;
import com.nameof.web.custom.component.factory.CustomRequestFactory;
import com.nameof.web.custom.component.request.CustomHttpServletRequest;
import com.nameof.web.custom.component.session.CustomSessionProcessor;
import com.nameof.web.filter.CacheSessionFilter;

/**
 * 处理请求之前，对HttpServletRequest包装类的实例化
 * 请求完成之后，提交自定义Session数据到缓存中
 * @author ChengPan
 */
@Component
@Profile(value = {SessionAccessor.EHCACHE, SessionAccessor.MEMCACHED
		, SessionAccessor.REDIS, SessionAccessor.REDIS_TEMPLATE, SessionAccessor.REDISSON})
public class CasCacheSessionFilterStrategy implements SessionFilterStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(CacheSessionFilter.class);
	
	@Autowired
	private CustomRequestFactory requestFactory;
	
	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		CustomHttpServletRequest wrapper = null;
		try {
			wrapper = requestFactory.getWrapperedRequest(req, resp);
			chain.doFilter(wrapper, resp);
		} finally {
			if (wrapper != null) {
				commitSessionToCache(wrapper);
				checkTokenCookie(wrapper, resp);
			}
		}
	}
	
	/**
	 * 提交会话数据到缓存
	 * @param wrapper
	 */
	private void commitSessionToCache(CustomHttpServletRequest wrapper) {
		try {
			if (wrapper != null && wrapper.getSession(false) instanceof CustomSessionProcessor) {
				((CustomSessionProcessor) wrapper.getSession()).commit();
			}
		} catch (Throwable e) {
			LOG.error("commitSessionToCache fail {}", e);
		}
	}

	/**
	 * 检查sessionid cookie
	 * @param req
	 * @param resp
	 */
	private void checkTokenCookie(HttpServletRequest req, HttpServletResponse resp) {
		String token = CookieUtil.getCookieValue(req, Constants.GLOBAL_SESSION_ID);
        if (StringUtils.isBlank(token) && req.getSession(false) == null) {
        	token = UUID.randomUUID().toString();
        	CookieUtil.addCookie(resp, Constants.GLOBAL_SESSION_ID, token);
        }
	}

}
