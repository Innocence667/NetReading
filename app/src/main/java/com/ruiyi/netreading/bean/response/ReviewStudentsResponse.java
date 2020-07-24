package com.ruiyi.netreading.bean.response;

import java.util.List;

public class ReviewStudentsResponse {

    /**
     * success : 200
     * data : [{"studentGuid":"4b5cbe599e9b4bcfa77228c912d2d2f6","testCode":"10014","isAbnormal":false,"collect":false,"time":"2020-05-13T12:20:33.239897","score":12},{"studentGuid":"f366d82153c5438da9ea639c952d861c","testCode":"10017","isAbnormal":false,"collect":false,"time":"2020-05-13T12:20:36.262486","score":9}]
     */

    private int success;
    private List<DataBean> data;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Comparable<DataBean> {
        /**
         * studentGuid : 4b5cbe599e9b4bcfa77228c912d2d2f6
         * testCode : 10014
         * isAbnormal : false
         * collect : false
         * time : 2020-05-13T12:20:33.239897
         * score : 12.0
         */

        private String studentGuid;
        private String testCode; //试题id
        private boolean isAbnormal;
        private boolean collect; //收藏
        private String time; //时间
        private double score; //分数

        public String getStudentGuid() {
            return studentGuid;
        }

        public void setStudentGuid(String studentGuid) {
            this.studentGuid = studentGuid;
        }

        public String getTestCode() {
            return testCode;
        }

        public void setTestCode(String testCode) {
            this.testCode = testCode;
        }

        public boolean isIsAbnormal() {
            return isAbnormal;
        }

        public void setIsAbnormal(boolean isAbnormal) {
            this.isAbnormal = isAbnormal;
        }

        public boolean isCollect() {
            return collect;
        }

        public void setCollect(boolean collect) {
            this.collect = collect;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        //负整数、零或正整数对应小于、等于或大于
        @Override
        public int compareTo(DataBean o) {
            if (o.score == this.score) {
                return 0;
            } else if (o.score > this.score) {
                return 1;
            } else if (o.score < this.score) {
                return -1;
            }
            return 0;
        }
    }
}
