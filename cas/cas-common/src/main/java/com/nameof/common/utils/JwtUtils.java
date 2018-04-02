package com.nameof.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;


public class JwtUtils {
	
	private static final byte[] SIGN_KEY = "E1MTMzMTE1NjMsInN1YiI6IjEyMyIsImNyZWF0ZWQiOjE1MTI3MDY3NjM3NjB9".getBytes();
	
	/** 默认授权24小时 */
	private static final int EXPIRE = 24 * 60 * 60 * 1000;
	
	/** 单点登录默认授权3分钟 */
	private static final int SSO_TICKET_EXPIRE = 3 * 60 * 1000;
	
	public static String generate(String subject) {
		return Jwts.builder()
				  .signWith(SignatureAlgorithm.HS256, SIGN_KEY)
				  .setSubject(subject)
				  .setExpiration(getExpireDate())
				  .compact();
	}
	
	/**
	 * 生成单点登录的授权票据
	 * @param subject
	 * @return
	 */
	public static String generateSSOTicket(String subject, Map<String, Object> data) {
		JwtBuilder jb = Jwts.builder()
				  .signWith(SignatureAlgorithm.HS256, SIGN_KEY)
				  .setSubject(subject)
				  .setExpiration(new Date(System.currentTimeMillis() + SSO_TICKET_EXPIRE));
		if (data != null) {
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				jb.claim(entry.getKey(), entry.getValue());
			}
		}
		return jb.compact();
	}
	
	public static Claims getClaims(String token) {
		Jws<Claims> jws = Jwts.parser().setSigningKey(SIGN_KEY).parseClaimsJws(token);
		return jws.getBody();
	}
	
	public static void validate(String token) {
		Jwts.parser().setSigningKey(SIGN_KEY).parse(token);
	}

	private static Date getExpireDate() {
		return new Date(System.currentTimeMillis() + EXPIRE);
	}
}
