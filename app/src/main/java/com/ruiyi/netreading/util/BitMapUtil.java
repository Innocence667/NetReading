package com.ruiyi.netreading.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.ruiyi.netreading.bean.LocalImageData;
import com.ruiyi.netreading.controller.MyCallBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class BitMapUtil {
    private MyCallBack callBack;

    /**
     * 图片下载
     *
     * @param urls
     */
    public void downLoadPicture(List<String> urls, MyCallBack callBack) {
        this.callBack = callBack;
        new MyBitmapTark().execute(urls);
    }

    class MyBitmapTark extends AsyncTask<List<String>, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(List<String>... lists) {
            return downBitmap(lists[0]);
        }

        @SuppressLint("WrongThread")
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Log.e("BitMapUtil", "onPostExecute: 图片下载成功");
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/123.jpg");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                LocalImageData localImageData = new LocalImageData();
                localImageData.setPath(file.getAbsolutePath());
                callBack.onSuccess(localImageData);
            } catch (FileNotFoundException e) {
                Log.e("downLoadPicture", "onPostExecute: 图片保存失败");
                e.printStackTrace();
            }
        }
    }

    private Bitmap downBitmap(List<String> urls) {
        HttpURLConnection connection = null;
        List<String> imgUrls = urls;
        try {
            connection = (HttpURLConnection) new URL(imgUrls.get(0)).openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 0;//宽高压缩为原来的1/2
                //options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream(), null, options);
                if (imgUrls.size() == 1) {
                    Log.e("BitMapUtil", "downBitmap:单个图片");
                    return bitmap;
                } else {
                    Log.e("BitMapUtil", "downBitmap:多个图片");
                    imgUrls.remove(0);
                    Bitmap bitmap1 = downBitmap(imgUrls);
                    return imageMerge(bitmap, bitmap1);
                }
            } else {
                callBack.onFailed("downBitmap:图片下载异常");
            }
        } catch (IOException e) {
            Log.e("BitMapUtil", "downBitmap: " + e.getMessage());
            Log.e("BitMapUtil", "downBitmap:图片下载异常");
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return null;
    }

    private Bitmap imageMerge(Bitmap bitmap1, Bitmap bitmap2) {
        Log.e("BitMapUtil", "imageMerge: 图片拼接");
        int width = Math.max(bitmap1.getWidth(), bitmap2.getWidth());
        int height = bitmap1.getHeight() + bitmap2.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.parseColor("#D6E6F5"));
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(bitmap1, 0, 0, null);
        canvas.drawBitmap(bitmap2, 0, bitmap1.getHeight(), null);
        return bitmap;
    }
}
