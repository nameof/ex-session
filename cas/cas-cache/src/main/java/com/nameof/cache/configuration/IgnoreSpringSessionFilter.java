package com.nameof.cache.configuration;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.nameof.common.domain.SessionAccessor;

/**
 *   在集成spring-session的情况下，spring-session会在容器中寻找springSessionRepositoryFilter的bean，
 * 这个bean实现容器HttpSession的替换，它由{@link org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession}
 * 注册。<br/>
 *   如果Profile使用的不是spring-session，即{@link com.nameof.cache.configuration.SpringSessionConfig}
 * 没有开启{@link org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession} 
 * 则在容器中注册一个这样的bean，它什么也不干，只是避免sping找不到bean而报错。
 * @author ChengPan
 */
@Component("springSessionRepositoryFilter")
@Profile(value = {SessionAccessor.EHCACHE, SessionAccessor.MEMCACHED
		, SessionAccessor.REDIS, SessionAccessor.REDIS_TEMPLATE, SessionAccessor.REDISSON})
public class IgnoreSpringSessionFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		chain.doFilter(request, response);
	}

}
