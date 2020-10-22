package com.ruiyi.netreading.bean;

public class StepScore {

    /**
     * id : 1
     * index : 0
     * field : stepPoints
     * params : 2.0
     * x : 589
     * y : 82
     * pageName : 试卷A、B面
     */

    private int id; //序号
    private int index; //第几个小题的数据
    private String field; //类型(对号 tick、半对号 halfTick、错号 cross、文本  text、步骤分 stepPoints )
    private double params; //得分
    private float x; //x坐标
    private float y; //y坐标
    private String pageName;

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

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }
}
