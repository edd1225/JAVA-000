package com.qj.week13.mq.demo.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import javax.jms.Queue;

/**
 * 队列配置 使用配置的方式注入消息队列名称
 */
@Configuration
public class QueueConfig {
    /**
     * 消息队列名称
     */
    @Value("${queue}")
    private String queue;

    /**
     * 注入队列名称
     * @return
     */
    @Bean
    public Queue logQueue() {
        //订阅模式(消费者与点对点一致)
        //ActiveMQTopic activeMQTopic = new ActiveMQTopic("topic-my");
        //点对点模式
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(queue);
        return activeMQQueue;
    }
}

