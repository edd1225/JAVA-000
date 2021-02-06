package com.qj.week13.mq.demo;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 监听消息
 */
@Component
public class Consumer {

	@JmsListener(destination = "${queue}")
	public void receive(String msg) {
		System.out.println("监听器收到msg:" + msg);
	}
}

