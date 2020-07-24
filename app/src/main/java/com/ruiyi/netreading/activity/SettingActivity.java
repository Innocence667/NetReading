package com.ruiyi.netreading.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ruiyi.netreading.controller.ActivityCollector;
import com.ruiyi.netreading.controller.MyCallBack;
import com.ruiyi.netreading.controller.MyModel;
import com.ruiyi.netreading.util.PreferencesService;
import com.ruiyi.netreading.util.ToastUtils;
import com.ruiyi.netreading.util.Tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Response;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private MyModel myModel;

    private TextView head_back; //后退
    private TextView head_title; //title
    private TextView main_setting; //设置

    private LinearLayout checkForUpdates, userFeedback, userExit;
    private TextView userName; //用户名称
    private TextView schoolName; //学校名称
    private TextView versionNum; //版本号

    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private PackageInfo packageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_setting);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        myModel = new MyModel();
        initView();
    }

    private void initView() {
        head_back = findViewById(R.id.head_back);
        head_back.setVisibility(View.VISIBLE);
        head_back.setOnClickListener(this);
        head_title = findViewById(R.id.head_title);
        main_setting = findViewById(R.id.main_setting);
        head_title.setText("设 置");
        main_setting.setVisibility(View.GONE);
        checkForUpdates = findViewById(R.id.checkForUpdates);
        checkForUpdates.setOnClickListener(this);
        userFeedback = findViewById(R.id.userFeedback);
        userFeedback.setOnClickListener(this);
        userExit = findViewById(R.id.userExit);
        userExit.setOnClickListener(this);
        userName = findViewById(R.id.userName);
        schoolName = findViewById(R.id.schoolName);
        versionNum = findViewById(R.id.versionNum);
        Map<String, String> user = PreferencesService.getInstance(context).getUser();
        userName.setText(user.get("userName"));
        schoolName.setText(user.get("userSchool"));
        PackageManager packageManager = this.getPackageManager();
        try {
            packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            versionNum.setText("(当前版本" + packageInfo.versionName + ")");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_back:
                this.finish();
                break;
            case R.id.checkForUpdates:
                checkUpdates();
                break;
            case R.id.userFeedback:
                userFeedback();
                break;
            case R.id.userExit:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog, null);
                builder.setView(view);
                builder.setCancelable(false);
                TextView dialog_title = view.findViewById(R.id.dialog_title);
                dialog_title.setText("提示");
                TextView dialog_msg = view.findViewById(R.id.dialog_msg);
                dialog_msg.setText("是否确定退出？");
                Button dialog_determine = view.findViewById(R.id.dialog_determine);
                dialog_determine.setOnClickListener(this);
                Button dialog_cancel = view.findViewById(R.id.dialog_cancel);
                dialog_cancel.setOnClickListener(this);
                dialog = builder.create();
                dialog.show();
                break;
            case R.id.dialog_determine:
                dialog.cancel();
                //ActivityCollector.finishAll();
                PreferencesService.getInstance(context).clearUserData();
                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                SettingActivity.this.finish();
                break;
            case R.id.dialog_cancel:
                dialog.cancel();
                break;
            case R.id.feedback_commit:
                commitFeedback();
                break;
            case R.id.feedback_cancel:
                dialog.cancel();
                break;
        }
    }

    //提交用户意见反馈
    private void commitFeedback() {

    }

    //用户反馈
    private void userFeedback() {
        dialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.feedback_dialog, null);
        dialog.setView(view);
        EditText feedback_input = view.findViewById(R.id.feedback_input);
        final TextView feedback_fontNum = view.findViewById(R.id.feedback_fontNum);
        Button feedback_commit = view.findViewById(R.id.feedback_commit);
        feedback_commit.setOnClickListener(this);
        Button feedback_cancel = view.findViewById(R.id.feedback_cancel);
        feedback_cancel.setOnClickListener(this);
        dialog.setCancelable(false);
        dialog.show();
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = (int) (Tool.getScreenparameters(SettingActivity.this).width() * 0.75);
        dialog.getWindow().setAttributes(layoutParams);
        feedback_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //输入前的监听
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //输入内容变化的监听
                feedback_fontNum.setText((500 - s.toString().length()) + "/500");
            }

            @Override
            public void afterTextChanged(Editable s) {
                //输入后的监听
            }
        });
    }

    //检查更新
    private void checkUpdates() {
        dialog = new AlertDialog.Builder(context).create();
        View view = LayoutInflater.from(context).inflate(R.layout.update_dialog, null);
        dialog.setView(view);
        final ProgressBar update_progress = view.findViewById(R.id.update_progress);
        final TextView update_msg = view.findViewById(R.id.update_msg);
        LinearLayout userCheck = view.findViewById(R.id.userCheck);
        final Button update_determine = view.findViewById(R.id.update_determine);
        final Button update_cancel = view.findViewById(R.id.update_cancel);
        dialog.setCancelable(false);
        update_msg.setText("正在连接服务器……");
        update_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("正在下载新版本...");
                //不能手动取消下载进度对话框
                progressDialog.setCancelable(false);
                progressDialog.show();
                downloadAPK();
            }
        });
        update_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();

        myModel.Authenticate(context, new MyCallBack() {
            @Override
            public void onSuccess(final Object model) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //{"version":"MarkApp-1.0.87"}
                        String version = (String) model;
                        String[] serviceVersion = version.split(":")[1].split("-")[1].split("\\.");
                        String[] locationVersion = packageInfo.versionName.split("\\.");
                        boolean isNew = false;//是否有新版本
                        if (Integer.valueOf(serviceVersion[0].replace("\"}", "")) > Integer.valueOf(locationVersion[0])) {
                            isNew = true;
                        } else {
                            if (Integer.valueOf(serviceVersion[1].replace("\"}", "")) > Integer.valueOf(locationVersion[1])) {
                                isNew = true;
                            } else {
                                if (Integer.valueOf(serviceVersion[2].replace("\"}", "")) > Integer.valueOf(locationVersion[2])) {
                                    isNew = true;
                                }
                            }
                        }
                        if (isNew) {
                            update_progress.setVisibility(View.GONE);
                            update_msg.setText("发现新版本" + version.split(":")[1].split("-")[1].replace("\"}", ""));
                            update_determine.setVisibility(View.VISIBLE);
                            update_cancel.setVisibility(View.VISIBLE);
                            update_cancel.setText("取消");
                        } else {
                            update_progress.setVisibility(View.GONE);
                            update_msg.setText("已是最新版本");
                            update_determine.setVisibility(View.GONE);
                            update_cancel.setVisibility(View.VISIBLE);
                            update_cancel.setText("确定");
                        }
                    }
                });
            }

            @Override
            public void onFailed(final String str) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(context, str);
                    }
                });
            }
        });
    }

    //现在最新apk
    private void downloadAPK() {
        myModel.Download(context, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                Response response = (Response) model;
                progressDialog.setMax((int) (response.body().contentLength() / 1024));
                progressDialog.setProgressNumberFormat("0KB/" + response.body().contentLength() + "KB");
                InputStream is = null;
                FileOutputStream fos = null;
                is = response.body().byteStream();
                if (is != null) {
                    File parentFile = new File(Tool.DOWNFILE);
                    if (!parentFile.exists()) {
                        parentFile.mkdir();
                    }
                    try {
                        fos = new FileOutputStream(new File(Tool.DOWNFILE, "update.apk"));
                        byte[] bytes = new byte[1024];
                        int len = -1;
                        long a = -1;
                        while ((len = is.read(bytes)) != -1) {
                            fos.write(bytes, 0, len);
                            a += len;
                            Log.e("downloadAPK", "文件写入中……" + a);
                            progressDialog.setProgress((int) (a / 1024));
                            progressDialog.setProgressNumberFormat(a + "KB/" + response.body().contentLength() + "KB");
                        }
                        progressDialog.cancel();
                        OpenFile(new File(Tool.DOWNFILE, "update.apk"));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("downloadAPK", "InputStream为空");
                }
                try {
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(final String str) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.cancel();
                        ToastUtils.showToast(context, str);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    //打开下载好的apk
    private void OpenFile(File apk) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
        startActivity(intent);
    }
}
