package com.ruiyi.netreading.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.Map;

/**
 * //数据保存到本地
 * Created by mayn on 2018/6/20
 */
public class PreferencesService {

    private static PreferencesService preferencesService;

    private static Context mContext;
    private SharedPreferences sharedPreferences;
    private Editor editor;

    public static PreferencesService getInstance(Context context) {
        if (preferencesService == null) {
            preferencesService = new PreferencesService(context);
            mContext = context;
        }
        return preferencesService;
    }

    public PreferencesService(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        }
        editor = sharedPreferences.edit();
    }

    /*commit和apply方法的区别：
     *1.commit和apply虽然都是原子性操作，但是原子的操作不同，commit是原子提交到数据库，所以从提交数据到存在Disk中都是同步过程，中间不可打断。
     *2.而apply方法的原子操作是原子提交的内存中，而非数据库，所以在提交到内存中时不可打断，之后再异步提交数据到数据库中，因此也不会有相应的返回值。
     *3.所有commit提交是同步过程，效率会比apply异步提交的速度慢，但是apply没有返回值，永远无法知道存储是否失败。
     *4.在不关心提交结果是否成功的情况下，优先考虑apply方法。
     */


    /**
     * 保存第一次选择的服务器地址
     *
     * @param serPath 要保存的服务器名称(高县、双流)
     */
    public PreferencesService saveServicePath(String serPath) {
        editor.putString("servicePath", serPath);
        editor.commit();
        return this;
    }

    public String getServicePath() {
        return sharedPreferences.getString("servicePath", "");
    }

    //服务器地址要显示的内容
    public PreferencesService savePath(String path) {
        editor.putString("path", path);
        editor.commit();
        return this;
    }

    public String getPath() {
        return sharedPreferences.getString("path", "");
    }

    /**
     * 保存用户信息
     *
     * @param uName
     * @param uSchool
     * @return
     */
    public PreferencesService saveUser(String uName, String uGuid, String uSchool) {
        editor.putString("userName", uName);
        editor.putString("userGuid", uGuid);
        editor.putString("userSchool", uSchool);
        editor.commit();
        return this;
    }

    public Map<String, String> getUser() {
        Map<String, String> map = new HashMap<>();
        map.put("userName", sharedPreferences.getString("userName", ""));
        map.put("userSchool", sharedPreferences.getString("userSchool", ""));
        return map;
    }

    //获取用户guid
    public String getUserGuid() {
        return sharedPreferences.getString("userGuid", "");
    }

    public void clearUserData() {
        editor.putString("userName", "");
        editor.putString("userGuid", "");
        editor.putString("userSchool", "");
        editor.putString("topScore", "");
        editor.commit();
    }

    /**
     * 保存设备厂商
     *
     * @param brand
     * @return
     */
    public PreferencesService saveDeviceBrand(String brand) {
        editor.putString("Brand", brand);
        editor.commit();
        return this;
    }

    /**
     * 获取设备厂商
     *
     * @return
     */
    public String getDeviceBrand() {
        return sharedPreferences.getString("Brand", "");
    }


    /**
     * 设置自动提交
     *
     * @param bol
     */
    public PreferencesService saveAutoSubmit(Boolean bol) {
        editor.putBoolean("AutoSubmit", bol);
        editor.commit();
        return this;
    }

    //默认开启
    public boolean getAutoSubmit() {
        return sharedPreferences.getBoolean("AutoSubmit", true);
    }

    /**
     * 设置小数赋分功能
     *
     * @param bol
     */
    public PreferencesService savePointFive(Boolean bol) {
        editor.putBoolean("Pointfive", bol);
        editor.commit();
        return this;
    }

    //默认关闭
    public boolean getPointFive() {
        return sharedPreferences.getBoolean("Pointfive", false);
    }

    /**
     * 步骤分打分模式(true开启减分模式、false关闭减分模式)默认关闭减分模式
     *
     * @param bol
     */
    public PreferencesService saveStepMode(Boolean bol) {
        editor.putBoolean("StepScoreMode", bol);
        editor.commit();
        return this;
    }

    public boolean getStepMode() {
        return sharedPreferences.getBoolean("StepScoreMode", false);
    }

    /**
     * 设置置顶分数
     *
     * @param str
     */
    public PreferencesService saveTopScore(String str) {
        editor.putString("topScore", str);
        editor.commit();
        return this;
    }

    public String getTopScore() {
        return sharedPreferences.getString("topScore", "");
    }

    /**
     * 设置spen颜色
     *
     * @param string
     * @return
     */
    public PreferencesService saveSpenColor(String string) {
        editor.putString("spenColor", string);
        editor.commit();
        return this;
    }

    public String getSpenColor() {
        return sharedPreferences.getString("spenColor", "#CF2257");
    }

    /**
     * 设置spen的宽度
     *
     * @param size
     * @return
     */
    public PreferencesService saveSpenSize(int size) {
        editor.putInt("spenSize", size);
        editor.commit();
        return this;
    }

    public int getSpenSize() {
        return sharedPreferences.getInt("spenSize", 3);
    }

    // 0横屏-ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    // 1竖屏-ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    public PreferencesService saveScreenType(int type) {
        editor.putInt("screen", type);
        editor.commit();
        return this;
    }

    public int getScreenType() {
        return sharedPreferences.getInt("screen", 0);
    }
}
