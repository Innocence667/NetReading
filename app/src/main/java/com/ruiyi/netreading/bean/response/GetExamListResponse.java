package com.ruiyi.netreading.bean.response;

import java.util.List;

public class GetExamListResponse {

    /**
     * success : 200
     * examList : [{"examGuid":"af42c958c0644d6ebae61addaa658439","examName":"史博测试20200508","schoolGuid":"30E0E3D1341049D39AD513E854836AC1","gradeGuid":"ec7daf51ab44432694beb09f8769a8dc","paperGuid":"891dea52601340c4960bd974b5aabbf5","paperName":"语文","courseId":"01","status":2,"number":2,"examTime":"2020-05-08T00:00:00"},{"examGuid":"a5f26b6df6034145aebb71be39da0b09","examName":"5.6测试上午","schoolGuid":"30E0E3D1341049D39AD513E854836AC1","gradeGuid":"ec7daf51ab44432694beb09f8769a8dc","paperGuid":"d73643cad10240709eaf834334f46697","paperName":"语文","courseId":"01","status":2,"number":2,"examTime":"2020-05-06T00:00:00"},{"examGuid":"38db7bec38954e4cbd6184eeec8435f5","examName":"4.27-02","schoolGuid":"30E0E3D1341049D39AD513E854836AC1","gradeGuid":"ec7daf51ab44432694beb09f8769a8dc","paperGuid":"9beeacae80dc46b0911c18c9e2616a06","paperName":"语文","courseId":"01","status":1,"number":2,"examTime":"2020-04-27T00:00:00"},{"examGuid":"622acf89dc714cf09a4da44b2f8d9359","examName":"4.26周日","schoolGuid":"30E0E3D1341049D39AD513E854836AC1","gradeGuid":"ec7daf51ab44432694beb09f8769a8dc","paperGuid":"d5e75d7f4e8b4aa89ccb916b2baf417a","paperName":"语文","courseId":"01","status":2,"number":2,"examTime":"2020-04-26T00:00:00"}]
     */

    private int success;
    private List<ExamListBean> examList;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public List<ExamListBean> getExamList() {
        return examList;
    }

    public void setExamList(List<ExamListBean> examList) {
        this.examList = examList;
    }

    public static class ExamListBean {
        /**
         * typeName : 月考
         * examGuid : af42c958c0644d6ebae61addaa658439
         * examName : 史博测试20200508
         * schoolGuid : 30E0E3D1341049D39AD513E854836AC1
         * gradeGuid : ec7daf51ab44432694beb09f8769a8dc
         * paperGuid : 891dea52601340c4960bd974b5aabbf5
         * paperName : 语文
         * courseId : 01
         * status : 2
         * number : 2
         * isClosed : false
         * examTime : 2020-05-08T00:00:00
         */

        private String typeName;
        private String examGuid; //考试guid
        private String examName; //考试名称
        private String schoolGuid; //学校guid
        private String gradeGuid; //年级guid
        private String paperGuid; //试卷guid
        private String paperName; //试卷名称
        private String courseId;
        private int status; //(2可以阅卷、3统计完成)
        private int number; //答题卡的数量
        private boolean isClosed; //是否关闭考试
        private String examTime;

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getExamGuid() {
            return examGuid;
        }

        public void setExamGuid(String examGuid) {
            this.examGuid = examGuid;
        }

        public String getExamName() {
            return examName;
        }

        public void setExamName(String examName) {
            this.examName = examName;
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

        public String getPaperGuid() {
            return paperGuid;
        }

        public void setPaperGuid(String paperGuid) {
            this.paperGuid = paperGuid;
        }

        public String getPaperName() {
            return paperName;
        }

        public void setPaperName(String paperName) {
            this.paperName = paperName;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public boolean isClosed() {
            return isClosed;
        }

        public void setClosed(boolean closed) {
            isClosed = closed;
        }

        public String getExamTime() {
            return examTime;
        }

        public void setExamTime(String examTime) {
            this.examTime = examTime;
        }
    }
}
