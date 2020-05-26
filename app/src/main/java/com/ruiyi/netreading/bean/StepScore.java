package com.ruiyi.netreading.bean;

public class StepScore {

    /**
     * id : 1
     * index : 0
     * field : stepPoints
     * params : 2.0
     * x : 589
     * y : 82
     */

    private int id; //序号
    private int index;
    private String field;
    private double params; //得分
    private int x; //x坐标
    private int y; //y坐标

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public double getParams() {
        return params;
    }

    public void setParams(double params) {
        this.params = params;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
