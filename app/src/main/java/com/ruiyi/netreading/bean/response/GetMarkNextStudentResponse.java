package com.ruiyi.netreading.bean.response;

import java.util.List;

public class GetMarkNextStudentResponse {
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

    public static class DataBean {
        /**
         * studentData : {"taskGuid":"e7d9aa5873b42794ff4ffdcd344b9583","studentGuid":"882e435092d34a3c8e73a8bf24196e22","testCode":"10000","collect":false,"imagePath":"[{\"PageName\":\"A\",\"Path\":\"D:/Scan/20200508092814/41/raw/1206174759856/3_20191206174807\",\"Region\":null}]","questions":[{"id":192992,"number":31,"subNumber":"0","score":-1,"fullScore":0}]}
         * isOnline : 0
         * imageHeight : 566
         * imageWidth : 1361
         * imageArr : [{"src":"data:image/jpeg;base64","index":0}]
         */

        private String isOnline; //0线下、1线上、2导入
        private StudentDataBean studentData;
        private int imageHeight;
        private int imageWidth;
        private List<ImageArrBean> imageArr;

        public String getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(String isOnline) {
            this.isOnline = isOnline;
        }

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
            private boolean collect; //是否收藏
            private String imagePath;
            private List<QuestionsBean> questions;
            private boolean isAbnormal; //是否是异常

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

            //获取新数据和回评数据共用一个模型，所以会出现获取新数据接口中有的参数和模型中的对应不上
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
                //gradeData测试数据{"gradeMode":3,"stepModeAdd":true,"stepLength":"+1.5"}
                //gradeMode 1：总分模式 2：键盘打分 3：步骤分模式
                private String gradeData; //只考虑gradeMode为3的情况，其他值忽略，stepModeAdd：true加分模式，false减法模式
                private boolean hasArbitrated; //是否仲裁过(默认false)仲裁过的题目,一二评教师回评无法修改
                private double firstScore; //一评教师分数
                private double secondScore; //二评教师分数

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

                public String getGradeData() {
                    return gradeData;
                }

                public boolean isHasArbitrated() {
                    return hasArbitrated;
                }

                public void setGradeData(String gradeData) {
                    this.gradeData = gradeData;
                }

                public void setHasArbitrated(boolean hasArbitrated) {
                    this.hasArbitrated = hasArbitrated;
                }

                public double getFirstScore() {
                    return firstScore;
                }

                public double getSecondScore() {
                    return secondScore;
                }

                public void setFirstScore(double firstScore) {
                    this.firstScore = firstScore;
                }

                public void setSecondScore(double secondScore) {
                    this.secondScore = secondScore;
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
