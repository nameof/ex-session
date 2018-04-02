package com.nameof.sso.client.web.sso;

public class SSOConfiguration {
	
	private String casLoginUrl = "http://localhost:8080/cas-web/login";
	
	private String casLogoutUrl = "http://localhost:8080/cas-web/logout";

	private String clientLogoutUrl = "http://localhost:8080/sso-client/logout";
	
	private String validateTicketUrl = "http://localhost:8080/cas-web/sso/validateTicket";
	
	/** 接入CAS的授权ID，在验证ticket时传递 */
	private String clientId = "";
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getCasLoginUrl() {
		return casLoginUrl;
	}

	public void setCasLoginUrl(String casLoginUrl) {
		this.casLoginUrl = casLoginUrl;
	}

	public String getCasLogoutUrl() {
		return casLogoutUrl;
	}

	public void setCasLogoutUrl(String casLogoutUrl) {
		this.casLogoutUrl = casLogoutUrl;
	}

	public String getClientLogoutUrl() {
		return clientLogoutUrl;
	}

	public void setClientLogoutUrl(String clientLogoutUrl) {
		this.clientLogoutUrl = clientLogoutUrl;
	}

	public String getValidateTicketUrl() {
		return validateTicketUrl;
	}

	public void setValidateTicketUrl(String validateTicketUrl) {
		this.validateTicketUrl = validateTicketUrl;
	}
}
