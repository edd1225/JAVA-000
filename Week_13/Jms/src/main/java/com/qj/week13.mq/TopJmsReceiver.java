package com.qj.week13.mq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 消费者 订阅(关注和订阅)
 */
@SuppressWarnings("ALL")
public class TopJmsReceiver {

	private static String BROKERURL = "tcp://127.0.0.1:61616";

	/**
	 * 当前消息队列名(主题)
	 */
	private static String TOP_QUEUE = "top-queue";


	static public void start() throws JMSException {
		System.out.println("消费者启动...");
	
		// 创建ActiveMQConnectionFactory 会话工厂，参数一：账号，参数二：密码，参数三：消息队列通信地址
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKERURL);
		Connection connection = activeMQConnectionFactory.createConnection();
		// 启动JMS 连接
		connection.start();
	
		// 参数一
		// false 不开消息事物，消息主要发送消费者,则表示消息已经签收 修改为true 表示 以事务提交
		// true  开启事物, 发送消息和接收消息后面,调用 session.commit(); 提交事务,表示成功发送或接收
		//参数二
		//	Session.AUTO_ACKNOWLEDGE    消息自动签收(不建议使用)
		// Session.CLIENT_ACKNOWLEDGE  方法手动签收，客戶端调用,textMessage.acknowledge();
		// Session.DUPS_OK_ACKNOWLEDGE  不是必须签收，消息可能会重复发送。消息只有在被确认之后，才认为已经被成功地消费了
		Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
	
		// 获取一个主题对象
		Topic topic = session.createTopic(TOP_QUEUE);
		//创建消费者
		MessageConsumer consumer = session.createConsumer(topic);
		//接收消息
		while (true) {
			TextMessage textMessage = (TextMessage) consumer.receive();
			if (textMessage != null) {
				System.out.println("接受到消息:" + textMessage.getText());
				//手动签收
				textMessage.acknowledge();
				//提交事务
				session.commit();
			} else {
				break;
			}
		}
		connection.close();
	}


	/**
	 * 启动消费者
	 * @param args
	 * @throws JMSException
	 */
	public static void main(String[] args) throws JMSException {
		start();
	}

}