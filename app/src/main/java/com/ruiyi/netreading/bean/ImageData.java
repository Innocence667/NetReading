package com.ruiyi.netreading.bean;

//图片信息
public class ImageData {

    //图片宽高
    private int width;
    private int height;

    public ImageData(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
