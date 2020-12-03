package cn.qj.week3.gateway.ser1;

import cn.qj.week3.gateway.server.codes.pojo.SubscribeReq;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqClientHandler extends ChannelHandlerAdapter {

    /**
     * Creates a client-side handler.
     */
    public SubReqClientHandler() {
    }


    public void channelActive(ChannelHandlerContext ctx) {
	for (int i = 0; i < 10; i++) {
	    ctx.write(subReq(i));
	}
	ctx.flush();
    }

    private SubscribeReq subReq(int i) {
	SubscribeReq req = new SubscribeReq();
	req.setAddress("hangzhou ");
	req.setPhoneNumber("1380000000");
	req.setProductName("Nettytest");
	req.setSubReqID(i);
	req.setUserName("qianjiang");
	return req;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
	    throws Exception {
	System.out.println("Receive server response : [" + msg + "]");
    }


    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
	ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	cause.printStackTrace();
	ctx.close();
    }
}