package cn.qj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisTest {

    @Autowired
    private DistriubutedLock distriubutedLock;

    public void testDistributedLock() {
        Boolean lock = distriubutedLock.getLock("20201231");
        if(lock) {
            System.out.println("加锁成功！！！");
            System.out.println(distriubutedLock.get("20201231"));
            distriubutedLock.releaseLock("20201231");
        } else {
            System.out.println("加锁失败！！！");
        }
    }
}
