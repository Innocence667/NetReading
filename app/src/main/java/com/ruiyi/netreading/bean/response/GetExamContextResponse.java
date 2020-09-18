package com.ruiyi.netreading.bean.response;

import java.util.List;

public class GetExamContextResponse {
    //TODO 线上考试图片使用url，线下使用base64数据

    /**
     * success : 200
     * taskList : [{"taskGuid":"8c627ccbb2063130d7810656c5cdf5dd","taskName":"主观题1","taskCount":30,"markNumber":1,"order":1,"questions":[{"taskGuid":"8c627ccbb2063130d7810656c5cdf5dd","number":15,"subNumber":"0"}],"avgList":[{"number":15,"subNumber":"0","sumScore":10,"mySumScore":10,"count":1}],"teacherData":[{"teacherCount":0,"teacherNumber":1}],"canMark":true}]
     */

    private int success;
    private List<TaskListBean> taskList;
    private List<TaskListBean> taskLists;//存放所有的双评任务，不一定都是这个教师的任务

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public List<TaskListBean> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskListBean> taskList) {
        this.taskList = taskList;
    }

    public List<TaskListBean> getTaskLists() {
        return taskLists;
    }

    public void setTaskLists(List<TaskListBean> taskLists) {
        this.taskLists = taskLists;
    }

    public static class TaskListBean {
        /**
         * taskGuid : 8c627ccbb2063130d7810656c5cdf5dd
         * taskName : 主观题1
         * taskCount : 30
         * markNumber : 1
         * order : 1
         * questions : [{"taskGuid":"8c627ccbb2063130d7810656c5cdf5dd","number":15,"subNumber":"0"}]
         * avgList : [{"number":15,"subNumber":"0","sumScore":10,"mySumScore":10,"count":1}]
         * teacherData : [{"teacherCount":0,"teacherNumber":1}]
         * canMark : true //是否是自己的任务
         * style : 1
         * isonline : false
         * IsPublish ：false
         */

        private String taskGuid;
        private String taskName;
        private int taskCount; //所有教师的任务总量
        private int markNumber; //所有教师的已阅量
        private int order; //顺序
        private int style; //阅卷类型(1单评、2双评、3按班)
        private boolean canMark; //是否可以阅卷
        private int markNum;//双评模式下当前任务所有教师的已阅量，代替markNumber(所有一评教师的已阅量)
        private List<QuestionsBean> questions;
        private List<AvgListBean> avgList;
        private TeacherDataBean teacherData;// 如果是空的，表示是帮阅，如果有数据，表示是自己的任务
        private int isonline; //0线下、1线上、2导入
        private int identity;//身份(0,1、2、教师1-2 3、仲裁)
        private int arbCount; //数量(异常数量-仲裁数量)
        private boolean isPublish; //是否发布,发布后无法进行回评修改操作，按钮已发布

        public String getTaskGuid() {
            return taskGuid;
        }

        public void setTaskGuid(String taskGuid) {
            this.taskGuid = taskGuid;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public int getTaskCount() {
            return taskCount;
        }

        public void setTaskCount(int taskCount) {
            this.taskCount = taskCount;
        }

        public int getMarkNumber() {
            return markNumber;
        }

        public void setMarkNumber(int markNumber) {
            this.markNumber = markNumber;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public int getMarkNum() {
            return markNum;
        }

        public void setMarkNum(int markNum) {
            this.markNum = markNum;
        }

        public int getStyle() {
            return style;
        }

        public void setStyle(int style) {
            this.style = style;
        }

        public boolean isCanMark() {
            return canMark;
        }

        public void setCanMark(boolean canMark) {
            this.canMark = canMark;
        }

        public List<QuestionsBean> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionsBean> questions) {
            this.questions = questions;
        }

        public List<AvgListBean> getAvgList() {
            return avgList;
        }

        public void setAvgList(List<AvgListBean> avgList) {
            this.avgList = avgList;
        }

        public TeacherDataBean getTeacherData() {
            return teacherData;
        }

        public void setTeacherData(TeacherDataBean teacherData) {
            this.teacherData = teacherData;
        }

        public int getIsonline() {
            return isonline;
        }

        public void setIsonline(int isonline) {
            this.isonline = isonline;
        }

        public int getIdentity() {
            return identity;
        }

        public int getArbCount() {
            return arbCount;
        }

        public void setIdentity(int identity) {
            this.identity = identity;
        }

        public void setArbCount(int arbCount) {
            this.arbCount = arbCount;
        }

        public boolean isPublish() {
            return isPublish;
        }

        public void setPublish(boolean publish) {
            isPublish = publish;
        }

        public static class QuestionsBean {
            /**
             * taskGuid : 8c627ccbb2063130d7810656c5cdf5dd
             * number : 15
             * subNumber : 0
             */

            private String taskGuid;
            private int number;
            private String subNumber;

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
        }

        public static class AvgListBean {
            /**
             * number : 15
             * subNumber : 0
             * sumScore : 10
             * mySumScore : 10
             * count : 1
             */

            private int number;
            private String subNumber;
            private double sumScore;
            private double mySumScore;
            private int count;

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

            public double getSumScore() {
                return sumScore;
            }

            public void setSumScore(double sumScore) {
                this.sumScore = sumScore;
            }

            public double getMySumScore() {
                return mySumScore;
            }

            public void setMySumScore(double mySumScore) {
                this.mySumScore = mySumScore;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }
        }

        public static class TeacherDataBean {
            /**
             * teacherCount : 0
             * teacherNumber : 1
             */

            private int teacherCount; //自己任务量(0为自由阅卷)
            private int teacherNumber;//自己任务的已阅量

            public int getTeacherCount() {
                return teacherCount;
            }

            public void setTeacherCount(int teacherCount) {
                this.teacherCount = teacherCount;
            }

            public int getTeacherNumber() {
                return teacherNumber;
            }

            public void setTeacherNumber(int teacherNumber) {
                this.teacherNumber = teacherNumber;
            }
        }
    }
}
