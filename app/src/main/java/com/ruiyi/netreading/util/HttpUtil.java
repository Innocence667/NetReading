package com.ruiyi.netreading.util;

import com.google.gson.Gson;
import com.ruiyi.netreading.bean.UserBean;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
                .sslSocketFactory(getSSLSocketFactory())
                .build();
    }

    //https://blog.csdn.net/u014752325/article/details/73185351
    //获取这个SSLSocketFactory
    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //获取TrustManager
    private static TrustManager[] getTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };
        return trustAllCerts;
    }

    //获取HostnameVerifier
    public static HostnameVerifier getHostnameVerifier() {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
        return hostnameVerifier;
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
