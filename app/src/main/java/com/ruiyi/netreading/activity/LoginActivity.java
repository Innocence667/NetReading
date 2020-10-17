package com.ruiyi.netreading.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ruiyi.netreading.bean.UserBean;
import com.ruiyi.netreading.bean.response.LoginResponse;
import com.ruiyi.netreading.controller.ActivityCollector;
import com.ruiyi.netreading.controller.MyCallBack;
import com.ruiyi.netreading.controller.MyModel;
import com.ruiyi.netreading.util.LogUtils;
import com.ruiyi.netreading.util.PreferencesService;
import com.ruiyi.netreading.util.ToastUtils;
import com.ruiyi.netreading.util.Tool;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "LoginActivity";
    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    private Context context;

    private RelativeLayout login_main;
    private ImageView loginBg;
    private Spinner spinner; //地址选择器
    private TextView servicePathTv; //服务器地址
    private TextView modifyServicePath; //切换地址
    private EditText userName; //用户名
    private EditText userPwd; //密码
    private Button loginBtn; //登录
    private TextView versionTv; //版本号

    private MyModel loginModel;
    private String[] paths; //服务器地址数据源
    private String servicePath; //当前学校名称

    private Dialog dialog;
    private TextView urlHint; //title
    private EditText location, port; //地址、端口
    private LinearLayout btnLayout; //btn父布局
    private Button testUrlBtn, testUrlBtnCancel; //取消、测试
    private ProgressBar testProgress; //圆形进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_login);
        context = this;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
        if (!TextUtils.isEmpty(PreferencesService.getInstance(context).getUserGuid())) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            //intent.putExtra("userid", loginResponse.getData().getUserid());
            intent.putExtra("userid", PreferencesService.getInstance(context).getUserGuid());
            startActivity(intent);
            finish();
        } else {
            servicePath = "铧强中学";
            initView();
            loginModel = new MyModel();
            if (TextUtils.isEmpty(PreferencesService.getInstance(context).getServicePath())) {
                dialog.show();
            } else {
                //https://Slzxres.lexuewang.cn:8012
                //servicePathTv.setText(PreferencesService.getInstance(context).getServicePath());
                servicePathTv.setText(PreferencesService.getInstance(context).getPath());
            }
        }
        Log.e(TAG, "屏幕分辨率: " + Tool.getDefaultDisplay(LoginActivity.this).x + "-" + Tool.getDefaultDisplay(LoginActivity.this).y);
    }

    private void initView() {
        login_main = findViewById(R.id.login_main);
        paths = getResources().getStringArray(R.array.services);
        loginBg = findViewById(R.id.loginBg);
        loginBg.setBackgroundResource(R.drawable.login_bg2);
        spinner = findViewById(R.id.login_servicePath);
        servicePathTv = findViewById(R.id.servicePath);
        modifyServicePath = findViewById(R.id.modifyServicePath);
        modifyServicePath.setOnClickListener(this);
        userName = findViewById(R.id.login_name);
        userPwd = findViewById(R.id.login_pwd);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        versionTv = findViewById(R.id.versionTv);
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(this.getPackageName(), 0);
            versionTv.setText("v" + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
                        path = "https://slzxres.lexuewang.cn:8012";
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

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.setting_dialog, null);
        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);
        urlHint = view.findViewById(R.id.urlHint);
        location = view.findViewById(R.id.location);
        port = view.findViewById(R.id.port);
        btnLayout = view.findViewById(R.id.btnLayout);
        testUrlBtn = view.findViewById(R.id.testUrlBtn);
        testUrlBtnCancel = view.findViewById(R.id.testUrlBtnCancel);
        testUrlBtn.setOnClickListener(this);
        testUrlBtnCancel.setOnClickListener(this);
        testProgress = view.findViewById(R.id.testProgress);
        urlHint.setText(Html.fromHtml("格式:https://<font color = '#FF0000'>xxxx</font>.lexuewang.cn:<font color = '#FF0000'>1234</font>"));
        testUrlBtnCancel.setVisibility(View.GONE);

        //动态设置控件宽高
        Rect rect = Tool.getScreenparameters(this);
        Log.e(TAG, "屏幕的宽高是：" + rect.width() + " - " + rect.height());
        Log.e(TAG, "状态栏的高度是： " + Tool.getStatusBarHeight(this));
        if (rect.width() == 1024 && rect.height() == 768) { //三星p350
            login_main.setBackgroundResource(R.drawable.login_bg0);
            loginBg.setVisibility(View.GONE);
        } else if (rect.width() == 1920 && rect.height() == 1200) { //华为C5
            login_main.setBackgroundResource(R.drawable.login_bg);
            loginBg.setVisibility(View.VISIBLE);
            //获取背景图片缩放率
            float wZoom = 2048 * 1f / rect.width();
            float hZoom = 1536 * 1f / (rect.height() - Tool.getStatusBarHeight(this));
            //计算登录白框的大小
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (1386 / wZoom), (int) (922 / hZoom) + 200);
            Log.e(TAG, "initView: paramsW-" + (1386 / wZoom));
            Log.e(TAG, "initView: paramsH-" + (922 / hZoom));
            //计算距离顶部padding
            int topPadding = (int) ((rect.height() - Tool.getStatusBarHeight(this) - 63 - (922 / hZoom)) / 2);
            Log.e(TAG, "topPadding: " + topPadding);
            params.setMargins(0, topPadding, 0, 0);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            loginBg.setLayoutParams(params);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modifyServicePath:
                location.setText("");
                port.setText("");
                testUrlBtnCancel.setVisibility(View.VISIBLE);
                testUrlBtn.setVisibility(View.VISIBLE);
                testUrlBtn.setEnabled(true);
                testProgress.setVisibility(View.GONE);
                dialog.show();
                break;
            case R.id.loginBtn:
                loginBtn.setEnabled(false);
                loginBtn.setText("登录中……");
                loginBtn.setBackground(getResources().getDrawable(R.drawable.btn_login_bg_down));
                servicePath = getServiceName(PreferencesService.getInstance(context).getServicePath());
                final UserBean userBean = getUserInput();
                if (userBean != null) {
                    userBean.setIsmemory(false);
                    userBean.setTerminal(1);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //LoadingUtil.getInstance(context).showDialog();
                            loginModel.getUser(context, userBean, new MyCallBack() {

                                @Override
                                public void onSuccess(Object model) {
                                    showSueecssPage((LoginResponse) model);
                                }

                                @Override
                                public void onFailed(String str) {
                                    if (str.contains("该教师下没有任何菜单访问权限~")) {
                                        showFailedPage("该教师下没有任何菜单访问权限~");
                                    } else {
                                        showFailedPage("用户名或密码错误");
                                    }
                                }

                            });
                        }
                    });
                }
                break;
            case R.id.testUrlBtn:
                /*if (TextUtils.isEmpty(location.getText().toString().trim())) {
                    ToastUtils.showToast(context, "请输入地址");
                    return;
                }
                if (TextUtils.isEmpty(port.getText().toString().trim())) {
                    ToastUtils.showToast(context, "请输入端口");
                    return;
                }*/
                testUrlBtn.setEnabled(false);
                testProgress.setVisibility(View.VISIBLE);
                testUrlBtnCancel.setVisibility(View.GONE);
                testUrlBtn.setVisibility(View.GONE);
                testUrl();
                break;
            case R.id.testUrlBtnCancel:
                dialog.cancel();
                break;
        }
    }

    //测试用户输入的网址是否正确
    private void testUrl() {
        String url = "https://" + location.getText().toString().trim() + ".lexuewang.cn:" + port.getText().toString().trim() + "/login/home/index";
        if (TextUtils.isEmpty(location.getText().toString().trim())) {
            url = "https://riyun.lexuewang.cn:" + port.getText().toString().trim() + "/login/home/index";
        }
        //测试服务器没端口
        if (TextUtils.isEmpty(location.getText().toString().trim()) && "8008".equals(port.getText().toString().trim())) {
            url = "https://riyun.lexuewang.cn/login/home/index";
        }

        loginModel.testUrl(context, url, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                int code = (int) model;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(port.getText().toString().trim())) {
                            servicePath = "https://" + location.getText().toString().trim() + ".lexuewang.cn:" + port.getText().toString().trim();
                        } else {
                            servicePath = "https://" + location.getText().toString().trim() + ".lexuewang.cn";
                        }
                        if (TextUtils.isEmpty(location.getText().toString().trim())) {
                            servicePath = "https://riyun.lexuewang.cn:" + port.getText().toString().trim();
                        }
                        if ("https://riyun.lexuewang.cn:8008".equals(servicePath)) {
                            PreferencesService.getInstance(context).saveServicePath("https://riyun.lexuewang.cn");
                        } else {
                            PreferencesService.getInstance(context).saveServicePath(servicePath);
                        }
                        if (!TextUtils.isEmpty(port.getText().toString().trim())) {
                            PreferencesService.getInstance(context).savePath(location.getText().toString().trim() + " (" + port.getText().toString().trim() + ")");
                            servicePathTv.setText(location.getText().toString().trim() + " (" + port.getText().toString().trim() + ")");
                        } else {
                            PreferencesService.getInstance(context).savePath(location.getText().toString().trim());
                            servicePathTv.setText("riyun");
                        }
                        dialog.cancel();
                    }
                });
            }

            @Override
            public void onFailed(final String str) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        testUrlBtn.setEnabled(true);
                        testProgress.setVisibility(View.GONE);
                        testUrlBtn.setVisibility(View.VISIBLE);
                        LogUtils.logE("testUrl", "连接服务器失败，请检查服务器地址或端口号");
                        ToastUtils.showToast(context, str);
                    }
                });
            }
        });
    }

    private String getServiceName(String servicePath) {
        String serName = "";
        switch (servicePath) {
            case "https://rshqzx.lexuewang.cn:8028":
                serName = "铧强中学";
                break;
            case "https://slzxres.lexuewang.cn:8012":
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
                PreferencesService.getInstance(context).saveUser(loginResponse.getData().getRealname(), loginResponse.getData().getUguid(), loginResponse.getData().getSname());
                ToastUtils.showTopToast(context, "欢迎您：" + loginResponse.getData().getRealname(), R.style.Toast_Animation);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                //intent.putExtra("userid", loginResponse.getData().getUserid());
                intent.putExtra("userid", loginResponse.getData().getUguid());
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
                loginBtn.setText("登 录");
                loginBtn.setBackground(getResources().getDrawable(R.drawable.btn_login_click));
                //LoadingUtil.getInstance(context).closeDialog();
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
        ActivityCollector.removeActivity(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //LoadingUtil.getInstance(context).closeDialog();
            }
        });
    }

    private long clickTime = 0;

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
