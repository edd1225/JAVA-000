package week11.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class DistributedCount {

    private static final String INIT_COUNT_VALUE = "1";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Long increment(String key) {
        if(redisTemplate.boundValueOps(key).get() == null) {
            redisTemplate.boundValueOps(key).set(INIT_COUNT_VALUE);
            return 1L;
        } else {
            return redisTemplate.boundValueOps(key).increment();
        }
    }

    public String get(String key) {
        return redisTemplate.boundValueOps(key).get();
    }

    public void releaseCount(String key) {
        redisTemplate.delete(key);
    }
}
