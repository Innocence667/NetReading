package com.ruiyi.netreading.util;

import com.google.gson.Gson;
import com.ruiyi.netreading.bean.UserBean;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    public static final MediaType TYPEJSON = MediaType.parse("application/json; charset=utf-8");
    private static HttpUtil httpUtil;
    private OkHttpClient okHttpClient;

    public static HttpUtil getInstance() {
        if (httpUtil == null) {
            synchronized (HttpUtil.class) {
                if (httpUtil == null) {
                    httpUtil = new HttpUtil();
                }
            }
        }
        return httpUtil;
    }

    private HttpUtil() {
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    //post表单form提交
    private Call _PostForm(String url, String json) {
        UserBean userBean = new Gson().fromJson(json, UserBean.class);
        RequestBody body = new FormBody.Builder()
                .add("username", userBean.getUsername())
                .add("password", userBean.getPassword())
                .add("ismemory", String.valueOf(userBean.getIsmemory()))
                .add("terminal", String.valueOf(userBean.getTerminal()))
                .build();
        Request request = new Request.Builder().url(url).post(body).build();
        return okHttpClient.newCall(request);
    }

    public Call Login(String url, String json) {
        return _PostForm(url, json);
    }

    //postJson提交
    private Call _PostJSON(String url, String json) {
        //已过时，使用下面方法代替
        //RequestBody body = RequestBody.create(TYPEJSON, json);
        RequestBody body = RequestBody.Companion.create(json, MediaType.parse("application/json;charset=utf-8"));
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = okHttpClient.newCall(request);
        return call;
    }

    public Call PostResponse(String url, String json) {
        return _PostJSON(url, json);
    }
}
