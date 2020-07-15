package com.ruiyi.netreading;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.ruiyi.netreading.util.PreferencesService;
import com.ruiyi.netreading.util.Tool;

import java.io.File;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        File imagecache = new File(Tool.IMAGEPATH);
        File handwriting = new File(Tool.HANDWRITING);
        File download = new File(Tool.DOWNFILE);
        if (!imagecache.exists()) {
            Log.e("MyApplication", "创建图片缓存文件夹");
            imagecache.mkdirs();
        }
        if (!handwriting.exists()) {
            Log.e("MyApplication", "创建笔记缓存文件夹");
            handwriting.mkdirs();
        }
        if (!download.exists()) {
            Log.e("MyApplication", "创建更新包文件夹");
            download.mkdirs();
        }
        //设置默认：自动提交、小数赋分、置顶分数
        PreferencesService.getInstance(context).saveAutoSubmit(true).savePointFive(false).saveDeviceBrand(Tool.getDeviceBrand());
        Log.e("MyApplication", "设备厂商: " + Tool.getDeviceBrand());
        Log.e("MyApplication", "设备型号: " + Tool.getSystemModel());
        Log.e("MyApplication", "设备android版本: " + Tool.getSystemVersion());
    }

    public static Context getContext() {
        return context;
    }
}
