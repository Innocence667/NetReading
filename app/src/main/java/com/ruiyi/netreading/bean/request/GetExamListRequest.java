package com.ruiyi.netreading.bean.request;

public class GetExamListRequest {

    /**
     * status : 1
     * schoolGuid :
     * gradeGuid :
     * beginDate :
     * endDate :
     * teacherGuid : 15436ef7e9d14140b1ac613938970865
     */

    private int status;//0已结束、1进行中(阅卷任务当前的状态)
    private String schoolGuid;
    private String gradeGuid;
    private String beginDate;
    private String endDate;
    private String teacherGuid;//教师guid

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSchoolGuid() {
        return schoolGuid;
    }

    public void setSchoolGuid(String schoolGuid) {
        this.schoolGuid = schoolGuid;
    }

    public String getGradeGuid() {
        return gradeGuid;
    }

    public void setGradeGuid(String gradeGuid) {
        this.gradeGuid = gradeGuid;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTeacherGuid() {
        return teacherGuid;
    }

    public void setTeacherGuid(String teacherGuid) {
        this.teacherGuid = teacherGuid;
    }
}
