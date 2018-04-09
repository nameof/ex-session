package com.nameof.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;
import com.nameof.common.constant.Constants;
import com.nameof.common.utils.QRCodeUtils;
import com.nameof.web.aop.log.AutoLog;

@Controller
@RequestMapping("/public")
public class WebController extends BaseController {
	
	@RequestMapping(value = "setAttribute", method = RequestMethod.POST)
	@AutoLog(description = "setAttribute")
	public String setAttribute(String attributeName, String attributeValue,
			HttpSession session) throws WriterException, IOException {
		session.setAttribute(attributeName, attributeValue);
		return "redirect:/";
	}

	/**
	 * 生成带有会话id的二维码供客户端登录
	 * @param session
	 * @param loginId
	 * @throws WriterException
	 * @throws IOException
	 */
	@RequestMapping("loginQRCode")
	public void loginQRCode(HttpSession session, HttpServletResponse resp, String loginId) throws WriterException, IOException {
		if (StringUtils.isEmpty(loginId) || !loginId.equals(session.getAttribute(Constants.WEB_LOGIN_ACCESS_KEY))) {
			return;
		}
		JSONObject json = new JSONObject();
		json.put("sessionid", session.getId());
		QRCodeUtils.writeQRcodeToStream(json.toJSONString(), resp.getOutputStream());
	}
	
	/**
	 * 网页轮询验证扫码登录
	 * @param session
	 * @param loginId
	 * @return 用户是否已登录
	 */
	@RequestMapping(value = "/verifyQRCodeLogin", method = RequestMethod.POST)
	@ResponseBody
	public Boolean verifyQRCodeLogin(HttpSession session, String loginId) {
		if (StringUtils.isEmpty(loginId) || !loginId.equals(session.getAttribute(Constants.WEB_LOGIN_ACCESS_KEY))) {
			return null;
		}
		if (session.getAttribute("user") == null) {
			return Boolean.FALSE;
		}
		else {
			return Boolean.TRUE;
		}
	}
}
