package cn.qj.week3.gateway.server;


import cn.qj.week3.gateway.server.inbound.HttpInboundServer;


/** 主要启动类
 *
 * Netty 服务端
 *  @Author edd1225(qianjiang)
 *  @Date 2020/11/04 上午
 *
 */
public class NettyServerApp {

    public final static String PROPERTIES_PATH = "/META-INF/HttpApplication.properties";

    public static void main(String[] args) {
        HttpInboundServer server = new HttpInboundServer(PROPERTIES_PATH);
        try {
            server.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
