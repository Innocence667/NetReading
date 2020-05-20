package com.ruiyi.netreading.bean;

import android.graphics.PointF;

public class SpenStroke {
    private PointF[] pointS; //触摸点
    //private float[] floatS; //压力
    //private int[] integerS; //时间戳

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
