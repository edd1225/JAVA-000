package cn.qj.week3.gateway.ser1;

import cn.qj.week3.gateway.server.codes.pojo.SubscribeReq;
import cn.qj.week3.gateway.server.codes.pojo.SubscribeResp;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public class SubReqServerHandler extends ChannelHandlerAdapter {


    public void channelRead(ChannelHandlerContext ctx, Object msg)
	    throws Exception {
	SubscribeReq req = (SubscribeReq) msg;
	if ("qianjiang".equalsIgnoreCase(req.getUserName())) {
	    System.out.println("Service accept client subscrib req : ["
		    + req.toString() + "]");
	    ctx.writeAndFlush(resp(req.getSubReqID()));
	}
    }

    private SubscribeResp resp(int subReqID) {
	SubscribeResp resp = new SubscribeResp();
	resp.setSubReqID(subReqID);
	resp.setRespCode(0);
	resp.setDesc("Netty book order succeed, 3 days later, sent to the designated address");
	return resp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	cause.printStackTrace();
	ctx.close();// 发生异常，关闭链路
    }
}
