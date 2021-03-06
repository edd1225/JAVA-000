package cn.qj.week3.gateway.client;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

/**
 * Test
 *
 * @author qianjiang on 2020/11/2
 */
public class ClientTest {

    public static void main(String[] args) {
        NettyHttpClient client = new NettyHttpClient();
        client.asyncGet("http://localhost:8808/test", null, fullHttpResponse -> {
            ByteBuf buf = fullHttpResponse.content();
            String result = buf.toString(CharsetUtil.UTF_8);
            System.out.println("response -> "+result);
        });
    }

}
