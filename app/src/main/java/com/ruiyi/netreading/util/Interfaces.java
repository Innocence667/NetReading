package com.ruiyi.netreading.util;

import android.content.Context;

public class Interfaces {
    private String SERVICEPATH;

    private static Interfaces interfaces;

    public static Interfaces getInstance(Context context) {
        if (interfaces != null) {
            //当前操作的目的是为了每次更改ip的时候可以重新获取一个interface实例
            interfaces = null;
        }
        if (interfaces == null) {
            interfaces = new Interfaces(context);
        }
        return interfaces;
    }

    private String RUIYI;
    //登录接口
    public String LOGIN;
    //获取考试列表
    public String GETEXAMLISTAPP;
    //获取考试题目列表
    public String GETTEACHERTASKLIST;
    //判断是否可以进行帮阅
    public String OTHERTASK;
    //获取阅卷数据
    public String GETMARKDATA;
    //获取下一个学生的数据
    public String MARKNEXTSTUDENT;
    //未批数据提交接口
    public String SAVEMARKDATA;
    //回评数据提交接口
    public String UPDATEMARKDATA;
    //回评显示获取数据接口
    public String REVIEWSTUDENTS;
    //获取一个已批阅的数据
    public String GETSTUDENTMARKDATA;
    //收藏、取消题目
    public String COLLECTQUEXTION;
    //正常、异常试卷
    public String ABNORMALQUESTION;
    //获取评分详情
    public String GETMARKAVGSCORE;
    //获取最新版本接口
    public String AUTHENTICATE;
    //最新版本下载接口
    public String DOWNLOAD;
    //图片旋转接口
    public String ROTOTEANSWERIMG;


    //我的任务列表接口
    public String TASKS;
    //任务具体信息接口
    public String TASKINFO;
    //是否已领取任务接口
    public String EXISTTASK;
    //未领取题目列表接口
    public String UNCLAIMEDTASK;
    //领取任务接口
    public String CLAIMTASK;
    //回评下一个学生的数据
    public String SHOWSTUDENTMART;
    //获取所有收藏的数据
    public String STARLIST;
    //意见反馈接口
    public String SAVEOPINION;

    private Interfaces(Context context) {
        SERVICEPATH = PreferencesService.getInstance(context).getServicePath();
        RUIYI = SERVICEPATH + "/RayeeMark/api/markset/";
        LOGIN = SERVICEPATH + "/login/home/logincheck";
        //LOGIN = SERVICEPATH + "/base/home/logincheck";
        GETEXAMLISTAPP = RUIYI + "getExamListApp";
        GETTEACHERTASKLIST = RUIYI + "getTeacherTaskList";
        OTHERTASK = RUIYI + "OtherTask";
        GETMARKDATA = RUIYI + "getMarkData";
        MARKNEXTSTUDENT = RUIYI + "MarkNextStudent";
        SAVEMARKDATA = RUIYI + "SaveMarkDataApp";
        UPDATEMARKDATA = RUIYI + "UpDateMarkDataApp";
        REVIEWSTUDENTS = RUIYI + "ReviewStudents";
        GETSTUDENTMARKDATA = RUIYI + "GetStudentMarkData";
        COLLECTQUEXTION = RUIYI + "CollectQuestion";
        ABNORMALQUESTION = RUIYI + "AbnormalQuestion";
        GETMARKAVGSCORE = RUIYI + "GetMarkAvgScore";
        AUTHENTICATE = RUIYI + "Authenticate";
        DOWNLOAD = RUIYI + "Download";
        ROTOTEANSWERIMG = SERVICEPATH + "/YunExam/ExamAPIExpose/RotateAnswerImg";


        TASKS = RUIYI + "Tasks";
        TASKINFO = RUIYI + "TaskInfo";
        EXISTTASK = RUIYI + "ExistTask";
        UNCLAIMEDTASK = RUIYI + "UnClaimedTask";
        CLAIMTASK = RUIYI + "ClaimTask";
        SHOWSTUDENTMART = RUIYI + "ShowStudentMark";
        STARLIST = RUIYI + "StarList";
        SAVEOPINION = RUIYI + "SaveOpinion";
    }
}
