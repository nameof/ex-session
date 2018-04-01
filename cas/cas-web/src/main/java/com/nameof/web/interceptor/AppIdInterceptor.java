package com.nameof.web.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nameof.common.constant.Constants;
import com.nameof.common.domain.HandleResult;
import com.nameof.common.utils.ResponseUtils;

/**
 * 简单的验证APP请求是否包含合法的AppId，此防护虽然是脆弱的，但与使用非对称的身份验证区别不大，只是尽量避免不合法请求
 * @author ChengPan
 */
public class AppIdInterceptor implements HandlerInterceptor {
	
	/** 允许访问的AppId */
	private List<String> appIds = null;
	
	public AppIdInterceptor(List<String> ids) {
		this.appIds = ids;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String appId = request.getHeader(Constants.WEB_HEADER_APPID);
		if (appIds == null || !appIds.contains(appId)) {
			HandleResult error = HandleResult.error("非法请求");
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
