package com.ruiyi.netreading.bean.response;

import java.util.List;

//获取试卷数据
public class GetMarkDataResponse {

    /**
     * success : 200
     * testpaper : {"examName":"5.6测试上午","paperName":"语文","courseName":"语文","courseId":"01"}
     * questions : [{"taskGuid":"1ae525a15dabd1597811854db068de48","number":6,"subNumber":"0","score":6}]
     * teacherTask : {"taskGuid":"1ae525a15dabd1597811854db068de48","paperGuid":"d73643cad10240709eaf834334f46697","markNumber":72,"markCount":72,"teacherName":"语文教师1","taskCount":463,"markSum":463,"isFree":false}
     */

    private int success;
    private TestpaperBean testpaper;
    private TeacherTaskBean teacherTask;
    private List<QuestionsBean> questions;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public TestpaperBean getTestpaper() {
        return testpaper;
    }

    public void setTestpaper(TestpaperBean testpaper) {
        this.testpaper = testpaper;
    }

    public TeacherTaskBean getTeacherTask() {
        return teacherTask;
    }

    public void setTeacherTask(TeacherTaskBean teacherTask) {
        this.teacherTask = teacherTask;
    }

    public List<QuestionsBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionsBean> questions) {
        this.questions = questions;
    }

    public static class TestpaperBean {
        /**
         * examName : 5.6测试上午
         * paperName : 语文
         * courseName : 语文
         * courseId : 01
         */

        private String examName;
        private String paperName;
        private String courseName;
        private String courseId; //学科id

        public String getExamName() {
            return examName;
        }

        public void setExamName(String examName) {
            this.examName = examName;
        }

        public String getPaperName() {
            return paperName;
        }

        public void setPaperName(String paperName) {
            this.paperName = paperName;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }
    }

    public static class TeacherTaskBean {
        /**
         * taskGuid : 1ae525a15dabd1597811854db068de48
         * paperGuid : d73643cad10240709eaf834334f46697
         * markNumber : 72
         * markCount : 72
         * teacherName : 语文教师1
         * taskCount : 463
         * markSum : 463
         * isFree : false
         */

        private String taskGuid;
        private String paperGuid;
        private int markNumber; //登录教师已阅量
        private int markCount; //登录教师任务总量
        private String teacherName;
        private int taskCount; ///所有教师任务总量
        private int markSum; //所有教师已阅量
        private boolean isFree; //是否是自由阅卷

        public String getTaskGuid() {
            return taskGuid;
        }

        public void setTaskGuid(String taskGuid) {
            this.taskGuid = taskGuid;
        }

        public String getPaperGuid() {
            return paperGuid;
        }

        public void setPaperGuid(String paperGuid) {
            this.paperGuid = paperGuid;
        }

        public int getMarkNumber() {
            return markNumber;
        }

        public void setMarkNumber(int markNumber) {
            this.markNumber = markNumber;
        }

        public int getMarkCount() {
            return markCount;
        }

        public void setMarkCount(int markCount) {
            this.markCount = markCount;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public int getTaskCount() {
            return taskCount;
        }

        public void setTaskCount(int taskCount) {
            this.taskCount = taskCount;
        }

        public int getMarkSum() {
            return markSum;
        }

        public void setMarkSum(int markSum) {
            this.markSum = markSum;
        }

        public boolean isIsFree() {
            return isFree;
        }

        public void setIsFree(boolean isFree) {
            this.isFree = isFree;
        }
    }

    public static class QuestionsBean {
        /**
         * taskGuid : 1ae525a15dabd1597811854db068de48
         * number : 6
         * subNumber : 0
         * score : 6
         */

        private String taskGuid;
        private int number;
        private String subNumber;
        private int score;

        public String getTaskGuid() {
            return taskGuid;
        }

        public void setTaskGuid(String taskGuid) {
            this.taskGuid = taskGuid;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getSubNumber() {
            return subNumber;
        }

        public void setSubNumber(String subNumber) {
            this.subNumber = subNumber;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
