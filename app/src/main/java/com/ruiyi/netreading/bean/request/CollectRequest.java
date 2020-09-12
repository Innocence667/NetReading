package com.ruiyi.netreading.bean.request;

public class CollectRequest {
    /**
     * taskGuid :
     * studentGuid :
     * value :
     */

    private String taskGuid;
    private String studentGuid;
    private String value; //0:取消收藏，其余为收藏  异常卷状态(0正常、1异常)
    private int identity;//0、单评 1、教师一 2、教师二 3、双评

    public String getTaskGuid() {
        return taskGuid;
    }

    public void setTaskGuid(String taskGuid) {
        this.taskGuid = taskGuid;
    }

    public String getStudentGuid() {
        return studentGuid;
    }

    public void setStudentGuid(String studentGuid) {
        this.studentGuid = studentGuid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }
}
