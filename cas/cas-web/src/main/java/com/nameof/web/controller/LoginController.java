package com.nameof.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nameof.common.constant.Constants;
import com.nameof.common.domain.User;
import com.nameof.common.jwt.JwtHandler;
import com.nameof.common.utils.CookieUtil;
import com.nameof.common.utils.SpringUtils;
import com.nameof.common.utils.UrlBuilder;
import com.nameof.mq.message.LogoutMessage;
import com.nameof.mq.sender.Sender;
import com.nameof.service.UserService;
import com.nameof.web.aop.log.AutoLog;

@Controller
public class LoginController extends BaseController {

	/** "记住我"过期策略为15天，作用于Cookie的maxAge，Session的MaxInactiveInterval */
	private static final int REMEMBER_LOGIN_STATE_TIME = 15 * 24 * 60 * 60;
	
	private static final Charset URL_ENCODING_CHARSET = Charset.forName("UTF-8");
	
	@Autowired
	private Sender logoutMessageSender;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtHandler jwtHandler;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(String returnUrl,
						String logoutUrl,
					    HttpSession session,
					    HttpServletResponse resp,
					    Model model) throws IOException {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			
			if (StringUtils.isNotBlank(returnUrl) && StringUtils.isNotBlank(logoutUrl)) {
				logger.debug("user {} login from {} logout url is {}", new Object[]{user.getName(), returnUrl, logoutUrl});
				
				//存储客户端注销地址
				storeLogoutUrl(session, logoutUrl);

				//返回客户端站点
				backToClient(returnUrl, session, resp);
				return null;
			}
			else {
				return "redirect:index";//不允许重复登录
			}
		}
		
		return rendToLoginView(returnUrl, logoutUrl, session);
	}

	/** 处理网页登录 */
	@RequestMapping(value = "/processLogin", method = RequestMethod.POST)
	@AutoLog(description = "登录")
	public String processLogin(User inputUser,
							   Boolean rememberMe,
							   String returnUrl,
							   String logoutUrl,
							   String loginId,
							   HttpSession session,
							   HttpServletResponse resp,
							   HttpServletRequest req,
							   Model model) throws IOException {
		if (StringUtils.isEmpty(loginId)) {
			return "redirect:/login";
		}
		
		if (!loginId.equals(session.getAttribute(Constants.WEB_LOGIN_ACCESS_KEY))) {
			model.addAttribute("error", "页面已失效，请刷新重试");
			return rendToLoginView(returnUrl, logoutUrl, session);
		}
		session.removeAttribute(Constants.WEB_LOGIN_ACCESS_KEY);
		
		User user = userService.verifyUserLogin(inputUser);
		if (user == null) {
			model.addAttribute("error", "用户名或密码错误!");
			return rendToLoginView(returnUrl, logoutUrl, session);
		}
		else {
			session.setAttribute("user", user);
			if (rememberMe == Boolean.TRUE) {
				rememberMe(session, req, resp);
			}

			logger.debug("user {} login, from {} logout url is {}", new Object[]{user.getName(), returnUrl, logoutUrl});
			
			//存储客户端注销地址
			storeLogoutUrl(session, logoutUrl);
			
			//返回客户端站点
			if (StringUtils.isNotBlank(returnUrl)) {
				backToClient(returnUrl, session, resp);
				return null;
			}
			
			return "redirect:/index";
		}
	}
	
	private void rememberMe(HttpSession session, HttpServletRequest req, HttpServletResponse resp) {
		session.setMaxInactiveInterval(REMEMBER_LOGIN_STATE_TIME);
		Cookie sessionCookie = CookieUtil.getCookie(req, Constants.GLOBAL_SESSION_ID);
		if (sessionCookie != null) {
			sessionCookie.setMaxAge(REMEMBER_LOGIN_STATE_TIME);
			resp.addCookie(sessionCookie);
		}
	}

	private String rendToLoginView(String returnUrl, String logoutUrl, HttpSession session) {
		//返回地址、注销地址存入表单隐藏域
		SpringUtils.setAttribute(Constants.WEB_RETURN_URL_KEY, returnUrl);
		SpringUtils.setAttribute(Constants.WEB_LOGOUT_URL_KEY, logoutUrl);
		String loginId = UUID.randomUUID().toString();
		SpringUtils.setAttribute(Constants.WEB_LOGIN_ACCESS_KEY, loginId);
		session.setAttribute(Constants.WEB_LOGIN_ACCESS_KEY, loginId);
		return "login";
	}

	/**
	 * 存储客户端站点登出地址到session
	 * @param session
	 * @param logoutUrl 登出地址
	 * @throws UnsupportedEncodingException
	 */
	private void storeLogoutUrl(HttpSession session, String logoutUrl) throws UnsupportedEncodingException {
		
		if (StringUtils.isBlank(logoutUrl))
			return;
		
		@SuppressWarnings("unchecked")
		List<String> logoutUrls = (List<String>) session.getAttribute(Constants.WEB_LOGOUT_URL_KEY);
		if (logoutUrls == null) {
			logoutUrls = new ArrayList<>();
		}
		
		logoutUrls.add(URLDecoder.decode(logoutUrl, URL_ENCODING_CHARSET.name()));
		
		//即时交互缓存的序列化实现的session，对象实例变化，此处需要重新set
		session.setAttribute(Constants.WEB_LOGOUT_URL_KEY, logoutUrls);
	}

	/** 颁发JWT票据，并将用户名信息与全局sessiod存储在JWT中，返回客户端站点 */
	private void backToClient(String returnUrl, HttpSession session, HttpServletResponse response) throws IOException {
		User user = (User) session.getAttribute("user");
		Map<String, Object> data = new HashMap<>(1);
		data.put("gloabSessionId", session.getId());
		String ticket = jwtHandler.generateSSOTicket(user.getName(), data);
		
		UrlBuilder builder = UrlBuilder.parse(URLDecoder.decode(returnUrl, URL_ENCODING_CHARSET.name()));
		builder.addParameter(Constants.SSO_TICKET_KEY, ticket);
		response.sendRedirect(builder.toString());
	}
	
	
	/** 注销全局会话，并向客户端站点发送注销消息 */
	@RequestMapping(value = "/logout")
	public String logout(HttpSession session, HttpServletResponse response) {
		@SuppressWarnings("unchecked")
		List<String> logoutUrls = (List<String>) session.getAttribute(Constants.WEB_LOGOUT_URL_KEY);

		//send logout message
		if (logoutUrls != null) {
			logoutMessageSender.sendMessage(new LogoutMessage(session.getId(), logoutUrls));
		}
		
		CookieUtil.addCookie(response, Constants.GLOBAL_SESSION_ID, "", 0);
		
		session.invalidate();
		return "redirect:/login";
	}
}