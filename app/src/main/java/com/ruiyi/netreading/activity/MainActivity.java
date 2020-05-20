package com.ruiyi.netreading.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import com.ruiyi.netreading.adapter.TaskListAdapter;
import com.ruiyi.netreading.bean.request.GetExamContextRequest;
import com.ruiyi.netreading.bean.request.GetExamListRequest;
import com.ruiyi.netreading.bean.response.GetExamContextResponse;
import com.ruiyi.netreading.bean.response.GetExamListResponse;
import com.ruiyi.netreading.bean.response.LoginResponse;
import com.ruiyi.netreading.controller.MyCallBack;
import com.ruiyi.netreading.controller.MyModel;
import com.ruiyi.netreading.util.ToastUtils;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private Context context;
    private String teacherGuid;//教师guid

    private ExpandableListView listView;//二级listView
    private TaskListAdapter taskListAdapter;//适配器

    private MyModel getExamListModel;
    private GetExamListResponse getExamListResponse;//考试任务模型(一级列表)
    private GetExamContextResponse getExamContextResponse;//某个任务题目列表模型(二级列表)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        context = this;
        initView();
        getExamListModel = new MyModel();
        Intent intent = getIntent();
        teacherGuid = intent.getStringExtra("userid");
        GetExamListRequest getExamListRequest = new GetExamListRequest();
        getExamListRequest.setStatus(1);
        getExamListRequest.setTeacherGuid(teacherGuid);
        getExamListModel.getTaskList(this, getExamListRequest, new MyCallBack() {
            @Override
            public void onSuccess(Object object) {
                getExamListResponse = (GetExamListResponse) object;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        taskListAdapter = new TaskListAdapter(context, getExamListResponse.getExamList());
                        taskListAdapter.setMyClickListener(new TaskListAdapter.OnclickListener() {
                            @Override
                            public void myOnclickListenet(int position) {
                                if (!TextUtils.isEmpty(teacherGuid) && !TextUtils.isEmpty(getExamContextResponse.getTaskList().get(position).getTaskGuid())) {
                                    Intent marking = new Intent(MainActivity.this, MarkingActivity.class);
                                    marking.putExtra("teacherGuid", teacherGuid);
                                    marking.putExtra("taskGuid", getExamContextResponse.getTaskList().get(position).getTaskGuid());
                                    startActivity(marking);
                                } else {
                                    if (TextUtils.isEmpty(teacherGuid)) {
                                        Log.e(TAG, "myOnclickListenet: 参数teacherGuid为null");
                                    } else {
                                        Log.e(TAG, "myOnclickListenet: 参数taskGuid为null");
                                    }
                                }
                            }
                        });
                        listView.setAdapter(taskListAdapter);
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
        listView = findViewById(R.id.taskListView);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                final GetExamContextRequest request = new GetExamContextRequest();
                request.setTeacherGuid(teacherGuid);
                request.setPaperGuid(getExamListResponse.getExamList().get(groupPosition).getPaperGuid());
                getExamListModel.getTaskContext(context, request, new MyCallBack() {

                    @Override
                    public void onSuccess(Object object) {
                        getExamContextResponse = (GetExamContextResponse) object;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                taskListAdapter.setChilds(getExamContextResponse.getTaskList());
                                taskListAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onFailed(final String str) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast(context, str);
                                Log.e(TAG, "获取任务详情请求失败," + str);
                            }
                        });
                    }
                });
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
                int count = taskListAdapter.getGroupCount();
                for (int i = 0; i < count; i++) {
                    if (i != groupPosition) {
                        listView.collapseGroup(i);
                    }
                }
            }
        });
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showToast(context, str);
            }
        });
    }
}
