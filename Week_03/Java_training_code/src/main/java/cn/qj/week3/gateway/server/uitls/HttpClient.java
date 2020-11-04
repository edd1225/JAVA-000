package cn.qj.week3.gateway.server.uitls;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * HttpClient
 *
 * @author QIANJIANG on 2020/11/4
 */
public interface HttpClient<T> {

    String syncGet(String url) throws IOException;

    void asyncGet(String url, Map<String, String> headersMap, Consumer<T> consumer);

    String post(String url, String jsonData) throws IOException;

}
