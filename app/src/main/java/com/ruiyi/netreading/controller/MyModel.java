package com.ruiyi.netreading.controller;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

    //登录账号
    public void getUser(Context context, UserBean userBean, final MyCallBack callback) {
        Log.e("getUser", gson.toJson(userBean));
        Call call = HttpUtil.getInstance().Login(Interfaces.getInstance(context).LOGIN,
                gson.toJson(userBean));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200 && json.contains("\"code\":200,\"message\":\"SUCCESS\"")) {
                    LogUtils.logE("getUser", "登录返回结果" + json);
                    callback.onSuccess(gson.fromJson(json, LoginResponse.class));
                } else {
                    callback.onFailed(json);
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
                callback.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200 && json.contains("\"success\":200")) {
                    LogUtils.logE("GetExamListModel", "获取考试列表：" + json);
                    callback.onSuccess(gson.fromJson(json, GetExamListResponse.class));
                } else {
                    callback.onFailed(json);
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
                childCallback.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200 && json.contains("\"success\":200")) {
                    LogUtils.logE("getTaskContext", "获取考试题目列表：" + json);
                    childCallback.onSuccess(gson.fromJson(json, GetExamContextResponse.class));
                } else {
                    childCallback.onFailed(json);
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
                callBack.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200 && json.contains("\"success\":200")) {
                    LogUtils.logE("getMarkData", "获取阅卷数据：" + json);
                    callBack.onSuccess(gson.fromJson(json, GetMarkDataResponse.class));
                } else {
                    callBack.onFailed(json);
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
                callBack.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200 && json.contains("\"success\":200")) {
                    LogUtils.logE("getMarkNextStudent", "获取新数据结果：" + json);
                    callBack.onSuccess(gson.fromJson(json, GetMarkNextStudentResponse.class));
                } else {
                    callBack.onFailed(json);
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
                callBack.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200 && json.contains("\"success\":200")) {
                    LogUtils.logE("getStudentMarkData", "获取已批阅数据：" + json);
                    callBack.onSuccess(gson.fromJson(json, GetMarkNextStudentResponse.class));
                } else {
                    callBack.onFailed(json);
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
                callBack.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200 && json.contains("\"success\":200")) {
                    LogUtils.logE("reviewStudents", "回评列表接口:" + json);
                    callBack.onSuccess(gson.fromJson(json, ReviewStudentsResponse.class));
                } else {
                    callBack.onFailed(json);
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
                callBack.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200 && json.contains("\"success\":200")) {
                    LogUtils.logE("saveMarkData", "新数据提交返回结果：" + json);
                    callBack.onSuccess(gson.fromJson(json, SavaDataResponse.class));
                } else {
                    callBack.onFailed(json);
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
                callBack.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200 && json.contains("\"success\":200")) {
                    LogUtils.logE("upDateMarkData", "回评数据提交返回结果：" + json);
                    callBack.onSuccess(gson.fromJson(json, SavaDataResponse.class));
                } else {
                    callBack.onFailed(json);
                }
            }
        });
    }

    //收藏、取消题目
    public void collectQuestion(Context context, final CollectRequest request, final MyCallBack callBack) {
        Log.e("collectQuestion", gson.toJson(request));
        Call call = HttpUtil.getInstance().PostResponse(Interfaces.getInstance(context).COLLECTQUEXTION, gson.toJson(request));
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callBack.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200 && json.contains("\"success\":200")) {
                    LogUtils.logE("collectQuestion", "收藏、取消题目结果：" + json);
                    callBack.onSuccess(null);
                } else {
                    callBack.onFailed(json);
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
                callBack.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                if (response.code() == 200) {
                    LogUtils.logE("getMarkAvgScore", "获取评分详情结果：" + json);
                    // TODO Gson解析集合
                    List<GetMarkAvgScoreResponse> avgScoreResponseList = gson.fromJson(json, new TypeToken<List<GetMarkAvgScoreResponse>>() {
                    }.getType());
                    callBack.onSuccess(avgScoreResponseList);
                } else {
                    callBack.onFailed(json);
                }
            }
        });
    }

    //确定按钮提交数据
    public void Submitdata() {

    }
}
