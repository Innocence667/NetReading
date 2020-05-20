package com.ruiyi.netreading.controller;

public interface MyCallBack {

    //成功
    void onSuccess(Object model);

    //失败
    void onFailed(String str);
}
