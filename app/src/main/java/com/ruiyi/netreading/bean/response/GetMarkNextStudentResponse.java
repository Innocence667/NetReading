package com.ruiyi.netreading.bean.response;

import java.util.List;

public class GetMarkNextStudentResponse {

    /*{ //已批阅数据
        "success": 200,
            "data": {
        "studentData": {
            "taskGuid": "23360352027a229d27813e3fda93adf6",
                    "studentGuid": "4b5cbe599e9b4bcfa77228c912d2d2f6",
                    "collect": false,
                    "imagePath": "[{\"PageName\":\"A\",\"Path\":\"D:/Scan/20200512144334/01/raw/1206104217750/15_20191206104239\",\"Region\":null}]",
                    "isAbnormal": false,
                    "testCode": "10014",
                    "questions": [
            {
                "id": 213470,
                    "number": 16,
                    "subNumber": "0",
                    "fullScore": 20,
                    "stepScore": "[]",
                    "score": 12
            }
			]
        },
        "imageHeight": 440,
                "imageWidth": 1414,
                "imageArr": [
        {
            "src": "data:image/jpeg;base64",
                "index": 0
        }
		]
    }
    }*/

    /*{ //未批阅数据
        "success": 200,
            "data": {
        "studentData": {
            "taskGuid": "c9be7db297b6a0c4bf6c46cc748ea7f6",
                    "studentGuid": "8744508d90dc425ebd292cc5e84107bf",
                    "testCode": "10000",
                    "collect": false,
                    "imagePath": "[{\"PageName\":\"A\",\"Path\":\"D:/Scan/20200512144334/01/raw/1206104217750/28_20191206104254\",\"Region\":null}]",
                    "questions": [
            {
                "id": 212916,
                    "number": 16,
                    "subNumber": "0",
                    "score": -1,
                    "fullScore": 20
            }
			]
        },
        "imageHeight": 910,
                "imageWidth": 1426,
                "imageArr": [
        {
            "src": "data:image/jpeg;base64",
                "index": 0
        }
		]
    }
    }*/

    /**
     * success : 200
     * data : {"studentData":{"taskGuid":"e7d9aa5873b42794ff4ffdcd344b9583","studentGuid":"882e435092d34a3c8e73a8bf24196e22","testCode":"10000","collect":false,"imagePath":"[{\"PageName\":\"A\",\"Path\":\"D:/Scan/20200508092814/41/raw/1206174759856/3_20191206174807\",\"Region\":null}]","questions":[{"id":192992,"number":31,"subNumber":"0","score":-1,"fullScore":0}]},"imageHeight":566,"imageWidth":1361,"imageArr":[{"src":"data:image/jpeg;base64","index":0}]}
     */

    private int success;
    private DataBean data;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * studentData : {"taskGuid":"e7d9aa5873b42794ff4ffdcd344b9583","studentGuid":"882e435092d34a3c8e73a8bf24196e22","testCode":"10000","collect":false,"imagePath":"[{\"PageName\":\"A\",\"Path\":\"D:/Scan/20200508092814/41/raw/1206174759856/3_20191206174807\",\"Region\":null}]","questions":[{"id":192992,"number":31,"subNumber":"0","score":-1,"fullScore":0}]}
         * imageHeight : 566
         * imageWidth : 1361
         * imageArr : [{"src":"data:image/jpeg;base64","index":0}]
         */

        private StudentDataBean studentData;
        private int imageHeight;
        private int imageWidth;
        private List<ImageArrBean> imageArr;

        public StudentDataBean getStudentData() {
            return studentData;
        }

        public void setStudentData(StudentDataBean studentData) {
            this.studentData = studentData;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public void setImageHeight(int imageHeight) {
            this.imageHeight = imageHeight;
        }

        public int getImageWidth() {
            return imageWidth;
        }

        public void setImageWidth(int imageWidth) {
            this.imageWidth = imageWidth;
        }

        public List<ImageArrBean> getImageArr() {
            return imageArr;
        }

        public void setImageArr(List<ImageArrBean> imageArr) {
            this.imageArr = imageArr;
        }

        public static class StudentDataBean {
            /**
             * taskGuid : e7d9aa5873b42794ff4ffdcd344b9583
             * studentGuid : 882e435092d34a3c8e73a8bf24196e22
             * testCode : 10000
             * collect : false
             * imagePath : [{"PageName":"A","Path":"D:/Scan/20200508092814/41/raw/1206174759856/3_20191206174807","Region":null}]
             * questions : [{"id":192992,"number":31,"subNumber":"0","score":-1,"fullScore":0}]
             */

            private String taskGuid;
            private String studentGuid;
            private String testCode;
            private boolean collect;
            private String imagePath;
            private List<QuestionsBean> questions;
            private boolean isAbnormal;

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

            public String getTestCode() {
                return testCode;
            }

            public void setTestCode(String testCode) {
                this.testCode = testCode;
            }

            public boolean isCollect() {
                return collect;
            }

            public void setCollect(boolean collect) {
                this.collect = collect;
            }

            public String getImagePath() {
                return imagePath;
            }

            public void setImagePath(String imagePath) {
                this.imagePath = imagePath;
            }


            public List<QuestionsBean> getQuestions() {
                return questions;
            }

            public void setQuestions(List<QuestionsBean> questions) {
                this.questions = questions;
            }

            public boolean isAbnormal() {
                return isAbnormal;
            }

            public void setAbnormal(boolean abnormal) {
                isAbnormal = abnormal;
            }

            public static class QuestionsBean {
                /**
                 * id : 192992
                 * number : 31
                 * subNumber : 0
                 * score : -1
                 * fullScore : 0
                 * stepScore : null
                 * coordinate : null
                 */

                private int id;
                private int number; //题号
                private String subNumber; //小题号
                private double score; //题目得分
                private double fullScore; //题目总分
                private String stepScore; //步骤分
                private String coordinate; //笔迹

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
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

                public double getScore() {
                    return score;
                }

                public void setScore(double score) {
                    this.score = score;
                }

                public double getFullScore() {
                    return fullScore;
                }

                public void setFullScore(double fullScore) {
                    this.fullScore = fullScore;
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

        public static class ImageArrBean implements Comparable<ImageArrBean> {
            /**
             * src : data:image/jpeg;base64
             * index : 0
             * width : 1753
             * heigth : 1324
             * pageName : A
             */

            private String src;
            private int index; //图片位置
            private int width; //图片宽
            private int height; //图片高
            private String pageName; //图片A、B面

            public String getSrc() {
                return src;
            }

            public int getIndex() {
                return index;
            }

            public int getWidth() {
                return width;
            }

            public int getHeigth() {
                return height;
            }

            public String getPageName() {
                return pageName;
            }

            public void setSrc(String src) {
                this.src = src;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public void setHeigth(int heigth) {
                this.height = heigth;
            }

            public void setPageName(String pageName) {
                this.pageName = pageName;
            }

            @Override
            public int compareTo(ImageArrBean o) {
                return this.getIndex() - o.getIndex();
            }
        }
    }
}
