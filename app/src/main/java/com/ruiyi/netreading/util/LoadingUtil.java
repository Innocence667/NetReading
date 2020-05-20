package com.ruiyi.netreading.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.ruiyi.netreading.activity.R;

public class LoadingUtil {

    private static LoadingUtil util;
    private static Dialog dialog;
    private static AnimatorSet set;
    private static Context context;

    public static LoadingUtil getInstance(Context con) {
        context = con;
        if (util == null) {
            util = new LoadingUtil();
            dialog = new AlertDialog.Builder(context).create();
            set = new AnimatorSet();
        }
        return util;
    }

    public static void showDialog() {
        dialog.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.loading_layout, null);
        TextView redDot = view.findViewById(R.id.redDot);
        TextView blueDot = view.findViewById(R.id.blueDot);
        ValueAnimator translation1 = ObjectAnimator.ofFloat(redDot, "translationX", 0, 30, 0);
        //设置为循环执行
        translation1.setRepeatCount(Animation.INFINITE);
        ValueAnimator translation2 = ObjectAnimator.ofFloat(blueDot, "translationX", 0, -30, 0);
        translation2.setRepeatCount(Animation.INFINITE);
        set.playTogether(translation1, translation2);
        set.setDuration(1000);
        set.start();
        dialog.show();
        dialog.setContentView(view);
    }

    public static void closeDialog() {
        if (set != null) {
            set.cancel();
        }
        dialog.cancel();
        dialog = null;
    }
}
