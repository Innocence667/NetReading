package com.ruiyi.netreading.bean.request;

public class GetMarkAvgScoreRequest {

    /**
     * taskGuid :
     * teacherGuid :
     */

    private String taskGuid;
    private String teacherGuid;

    public String getTaskGuid() {
        return taskGuid;
    }

    public void setTaskGuid(String taskGuid) {
        this.taskGuid = taskGuid;
    }

    public String getTeacherGuid() {
        return teacherGuid;
    }

    public void setTeacherGuid(String teacherGuid) {
        this.teacherGuid = teacherGuid;
    }
}
