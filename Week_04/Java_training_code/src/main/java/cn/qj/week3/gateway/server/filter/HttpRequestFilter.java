package cn.qj.week3.gateway.server.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @Author edd1225(qianjiang)
 * @Date 2020/11/03 上午
 */
public interface HttpRequestFilter {

    void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx);
}
