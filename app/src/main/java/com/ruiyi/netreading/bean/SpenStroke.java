package com.ruiyi.netreading.bean;

import android.graphics.PointF;

public class SpenStroke {
    private float penSize; //笔的宽度
    private String color; //笔的颜色
    private String pageName; //当前页面(A、B面)
    private PointF[] pointS; //触摸点
    //private float[] floatS; //压力
    //private int[] integerS; //时间戳


    public float getPenSize() {
        return penSize;
    }

    public String getColor() {
        return color;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setPenSize(float penSize) {
        this.penSize = penSize;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public PointF[] getPointS() {
        return pointS;
    }

    public void setPointS(PointF[] pointS) {
        this.pointS = pointS;
    }

    /*public int[] getIntegerS() {
        return integerS;
    }

    public float[] getFloatS() {
        return floatS;
    }

    public void setFloatS(float[] floatS) {
        this.floatS = floatS;
    }

    public void setIntegerS(int[] integerS) {
        this.integerS = integerS;
    }*/
}
