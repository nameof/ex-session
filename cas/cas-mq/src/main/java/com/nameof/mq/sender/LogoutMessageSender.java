package com.nameof.mq.sender;

import org.springframework.stereotype.Component;

import com.nameof.mq.message.Message;
import com.nameof.mq.queue.RedisMessageQueue;

/**
 * 注销消息发送者
 * 
 * @author ChengPan
 */
@Component
public class LogoutMessageSender extends Sender {
	
	private static final String LOGOUT_QUEUE_NAME = "logoutQueue";

	private static RedisMessageQueue queue = new RedisMessageQueue(LOGOUT_QUEUE_NAME);
	
	@Override
	public void sendMessage(Message message) {
		queue.push(message);
	}

}
