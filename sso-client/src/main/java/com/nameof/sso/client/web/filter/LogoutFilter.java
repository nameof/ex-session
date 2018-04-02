package com.nameof.sso.client.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.nameof.sso.client.web.session.LogedSessionManager;

/**
 * 接收CAS的注销请求
 * @author ChengPan
 */
public class LogoutFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		String token = request.getParameter("token");
		HttpSession session = LogedSessionManager.get(token);
		if (session != null) {
			session.invalidate();
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}
	
	@Override
	public void destroy() {}
}
