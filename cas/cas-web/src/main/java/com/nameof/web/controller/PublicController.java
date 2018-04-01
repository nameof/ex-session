package com.nameof.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;
import com.nameof.common.utils.QRCodeUtils;

@Controller
@RequestMapping("/public")
public class PublicController {
	
	@RequestMapping(value = "setAttribute", method = RequestMethod.POST)
	public String setAttribute(String attributeName, String attributeValue,
			HttpSession session) throws WriterException, IOException {
		session.setAttribute(attributeName, attributeValue);
		return "redirect:/";
	}

	/**
	 * 生成带有会话id的二维码供客户端登录
	 * @param response
	 * @param request
	 * @param session
	 * @throws WriterException
	 * @throws IOException
	 */
	@RequestMapping("loginQRCode")
	public void loginQRCode(HttpServletResponse response, HttpServletRequest request,
			HttpSession session) throws WriterException, IOException {
		JSONObject json = new JSONObject();
		json.put("sessionid", session.getId());
		QRCodeUtils.writeQRcodeToStream(json.toJSONString(), response.getOutputStream());
	}
}
