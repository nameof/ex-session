package com.nameof.web.interceptor;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nameof.common.constant.Constants;
import com.nameof.common.domain.HandleResult;
import com.nameof.common.jwt.JwtHandler;
import com.nameof.common.utils.ResponseUtils;

/**
 * 验证Header中的JWT
 * @author ChengPan
 */
public class AppJwtInterceptor implements HandlerInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(AppJwtInterceptor.class);
	
	private JwtHandler jwtHandler;
	
	public void setJwtHandler(JwtHandler jwtHandler) {
		this.jwtHandler = jwtHandler;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		String jwt = request.getHeader(Constants.WEB_HEADER_APP_JWT);
		HandleResult result = HandleResult.error();
		if (StringUtils.isEmpty(jwt)) {
			result.setInfo("非法请求");
			return result(response, result);
		}
		try {
			jwtHandler.validate(jwt);
			result.setState(true);
		} catch (ExpiredJwtException e) {
			result.setInfo("授权已过期，请重新登录");
			LOG.info("jwt过期 ： {}", jwt);
		} catch (SignatureException e) {
			result.setInfo("非法授权");
			LOG.info("jwt非法 ： {}", jwt);
		} catch (Exception e) {
			result.setInfo("非法授权");
			LOG.info("jwt非法 ： {}", jwt);
		}
		return result(response, result);
	}
	
	private boolean result(HttpServletResponse resp, HandleResult result) throws IOException {
		if (result.isState())
			return true;
		ResponseUtils.writeAsJson(resp, result);
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {}

}
