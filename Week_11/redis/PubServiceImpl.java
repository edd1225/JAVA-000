package cn.qj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息发布服务
 */
@Component
public class PubServiceImpl {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送消息
     * @param channel
     * @param message
     */
    public void sendChannelMess(String channel, String message) {
        try {
            stringRedisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}