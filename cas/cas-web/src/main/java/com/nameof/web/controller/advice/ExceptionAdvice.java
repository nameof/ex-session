package com.nameof.web.controller.advice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;

import com.nameof.common.constant.Constants;
import com.nameof.common.domain.HandleResult;
import com.nameof.web.controller.BaseController;

@ControllerAdvice
public class ExceptionAdvice extends BaseController {
	
	@ExceptionHandler(value = Exception.class)
    public Object defaultErrorHandler(Exception e, HttpServletRequest request) {
        
		logger.error("请求处理异常", e);
		
		if (isRestApi(request)) {
			HandleResult.error("请求处理失败：" + e.getMessage());
        }
        return "error";
    }
	
	/**
	 * 当前请求的Controller是否以ResponseBody直接返回数据，REST接口
	 * @param request
	 * @return
	 */
	private boolean isRestApi(HttpServletRequest request) {
		Object handler = request.getAttribute(Constants.WEB_MVC_CURRENT_HANDLER);
		if (!handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			return false;
		}
		HandlerMethod hm = ((HandlerMethod) handler);
		if (hm.getMethodAnnotation(ResponseBody.class) != null) {
			return true;
		}
		Class<?> controllerClass = hm.getBeanType();
		ResponseBody rb = controllerClass.getAnnotation(ResponseBody.class);
		RestController rc = controllerClass.getAnnotation(RestController.class);
		return rb != null || rc != null;
	}
}
