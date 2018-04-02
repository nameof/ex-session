package com.nameof.sso.client.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.nameof.sso.client.web.sso.SSOConfiguration;
import com.nameof.sso.client.web.sso.SingleSignOnDispatcher;

/**
 *    登录过滤
 *    并进行单点登录过程
 * @author ChengPan
 */
public class AuthenticationFilter implements Filter {
	
	private SingleSignOnDispatcher ssoDispatcher = new SingleSignOnDispatcher();
	
	private SSOConfiguration ssoConfig = new SSOConfiguration();
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (request.getSession().getAttribute("user") == null) {
			String ticket = request.getParameter("jwtTicket");
			if (StringUtils.isEmpty(ticket)) {
				ssoDispatcher.phaseOne(request, response, ssoConfig);
				return;
			}
			ssoDispatcher.phaseTwo(request, response, ssoConfig);
		}
		else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
		String casLoginUrl = config.getInitParameter("CasLoginUrl");
		if (StringUtils.isNotBlank(casLoginUrl)) {
			ssoConfig.setCasLoginUrl(casLoginUrl);
		}
		
		String validateTicketUrl = config.getInitParameter("ValidateTicketUrl");
		if (StringUtils.isNotBlank(validateTicketUrl)) {
			ssoConfig.setValidateTicketUrl(validateTicketUrl);
		}
		
		String clientLogoutUrl = config.getInitParameter("ClientLogoutUrl");
		if (StringUtils.isNotBlank(clientLogoutUrl)) {
			ssoConfig.setClientLogoutUrl(clientLogoutUrl);
		}
		
		String casLogoutUrl = config.getInitParameter("CasLogoutUrl");
		if (StringUtils.isNotBlank(casLogoutUrl)) {
			ssoConfig.setCasLogoutUrl(casLogoutUrl);
		}
	}

	@Override
	public void destroy() {}
}
