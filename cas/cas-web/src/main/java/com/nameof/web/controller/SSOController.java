package com.nameof.web.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nameof.common.domain.HandleResult;
import com.nameof.common.utils.JwtUtils;

@RestController
@RequestMapping("/sso")
public class SSOController extends BaseController {

	/**
	 * 为客户端站点验证token, 返回单点登录用户信息和全局sessionid
	 * @param session
	 * @return JWT中存储的用户名、全局sessionid
	 */
	@RequestMapping(value = "/validateTicket", method = RequestMethod.POST)
	public HandleResult validatetoken(HttpSession session, String jwtTicket) {
		if (StringUtils.isEmpty(jwtTicket)) {
			return HandleResult.error("非法请求");
		}
		try {
			Claims claims = JwtUtils.getClaims(jwtTicket);
			HandleResult result = HandleResult.success();
			result.put("subject", claims.getSubject());
			result.put("gloabSessionId", claims.get("gloabSessionId"));
			logger.debug("SSO ticket验证成功，subject ： {}, gloabsessionId: {}", claims.getSubject(), claims.get("gloabSessionId"));
			return result;
		} catch (ExpiredJwtException e) {
			logger.info("SSO jwt Ticket过期 ： {}", jwtTicket);
			return HandleResult.error("授权已过期");
		} catch (SignatureException e) {
			logger.info("SSO jwt Ticket非法 ： {}", jwtTicket);
			return HandleResult.error("非法授权");
		} catch (Exception e) {
			logger.info("SSO jwt Ticket非法 ： {}", jwtTicket);
			return HandleResult.error("非法授权");
		}
	}
}
