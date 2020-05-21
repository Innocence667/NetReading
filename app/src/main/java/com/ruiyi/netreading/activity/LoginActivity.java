package com.ruiyi.netreading.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ruiyi.netreading.bean.UserBean;
import com.ruiyi.netreading.bean.response.LoginResponse;
import com.ruiyi.netreading.controller.MyCallBack;
import com.ruiyi.netreading.controller.MyModel;
import com.ruiyi.netreading.util.LoadingUtil;
import com.ruiyi.netreading.util.PreferencesService;
import com.ruiyi.netreading.util.ToastUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "LoginActivity";
    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    private Context context;

    private Spinner spinner; //地址选择器
    private EditText userName; //用户名
    private EditText userPwd; //密码
    private Button loginBtn; //登录

    private MyModel loginModel;
    private String[] paths; //服务器地址数据源
    private String servicePath; //当前学校名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏顶部title栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        context = this;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
        servicePath = "铧强中学";
        initView();
        loginModel = new MyModel();
    }

    private void initView() {
        paths = getResources().getStringArray(R.array.services);
        spinner = findViewById(R.id.login_servicePath);
        userName = findViewById(R.id.login_name);
        userPwd = findViewById(R.id.login_pwd);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "你选择了" + paths[position]);
                servicePath = paths[position];
                String path = "";
                switch (servicePath) {
                    case "铧强中学":
                        path = "https://rshqzx.lexuewang.cn:8028";
                        break;
                    case "双流中学":
                        path = "https://slzxres.lexuewang.cn:8028";
                        break;
                    case "青神中学（高一）":
                        path = "https://qszxgyres.lexuewang.cn:8028";
                        break;
                    case "青神中学（高二、三）":
                        path = "https://qszxres.lexuewang.cn:8024";
                        break;
                    case "北大附中-本部":
                        path = "https://bdfzres.lexuewang.cn:8028";
                        break;
                    case "曲靖市第一中学":
                        path = "https://qjyzres.lexuewang.cn:8028";
                        break;
                    case "四川省高县中学":
                        path = "https://gxzxres.lexuewang.cn:8028";
                        break;
                    case "成都新津为明学校":
                        path = "https://cdxjwmxx.lexuewang.cn:8005";
                        break;
                    case "青岛第二中学分校":
                        path = "https://qdezfx.lexuewang.cn:8028";
                        break;
                    case "广州中学(五山校区)":
                        path = "https://gzzxws.lexuewang.cn:8028";
                        break;
                    case "青岛第二中学(高一)":
                        path = "https://qdez.lexuewang.cn:8006";
                        break;
                    case "青岛第二中学(高二)":
                        path = "https://qdezgs.lexuewang.cn:8016";
                        break;
                    case "青岛第二中学(高三)":
                        path = "https://qdezgy.lexuewang.cn:8026";
                        break;
                    case "遂宁中学外国语实验学校":
                        path = "https://szwgyg1res.lexuewang.cn:8028";
                        break;
                    case "北京(测试)":
                        path = "https://192.168.1.130:8028";
                        break;
                    case "北京(测试2)":
                        path = "https://192.168.10.21:8028";
                        break;
                    case "云测评--乐学网":
                        path = "https://riyun.lexuewang.cn:8002";
                        break;
                    case "睿易学院":
                        path = "https://test.ruiyiwangxiao.com/RayeeMark";
                        break;
                }
                PreferencesService.getInstance(context).saveServicePath(path);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        loginBtn.setEnabled(false);
        servicePath = getServiceName(PreferencesService.getInstance(context).getServicePath());
        final UserBean userBean = getUserInput();
        if (userBean != null) {
            userBean.setIsmemory(false);
            userBean.setTerminal(2);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingUtil.getInstance(context).showDialog();
                    loginModel.getUser(context, userBean, new MyCallBack() {

                        @Override
                        public void onSuccess(Object model) {
                            showSueecssPage((LoginResponse) model);
                        }

                        @Override
                        public void onFailed(String str) {
                            showFailedPage(str);
                        }

                    });
                }
            });
        }
    }

    private String getServiceName(String servicePath) {
        String serName = "";
        switch (servicePath) {
            case "https://rshqzx.lexuewang.cn:8028":
                serName = "铧强中学";
                break;
            case "https://slzxres.lexuewang.cn:8028":
                serName = "双流中学";
                break;
            case "https://qszxgyres.lexuewang.cn:8028":
                serName = "青神中学（高一）";
                break;
            case "https://qszxres.lexuewang.cn:8024":
                serName = "青神中学（高二、三）";
                break;
            case "https://bdfzres.lexuewang.cn:8028":
                serName = "北大附中-本部";
                break;
            case "https://qjyzres.lexuewang.cn:8028":
                serName = "曲靖市第一中学";
                break;
            case "https://gxzxres.lexuewang.cn:8028":
                serName = "四川省高县中学";
                break;
            case "https://cdxjwmxx.lexuewang.cn:8005":
                serName = "成都新津为明学校";
                break;
            case "https://qdezfx.lexuewang.cn:8028":
                serName = "青岛第二中学分校";
                break;
            case "https://gzzxws.lexuewang.cn:8028":
                serName = "广州中学(五山校区)";
                break;
            case "https://qdez.lexuewang.cn:8006":
                serName = "青岛第二中学(高一)";
                break;
            case "https://qdezgs.lexuewang.cn:8016":
                serName = "青岛第二中学(高二)";
                break;
            case "https://qdezgy.lexuewang.cn:8026":
                serName = "青岛第二中学(高三)";
                break;
            case "https://szwgyg1res.lexuewang.cn:8028":
                serName = "遂宁中学外国语实验学校";
                break;
            case "https://192.168.1.130:8028":
                serName = "北京(测试)";
                break;
            case "https://192.168.10.21:8028":
                serName = "北京(测试2)";
                break;
            case "https://riyun.lexuewang.cn:8002":
                serName = "云测评--乐学网";
                break;
            case "https://test.ruiyiwangxiao.com/RayeeMark":
                serName = "睿易学院";
                break;
        }
        return serName;
    }

    //获取用户输入内容
    private UserBean getUserInput() {
        if (!TextUtils.isEmpty(userName.getText().toString())) {
            if (!TextUtils.isEmpty(userPwd.getText().toString())) {
                UserBean bean = new UserBean();
                bean.setUsername(userName.getText().toString().trim());
                bean.setPassword(userPwd.getText().toString().trim());
                return bean;
            } else {
                loginBtn.setEnabled(true);
                Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
            loginBtn.setEnabled(true);
            Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    //登录成功
    public void showSueecssPage(final LoginResponse loginResponse) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showTopToast(context, "欢迎您：" + loginResponse.getData().getUsername(), R.style.Toast_Animation);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userid", loginResponse.getData().getUserid());
                startActivity(intent);
                finish();
            }
        });
    }

    //登录失败
    public void showFailedPage(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginBtn.setEnabled(true);
                LoadingUtil.getInstance(context).closeDialog();
                Log.e(TAG, "登录失败：" + str);
                ToastUtils.showToast(context, str);
            }
        });
    }

    //设置app字体大小禁止跟随系统字体大小(样式跟随系统：宋体、华康少女)
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) { //申请成功
            for (int i = 0; i < permissions.length; i++) {
                Log.e("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
                if (grantResults[i] == -1) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("警告")
                            .setMessage("请前往设置->应用->睿易云阅卷->权限中打开相关权限，否则功能无法正常运行")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                    break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingUtil.getInstance(context).closeDialog();
            }
        });
    }
}
