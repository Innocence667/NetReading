package com.ruiyi.netreading.bean.response;

//新数据保存成功返回结果
public class SavaDataResponse {

    /**
     * status : 1
     * success : 200
     * myCount : 35
     * myNumber : 3
     * taskCount : 35
     * taskNumber : 3
     */

    private int status;
    private int success;
    private int myCount; //我的任务量
    private int myNumber; //我的已阅量
    private int taskCount; //任务总量
    private int taskNumber; //任务已阅量
    private int arbCount; //仲裁教师的任务量

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getMyCount() {
        return myCount;
    }

    public void setMyCount(int myCount) {
        this.myCount = myCount;
    }

    public int getMyNumber() {
        return myNumber;
    }

    public void setMyNumber(int myNumber) {
        this.myNumber = myNumber;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    public int getArbCount() {
        return arbCount;
    }

    public void setArbCount(int arbCount) {
        this.arbCount = arbCount;
    }
}
