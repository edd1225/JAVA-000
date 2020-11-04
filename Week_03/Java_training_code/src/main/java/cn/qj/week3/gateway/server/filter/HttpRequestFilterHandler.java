package cn.qj.week3.gateway.server.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 *
 *  @Author edd1225(qianjiang)
 *  @Date 2020/11/03 上午
 *
 */
public class HttpRequestFilterHandler extends ChannelInboundHandlerAdapter implements HttpRequestFilter{

    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {

       //伪造Cookie
        fullRequest.headers().set("Cookie","SESSIONID=b8dd5bd9-9fb7-48cb-a86b-e079cbdf554fb8");
        //设置其他的信息

        fullRequest.headers().set("nio", "hello,   qianjiang !");
    }
}
