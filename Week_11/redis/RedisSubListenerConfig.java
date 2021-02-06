package cn.qj;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * redis监听
 */
@Component
public class RedisSubListenerConfig {

    private static final String CHANNEL = "test_channel";

    /**
     * 初始化监听器
     * @param connectionFactory
     * @param listenerAdapter
     * @return
     */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(CHANNEL));
        return container;
    }

    /**
     * 绑定消息监听者和接收监听的方法
     * @param subService
     * @return
     */
    @Bean
    MessageListenerAdapter listenerAdapter(SubServiceImpl subService) {
        return new MessageListenerAdapter(subService, "receiveMessage");
    }


    @Bean
    StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * 注册订阅者
     * @return
     */
    @Bean
    SubServiceImpl receiver() {
        return new SubServiceImpl();
    }
}