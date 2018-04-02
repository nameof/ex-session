package com.nameof.common.constant;

public interface Constants {
	/** session存储通道的key值 */
	String SESSION_ACCESSOR = "sessionAccessor";
	
	/** spring profile */
	String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
	
	String GLOBAL_SESSION_ID = "token";
	
	String WEB_HEADER_APPID = "AppId";
	
	String WEB_HEADER_JWT = "JWT";
	
	String WEB_LOGIN_ACCESS_KEY = "loginId";
	
	/** 返回地址参数名 */
	String WEB_RETURN_URL_KEY = "returnUrl";
	
	/** 注销地址参数名 */
	String WEB_LOGOUT_URL_KEY = "logoutUrl";
	
	/** 单点登录授权票据 */
	String SSO_TICKET_KEY = "jwtTicket";
}
