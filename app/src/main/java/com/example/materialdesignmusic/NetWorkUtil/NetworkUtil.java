package com.example.materialdesignmusic.NetWorkUtil;

import com.example.materialdesignmusic.CommonData.CommonData;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class NetworkUtil {
    /**
     * @param Url 请求地址
     * @param callback 回调函数
     * */
    public static void requestUrlToData(String Url,okhttp3.Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(CommonData.LOCALHOST + Url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
