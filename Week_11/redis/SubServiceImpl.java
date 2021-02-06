package cn.qj;

import org.springframework.stereotype.Component;

/**
 * 接收消息服务
 */
@Component("subService")
public class SubServiceImpl {
    /**
     * 接收消息
     * @param message
     */
    public void receiveMessage(String message) {
        System.out.println("我收到通道里你发的的消息了....." + message);
    }
}