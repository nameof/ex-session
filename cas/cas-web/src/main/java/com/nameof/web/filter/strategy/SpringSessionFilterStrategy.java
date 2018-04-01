package com.nameof.web.filter.strategy;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.nameof.common.constant.SessionAccessor;

/**
 * 在使用spring session的情况下，什么也不做，由spring session去包装servlet对象
 * @author ChengPan
 */
@Component
@Profile(value = {SessionAccessor.SPRING_SESSION})
public class SpringSessionFilterStrategy implements SessionFilterStrategy {

	@Override
	public void doFilter(HttpServletRequest req, HttpServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		chain.doFilter(req, resp);
	}

}
