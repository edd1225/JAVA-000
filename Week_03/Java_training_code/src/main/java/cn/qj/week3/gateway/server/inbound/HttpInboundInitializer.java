package cn.qj.week3.gateway.server.inbound;

import cn.qj.week3.gateway.server.filter.HttpRequestFilterHandler;
import cn.qj.week3.gateway.server.outbound.OkHttpOutboundHandler;
import cn.qj.week3.gateway.server.router.RandomHttpEndPointRouter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
/**
 *
 *  @Author edd1225(qianjiang)
 *  @Date 2020/11/03 上午
 *
 */
public class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(
                new HttpServerCodec(),
                new HttpObjectAggregator(1024 * 1024),
                new HttpInboundHandler(
                       // new SubReqClientHandler()
                        //new NettyHttpOutboundHandler(new RandomHttpEndPointRouter()),
                        new OkHttpOutboundHandler(new RandomHttpEndPointRouter()),
                        new HttpRequestFilterHandler()

                )
        );
    }
}
