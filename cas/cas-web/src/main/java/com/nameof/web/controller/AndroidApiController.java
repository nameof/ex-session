package com.nameof.web.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;

import com.alibaba.fastjson.JSONObject;
import com.nameof.common.domain.HandleResult;
import com.nameof.common.domain.User;
import com.nameof.common.utils.JwtUtils;
import com.nameof.service.UserService;
import com.nameof.web.websocket.WsLoginHandler;

@RestController
@RequestMapping("/app")
public class AndroidApiController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private WsLoginHandler wsLoginHandler;
	
	@Value("${login.websocket.enable}")
	private boolean loginWithWebSocket;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public HandleResult login(User user) {
		if (StringUtils.isAnyEmpty(user.getName(), user.getPasswd())) {
			return HandleResult.error("用户名或密码不能为空");
		}
		user = userService.verifyUserLogin(user);
		if (user == null) {
			return HandleResult.error("用户名或密码错误");
		}
		HandleResult result = HandleResult.success("登录成功");
		result.put("userInfo", user);
		result.put("accessToken", JwtUtils.generate(user.getName()));
		return result;
	}
	

	/**
	 * 处理手机客户端扫码登录<br/>
	 * 由于APP登录授权后方可扫码请求，所以这里给指定username的用户直接登录成功
	 * @param username
	 * @param passwd
	 * @param session
	 * @return 登录结果提示消息
	 */
	@RequestMapping(value = "/processQRCodeLogin", method = RequestMethod.POST)
	public HandleResult processQRCodeLogin(String username, HttpSession session) {
		User user = userService.findUserByName(username);
		if (user == null) {
			return HandleResult.error("用户不存在");
		}
		session.setAttribute("user", user);
		sendWebSocketMsgIfnecessary(session);
		return HandleResult.success("登录成功");
	}
	

	/**
	 * 向浏览器推送发送当前session已登录
	 * @param session
	 */
	private void sendWebSocketMsgIfnecessary(HttpSession session) {
		if (loginWithWebSocket) {
			JSONObject json = new JSONObject();
			json.put("login", true);
			TextMessage text = new TextMessage(json.toString());
			wsLoginHandler.sendMessageToUser(session.getId(), text);
		}
	}
}
