package com.ruiyi.netreading.bean.request;

public class GetStudentMarkDataRequest {

    /**
     * teacherGuid :
     * taskGuid :
     * studentGuid :
     */

    private String teacherGuid;
    private String taskGuid;
    private String studentGuid;

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

    public String getStudentGuid() {
        return studentGuid;
    }

    public void setStudentGuid(String studentGuid) {
        this.studentGuid = studentGuid;
    }
}
