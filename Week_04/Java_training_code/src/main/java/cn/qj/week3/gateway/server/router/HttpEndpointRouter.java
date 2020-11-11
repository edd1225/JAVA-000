package cn.qj.week3.gateway.server.router;

/**
 *
 *
 * @author qianjiang on 2020/11/3
 */
public interface HttpEndpointRouter {

    String route(String[] endpoints);

}
