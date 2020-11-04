package cn.qj.week3.gateway.server.router;

import java.util.Random;

/**
 * RandomHttpEndPointRouter
 *
 * 随机路由
 * 随机获取实际后台服务地址
 *
 * @author qianjiang on 2020/11/3
 */
public class RandomHttpEndPointRouter implements HttpEndpointRouter{

    @Override
    public String route(String[] endpoints) {
        Random random = new Random();
        return endpoints[random.nextInt(endpoints.length)];
    }
}
