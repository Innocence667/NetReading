package com.ruiyi.netreading.bean.response;

import java.util.List;

public class GetExamContextResponse {

    /**
     * success : 200
     * taskList : [{"taskGuid":"8c627ccbb2063130d7810656c5cdf5dd","taskName":"主观题1","taskCount":30,"markNumber":1,"order":1,"questions":[{"taskGuid":"8c627ccbb2063130d7810656c5cdf5dd","number":15,"subNumber":"0"}],"avgList":[{"number":15,"subNumber":"0","sumScore":10,"mySumScore":10,"count":1}],"teacherData":[{"teacherCount":0,"teacherNumber":1}],"canMark":true}]
     */

    private int success;
    private List<TaskListBean> taskList;

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
         */

        private String taskGuid;
        private String taskName;
        private int taskCount; //所有教师的任务总量
        private int markNumber; //所有教师的已阅量
        private int order;
        private boolean canMark;
        private List<QuestionsBean> questions;
        private List<AvgListBean> avgList;
        private List<TeacherDataBean> teacherData;

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

        public List<TeacherDataBean> getTeacherData() {
            return teacherData;
        }

        public void setTeacherData(List<TeacherDataBean> teacherData) {
            this.teacherData = teacherData;
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
