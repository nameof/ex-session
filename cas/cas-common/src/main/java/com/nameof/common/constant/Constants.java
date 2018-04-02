package com.nameof.common.constant;

public interface Constants {
	/** session存储通道的key值 */
	String SESSION_ACCESSOR = "sessionAccessor";
	
	/** spring profile */
	String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
	
	/** like JSESSIONID */
	String GLOBAL_SESSION_ID = "token";
	
	/** APP客户端传递AppId的header */
	String WEB_HEADER_APP_ID = "AppId";
	
	/** APP客户端传递JWT的header */
	String WEB_HEADER_APP_JWT = "JWT";
	
	String WEB_LOGIN_ACCESS_KEY = "loginId";
	
	/** 返回地址参数名 */
	String WEB_RETURN_URL_KEY = "returnUrl";
	
	/** 注销地址参数名 */
	String WEB_LOGOUT_URL_KEY = "logoutUrl";
	
	/** 单点登录授权票据 */
	String SSO_TICKET_KEY = "jwtTicket";
	
	/** 单点登录客户端回传授权票据的header */
	String SSO_HEADER_TICKET_KEY = "jwtTicket";
	
	/** 单点登录客户端传递的ClientId header */
	String SSO_HEADER_CLIENT_ID = "ClientId";
}
