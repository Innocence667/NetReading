package com.ruiyi.netreading.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ruiyi.netreading.adapter.TaskListAdapter;
import com.ruiyi.netreading.bean.request.GetExamContextRequest;
import com.ruiyi.netreading.bean.request.GetExamListRequest;
import com.ruiyi.netreading.bean.response.GetExamContextResponse;
import com.ruiyi.netreading.bean.response.GetExamListResponse;
import com.ruiyi.netreading.bean.response.LoginResponse;
import com.ruiyi.netreading.controller.ActivityCollector;
import com.ruiyi.netreading.controller.MyCallBack;
import com.ruiyi.netreading.controller.MyModel;
import com.ruiyi.netreading.util.LoadingUtil;
import com.ruiyi.netreading.util.ToastUtils;
import com.scwang.smartrefresh.header.BezierCircleHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "MainActivity";
    private Context context;
    private boolean isFresh; //是否需要刷新任务列表
    private String teacherGuid;//教师guid
    private String PaperGuid; //试卷guid
    private long clickTime = 0; //第一次点击后退的时间

    private TextView main_setting; //设置
    private SmartRefreshLayout refreshLayou;//只能刷新控件
    private ExpandableListView listView;//二级listView
    private TaskListAdapter taskListAdapter;//适配器
    private int status; //当前考试的状态(2可以阅卷、3统计完成不可修改)

    private MyModel myModel;
    private GetExamListRequest getExamListRequest;//考试任务请求参数
    private GetExamListResponse getExamListResponse;//考试任务请求返回模型(一级列表)
    private GetExamContextResponse getExamContextResponse;//某个任务题目列表返回模型(二级列表)
    private GetExamContextRequest getExamContextRequest; //获取二级列表请求模型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置窗体全屏(隐藏系统table)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        context = this;
        initView();
        myModel = new MyModel();
        Intent intent = getIntent();
        teacherGuid = intent.getStringExtra("userid");
        getExamListRequest = new GetExamListRequest();
        getExamListRequest.setStatus(1);
        getExamListRequest.setTeacherGuid(teacherGuid);
        LoadingUtil.showDialog(context);
        myModel.getTaskList(this, getExamListRequest, new MyCallBack() {
            @Override
            public void onSuccess(Object object) {
                getExamListResponse = (GetExamListResponse) object;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingUtil.closeDialog();
                        taskListAdapter = new TaskListAdapter(context, getExamListResponse.getExamList());
                        taskListAdapter.setMyClickListener(new TaskListAdapter.OnclickListener() {
                            @Override
                            public void myOnclickListenet(int position) {
                                if (!TextUtils.isEmpty(teacherGuid) && !TextUtils.isEmpty(getExamContextResponse.getTaskList().get(position).getTaskGuid())) {
                                    //TODO 临时处理数据
                                    List<GetExamContextResponse.TaskListBean> data = new ArrayList<>();
                                    for (int i = 0; i < getExamContextResponse.getTaskList().size(); i++) {
                                        if (getExamContextResponse.getTaskList().get(i).isCanMark()) {
                                            data.add(getExamContextResponse.getTaskList().get(i));
                                        }
                                    }

                                    Intent marking = new Intent(MainActivity.this, MarkingActivity.class);
                                    marking.putExtra("teacherGuid", teacherGuid);
                                    marking.putExtra("taskGuid", data.get(position).getTaskGuid());
                                    marking.putExtra("status", status);
                                    marking.putExtra("type", getExamContextResponse.getTaskList().get(position).getStyle());
                                    startActivity(marking);
                                } else {
                                    ToastUtils.showToast(context, "参数异常");
                                    if (TextUtils.isEmpty(teacherGuid)) {
                                        Log.e(TAG, "myOnclickListenet: 参数teacherGuid为null");
                                    } else {
                                        Log.e(TAG, "myOnclickListenet: 参数taskGuid为null");
                                    }
                                }
                            }
                        });
                        listView.setAdapter(taskListAdapter);
                        status = getExamListResponse.getExamList().get(0).getStatus();
                        //默认展开第一个step
                        listView.expandGroup(0);
                        getExamContextRequest = new GetExamContextRequest();
                        getExamContextRequest.setTeacherGuid(teacherGuid);
                        PaperGuid = getExamListResponse.getExamList().get(0).getPaperGuid();
                        getExamContextRequest.setPaperGuid(PaperGuid);
                        myModel.getTaskContext(context, getExamContextRequest, new MyCallBack() {

                            @Override
                            public void onSuccess(Object object) {
                                getExamContextResponse = (GetExamContextResponse) object;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (getExamContextResponse.getTaskList() != null && getExamContextResponse.getTaskList().size() > 0) {
                                            //TODO 临时处理数据
                                            List<GetExamContextResponse.TaskListBean> data = new ArrayList<>();
                                            for (int i = 0; i < getExamContextResponse.getTaskList().size(); i++) {
                                                if (getExamContextResponse.getTaskList().get(i).isCanMark()) {
                                                    data.add(getExamContextResponse.getTaskList().get(i));
                                                }
                                            }
                                            taskListAdapter.setChilds(data);
                                            taskListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailed(final String str) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //failed to connect to riyun.lexuewang.cn/116.62.133.77 (port 8002) after 3000ms
                                        showFailedPage(str);
                                        Log.e(TAG, "获取任务详情请求失败," + str);
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailed(String str) {
                showFailedPage(str);
            }
        });
    }

    private void initView() {
        main_setting = findViewById(R.id.main_setting);
        main_setting.setOnClickListener(this);
        refreshLayou = findViewById(R.id.refreshLayou);
        //设置 Header 为 贝塞尔雷达 样式
        //refreshLayou.setRefreshHeader(new BezierRadarHeader(this).setEnableHorizontalDrag(true));
        //弹出圆圈
        refreshLayou.setRefreshHeader(new BezierCircleHeader(this));
        //设置 Footer 为 球脉冲 样式
        refreshLayou.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayou.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                myModel.getTaskList(context, getExamListRequest, new MyCallBack() {
                    @Override
                    public void onSuccess(Object model) {
                        //下拉刷新任务列表
                        getExamListResponse = (GetExamListResponse) model;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "下拉刷新  已更新任务列表");
                                if (getExamContextResponse == null) {
                                    Log.e(TAG, "run: getExamContextResponse是空的");
                                    return;
                                } else {
                                    if (getExamContextResponse.getTaskList() == null) {
                                        Log.e(TAG, "run: getExamContextResponse.getTaskList()是空的");
                                        return;
                                    }
                                }
                                //TODO 二级列表全部关闭
                                for (int i = 0; i < listView.getExpandableListAdapter().getGroupCount(); i++) {
                                    listView.collapseGroup(i);
                                }
                                status = getExamListResponse.getExamList().get(0).getStatus();
                                //默认展开第一个item
                                listView.expandGroup(0);
                                taskListAdapter.setParentData(getExamListResponse.getExamList());
                                taskListAdapter.notifyDataSetChanged();
                                refreshLayou.finishRefresh();
                                getExamContextRequest = new GetExamContextRequest();
                                getExamContextRequest.setTeacherGuid(teacherGuid);
                                PaperGuid = getExamListResponse.getExamList().get(0).getPaperGuid();
                                getExamContextRequest.setPaperGuid(PaperGuid);
                                myModel.getTaskContext(context, getExamContextRequest, new MyCallBack() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        getExamContextResponse = (GetExamContextResponse) object;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (getExamContextResponse.getTaskList() != null && getExamContextResponse.getTaskList().size() > 0) {
                                                    //TODO 临时处理数据
                                                    List<GetExamContextResponse.TaskListBean> data = new ArrayList<>();
                                                    for (int i = 0; i < getExamContextResponse.getTaskList().size(); i++) {
                                                        if (getExamContextResponse.getTaskList().get(i).isCanMark()) {
                                                            data.add(getExamContextResponse.getTaskList().get(i));
                                                        }
                                                    }
                                                    taskListAdapter.setChilds(data);
                                                    taskListAdapter.notifyDataSetChanged();
                                                } else {
                                                    taskListAdapter.clearChilds();
                                                    taskListAdapter.notifyDataSetChanged();
                                                    ToastUtils.showToast(context, "暂无数据");
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailed(final String str) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showFailedPage(str);
                                                Log.e(TAG, "获取任务详情请求失败," + str);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onFailed(String str) {
                        showFailedPage(str);
                    }
                });
            }
        });
        listView = findViewById(R.id.taskListView);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (!listView.isGroupExpanded(groupPosition)) {
                    getExamContextRequest = new GetExamContextRequest();
                    getExamContextRequest.setTeacherGuid(teacherGuid);
                    PaperGuid = getExamListResponse.getExamList().get(groupPosition).getPaperGuid();
                    getExamContextRequest.setPaperGuid(PaperGuid);
                    LoadingUtil.showDialog(context);
                    status = getExamListResponse.getExamList().get(groupPosition).getStatus();
                    myModel.getTaskContext(context, getExamContextRequest, new MyCallBack() {
                        @Override
                        public void onSuccess(Object object) {
                            LoadingUtil.closeDialog();
                            getExamContextResponse = (GetExamContextResponse) object;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (getExamContextResponse.getTaskList() != null && getExamContextResponse.getTaskList().size() > 0) {
                                        //TODO 临时处理数据
                                        List<GetExamContextResponse.TaskListBean> data = new ArrayList<>();
                                        for (int i = 0; i < getExamContextResponse.getTaskList().size(); i++) {
                                            if (getExamContextResponse.getTaskList().get(i).isCanMark()) {
                                                data.add(getExamContextResponse.getTaskList().get(i));
                                            }
                                        }
                                        taskListAdapter.setChilds(data);
                                        taskListAdapter.notifyDataSetChanged();
                                    } else {
                                        taskListAdapter.clearChilds();
                                        taskListAdapter.notifyDataSetChanged();
                                        ToastUtils.showToast(context, "暂无数据");
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailed(final String str) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showFailedPage(str);
                                    Log.e(TAG, "获取任务详情请求失败," + str);
                                }
                            });
                        }
                    });
                } else {
                    listView.collapseGroup(groupPosition);
                }
                return false;
            }
        });
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return true;
            }
        });

        //只展开一个
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                status = getExamListResponse.getExamList().get(groupPosition).getStatus();
                int count = taskListAdapter.getGroupCount();
                for (int i = 0; i < count; i++) {
                    if (i != groupPosition) {
                        listView.collapseGroup(i);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFresh) {
            myModel.getTaskContext(context, getExamContextRequest, new MyCallBack() {
                @Override
                public void onSuccess(Object object) {
                    getExamContextResponse = (GetExamContextResponse) object;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getExamContextResponse.getTaskList() != null && getExamContextResponse.getTaskList().size() > 0) {
                                //TODO 临时处理数据
                                List<GetExamContextResponse.TaskListBean> data = new ArrayList<>();
                                for (int i = 0; i < getExamContextResponse.getTaskList().size(); i++) {
                                    if (getExamContextResponse.getTaskList().get(i).isCanMark()) {
                                        data.add(getExamContextResponse.getTaskList().get(i));
                                    }
                                }
                                taskListAdapter.setChilds(data);
                                taskListAdapter.notifyDataSetChanged();
                            } else {
                                taskListAdapter.clearChilds();
                                taskListAdapter.notifyDataSetChanged();
                                ToastUtils.showToast(context, "暂无数据");
                            }
                        }
                    });
                }

                @Override
                public void onFailed(final String str) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showFailedPage(str);
                            Log.e(TAG, "获取任务详情请求失败," + str);
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFresh = true;
    }

    //任务获取成功
    public void showSueecssPage(final LoginResponse loginResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    //任务获取失败
    public void showFailedPage(final String str) {
        LoadingUtil.closeDialog();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (str.contains("failed to connect to") || str.contains("after 5000ms")) {
                    ToastUtils.showToast(context, "请求超时，无法连接到服务器");
                } else {
                    ToastUtils.showToast(context, str);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    public void updataChildData() {
        GetExamContextRequest request = new GetExamContextRequest();
        request.setTeacherGuid(teacherGuid);
        request.setPaperGuid(PaperGuid);
        myModel.getTaskContext(context, request, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                getExamContextResponse = (GetExamContextResponse) model;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (getExamContextResponse.getTaskList() != null && getExamContextResponse.getTaskList().size() > 0) {
                                    taskListAdapter.setChilds(getExamContextResponse.getTaskList());
                                    taskListAdapter.notifyDataSetChanged();
                                } else {
                                    taskListAdapter.clearChilds();
                                    taskListAdapter.notifyDataSetChanged();
                                    ToastUtils.showToast(context, "暂无数据");
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailed(String str) {
                showFailedPage(str);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_setting:
                startActivity(new Intent(context, SettingActivity.class));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - clickTime > 2000) {
                ToastUtils.showToast(context, "再按一次退出程序");
                clickTime = System.currentTimeMillis();
                return true;
            } else {
                ActivityCollector.finishAll();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
