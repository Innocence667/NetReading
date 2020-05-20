package com.ruiyi.netreading.bean.request;

public class GetExamContextRequest {
    private String teacherGuid; //教师Guid
    private String paperGuid; //试卷Guid

    public String getTeacherGuid() {
        return teacherGuid;
    }

    public String getPaperGuid() {
        return paperGuid;
    }

    public void setTeacherGuid(String teacherGuid) {
        this.teacherGuid = teacherGuid;
    }

    public void setPaperGuid(String paperGuid) {
        this.paperGuid = paperGuid;
    }
}
