package io.kimmking.rpcfx.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;

/**
 * @PROJECT_NAME: rpcfx
 * @PACKAGE_NAME: io.kimmking.rpcfx.api
 * @NAME: HttpClientUtils
 * @Author: guanys
 * @DATE: 12/16/20:5:04 PM
 * @Description: TODO
 * @Version: 1.0
 **/
@Slf4j
public class HttpClientUtils {

    private final static CloseableHttpClient httpClient;
    private final static int CONNECT_TIMEOUT = 5000;
    private final static RequestConfig requestConfig;

    static {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(500);
        connectionManager.setDefaultMaxPerRoute(50);
        requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECT_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(CONNECT_TIMEOUT).build();

        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public static String httpPostJson(final String json, final String url) {
        final HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8");
        StringEntity requestEntity = new StringEntity(json, StandardCharsets.UTF_8);
        httpPost.setEntity(requestEntity);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RpcfxException("httpclient请求失败!");
        }
    }
}
