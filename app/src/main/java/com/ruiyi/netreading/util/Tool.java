package com.ruiyi.netreading.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Display;

import com.ruiyi.netreading.bean.ImageData;
import com.ruiyi.netreading.bean.LocalImageData;
import com.ruiyi.netreading.bean.response.GetMarkNextStudentResponse;
import com.ruiyi.netreading.controller.MyCallBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tool {

    public static final String IMAGEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/imageacahe";
    public static final String HANDWRITING = Environment.getExternalStorageDirectory().getAbsolutePath() + "/handwriting";
    public static final String DOWNFILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downfile";

    //获取屏幕宽高
    public static Rect getScreenparameters(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Rect rect = new Rect();
        display.getRectSize(rect);
        return rect;
    }

    //获取屏幕宽高
    public static Point getDefaultDisplay(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }

    //获取状态栏的高度
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取手机厂商
     *
     * @return
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机android版本
     *
     * @return
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 解析base64数据
     *
     * @param imageArrBeanList 图片base64数据集合
     * @param id               题目id(保存图片的名称)
     * @param callBack         回调
     * @return
     */
    public static void base64ToBitmap(List<GetMarkNextStudentResponse.DataBean.ImageArrBean> imageArrBeanList, int id, MyCallBack callBack) {
        LocalImageData imageData = new LocalImageData();
        List<ImageData> imageDataList = new ArrayList<>();
        List<GetMarkNextStudentResponse.DataBean.ImageArrBean> imgArrData = imageArrBeanList;
        if (imgArrData == null || imgArrData.size() == 0) {
            callBack.onFailed("图片数据异常，再试一次。");
        } else {
            //排序
            Collections.sort(imgArrData);
            //获取第一个对象
            String base = imgArrData.get(0).getSrc();
            if (base.contains("data:image/jpeg;base64,")) {
                base = base.replace("data:image/jpeg;base64,", "");
            }
            byte[] bytes = Base64.decode(base, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageDataList.add(new ImageData(bitmap.getWidth(), bitmap.getHeight()));

            if (imgArrData.size() != 1) {
                imgArrData.remove(0);
                Bitmap bitmap1 = base64ToBitmap2(imageDataList, imgArrData);
                Bitmap pictur = MergePictur(bitmap, bitmap1);
                imageData.setPath(savePic(pictur, id));
                imageData.setList(imageDataList);
                callBack.onSuccess(imageData);
            } else {
                imageData.setPath(savePic(bitmap, id));
                imageData.setList(imageDataList);
                callBack.onSuccess(imageData);
            }
        }
    }

    /**
     * 图片解析
     *
     * @param imageDataList 存放每个图片的宽高
     * @param imgArrData    base64数据集合
     * @return
     */
    public static Bitmap base64ToBitmap2(List<ImageData> imageDataList, List<GetMarkNextStudentResponse.DataBean.ImageArrBean> imgArrData) {
        List<GetMarkNextStudentResponse.DataBean.ImageArrBean> imageArrBeanList = imgArrData;
        //获取第一个对象
        String base = imgArrData.get(0).getSrc();
        if (base.contains("data:image/jpeg;base64,")) {
            base = base.replace("data:image/jpeg;base64,", "");
        }
        byte[] bytes = Base64.decode(base, Base64.DEFAULT);
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageDataList.add(new ImageData(bitmap1.getWidth(), bitmap1.getHeight()));

        if (imageArrBeanList.size() != 1) {
            imageArrBeanList.remove(0);
            Bitmap bitmap2 = base64ToBitmap2(imageDataList, imageArrBeanList);
            return MergePictur(bitmap1, bitmap2);
        } else {
            return bitmap1;
        }
    }

    //图片合并
    public static Bitmap MergePictur(Bitmap b1, Bitmap b2) {
        Bitmap bitmap = null;

        int width = Math.max(b1.getWidth(), b2.getWidth());
        int height = b1.getHeight() + b2.getHeight();

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(b1, 0, 0, null);
        canvas.drawBitmap(b2, 0, b1.getHeight(), null);

        return bitmap;
    }

    /**
     * 保存图片到本地
     *
     * @param bitmap  bitmap
     * @param picName 图片名称
     * @return 本地路径
     */
    public static String savePic(Bitmap bitmap, int picName) {
        File file = new File(Tool.IMAGEPATH + "/" + picName + ".jpeg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            try {
                fos.flush();
                fos.close();
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("savePic", "savePic: " + e.getMessage());
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("savePic", "savePic: " + e.getMessage());
            return null;
        }
    }
}
