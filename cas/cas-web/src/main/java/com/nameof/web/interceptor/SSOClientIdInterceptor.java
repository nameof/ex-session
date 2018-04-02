package com.nameof.web.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nameof.common.constant.Constants;
import com.nameof.common.domain.HandleResult;
import com.nameof.common.utils.ResponseUtils;

/**
 * 验证接入CAS的客户端站点
 * @author ChengPan
 */
public class SSOClientIdInterceptor  implements HandlerInterceptor {
	
	/** 允许授权访问的ClientId */
	private List<String> clientIds = null;
	
	public SSOClientIdInterceptor(List<String> ids) {
		this.clientIds = ids;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String clientId = request.getHeader(Constants.SSO_HEADER_CLIENT_ID);
		if (StringUtils.isEmpty(clientId) || !clientIds.contains(clientId)) {
			HandleResult error = HandleResult.error("未授权的客户端请求");
			ResponseUtils.writeAsJson(response, error);
			return false;
		}
		return true;
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
