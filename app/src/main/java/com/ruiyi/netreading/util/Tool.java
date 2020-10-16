package com.ruiyi.netreading.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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

import java.io.ByteArrayOutputStream;
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
     * @param getMarkNextStudentResponse 获取的学生数据
     * @param callBack                   回调
     * @return
     */
    private static int index = 0; //获取imageArr第几个位置数据

    public static void base64ToBitmap(GetMarkNextStudentResponse getMarkNextStudentResponse, MyCallBack callBack) {
        index = 0;
        LocalImageData imageData = new LocalImageData();
        List<ImageData> imageDataList = new ArrayList<>();
        List<GetMarkNextStudentResponse.DataBean.ImageArrBean> imgArrData = getMarkNextStudentResponse.getData().getImageArr();
        if (imgArrData == null || imgArrData.size() == 0) {
            callBack.onFailed("图片数据异常，再试一次。");
        } else {
            if ("0".equals(getMarkNextStudentResponse.getData().getIsOnline())) { //线下考试
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
                    //imgArrData.remove(0);
                    index = 1;
                    Bitmap bitmap1 = base64ToBitmap2(imageDataList, imgArrData, index);
                    Bitmap pictur = MergePictur(bitmap, bitmap1);
                    imageData.setPath(savePic(pictur, getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(0).getId()));
                    imageData.setList(imageDataList);
                    callBack.onSuccess(imageData);
                    bitmap1.recycle();
                } else {
                    imageData.setPath(savePic(bitmap, getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(0).getId()));
                    imageData.setList(imageDataList);
                    callBack.onSuccess(imageData);
                }
            } else if ("1".equals(getMarkNextStudentResponse.getData().getIsOnline())) { //线上考试
                List<String> paths = new ArrayList<>();
                for (int i = 0; i < getMarkNextStudentResponse.getData().getImageArr().size(); i++) {
                    paths.add(getMarkNextStudentResponse.getData().getImageArr().get(i).getSrc());
                }
                new BitMapUtil().downLoadPicture(paths, callBack);
                LoadingUtil.closeDialog();
            }
        }
    }

    /**
     * 图片解析
     *
     * @param imageDataList 存放每个图片的宽高
     * @param imgArrData    base64数据集合
     * @param loc           获取数据的位置
     * @return
     */
    public static Bitmap base64ToBitmap2(List<ImageData> imageDataList, List<GetMarkNextStudentResponse.DataBean.ImageArrBean> imgArrData, int loc) {
        List<GetMarkNextStudentResponse.DataBean.ImageArrBean> imageArrBeanList = imgArrData;
        //获取第一个对象
        String base = imgArrData.get(loc).getSrc();
        if (base.contains("data:image/jpeg;base64,")) {
            base = base.replace("data:image/jpeg;base64,", "");
        }
        byte[] bytes = Base64.decode(base, Base64.DEFAULT);
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        //Bitmap bb = compressBitmap(bitmap1);
        //bitmap1 = matrixBitmap(bitmap1);
        imageDataList.add(new ImageData(bitmap1.getWidth(), bitmap1.getHeight()));
        if (imageArrBeanList.size() != loc + 1) {
            //imageArrBeanList.remove(0);
            Bitmap bitmap2 = base64ToBitmap2(imageDataList, imageArrBeanList, (loc + 1));
            return MergePictur(bitmap1, bitmap2);
        } else {
            return bitmap1;
        }
    }

    //缩放法压缩
    private static Bitmap matrixBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    //质量压缩法(图片的大小是没有变的，因为质量压缩不会减少图片的像素，它是在保持像素的前提下改变图片的位深及透明度等，
    //来达到压缩图片的目的，这也是为什么该方法叫质量压缩方法。那么，图片的长，宽，像素都不变，那么bitmap所占内存大小是不会变的。)
    private static Bitmap compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        int options = 100;
        while (bos.toByteArray().length / 1024 > 500) {
            bos.reset(); //重置bos
            options -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, bos);
        }
        Bitmap bitmap1 = BitmapFactory.decodeByteArray(bos.toByteArray(), 0, bos.toByteArray().length);
        if (bos != null) {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap1;
    }

    //图片合并
    public static Bitmap MergePictur(Bitmap b1, Bitmap b2) {
        Bitmap bitmap = null;
        int width = Math.max(b1.getWidth(), b2.getWidth());
        int height = b1.getHeight() + b2.getHeight();
        //bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(b1, 0, 0, null);
        canvas.drawBitmap(b2, 0, b1.getHeight(), null);
        b1.recycle();
        b1 = null;
        b2.recycle();
        b2 = null;
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, fos);
            try {
                fos.flush();
                fos.close();
                bitmap.recycle();
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
