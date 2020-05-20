package com.ruiyi.netreading.bean;

import java.util.List;

//新数据提交模型
public class SaveMarkDataBean {

    /**
     * teacherGuid : 1234
     * taskGuid : 123
     * studentGuid : 1234
     * questions : [{"id":"","markScore":"","stepScore":""}]
     */

    private String teacherGuid;
    private String taskGuid;
    private String studentGuid;
    private List<QuestionsBean> questions;

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

    public List<QuestionsBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionsBean> questions) {
        this.questions = questions;
    }

    public static class QuestionsBean {
        /**
         * id :
         * markScore :
         * stepScore :
         * coordinate :
         */

        private String id; //题目id
        private String markScore; //题目得分
        private String stepScore; //题目步骤分
        private String coordinate; //笔迹数据

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMarkScore() {
            return markScore;
        }

        public void setMarkScore(String markScore) {
            this.markScore = markScore;
        }

        public String getStepScore() {
            return stepScore;
        }

        public void setStepScore(String stepScore) {
            this.stepScore = stepScore;
        }

        public String getCoordinate() {
            return coordinate;
        }

        public void setCoordinate(String coordinate) {
            this.coordinate = coordinate;
        }
    }
}
