package com.ruiyi.netreading.bean.request;

public class GetMarkDataRequest {

    /**
     * teacherGuid : 15436ef7e9d14140b1ac613938970865
     * taskGuid : 1ae525a15dabd1597811854db068de48
     */

    private String teacherGuid;
    private String taskGuid;

    public String getTeacherGuid() {
        return teacherGuid;
    }

    public void setTeacherGuid(String teacherGuid) {
        this.teacherGuid = teacherGuid;
    }

    public String getTaskGuid() {
        return taskGuid;
    }

    public void setTaskGuid(String taskGuid) {
        this.taskGuid = taskGuid;
    }
}
