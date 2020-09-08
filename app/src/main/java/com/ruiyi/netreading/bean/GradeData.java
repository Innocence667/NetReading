package com.ruiyi.netreading.bean;

//提交数据步骤分标签的类型
public class GradeData {

    /**
     * gradeMode : 3 //1：总分模式 2：键盘打分 3：步骤分模式
     * stepModeAdd : true //true加分模式，false减法模式
     * stepLength : +1.5 //提交时选择的标签
     */

    private int gradeMode;
    private boolean stepModeAdd;
    private String stepLength;

    public int getGradeMode() {
        return gradeMode;
    }

    public void setGradeMode(int gradeMode) {
        this.gradeMode = gradeMode;
    }

    public boolean isStepModeAdd() {
        return stepModeAdd;
    }

    public void setStepModeAdd(boolean stepModeAdd) {
        this.stepModeAdd = stepModeAdd;
    }

    public String getStepLength() {
        return stepLength;
    }

    public void setStepLength(String stepLength) {
        this.stepLength = stepLength;
    }
}
