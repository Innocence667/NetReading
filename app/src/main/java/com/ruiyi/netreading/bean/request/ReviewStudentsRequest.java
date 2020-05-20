package com.ruiyi.netreading.bean.request;

//获取回评列表请求模型
public class ReviewStudentsRequest {

    /**
     * taskGudi : 123
     * teacherGuid : 123
     * searchStr :
     * status : 0
     */

    private String taskGuid; //任务guid
    private String teacherGuid; //教师guid
    private String searchStr; //搜索参数(可为空)
    private String status; //0:不异常，1异常，2收藏

    public String getTaskGudi() {
        return taskGuid;
    }

    public void setTaskGudi(String taskGudi) {
        this.taskGuid = taskGudi;
    }

    public String getTeacherGuid() {
        return teacherGuid;
    }

    public void setTeacherGuid(String teacherGuid) {
        this.teacherGuid = teacherGuid;
    }

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
