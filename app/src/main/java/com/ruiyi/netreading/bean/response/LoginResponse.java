package com.ruiyi.netreading.bean.response;

import java.util.List;

//登录成功返回数据
public class LoginResponse {

    /**
     * code : 200
     * message : SUCCESS
     * data : {"token":"f7e546f7efcccfaa4171906c5ea996cdc8340be982347db65cabf58048ec0e69824c8105528d12533d02d65dde13c1390465f6b0801530af","sguid":"30E0E3D1341049D39AD513E854836AC1","theme":"0","usertype":"2","uguid":"15436ef7e9d14140b1ac613938970865","realname":"语文教师1","userhead":"","teacher":"True","termid":"e4b6c256ed0449fa879ae9a8e488104a","termstart":"2020-02-03","termend":"2020-08-30","updpwd":"true","userid":"15436ef7e9d14140b1ac613938970865","username":"yuwen001","schoolid":"30E0E3D1341049D39AD513E854836AC1","status":"0","type":"2","gourl":"/base/teacher/index?t=f7e546f7efcccfaa4171906c5ea996cdc8340be982347db65cabf58048ec0e69824c8105528d12533d02d65dde13c1390465f6b0801530af","cookietoken":"Yk1b75VXui2dA5BuWd38YQ==","apitoken":"u32t10k15436ef7e9d14140b1ac61393897086515888795546d3f9ab038c088f91d4c98f589969ef7","subjectid":null,"cookie":{"ri_guid":"15436ef7e9d14140b1ac613938970865","ri_type":"2","ri_token":"Yk1b75VXui2dA5BuWd38YQ==","ri_timestamp":1588875954,"ri_return":1},"menus":[{"name":"首页","order":1,"guid":"62350e7b464b4b709b5f2ae4f0b7dc9f","page":[{"guid":"51b31fdb72104390a9a8881338d56afe","choose":true,"name":"我的班级","order":5,"url":"https://riyun.lexuewang.cn:8002/Base/teacher/myclass?t={token}","action":[{"choose":true,"name":"list/列表"}]},{"guid":"1f878d597c8c4e6d8d41ebfb7273c036","choose":true,"name":"学生组","order":20,"url":"https://riyun.lexuewang.cn:8002/QuesWork/web/index.html#StudentGroup/index","action":[{"choose":true,"name":"list/列表"}]},{"guid":"1e3814c9f6194b38bbc03a7abe95a8a4","choose":true,"name":"家校通","order":65,"url":"https://riyun.lexuewang.cn:8002/Base/teacher/family?t={token}","action":[{"choose":true,"name":"aotusendbtn/自动发送设置"},{"choose":true,"name":"range/日期范围"},{"choose":true,"name":"read/阅读反馈"},{"choose":true,"name":"deletebtn/删除按钮"},{"choose":true,"name":"send/发送信息"},{"choose":true,"name":"createbtn/创建问卷"},{"choose":true,"name":"jwinfo/问卷详情"},{"choose":true,"name":"term/学期"},{"choose":true,"name":"list/列表"}]}]}],"classeArr":null,"topic":["15436ef7e9d14140b1ac613938970865"]}
     */

    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * token : f7e546f7efcccfaa4171906c5ea996cdc8340be982347db65cabf58048ec0e69824c8105528d12533d02d65dde13c1390465f6b0801530af
         * sguid : 30E0E3D1341049D39AD513E854836AC1
         * sname : 四川双流中学
         * theme : 0
         * usertype : 2
         * uguid : 15436ef7e9d14140b1ac613938970865
         * realname : 语文教师1
         * userhead :
         * teacher : True
         * termid : e4b6c256ed0449fa879ae9a8e488104a
         * termstart : 2020-02-03
         * termend : 2020-08-30
         * updpwd : true
         * userid : 15436ef7e9d14140b1ac613938970865
         * username : yuwen001
         * schoolid : 30E0E3D1341049D39AD513E854836AC1
         * status : 0
         * type : 2
         * gourl : /base/teacher/index?t=f7e546f7efcccfaa4171906c5ea996cdc8340be982347db65cabf58048ec0e69824c8105528d12533d02d65dde13c1390465f6b0801530af
         * cookietoken : Yk1b75VXui2dA5BuWd38YQ==
         * apitoken : u32t10k15436ef7e9d14140b1ac61393897086515888795546d3f9ab038c088f91d4c98f589969ef7
         * subjectid : null
         * cookie : {"ri_guid":"15436ef7e9d14140b1ac613938970865","ri_type":"2","ri_token":"Yk1b75VXui2dA5BuWd38YQ==","ri_timestamp":1588875954,"ri_return":1}
         * menus : [{"name":"首页","order":1,"guid":"62350e7b464b4b709b5f2ae4f0b7dc9f","page":[{"guid":"51b31fdb72104390a9a8881338d56afe","choose":true,"name":"我的班级","order":5,"url":"https://riyun.lexuewang.cn:8002/Base/teacher/myclass?t={token}","action":[{"choose":true,"name":"list/列表"}]},{"guid":"1f878d597c8c4e6d8d41ebfb7273c036","choose":true,"name":"学生组","order":20,"url":"https://riyun.lexuewang.cn:8002/QuesWork/web/index.html#StudentGroup/index","action":[{"choose":true,"name":"list/列表"}]},{"guid":"1e3814c9f6194b38bbc03a7abe95a8a4","choose":true,"name":"家校通","order":65,"url":"https://riyun.lexuewang.cn:8002/Base/teacher/family?t={token}","action":[{"choose":true,"name":"aotusendbtn/自动发送设置"},{"choose":true,"name":"range/日期范围"},{"choose":true,"name":"read/阅读反馈"},{"choose":true,"name":"deletebtn/删除按钮"},{"choose":true,"name":"send/发送信息"},{"choose":true,"name":"createbtn/创建问卷"},{"choose":true,"name":"jwinfo/问卷详情"},{"choose":true,"name":"term/学期"},{"choose":true,"name":"list/列表"}]}]}]
         * classeArr : null
         * topic : ["15436ef7e9d14140b1ac613938970865"]
         */

        private String token;
        private String sguid;
        private String sname;
        private String theme;
        private String usertype;
        private String uguid;
        private String realname;
        private String headimg;
        private String teacher;
        private String termid;
        private String termstart;
        private String termend;
        private String updpwd;
        private String userid;
        private String username;
        private String schoolid;
        private String status;
        private String type;
        private String gourl;
        private String cookietoken;
        private String apitoken;
        private Object subjectid;
        private CookieBean cookie;
        private Object classeArr;
        private List<MenusBean> menus;
        private List<String> topic;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getSguid() {
            return sguid;
        }

        public void setSguid(String sguid) {
            this.sguid = sguid;
        }

        public String getSname() {
            return sname;
        }

        public void setSname(String sname) {
            this.sname = sname;
        }

        public String getTheme() {
            return theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }

        public String getUsertype() {
            return usertype;
        }

        public void setUsertype(String usertype) {
            this.usertype = usertype;
        }

        public String getUguid() {
            return uguid;
        }

        public void setUguid(String uguid) {
            this.uguid = uguid;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }

        public String getHeadimg() {
            return headimg;
        }

        public void setHeadimg(String headimg) {
            this.headimg = headimg;
        }

        public String getTeacher() {
            return teacher;
        }

        public void setTeacher(String teacher) {
            this.teacher = teacher;
        }

        public String getTermid() {
            return termid;
        }

        public void setTermid(String termid) {
            this.termid = termid;
        }

        public String getTermstart() {
            return termstart;
        }

        public void setTermstart(String termstart) {
            this.termstart = termstart;
        }

        public String getTermend() {
            return termend;
        }

        public void setTermend(String termend) {
            this.termend = termend;
        }

        public String getUpdpwd() {
            return updpwd;
        }

        public void setUpdpwd(String updpwd) {
            this.updpwd = updpwd;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSchoolid() {
            return schoolid;
        }

        public void setSchoolid(String schoolid) {
            this.schoolid = schoolid;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getGourl() {
            return gourl;
        }

        public void setGourl(String gourl) {
            this.gourl = gourl;
        }

        public String getCookietoken() {
            return cookietoken;
        }

        public void setCookietoken(String cookietoken) {
            this.cookietoken = cookietoken;
        }

        public String getApitoken() {
            return apitoken;
        }

        public void setApitoken(String apitoken) {
            this.apitoken = apitoken;
        }

        public Object getSubjectid() {
            return subjectid;
        }

        public void setSubjectid(Object subjectid) {
            this.subjectid = subjectid;
        }

        public CookieBean getCookie() {
            return cookie;
        }

        public void setCookie(CookieBean cookie) {
            this.cookie = cookie;
        }

        public Object getClasseArr() {
            return classeArr;
        }

        public void setClasseArr(Object classeArr) {
            this.classeArr = classeArr;
        }

        public List<MenusBean> getMenus() {
            return menus;
        }

        public void setMenus(List<MenusBean> menus) {
            this.menus = menus;
        }

        public List<String> getTopic() {
            return topic;
        }

        public void setTopic(List<String> topic) {
            this.topic = topic;
        }

        public static class CookieBean {
            /**
             * ri_guid : 15436ef7e9d14140b1ac613938970865
             * ri_type : 2
             * ri_token : Yk1b75VXui2dA5BuWd38YQ==
             * ri_timestamp : 1588875954
             * ri_return : 1
             */

            private String ri_guid;
            private String ri_type;
            private String ri_token;
            private int ri_timestamp;
            private int ri_return;

            public String getRi_guid() {
                return ri_guid;
            }

            public void setRi_guid(String ri_guid) {
                this.ri_guid = ri_guid;
            }

            public String getRi_type() {
                return ri_type;
            }

            public void setRi_type(String ri_type) {
                this.ri_type = ri_type;
            }

            public String getRi_token() {
                return ri_token;
            }

            public void setRi_token(String ri_token) {
                this.ri_token = ri_token;
            }

            public int getRi_timestamp() {
                return ri_timestamp;
            }

            public void setRi_timestamp(int ri_timestamp) {
                this.ri_timestamp = ri_timestamp;
            }

            public int getRi_return() {
                return ri_return;
            }

            public void setRi_return(int ri_return) {
                this.ri_return = ri_return;
            }
        }

        public static class MenusBean {
            /**
             * name : 首页
             * order : 1
             * guid : 62350e7b464b4b709b5f2ae4f0b7dc9f
             * page : [{"guid":"51b31fdb72104390a9a8881338d56afe","choose":true,"name":"我的班级","order":5,"url":"https://riyun.lexuewang.cn:8002/Base/teacher/myclass?t={token}","action":[{"choose":true,"name":"list/列表"}]},{"guid":"1f878d597c8c4e6d8d41ebfb7273c036","choose":true,"name":"学生组","order":20,"url":"https://riyun.lexuewang.cn:8002/QuesWork/web/index.html#StudentGroup/index","action":[{"choose":true,"name":"list/列表"}]},{"guid":"1e3814c9f6194b38bbc03a7abe95a8a4","choose":true,"name":"家校通","order":65,"url":"https://riyun.lexuewang.cn:8002/Base/teacher/family?t={token}","action":[{"choose":true,"name":"aotusendbtn/自动发送设置"},{"choose":true,"name":"range/日期范围"},{"choose":true,"name":"read/阅读反馈"},{"choose":true,"name":"deletebtn/删除按钮"},{"choose":true,"name":"send/发送信息"},{"choose":true,"name":"createbtn/创建问卷"},{"choose":true,"name":"jwinfo/问卷详情"},{"choose":true,"name":"term/学期"},{"choose":true,"name":"list/列表"}]}]
             */

            private String name;
            private int order;
            private String guid;
            private List<PageBean> page;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getOrder() {
                return order;
            }

            public void setOrder(int order) {
                this.order = order;
            }

            public String getGuid() {
                return guid;
            }

            public void setGuid(String guid) {
                this.guid = guid;
            }

            public List<PageBean> getPage() {
                return page;
            }

            public void setPage(List<PageBean> page) {
                this.page = page;
            }

            public static class PageBean {
                /**
                 * guid : 51b31fdb72104390a9a8881338d56afe
                 * choose : true
                 * name : 我的班级
                 * order : 5
                 * url : https://riyun.lexuewang.cn:8002/Base/teacher/myclass?t={token}
                 * action : [{"choose":true,"name":"list/列表"}]
                 */

                private String guid;
                private boolean choose;
                private String name;
                private int order;
                private String url;
                private List<ActionBean> action;

                public String getGuid() {
                    return guid;
                }

                public void setGuid(String guid) {
                    this.guid = guid;
                }

                public boolean isChoose() {
                    return choose;
                }

                public void setChoose(boolean choose) {
                    this.choose = choose;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getOrder() {
                    return order;
                }

                public void setOrder(int order) {
                    this.order = order;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public List<ActionBean> getAction() {
                    return action;
                }

                public void setAction(List<ActionBean> action) {
                    this.action = action;
                }

                public static class ActionBean {
                    /**
                     * choose : true
                     * name : list/列表
                     */

                    private boolean choose;
                    private String name;

                    public boolean isChoose() {
                        return choose;
                    }

                    public void setChoose(boolean choose) {
                        this.choose = choose;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }
                }
            }
        }
    }
}
