package cn.qj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DistriubutedLock {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String DEFAULT_LOCK_VALUE = "lock";

    private static final Long DEFAULT_EXPIRED_TIME = 30000L;

    /**
     * 获取锁
     * @param key
     * @return
     */
    public boolean getLock(String key) {
        Boolean b = redisTemplate.opsForValue().setIfAbsent(key, DEFAULT_LOCK_VALUE, DEFAULT_EXPIRED_TIME, TimeUnit.MILLISECONDS);
        if(b != null && b) {
            return true;
        } else {
            return false;
        }
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
