package com.nameof.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.MDC;

import com.nameof.common.domain.User;

public class MDCFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		storeMDC(request);
		chain.doFilter(request, response);
		MDC.clear();
	}

	private void storeMDC(ServletRequest request) {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession(false);
		if (session != null) {
			 MDC.put("sessionId", session.getId());
			 User user = (User) session.getAttribute("user");
			 if (user != null) {
				 MDC.put("userName", user.getName());
			 }
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}
	
	@Override
	public void destroy() {}
}
