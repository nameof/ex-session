package com.nameof.sso.client.web.sso;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nameof.sso.client.domain.HandleResult;
import com.nameof.sso.client.domain.User;
import com.nameof.sso.client.utils.HttpRequest;
import com.nameof.sso.client.utils.UrlBuilder;
import com.nameof.sso.client.web.session.LogedSessionManager;

public class SingleSignOnDispatcher {
	
	private static String URL_ENCODING_CHARSET = "UTF-8";
	
	/** 返回地址参数名 */
	private static  final String RETURN_URL_KEY = "returnUrl";
	
	/** 注销地址参数名 */
	private static  final String LOGOUT_URL_KEY = "logoutUrl";

	/** CAS授权给客户端的票据传递参数名 */
	private static  final String AUTHC_TICKET_KEY = "jwtTicket";
	
	/** 回传给CAS的票据header名  */
	private static  final String VALIDATE_HEADER_TICKET_KEY = "jwtTicket";
	
	/** 回传给CAS的ClientID header名  */
	private static  final String VALIDATE_HEADER_CLIENTID_KEY = "ClientId";
	
	/**
	 * 第一阶段，跳转到CAS进行登录
	 * @param request
	 * @param response
	 * @param ssoConfig
	 * @throws IOException
	 */
	public void phaseOne(HttpServletRequest request, HttpServletResponse response, SSOConfiguration ssoConfig) throws IOException {
		//redirect to cas-web login
		UrlBuilder builder = UrlBuilder.parse(ssoConfig.getCasLoginUrl());
		builder.addParameter(RETURN_URL_KEY, URLEncoder.encode(request.getRequestURL().toString(), URL_ENCODING_CHARSET));
		builder.addParameter(LOGOUT_URL_KEY, URLEncoder.encode(ssoConfig.getClientLogoutUrl(), URL_ENCODING_CHARSET));
		response.sendRedirect(builder.toString());
	}
	
	/**
	 * 第二阶段，验证ticket，获取用户信息
	 * @param request
	 * @param response
	 * @param ssoConfig
	 * @throws IOException
	 */
	public void phaseTwo(HttpServletRequest request, HttpServletResponse response, SSOConfiguration ssoConfig) throws IOException {
		String ticket = request.getParameter(AUTHC_TICKET_KEY);
		Map<String, String> header = new HashMap<>();
		header.put(VALIDATE_HEADER_TICKET_KEY, ticket);
		header.put(VALIDATE_HEADER_CLIENTID_KEY, ssoConfig.getClientId());
		HandleResult result = HttpRequest.postHandleResult(ssoConfig.getValidateTicketUrl(), null, header);
		if (result.isState()) {
			String username = result.getString("subject");
			User user = new User(username, "");
			request.getSession().setAttribute("user", user);
			
			String gloabSessionId = result.getString("gloabSessionId");

			//添加到已登录session管理器
			LogedSessionManager.attach(gloabSessionId, request.getSession());
			
			//存储有效票据到session，以备session监听器从LogedSessionManager中移除invalidate的session
			request.getSession().setAttribute("gloabSessionId", gloabSessionId);
			
			//存储全局注销地址，以便页面输出，注销
			request.getSession().setAttribute("CasLogoutUrl", ssoConfig.getCasLogoutUrl());

			UrlBuilder builder = UrlBuilder.parse(request.getRequestURL().toString());
			builder.removeParameter(AUTHC_TICKET_KEY);
			response.sendRedirect(builder.toString());
			return;
		}
		throw new IllegalStateException(result.getInfo());
	}

}
