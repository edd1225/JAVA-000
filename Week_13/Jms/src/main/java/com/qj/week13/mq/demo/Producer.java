package com.qj.week13.mq.demo;

import com.alibaba.fastjson.JSONObject;
import com.qj.week13.mq.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
    
import javax.jms.Queue;
     
    /**
    * 发送消息
    */
    @Component
        @EnableScheduling
        public class Producer {
        /**
         * mq对象
         */
         @Autowired
         private JmsMessagingTemplate jmsMessagingTemplate;
      /**
     * 队列名称
        */
        @Autowired
        private Queue queue;
  
     /**
      * 每五秒发送一次
      */
        @Scheduled(fixedDelay = 5000)
        public void send() {
        System.out.println();
        //发送对象转为json字符串
        UserEntity userEntity = new UserEntity(1L, "2",15);
        String json = JSONObject.toJSONString(userEntity);
        System.out.println("发送消息="+json);
        jmsMessagingTemplate.convertAndSend(queue, "测试消息队列" + json+"--"+System.currentTimeMillis());
        }
}