package com.nameof.mq.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nameof.common.utils.RedisUtil;
import com.nameof.mq.message.Message;
import com.nameof.mq.queue.MessageQueue;
import com.nameof.mq.queue.RedisMessageQueue;
import com.nameof.mq.receiver.LogoutMessageReceiver;
import com.nameof.mq.receiver.Receiver;
import com.nameof.mq.sender.LogoutMessageSender;
import com.nameof.mq.sender.Sender;

/**
 * 注销消息调度器，开启一个{@link cas.mq.support.LogoutReceiverDispatcher.LogoutMessageHandler}线程
 * 处理注销消息
 * 
 * @author ChengPan
 */
//FIXME 此处存在过度设计嫌疑
@Component
public class LogoutReceiverDispatcher{
	
	/** 处理注销消息的线程实例 */
	private static LogoutMessageHandler workerThread = new LogoutMessageHandler();;
	
	private static final Logger logger = LoggerFactory.getLogger(LogoutReceiverDispatcher.class);
	
	private LogoutReceiverDispatcher() {}
	
	private static final LogoutReceiverDispatcher INSTANCE = new LogoutReceiverDispatcher();

	/** 任务状态 */
	public static final int WORKER_STATE_INIT = 0;
	public static final int WORKER_STATE_STARTED = 1;
	public static final int WORKER_STATE_SHUTDOWN = 2;
	
	@SuppressWarnings({ "unused" })
	private volatile int workerState = WORKER_STATE_INIT; // 0 - init, 1 - started, 2 - shut down
	
	private static final AtomicIntegerFieldUpdater<LogoutReceiverDispatcher> WORKER_STATE_UPDATER =
	        AtomicIntegerFieldUpdater.newUpdater(LogoutReceiverDispatcher.class, "workerState");
	
	@PostConstruct
	public static void start() {
		
		switch (WORKER_STATE_UPDATER.get(INSTANCE)) {
	        case WORKER_STATE_INIT:
	            if (WORKER_STATE_UPDATER.compareAndSet(INSTANCE, WORKER_STATE_INIT, WORKER_STATE_STARTED)) {
	                workerThread.start();
	            }
	            break;
	        case WORKER_STATE_STARTED:
	            break;
	        case WORKER_STATE_SHUTDOWN:
	            throw new IllegalStateException("cannot be started once stopped");
	        default:
	            throw new Error("Invalid WorkerState");
		}
	}
	
	@PreDestroy
	public static void stop() {
		if (WORKER_STATE_UPDATER.compareAndSet(INSTANCE, WORKER_STATE_STARTED, WORKER_STATE_SHUTDOWN)) {
			workerThread.setHandleMsg(false);
		}
	}
	
	/**
	 * 默认情况下{@link cas.mq.support.LogoutReceiverDispatcher.LogoutMessageHandler}会持续
	 * 从缓存中获取注销消息，并存入msgBuffer缓冲队列。同时开启{@link cas.mq.support.LogoutReceiverDispatcher.LogoutMessageHandler#INIT_POOL_SIZE}
	 * 个线程对缓存队列中的注销消息进行处理。
	 * @author ChengPan
	 */
	private static class LogoutMessageHandler extends Thread{

		/** 处理注销消息的线程数量 */
		private static final int INIT_POOL_SIZE = 2;
		
		private ExecutorService executor = Executors.newFixedThreadPool(INIT_POOL_SIZE);
		
		/** 线程运行标志 */
		private volatile boolean handleMsg = true;

		/** redis远程队列名 */
		private static final String LOGOUT_QUEUE_NAME = "logoutQueue";
		
		/** redis远程队列 */
		private static final MessageQueue queue = new RedisMessageQueue(LOGOUT_QUEUE_NAME);
		
		/** 用于线程退出时，将{@link #executor}未处理完成的Message，重发返到消息队列中，做到消息可靠 */
		private static Sender logoutMessageSender = new LogoutMessageSender();
		
		/** 消息缓冲队列 */
		private static final BlockingQueue<Message> msgBuffer = new LinkedBlockingQueue<>();
		
		private static final Logger logger = LoggerFactory.getLogger(LogoutMessageHandler.class);
		
		@Override
		public void run() {
			
			logger.debug("handle message thread start");
			
			for(int i = 0; i < INIT_POOL_SIZE; i++) {
				executor.execute(new DispatchRuner(msgBuffer));
			}
			
			while (handleMsg) {
				try {
					Message message = queue.pop();
					if (message != null) {
						msgBuffer.put(message);
					}
				} catch (InterruptedException e) {
					logger.error("注销消息获取异常", e);
				}
			}
			
			executor.shutdownNow();

			//重发消息到消息队列中
			for (Message message : msgBuffer) {
				logoutMessageSender.sendMessage(message);
			}
			
			//释放Jedis资源
			RedisUtil.returnResource();
			
			logger.debug("handle message thread quit");
		}

		public boolean isHandleMsg() {
			return this.handleMsg;
		}

		/**
		 * 如果设置线程标志为false，则消息线程将退出
		 * @param handleMsg 线程运行标志
		 */
		public void setHandleMsg(boolean handleMsg) {
			this.handleMsg = handleMsg;
		}
		
	}

	/**
	 * {@link cas.mq.support.LogoutReceiverDispatcher.DispatchRuner} 的任务是从缓冲队列中获取注销消息
	 * 并使用{@link cas.mq.receiver.LogoutMessageReceiver}实例对消息进行处理
	 * @author ChengPan
	 */
	private static class DispatchRuner implements Runnable{

		/** 消息缓冲队列 */
		private BlockingQueue<Message> msgBuffer;
		
		/** 注销消息接收者 */
		private Receiver logoutMessageReceiver = new LogoutMessageReceiver();
		
		private static final Logger logger = LoggerFactory.getLogger(DispatchRuner.class);

		public DispatchRuner(BlockingQueue<Message> msgBuffer) {
			this.msgBuffer = msgBuffer;
		}
		
		@Override
		public void run() {
			logger.debug("线程 {} 开始处理消息", Thread.currentThread().getId());
			while (true) {
				//处理异常，健壮执行
				try {
					Message msg = msgBuffer.take();
					logoutMessageReceiver.handleMessage(msg);
				} catch (InterruptedException e) {
					logger.error("注销消息处理异常", e);
				}
			}
		}
		
	}
	
}
