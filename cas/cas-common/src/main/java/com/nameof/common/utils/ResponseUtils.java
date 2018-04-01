package com.nameof.common.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ResponseUtils {
	
	public static void writeAsJson(HttpServletResponse resp, Object obj) throws IOException {
		resp.setContentType("application/json; charset=utf-8");
		resp.getWriter().write(JsonUtils.toJSONString(obj));
	}
}
