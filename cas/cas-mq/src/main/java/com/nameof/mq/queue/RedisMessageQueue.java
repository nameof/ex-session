package com.nameof.mq.queue;

import java.util.List;

import com.nameof.common.utils.JsonUtils;
import com.nameof.common.utils.RedisUtil;
import com.nameof.mq.message.Message;

/**
 * 基于Redis的list数据结构实现的消息队列
 * 
 * @author ChengPan
 */
public class RedisMessageQueue extends MessageQueue {

	private final String queueName;
	
	public RedisMessageQueue(String queueName) {
		this.queueName = queueName;
	}
	
	@Override
	public void push(Message message) {
		RedisUtil.getJedis().lpush(queueName, JsonUtils.toJSONString(message));
	}

	@Override
	public Message pop() {
		//redis阻塞操作队列，获取成功返回2个元素，第一个是list的key，第二个是值
		List<String> list = RedisUtil.getJedis().brpop(0, queueName);
		return JsonUtils.toBean(list.get(1), Message.class);
	}
}
