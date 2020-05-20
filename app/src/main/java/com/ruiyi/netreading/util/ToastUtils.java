package com.ruiyi.netreading.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ruiyi.netreading.activity.R;

import java.lang.reflect.Field;

/**
 * Toast工具类，防止用户多次点击显示多次
 * Created by mayn on 2018/6/30
 */
public class ToastUtils {
    private static Context context;
    private static Toast toast = null;
    private static Toast topToast = null;

    public static void showToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /**
     * 自定义Toast显示和动画
     *
     * @param context Context
     * @param str     显示内容
     * @param styleId 动画
     */
    public static void showTopToast(Context context, CharSequence str, int styleId) {

        //该代码解决多次显示后，后面的toast显示的时间短的问题
        if (topToast != null) {
            topToast.cancel();
            topToast = null;
        }

        if (topToast == null) {
            topToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            topToast.setGravity(Gravity.TOP, 0, 0);
            View view = LayoutInflater.from(context).inflate(R.layout.top_toast, null);
            TextView tv = view.findViewById(R.id.toastTv);
            topToast.setView(view);
            tv.setText(str);
            Object mTN;
            try {
                mTN = getField(topToast, "mTN");
                if (mTN != null) {
                    Object mParams = getField(mTN, "mParams");
                    if (mParams != null && mParams instanceof WindowManager.LayoutParams) {
                        WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
                        params.windowAnimations = styleId;
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.top_toast, null);
            TextView tv = view.findViewById(R.id.toastTv);
            topToast.setView(view);
            tv.setText(str);
            topToast.setDuration(Toast.LENGTH_SHORT);
        }
        topToast.show();
    }

    /**
     * 反射字段
     *
     * @param object    要反射的对象
     * @param fieleName 要反射的字段名称
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private static Object getField(Object object, String fieleName) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieleName);
        if (field != null) {
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }
}
