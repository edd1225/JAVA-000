package cn.qj.week2;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/* Copyright @ 2019 Citycloud Co. Ltd.
 * All right reserved.
 * OkHttpDemo
 *
 * @author edd1225
 * @Description: java类作用描述
 * @create 2020/10/28 12:23
 **/
public class OkHttpDemo {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder().url("http://localhost:8801").build();
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } finally {
            client.clone();
        }
    }
}