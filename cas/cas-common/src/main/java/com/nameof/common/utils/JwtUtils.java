package com.nameof.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;


public class JwtUtils {
	
	private static final byte[] SIGN_KEY = "E1MTMzMTE1NjMsInN1YiI6IjEyMyIsImNyZWF0ZWQiOjE1MTI3MDY3NjM3NjB9".getBytes();
	
	/** 默认授权24小时 */
	private static final int EXPIRE = 24 * 60 * 60 * 1000;
	
	public static String generate(String subject) {
		return Jwts.builder()
				  .signWith(SignatureAlgorithm.HS256, SIGN_KEY)
				  .setSubject(subject)
				  .setExpiration(getExpireDate())
				  .compact();
	}
	
	public static String getSubject(String token) {
		Jws<Claims> jws = Jwts.parser().setSigningKey(SIGN_KEY).parseClaimsJws(token);
		return jws.getBody().getSubject();
	}
	
	public static void validate(String token) {
		Jwts.parser().setSigningKey(SIGN_KEY).parse(token);
	}

	private static Date getExpireDate() {
		return new Date(System.currentTimeMillis() + EXPIRE);
	}
}
