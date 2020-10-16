package com.ruiyi.netreading.controller;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ruiyi.netreading.bean.SaveMarkDataBean;
import com.ruiyi.netreading.bean.UserBean;
import com.ruiyi.netreading.bean.request.CollectRequest;
import com.ruiyi.netreading.bean.request.GetExamContextRequest;
import com.ruiyi.netreading.bean.request.GetExamListRequest;
import com.ruiyi.netreading.bean.request.GetMarkAvgScoreRequest;
import com.ruiyi.netreading.bean.request.GetMarkDataRequest;
import com.ruiyi.netreading.bean.request.GetStudentMarkDataRequest;
import com.ruiyi.netreading.bean.request.ReviewStudentsRequest;
import com.ruiyi.netreading.bean.response.GetExamContextResponse;
import com.ruiyi.netreading.bean.response.GetExamListResponse;
import com.ruiyi.netreading.bean.response.GetMarkAvgScoreResponse;
import com.ruiyi.netreading.bean.response.GetMarkDataResponse;
import com.ruiyi.netreading.bean.response.GetMarkNextStudentResponse;
import com.ruiyi.netreading.bean.response.LoginResponse;
import com.ruiyi.netreading.bean.response.ReviewStudentsResponse;
import com.ruiyi.netreading.bean.response.SavaDataResponse;
import com.ruiyi.netreading.util.HttpUtil;
import com.ruiyi.netreading.util.Interfaces;
import com.ruiyi.netreading.util.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyModel {

    private static Gson gson = new GsonBuilder().serializeNulls().create();

    //测试用户输入地址是否正确
    public void testUrl(Context context, String url, final MyCallBack callBack) {
        Log.e("testUrl", "testUrl: " + url);
        Call call = HttpUtil.getInstance().PostResponse(url, "");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed("testUrl:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200) {
                    Log.e("testUrl", response.code() + "   返回结果：" + json);
                    callBack.onSuccess(response.code());
                } else {
                    callBack.onFailed("testUrl:" + json);
                }
            }
        });
    }

    //登录账号
    public void getUser(Context context, UserBean userBean, final MyCallBack callback) {
        Log.e("getUser", gson.toJson(userBean));
        Log.e("getUser", "getUser: " + Interfaces.getInstance(context).LOGIN);
        Call call = HttpUtil.getInstance().Login(Interfaces.getInstance(context).LOGIN,
                gson.toJson(userBean));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailed("getUser:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    if (response.code() == 200 && (json.contains("\"code\":200,\"message\":\"SUCCESS\"") || json.contains("\"code\":200,\"msg\":\"SUCCESS\""))) {
                        LogUtils.logE("getUser", "登录返回结果" + json);
                        callback.onSuccess(gson.fromJson(json, LoginResponse.class));
                    } else {
                        callback.onFailed("getUser:" + json);
                    }
                } catch (JsonSyntaxException e) {
                    callback.onFailed(e.getMessage());
                }
            }
        });
    }

    //获取考试列表
    public void getTaskList(Context context, GetExamListRequest examListRequest, final MyCallBack callback) {
        Log.e("getTaskList", gson.toJson(examListRequest));
        Call call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).GETEXAMLISTAPP, gson.toJson(examListRequest));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailed("getTaskList:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    if (response.code() == 200 && json.contains("\"success\":200")) {
                        LogUtils.logE("getTaskList", "获取考试列表：" + json);
                        callback.onSuccess(gson.fromJson(json, GetExamListResponse.class));
                    } else {
                        callback.onFailed("getTaskList:" + json);
                    }
                } catch (JsonSyntaxException e) {
                    callback.onFailed(e.getMessage());
                }
            }
        });
    }

    //获取考试题目列表
    public void getTaskContext(Context context, final GetExamContextRequest request, final MyCallBack childCallback) {
        Log.e("getTaskContext", gson.toJson(request));
        Call call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).GETTEACHERTASKLIST, gson.toJson(request));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                childCallback.onFailed("getTaskContext:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    if (response.code() == 200 && json.contains("\"success\":200")) {
                        LogUtils.logE("getTaskContext", "获取考试题目列表：" + json);
                        childCallback.onSuccess(gson.fromJson(json, GetExamContextResponse.class));
                    } else {
                        childCallback.onFailed("getTaskContext:" + json);
                    }
                } catch (JsonSyntaxException e) {
                    childCallback.onFailed(e.getMessage());
                }
            }
        });
    }

    //判断是否可以进行帮阅
    public void OtherTask(Context context, final GetMarkDataRequest request, final MyCallBack callBack) {
        Log.e("OtherTask", "检测是否可以帮阅: " + gson.toJson(request));
        Call call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).OTHERTASK, gson.toJson(request));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed("OtherTask:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200 && json.contains("\"success\":200")) {
                    LogUtils.logE("OtherTask", json);
                    callBack.onSuccess(json);
                } else {
                    callBack.onFailed("OtherTask:" + json);
                }
            }
        });
    }

    //获取阅卷数据
    public void getMarkData(Context context, final GetMarkDataRequest request, final MyCallBack callBack) {
        Log.e("getMarkData", gson.toJson(request));
        Call call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).GETMARKDATA, gson.toJson(request));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed("getMarkData:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    if (response.code() == 200 && json.contains("\"success\":200")) {
                        LogUtils.logE("getMarkData", "获取阅卷数据：" + json);
                        callBack.onSuccess(gson.fromJson(json, GetMarkDataResponse.class));
                    } else {
                        callBack.onFailed("getMarkData:" + json);
                    }
                } catch (JsonSyntaxException e) {
                    callBack.onFailed(e.getMessage());
                }
            }
        });
    }

    //获取下一个未批阅的学生数据
    public void getMarkNextStudent(Context context, final GetMarkDataRequest request, final MyCallBack callBack) {
        Log.e("getMarkNextStudent", gson.toJson(request));
        Call call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).MARKNEXTSTUDENT, gson.toJson(request));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if ("timeout".equals(e.getMessage())) {
                    callBack.onFailed("网络请求超时");
                } else {
                    callBack.onFailed("getMarkNextStudent:" + e.getMessage());
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    if (response.code() == 200 && json.contains("\"success\":200")) {
                        //LogUtils.logE("getMarkNextStudent", "获取新数据结果：" + json);
                        callBack.onSuccess(gson.fromJson(json, GetMarkNextStudentResponse.class));
                    } else {
                        callBack.onFailed("getMarkNextStudent:" + json);
                    }
                } catch (JsonSyntaxException e) {
                    callBack.onFailed(e.getMessage());
                }
            }
        });
    }

    //获取已批阅的学生数据
    public void getStudentMarkData(Context context, final GetStudentMarkDataRequest request, final MyCallBack callBack) {
        Log.e("getStudentMarkData", gson.toJson(request));
        Call call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).GETSTUDENTMARKDATA, gson.toJson(request));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed("getStudentMarkData:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    if (response.code() == 200 && json.contains("\"success\":200")) {
                        //LogUtils.logE("getStudentMarkData", "获取已批阅数据：" + json);
                        callBack.onSuccess(gson.fromJson(json, GetMarkNextStudentResponse.class));
                    } else {
                        callBack.onFailed("getStudentMarkData:" + json);
                    }
                } catch (JsonSyntaxException e) {
                    callBack.onFailed(e.getMessage());
                }
            }
        });
    }

    //调用回评列表接口
    public void reviewStudents(Context context, final ReviewStudentsRequest request, final MyCallBack callBack) {
        Log.e("reviewStudents", gson.toJson(request));
        Call call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).REVIEWSTUDENTS, gson.toJson(request));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed("reviewStudents:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    if (response.code() == 200 && json.contains("\"success\":200")) {
                        LogUtils.logE("reviewStudents", "回评列表接口:" + json);
                        callBack.onSuccess(gson.fromJson(json, ReviewStudentsResponse.class));
                    } else {
                        callBack.onFailed("reviewStudents:" + json);
                    }
                } catch (JsonSyntaxException e) {
                    callBack.onFailed(e.getMessage());
                }
            }
        });
    }

    //新数据提交保存
    public void saveMarkData(Context context, SaveMarkDataBean saveData, final MyCallBack callBack) {
        Log.e("saveMarkData", gson.toJson(saveData));
        Call call = (HttpUtil.getInstance()).PostResponse(Interfaces.getInstance(context).SAVEMARKDATA, new GsonBuilder().serializeNulls().create().toJson(saveData));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed("saveMarkData:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    if (response.code() == 200 && json.contains("\"success\":200")) {
                        LogUtils.logE("saveMarkData", "新数据提交返回结果：" + json);
                        callBack.onSuccess(gson.fromJson(json, SavaDataResponse.class));
                    } else {
                        if (json.contains("当前教师或任务不存在！")) {
                            callBack.onFailed("当前教师或任务不存在！");
                        } else {
                            callBack.onFailed("saveMarkData:" + json);
                        }
                    }
                } catch (JsonSyntaxException e) {
                    callBack.onFailed(e.getMessage());
                }
            }
        });
    }

    //回评数据提交
    public void upDateMarkData(Context context, SaveMarkDataBean saveData, final MyCallBack callBack) {
        Log.e("upDateMarkData", gson.toJson(saveData));
        Call call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).UPDATEMARKDATA, gson.toJson(saveData));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed("upDateMarkData:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    if (response.code() == 200 && json.contains("\"success\":200")) {
                        LogUtils.logE("upDateMarkData", "回评数据提交返回结果：" + json);
                        callBack.onSuccess(gson.fromJson(json, SavaDataResponse.class));
                    } else {
                        callBack.onFailed("upDateMarkData:" + json);
                    }
                } catch (JsonSyntaxException e) {
                    callBack.onFailed(e.getMessage());
                }
            }
        });
    }

    /**
     * 收藏、取消题目(试卷正常、异常)
     *
     * @param context
     * @param request
     * @param type     1收藏/取消收藏请求、2试卷正常/试卷异常请求
     * @param callBack
     */
    public void collectQuestion(Context context, final CollectRequest request, int type, final MyCallBack callBack) {
        Log.e("collectQuestion   type-" + type, gson.toJson(request));
        Call call;
        if (type == 1) {
            call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).COLLECTQUEXTION, gson.toJson(request));
        } else {
            call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).ABNORMALQUESTION, gson.toJson(request));
        }
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed("collectQuestion:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    if (response.code() == 200 && json.contains("\"success\":200")) {
                        LogUtils.logE("collectQuestion", "收藏、取消题目结果：" + json);
                        callBack.onSuccess(null);
                    } else {
                        callBack.onFailed("collectQuestion:" + json);
                    }
                } catch (Exception e) {
                    callBack.onFailed(e.getMessage());
                }
            }
        });
    }

    //获取评分详情
    public void getMarkAvgScore(Context context, GetMarkAvgScoreRequest request, final MyCallBack callBack) {
        Log.e("getMarkAvgScore", gson.toJson(request));
        Call call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).GETMARKAVGSCORE, gson.toJson(request));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed("getMarkAvgScore:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (json.length() < 10) {
                    callBack.onFailed("暂无数据");
                    return;
                }
                try {
                    if (response.code() == 200) {
                        LogUtils.logE("getMarkAvgScore", "获取评分详情结果：" + json);
                        // TODO Gson解析集合
                        List<GetMarkAvgScoreResponse> avgScoreResponseList = gson.fromJson(json, new TypeToken<List<GetMarkAvgScoreResponse>>() {
                        }.getType());
                        callBack.onSuccess(avgScoreResponseList);
                    } else {
                        callBack.onFailed("getMarkAvgScore:" + json);
                    }
                } catch (JsonSyntaxException e) {
                    callBack.onFailed(e.getMessage());
                }
            }
        });
    }

    //获取当前服务器最新版本
    public void Authenticate(Context context, final MyCallBack callBack) {
        Call call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).AUTHENTICATE, "");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed("Authenticate:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    if (response.code() == 200 && json.contains("\"version\":\"MarkApp")) {
                        Log.e("Authenticate", "获取版本信息: " + json);
                        callBack.onSuccess(json);
                    } else {
                        callBack.onFailed("Authenticate:" + json);
                    }
                } catch (Exception e) {
                    callBack.onFailed(e.getMessage());
                }
            }
        });
    }

    //下载新版本apk
    public void Download(Context context, final MyCallBack callBack) {
        Call call = HttpUtil.getInstance().DownLoadFile(Interfaces.getInstance(context).DOWNLOAD);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed("Download:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("Download", "onResponse: " + response.body().contentLength());
                if (response.body().contentLength() <= 0) {
                    callBack.onFailed("文件下载失败:" + response.body().contentLength());
                } else {
                    callBack.onSuccess(response);
                }
            }
        });
    }

    /**
     * 线上考试图片旋转操作
     *
     * @param url       接口地址
     * @param parameter imgUrl=/resource/AnswerFile/20200911/07a8f51453fb4f8ab9f4824db04b28c7_1.jpeg&angle=-90
     * @param callBack
     */
    public void RotateAnswerImg(String url, String parameter, final MyCallBack callBack) {
        Call call = HttpUtil.getInstance().RotateImg(url, parameter);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed("RotateAnswerImg:" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    if (response.code() == 200 && json.contains("\"code\":200")) {
                        callBack.onSuccess(json);
                    } else {
                        callBack.onFailed("RotateAnswerImg:" + json);
                    }
                } catch (Exception e) {
                    callBack.onFailed(e.getMessage());
                }
            }
        });
    }
}
