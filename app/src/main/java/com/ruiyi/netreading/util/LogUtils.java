package com.ruiyi.netreading.util;

import android.util.Log;

/**
 * LogCat工具类
 * Created by mayn on 2018/7/10
 */
public class LogUtils {

    /**
     * 超出部分分段输出
     *
     * @param tag
     * @param content
     */
    public static void logE(String tag, String content) {
        int p = 2048;
        long length = content.length();
        if (length < p || length == p)
            Log.e(tag, content);
        else {
            while (content.length() > p) {
                String logContent = content.substring(0, p);
                //把原来的值替换为空
                content = content.replace(logContent, "");
                Log.e(tag, logContent);
            }
            Log.e(tag, content);
        }
    }

}
