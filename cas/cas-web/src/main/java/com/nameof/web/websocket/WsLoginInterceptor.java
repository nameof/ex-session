package com.nameof.web.websocket;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class WsLoginInterceptor implements HandshakeInterceptor {
	
	private static final Logger LOG = LoggerFactory.getLogger(WsLoginInterceptor.class);

	@Override
	public void afterHandshake(ServerHttpRequest req, ServerHttpResponse resp,
			WebSocketHandler handler, Exception e) {
		LOG.debug("afterHandshake : {}" , req.getPrincipal());
	}

	@Override
	public boolean beforeHandshake(ServerHttpRequest req,
			ServerHttpResponse resp, WebSocketHandler handler,
			Map<String, Object> map) throws Exception {
		
		LOG.debug("beforeHandshake : {}" , req.getPrincipal());
		
		ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) req;
		HttpSession session = serverHttpRequest.getServletRequest().getSession();
		map.put("token", session.getId());//put session id to websocket websession attribute
		return true;
	}

}
