package com.ruiyi.netreading;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.ruiyi.netreading.util.PreferencesService;
import com.ruiyi.netreading.util.Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private static Context context;
    private static List<String> colors;

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
        PreferencesService.getInstance(context)
                .saveAutoSubmit(true) //自动提交
                .savePointFive(false) //关闭0.5功能
                .saveDeviceBrand(Tool.getDeviceBrand()) //设备型号
                .saveSpenColor("#CF2257") //spen颜色
                .saveSpenSize(3); //spen宽度
        Log.e("MyApplication", "设备厂商: " + Tool.getDeviceBrand());
        Log.e("MyApplication", "设备型号: " + Tool.getSystemModel());
        Log.e("MyApplication", "设备android版本: " + Tool.getSystemVersion());
        colors = new ArrayList<>();
        colors.add("#EFDCD3");
        colors.add("#F59999");
        colors.add("#E86262");
        colors.add("#AA4446");
        colors.add("#6B4849");
        colors.add("#34231E");
        colors.add("#435772");
        colors.add("#2DA4A8");
        colors.add("#FEAA3A");
        colors.add("#FD6041");
        colors.add("#CF2257");
        colors.add("#404040");
        colors.add("#92BEE2");
        colors.add("#2286D8");
    }

    public static Context getContext() {
        return context;
    }

    public static List<String> getColors() {
        return colors;
    }
}
