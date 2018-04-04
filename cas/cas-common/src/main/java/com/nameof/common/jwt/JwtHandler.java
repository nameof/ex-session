package com.nameof.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * JWT token生成和解析
 * @author ChengPan
 */
@Component
public class JwtHandler {
	
	private byte[] signKey;
	
	/** APP默认授权时间，毫秒单位 */
	private long appTokenExpire;
	
	/** 单点登录默认授权时间，毫秒单位 */
	private long ssoTicketExpire;
	
	@Value("${jwt.app.token.expire}")
	private void setAppTokenExpire(String appTokenExpireStr) {
		Integer val = Integer.valueOf(appTokenExpireStr);
		if (val <= 0) {
			throw new IllegalArgumentException("jwt.app.token.expire 参数必须大于零");
		}
		this.appTokenExpire = val * 1000;
	}

	@Value("${jwt.sso.ticket.expire}")
	private void setSsoTicketExpire(int ssoTicketExpire) {
		Integer val = Integer.valueOf(ssoTicketExpire);
		if (val <= 0) {
			throw new IllegalArgumentException("jwt.sso.ticket.expire 参数必须大于零");
		}
		this.ssoTicketExpire = val * 1000;
	}
	
	@Value("${jwt.sign.key}")
	private void setSignKey(String signKeyStr) {
		if (StringUtils.isEmpty(signKeyStr)) {
			throw new IllegalArgumentException("jwt.sign.key 不能为空");
		}
		this.signKey = signKeyStr.getBytes();
	}

	/**
	 * 生成APP端REST接口使用的授权票据
	 * @param subject
	 */
	public String generateAppToken(String subject) {
		return Jwts.builder()
				  .signWith(SignatureAlgorithm.HS256, signKey)
				  .setSubject(subject)
				  .setExpiration(getAppTokenExpireDate())
				  .compact();
	}
	
	/**
	 * 生成单点登录的授权票据
	 * @param subject
	 * @return
	 */
	public String generateSSOTicket(String subject, Map<String, Object> data) {
		JwtBuilder jb = Jwts.builder()
				  .signWith(SignatureAlgorithm.HS256, signKey)
				  .setSubject(subject)
				  .setExpiration(getSsoTicketExpireDate());
		if (data != null) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				jb.claim(entry.getKey(), entry.getValue());
			}
		}
		return jb.compact();
	}
	
	public Claims getClaims(String token) {
		Jws<Claims> jws = Jwts.parser().setSigningKey(signKey).parseClaimsJws(token);
		return jws.getBody();
	}
	
	public void validate(String token) {
		Jwts.parser().setSigningKey(signKey).parse(token);
	}
	
	public Date getAppTokenExpireDate() {
		return new Date(System.currentTimeMillis() + appTokenExpire);
	}

	public Date getSsoTicketExpireDate() {
		return new Date(System.currentTimeMillis() + ssoTicketExpire);
	}
}
