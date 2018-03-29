package com.nameof.web.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WsLoginHandler extends TextWebSocketHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(WsLoginHandler.class);
    
	private static final Map<String, WebSocketSession> users;
    
    private static final String CLIENT_ID = "token";

    static {
        users = new ConcurrentHashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	String token = (String) session.getAttributes().get(CLIENT_ID);
        users.put(token, session);
        
        LOG.debug("afterConnectionEstablished , http session id : {}", token);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {}

    /**
     * 发送信息给指定用户
     * @param clientId
     * @param message
     * @return
     */
    public boolean sendMessageToUser(String clientId, TextMessage message) {
    	WebSocketSession session = users.get(clientId);
        if (session == null) {
        	LOG.warn("sendMessage {} to  User {} , but there is no WebSocketSession", message, clientId);
        	return false;
        }
        if (!session.isOpen()) {
        	LOG.warn("sendMessage {} to  User {}, but WebSocketSession is not open", message, clientId);
        	return false;
        }
        try {
            session.sendMessage(message);
        } catch (IOException e) {
        	LOG.error("sendMessage to  User {} fail {}", clientId, e);
            return false;
        }
        return true;
    }

    public boolean sendMessageToAllUsers(TextMessage message) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    	users.remove((String) session.getAttributes().get(CLIENT_ID));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        users.remove((String) session.getAttributes().get(CLIENT_ID));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
