## 简介

    简单的 Activemp JMS 示例代码

### 1.搜索 ActiveMQ 镜像

```bash
docker search activemq
```

### 2.获取 ActiveMQ 镜像

```bash
docker pull webcenter/activemq
```

### 3.docker 启动 ActiveMQ 命令

    简单使用docker启动一个：

```
docker run -d --name activemq -p 61617:61616 -p 8162:8161 webcenter/activemq
```

> 61616是 activemq 的容器使用端口（映射为61617） 
>
> 8161是 web 页面管理端口（对外映射为8162）

###  4.使用 docker ps 查看 ActiveMQ 已经运行了

```
docker ps -a
```

### 5.使用 docker exec -it activemq /bin/bash 进入 ActiveMQ

```
docker exec -it activemq /bin/bash 
```

## maven 项目，pom.xml

```
 <dependencies>          
 <dependency>           
 <groupId>org.apache.activemq</groupId>       
 <artifactId>activemq-core</artifactId>   
 <version>5.7.0</version>   
 </dependency>          
 </dependencies>
```

## 点对点 模式

重点是事务和签收方式

```
	//获取session
		// 参数一
		//     false 不开消息事物，消息主要发送消费者,则表示消息已经签收 修改为true 表示 以事务提交
		//     true  开启事物, 发送消息和接收消息后面,调用 session.commit(); 提交事务,表示成功发送或接收
		//参数二
		//	   Session.AUTO_ACKNOWLEDGE    消息自动签收(不建议使用)
		//    Session.CLIENT_ACKNOWLEDGE  方法手动签收，客戶端调用,textMessage.acknowledge();
		//    Session.DUPS_OK_ACKNOWLEDGE  不是必须签收，消息可能会重复发送。消息只有在被确认之后，才认为已经被成功地消费了
		Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
		
```

**生产者**
```java
package cn.qj.week13.mq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 生产者 消息(点对点)
 * 
 * @author edd1225
 *
 */
@SuppressWarnings("ALL")
public class Producer {
	/**
	 * 消息队列通信地址
	 */
	private static String BROKERURL = "tcp://127.0.0.1:61616";
	/**
	 * 当前消息队列名
	 */
private static String QUEUE = "ws-queue";
	
	static public void start() throws JMSException {
		System.out.println("生产者已经启动....");
		// 创建ActiveMQConnectionFactory 会话工厂，参数一：账号，参数二：密码，参数三：消息队列通信地址
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKERURL);
		Connection connection = activeMQConnectionFactory.createConnection();
		// 启动JMS 连接
		connection.start();
		//获取session
		// 参数一
		//     false 不开消息事物，消息主要发送消费者,则表示消息已经签收 修改为true 表示 以事务提交
		//     true  开启事物, 发送消息和接收消息后面,调用 session.commit(); 提交事务,表示成功发送或接收
		//参数二
		//	   Session.AUTO_ACKNOWLEDGE    消息自动签收(不建议使用)
		//    Session.CLIENT_ACKNOWLEDGE  方法手动签收，客戶端调用,textMessage.acknowledge();
		//    Session.DUPS_OK_ACKNOWLEDGE  不是必须签收，消息可能会重复发送。消息只有在被确认之后，才认为已经被成功地消费了
		Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
		// 创建一个队列
		Queue queue = session.createQueue(QUEUE);
		// 创建生产者
		MessageProducer producer = session.createProducer(queue);
		//发送消息
		for (int i = 1; i <= 5; i++) {
			System.out.println("我是消息" + i);
			TextMessage textMessage = session.createTextMessage("我是消息" + i);
			producer.send(textMessage);
	}
	
		//提交事务，connection.createSession 为true使用
		session.commit();
		System.out.println("发送成功!");
		
		//关闭连接
		connection.close();
}
	
    /**
	 * 启动生产者
     * @param args
	 * @throws JMSException
	 */
	public static void main(String[] args) throws JMSException {
		start();
	}

}
```

**消费者**
```
package cn.qj.week13.mq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 消费者 (点对点)
 */
@SuppressWarnings("ALL")
public class JmsReceiver {

	private static String BROKERURL = "tcp://127.0.0.1:61616";
	private static String QUEUE = "ws-queue";


	static public void start() throws JMSException {
		System.out.println("消费点启动...");
	
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
	
		// 获取一个队列
		Queue queue = session.createQueue(QUEUE);
		//创建消费者
		MessageConsumer consumer = session.createConsumer(queue);
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
```

## 发布与订阅模式（主题模式）

 和一对一就这里不一样  Topic topic = session.createTopic(TOP_QUEUE);

**发布主题**
```java
package cn.qj.week13.mq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 生产者 发布主题(关注和订阅)
 * 
 * @author edd1225
 *
 */
@SuppressWarnings("ALL")
public class TopProducer {
	/**
     * 消息队列通信地址
	 */
	private static String BROKERURL = "tcp://127.0.0.1:61616";
	/**
	 * 当前消息队列名(主题)
	 */
	private static String TOP_QUEUE = "top-queue";

	static public void start() throws JMSException {
		System.out.println("生产者已经启动....");
		// 创建ActiveMQConnectionFactory 会话工厂，参数一：账号，参数二：密码，参数三：消息队列通信地址
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKERURL);
		Connection connection = activeMQConnectionFactory.createConnection();
		// 启动JMS 连接
		connection.start();
		//获取session
		// 参数一
		//     false 不开消息事物，消息主要发送消费者,则表示消息已经签收 修改为true 表示 以事务提交
		//     true  开启事物, 发送消息和接收消息后面,调用 session.commit(); 提交事务,表示成功发送或接收
		//参数二
		//	   Session.AUTO_ACKNOWLEDGE    消息自动签收(不建议使用)
		//    Session.CLIENT_ACKNOWLEDGE  方法手动签收，客戶端调用,textMessage.acknowledge();
		//    Session.DUPS_OK_ACKNOWLEDGE  不是必须签收，消息可能会重复发送。消息只有在被确认之后，才认为已经被成功地消费了
		Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
		// 获取一个主题对象
		Topic topic = session.createTopic(TOP_QUEUE);
		// 创建生产者
		MessageProducer producer = session.createProducer(topic);
		//发送消息
		for (int i = 1; i <= 5; i++) {
			System.out.println("我是消息" + i);
			TextMessage textMessage = session.createTextMessage("我是消息" + i);
			producer.send(textMessage);
		}

		//提交事务，connection.createSession 为true使用
		session.commit();
		System.out.println("发送成功!");
	
		//关闭连接
		connection.close();
	}

	/**
     * 启动生产者
	 * @param args
     * @throws JMSException
	 */
	public static void main(String[] args) throws JMSException {
		start();
	}
}
```

**订阅主题**

```java
package cn.qj.week13.mq;

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
```

## springboot整合Activemq

#### pom.xml依赖

activemq -starter

```xml
<!-- springboot activemq依赖 -->      
 <dependency>         
 		<groupId>org.springframework.boot</groupId>   
 		<artifactId>spring-boot-starter-activemq</artifactId>     
 </dependency>
```

本篇所有依赖(fastjson)

```xml
 <!-- maven activemq 依赖-->
    <dependency>
        <groupId>org.apache.activemq</groupId>
        <artifactId>activemq-core</artifactId>
        <version>5.7.0</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <!-- spring boot web支持：mvc,aop... -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- https://mvnrepository.com/artifact/com.alibaba/fastjson -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.41</version>
    </dependency>
    <!-- springboot activemq依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-activemq</artifactId>
    </dependency>
```
#### application.yml

```yaml
spring:
  activemq:
    broker-url: tcp://127.0.0.1:61616
    user: admin
    password: admin
queue: ws-query-test
```

```java
#### 使用配置的方式注入消息队列名称 @Configuration
package cn.qj.week13.mq.demo.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

/**
 * 队列配置
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

```

#### 生产者(—和消费者应该是两个项目，但前面配置都相同)

 ```java
package cn.qj.week13.mq.demo;

import com.alibaba.fastjson.JSONObject;
import cn.qj.week13.mq.entity.UserEntity;
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
 ```

  

#### 消费者
```java
package cn.qj.week13.mq.demo;
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

```

#### 最后启动测试（我这生产者消费者丢一起了，看看数据结果）

