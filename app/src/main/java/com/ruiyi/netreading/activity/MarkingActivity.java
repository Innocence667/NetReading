package com.ruiyi.netreading.activity;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ruiyi.netreading.adapter.DoubleScoreTableAdapter;
import com.ruiyi.netreading.adapter.QuestionNumAdapter;
import com.ruiyi.netreading.adapter.ReviewAdatper;
import com.ruiyi.netreading.adapter.ScoreAdapter;
import com.ruiyi.netreading.adapter.ScoreDetailsAdapter;
import com.ruiyi.netreading.adapter.TopScoreAdapter;
import com.ruiyi.netreading.bean.ChildLocation;
import com.ruiyi.netreading.bean.CustomBean;
import com.ruiyi.netreading.bean.ImageData;
import com.ruiyi.netreading.bean.LocalImageData;
import com.ruiyi.netreading.bean.SaveMarkDataBean;
import com.ruiyi.netreading.bean.ScorePanel;
import com.ruiyi.netreading.bean.SpenStroke;
import com.ruiyi.netreading.bean.StepScore;
import com.ruiyi.netreading.bean.request.CollectRequest;
import com.ruiyi.netreading.bean.request.GetMarkAvgScoreRequest;
import com.ruiyi.netreading.bean.request.GetMarkDataRequest;
import com.ruiyi.netreading.bean.request.GetStudentMarkDataRequest;
import com.ruiyi.netreading.bean.request.ReviewStudentsRequest;
import com.ruiyi.netreading.bean.response.GetMarkAvgScoreResponse;
import com.ruiyi.netreading.bean.response.GetMarkDataResponse;
import com.ruiyi.netreading.bean.response.GetMarkNextStudentResponse;
import com.ruiyi.netreading.bean.response.ReviewStudentsResponse;
import com.ruiyi.netreading.bean.response.SavaDataResponse;
import com.ruiyi.netreading.controller.ActivityCollector;
import com.ruiyi.netreading.controller.MyCallBack;
import com.ruiyi.netreading.controller.MyModel;
import com.ruiyi.netreading.util.LoadingUtil;
import com.ruiyi.netreading.util.LogUtils;
import com.ruiyi.netreading.util.PreferencesService;
import com.ruiyi.netreading.util.ToastUtils;
import com.ruiyi.netreading.util.Tool;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenObjectBase;
import com.samsung.android.sdk.pen.document.SpenObjectStroke;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;
import com.samsung.android.sdk.pen.engine.SpenZoomListener;
import com.samsung.android.sdk.pen.pen.SpenPenManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MarkingActivity extends AppCompatActivity implements View.OnClickListener, SpenTouchListener {

    private static final int PENSIZE = 3; //笔画的宽度
    private float scale; //图片缩放的倍数

    private String TAG = "MarkingActivity";
    private Context context;
    private Handler handler;
    private MyModel myModel;

    private AlertDialog.Builder builder;
    private PopupWindow popupWindow;

    private DrawerLayout drawerLayout;
    private RelativeLayout main; //主布局
    private LinearLayout reigth; //侧滑布局

    private TextView number, time, score; //回评列表筛选
    private ListView reviewListView; //回评列表
    private TextView nodatahint; //没有回评数据提示

    private String teacherGuid;//教师guid
    private String taskGuid;//任务guid
    private int status; //当前阅卷状态
    private int style; //当前阅卷模式(1单评、3按班)

    private int LOCATION = 0; //当前题目的进度(12/100中的12，回评模式用的到),是集合的下标
    private int minLocation = 0; //当前显示题目的位置(合并题：当前第几个小题；非合并题：当前位置为0)
    private boolean reviewMode; //是否是回评模式
    private boolean isStepScore; //是否是步骤分模式
    private boolean doubleMode; //双栏模式
    private String doubleScore = "0";//双栏模式选中的分值
    private String SCORE; //当前题目的得分
    private String TOTAL; //当前题目的总分
    private double MaxScore;//当前题目的最高分数(合并题目的最高分数)
    private String tableStepScore; //当前步骤分选中的标签
    private List<ChildLocation> childLocations; //存放所有步骤分标签
    private float startY = 0;//用于记录操作标签分加减时，点击的初始Y轴坐标值
    private float startX = 0;//用于记录删除标签时，点击的初始位置
    private double stepScoreModeFullScore = -1;//步骤分模式题目总分
    private double stepScoreModeScore = -1;//步骤分模式得分
    private int index = -1; //当前操作标签的标识
    private long scoreClickTime = 0; //打分点击的时间

    private RelativeLayout spenView;
    private SpenNoteDoc mSpenNotDoc;
    private SpenPageDoc mSpenPageDoc;
    private SpenSurfaceView mSpenSimpleSurfaceView;
    private boolean isSpenFeatureEnable;
    private RelativeLayout tableParent; //显示步骤分标签布局

    private TextView questionScore;//题目得分
    private TextView questionNum;//题号
    private ListView scoreList;//打分面板
    private Button submiss;//提交按钮

    private ScoreAdapter scoreAdapter; //赋分列表适配器
    private ScorePanel scorePanel; //打分面板模型(分数值，是否选中)

    private CheckBox titleNo; //折合开关
    private RecyclerView mRecyclerView; //小题号列表
    private LinearLayoutManager linearLayoutManager;
    private TextView progressTips;//阅卷进度提示
    private QuestionNumAdapter questionNumAdapter; //题号选择适配器
    private List<String> strings; //合并题号列表
    private List<String> scores; //小题得分

    //底部功能区
    private CheckBox sideslip; //功能区折叠按钮
    private HorizontalScrollView scrollView; //功能区
    //批注、清除笔迹、步骤分、收藏、回评、继续阅卷、评分详情、设置
    private RelativeLayout commentsP, eliminateP, stepScoreP, collectionP, historyP, goOnParent, scoringDatailsP, settingP;
    private CheckBox comments; //批注
    private TextView eliminate;//清除笔记
    private CheckBox stepScore; //步骤分
    private CheckBox collection; //收藏
    private TextView history; //回评
    private TextView goOn; //继续阅卷
    private TextView scoringDatails; //评分详情
    private TextView setting; //设置
    private AlertDialog settingDialog; //设置dialog

    private List<ImageData> imageDataList; //存放每张图片的宽高
    private ImageData imageData;

    private GetMarkDataResponse response; //获取试卷试卷
    private GetMarkDataRequest request; //获取新数据请求模型
    private GetMarkNextStudentResponse getMarkNextStudentResponse; //获取新数据请求结果模型
    private SaveMarkDataBean saveMarkDataBean; //数据提交模型
    private SavaDataResponse saveResponse; //提交成功返回结果
    private List<SaveMarkDataBean.QuestionsBean> questionsBeanList; //新数据提交里的questions字段
    private List<GetMarkNextStudentResponse> cachePool = new ArrayList<>(); //存放数据的缓存池

    private ReviewStudentsResponse studentsResponse; //回评列表数据

    private CollectRequest collectRequest;//收藏(取消收藏)请求模型

    //设置布局控件
    private LinearLayout seting_main;
    private RelativeLayout topScoreSetting;
    private ImageView setting_back;
    private LinearLayout pointFiveView;
    private Switch pointFiveSwitch;
    private LinearLayout autoSubmitView;
    private Switch autoSubmitSwitch;
    private LinearLayout topScoreView;
    private GridView topScoreGridView;
    private CheckBox topSocre_0_5;
    private TextView topScoreClear;
    private TextView topScoreDetermine;

    private TopScoreAdapter topScoreAdapter;//置顶分数适配器

    //双栏模式控件
    private LinearLayout soubleLayout; //双栏父布局
    private CheckBox douleScoreCheckBox; //双栏展开/收起来
    private LinearLayout doubleScoreLayout; //双栏标签
    private Button double_markScore; //满分
    private ListView doubleListView; //双栏标签
    private DoubleScoreTableAdapter doubleScoreTableAdapter; //双栏分数适配器
    private List<Integer> integers; //步骤分适配器参数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_mark);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
            }
        };
        Intent intent = getIntent();
        teacherGuid = intent.getStringExtra("teacherGuid");
        taskGuid = intent.getStringExtra("taskGuid");
        status = intent.getIntExtra("status", 2);
        style = intent.getIntExtra("style", 1);
        request = new GetMarkDataRequest();
        request.setTaskGuid(taskGuid);
        request.setTeacherGuid(teacherGuid);
        initView();
        myModel = new MyModel();
        myModel.OtherTask(context, request, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                String json = (String) model;
                if ("{\"success\":200}".equals(json)) {
                    myModel.getMarkData(context, request, new MyCallBack() {
                        @Override
                        public void onSuccess(Object model) {
                            response = (GetMarkDataResponse) model;
                            if (response.getTeacherTask().getMarkSum() < response.getTeacherTask().getTaskCount()) { //当前任务是否全部完成
                                if (response.getTeacherTask().getMarkCount() != 0) { //不是自由阅卷
                                    if (response.getTeacherTask().getMarkNumber() < response.getTeacherTask().getMarkCount()) { //继续阅卷
                                        normalMOde();
                                    } else {
                                        if (style == 1) { //单评模式下自己的任务完成后可以继续批阅任务
                                            if (response.getTeacherTask().getMarkSum() < response.getTeacherTask().getTaskCount()) {
                                                normalMOde(); //正常批阅
                                            } else {
                                                goBackMode(); //回评
                                            }
                                        } else {
                                            goBackMode(); //回评
                                        }
                                    }
                                } else { //自由阅卷
                                    normalMOde();
                                }
                            } else { //任务已完成
                                goBackMode(); //回评
                            }
                        }

                        @Override
                        public void onFailed(String str) {
                            showFailedPage(str);
                        }
                    });
                }
            }

            @Override
            public void onFailed(String str) {

            }
        });
    }

    //正常模式
    private void normalMOde() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingUtil.showDialog(context);
                progressTips.setText(Html.fromHtml("<font color = '#245AD3'>"
                        + (response.getTeacherTask().getMarkNumber() + 1) + "</font>/" + response.getTeacherTask().getMarkCount()));
            }
        });
        myModel.getMarkNextStudent(context, request, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                minLocation = 0;
                collectRequest = new CollectRequest();
                getMarkNextStudentResponse = (GetMarkNextStudentResponse) model;
                cachePool.add(getMarkNextStudentResponse);
                if (response.getTeacherTask().getMarkCount() == 0) { //自由阅卷
                    if (response.getTeacherTask().getMarkSum() < response.getTeacherTask().getTaskCount()) { //当前任务没有阅完
                        getNextStudentCache();
                    }
                } else { //有任务
                    if (response.getTeacherTask().getMarkNumber() < response.getTeacherTask().getMarkCount()) { //自己的任务没有阅完
                        getNextStudentCache();
                    } else {
                        if (style == 1) {
                            if (response.getTeacherTask().getMarkSum() < response.getTeacherTask().getTaskCount()) {
                                getNextStudentCache();
                            }
                        }
                    }
                }
                if (getMarkNextStudentResponse.getData() == null || getMarkNextStudentResponse.getData().getStudentData() == null
                        || getMarkNextStudentResponse.getData().getStudentData().getQuestions() == null) {
                    Looper.prepare();
                    //需要弹出弹出，进行重新获取，暂时先提示用户
                    ToastUtils.showTopToast(context, "题目获取异常，请返回主页重新获取", R.style.Toast_Animation);
                    Looper.loop();
                    return;
                }
                MaxScore = getMaxScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getMarkNextStudentResponse.getData().getStudentData().isCollect()) {
                            collectRequest.setValue(String.valueOf(1));
                            collection.setChecked(true);
                        } else {
                            collectRequest.setValue(String.valueOf(0));
                            collection.setChecked(false);
                        }
                    }
                });
                initSaveData(getMarkNextStudentResponse);
                showSueecssPage(getMarkNextStudentResponse);
            }

            @Override
            public void onFailed(String str) {
                showFailedPage(str);
            }
        });
    }

    //回评模式
    private void goBackMode() {
        Log.e("222222", "goBackMode: 回评阅卷");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingUtil.showDialog(context);
            }
        });
        ReviewStudentsRequest reviews = new ReviewStudentsRequest();
        reviews.setSearchStr("");
        reviews.setTeacherGuid(teacherGuid);
        reviews.setTaskGudi(taskGuid);
        reviews.setStatus("-1"); //0:不异常，1异常，2收藏
        myModel.reviewStudents(context, reviews, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                reviewMode = true;
                studentsResponse = (ReviewStudentsResponse) model;
                final List<ReviewStudentsResponse.DataBean> dataBeanList = studentsResponse.getData();
                GetStudentMarkDataRequest getStudentMarkDataRequest = new GetStudentMarkDataRequest();
                if (studentsResponse.getData() == null || studentsResponse.getData().size() < 1) {
                    ToastUtils.showToast(context, "暂无数据");
                    //暂无数据
                    return;
                } else {
                    getStudentMarkDataRequest.setStudentGuid(studentsResponse.getData().get(dataBeanList.size() - 1).getStudentGuid());
                }
                getStudentMarkDataRequest.setTaskGuid(taskGuid);
                getStudentMarkDataRequest.setTeacherGuid(teacherGuid);
                LOCATION = dataBeanList.size() - 1;
                myModel.getStudentMarkData(context, getStudentMarkDataRequest, new MyCallBack() {
                    @Override
                    public void onSuccess(final Object model) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                minLocation = 0;
                                collectRequest = new CollectRequest();
                                getMarkNextStudentResponse = (GetMarkNextStudentResponse) model;
                                if (getMarkNextStudentResponse.getData() == null || getMarkNextStudentResponse.getData().getStudentData() == null
                                        || getMarkNextStudentResponse.getData().getStudentData().getQuestions() == null) {
                                    Looper.prepare();
                                    //需要弹出弹出，进行重新获取，暂时先提示用户
                                    ToastUtils.showTopToast(context, "题目获取异常，请返回主页重新获取", R.style.Toast_Animation);
                                    Looper.loop();
                                    return;
                                }
                                MaxScore = getMaxScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions());
                                if (getMarkNextStudentResponse.getData().getStudentData().isCollect()) {
                                    collectRequest.setValue(String.valueOf(1));
                                    collection.setChecked(true);
                                } else {
                                    collectRequest.setValue(String.valueOf(0));
                                    collection.setChecked(false);
                                }
                                initUpDateData(getMarkNextStudentResponse);
                                showSueecssPage(getMarkNextStudentResponse);
                                submiss.setEnabled(true);
                                progressTips.setText(Html.fromHtml("<font color = '#245AD3'>" + dataBeanList.size()
                                        + "</font>/" + response.getTeacherTask().getMarkCount()));
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
            public void onFailed(String str) {
                showFailedPage(str);
            }
        });
    }


    //获取当前题目的最高分
    private double getMaxScore(List<GetMarkNextStudentResponse.DataBean.StudentDataBean.QuestionsBean> questionsBeanList) {
        double maxScore = -1;
        for (int i = 0; i < questionsBeanList.size(); i++) {
            maxScore = Math.max(maxScore, questionsBeanList.get(i).getFullScore());
        }
        return maxScore;
    }

    //初始化提交数据
    private void initSaveData(GetMarkNextStudentResponse studentResponse) {
        saveMarkDataBean = new SaveMarkDataBean();
        saveMarkDataBean.setTeacherGuid(teacherGuid);
        saveMarkDataBean.setTaskGuid(taskGuid);
        saveMarkDataBean.setStudentGuid(studentResponse.getData().getStudentData().getStudentGuid());
        questionsBeanList = new ArrayList<>();
        for (int i = 0; i < studentResponse.getData().getStudentData().getQuestions().size(); i++) {
            SaveMarkDataBean.QuestionsBean questionsBean = new SaveMarkDataBean.QuestionsBean();
            questionsBean.setId(String.valueOf(studentResponse.getData().getStudentData().getQuestions().get(i).getId()));
            questionsBean.setMarkScore("-1");
            questionsBean.setStepScore(null);
            questionsBean.setCoordinate(null);
            questionsBeanList.add(questionsBean);
        }
        saveMarkDataBean.setQuestions(questionsBeanList);
    }

    //回评提交数据模型
    private void initUpDateData(GetMarkNextStudentResponse studentResponse) {
        saveMarkDataBean = new SaveMarkDataBean();
        saveMarkDataBean.setTeacherGuid(teacherGuid);
        saveMarkDataBean.setTaskGuid(taskGuid);
        saveMarkDataBean.setStudentGuid(studentResponse.getData().getStudentData().getStudentGuid());
        questionsBeanList = new ArrayList<>();
        for (int i = 0; i < studentResponse.getData().getStudentData().getQuestions().size(); i++) {
            SaveMarkDataBean.QuestionsBean bean = new SaveMarkDataBean.QuestionsBean();
            GetMarkNextStudentResponse.DataBean.StudentDataBean.QuestionsBean questionsBean = studentResponse.getData().getStudentData().getQuestions().get(i);
            bean.setId(String.valueOf(questionsBean.getId()));
            if (questionsBean.getScore() < 0) {
                bean.setMarkScore("-1");
            } else {
                double score = questionsBean.getScore();
                if (score == (int) score) {
                    bean.setMarkScore(String.valueOf((int) score));
                } else {
                    bean.setMarkScore(String.valueOf(score));
                }
            }
            bean.setStepScore(questionsBean.getStepScore());
            bean.setCoordinate(questionsBean.getCoordinate());
            questionsBeanList.add(bean);
        }
        saveMarkDataBean.setQuestions(questionsBeanList);
    }

    private void initView() {
        drawerLayout = findViewById(R.id.drawerLayou);
        main = findViewById(R.id.main);
        reigth = findViewById(R.id.reight);
        number = findViewById(R.id.number);
        number.setOnClickListener(this);
        time = findViewById(R.id.time);
        time.setOnClickListener(this);
        score = findViewById(R.id.score);
        score.setOnClickListener(this);
        reviewListView = findViewById(R.id.reviewListView);
        nodatahint = findViewById(R.id.nodatahint);

        questionScore = findViewById(R.id.questionScore);
        questionNum = findViewById(R.id.questionNum);
        scoreList = findViewById(R.id.scoreList);
        submiss = findViewById(R.id.questionSubmiss);
        submiss.setOnClickListener(this);
        titleNo = findViewById(R.id.titleNo);
        titleNo.setOnClickListener(this);
        mRecyclerView = findViewById(R.id.myHorizontalScrollview);

        progressTips = findViewById(R.id.progressTips);

        sideslip = findViewById(R.id.sideslip);
        sideslip.setOnClickListener(this);
        scrollView = findViewById(R.id.function);

        commentsP = findViewById(R.id.commentsP);
        commentsP.setOnClickListener(this);
        comments = findViewById(R.id.comments);
        comments.setOnClickListener(this);
        eliminateP = findViewById(R.id.eliminateP);
        eliminateP.setOnClickListener(this);
        eliminate = findViewById(R.id.eliminate);
        eliminate.setOnClickListener(this);
        stepScoreP = findViewById(R.id.stepScoreP);
        stepScoreP.setOnClickListener(this);
        stepScore = findViewById(R.id.stepScore);
        stepScore.setOnClickListener(this);
        collectionP = findViewById(R.id.collectionP);
        collectionP.setOnClickListener(this);
        collection = findViewById(R.id.collection);
        collection.setOnClickListener(this);
        historyP = findViewById(R.id.historyP);
        historyP.setOnClickListener(this);
        history = findViewById(R.id.history);
        history.setOnClickListener(this);
        goOnParent = findViewById(R.id.goOnParent);
        goOn = findViewById(R.id.goOn);
        goOn.setOnClickListener(this);
        scoringDatailsP = findViewById(R.id.scoringDatailsP);
        scoringDatailsP.setOnClickListener(this);
        scoringDatails = findViewById(R.id.scoringDatails);
        scoringDatails.setOnClickListener(this);
        settingP = findViewById(R.id.settingP);
        settingP.setOnClickListener(this);
        setting = findViewById(R.id.setting);
        setting.setOnClickListener(this);

        soubleLayout = findViewById(R.id.soubleLayout);
        douleScoreCheckBox = findViewById(R.id.douleScoreCheckBox);
        douleScoreCheckBox.setOnClickListener(this);
        doubleScoreLayout = findViewById(R.id.doubleScoreLayout);
        double_markScore = findViewById(R.id.double_markScore);
        double_markScore.setOnClickListener(this);
        doubleListView = findViewById(R.id.doubleListView);

        //初始化SpenView
        isSpenFeatureEnable = false; //是否支持手写笔
        Spen spen = new Spen();
        try {
            spen.initialize(context);
            isSpenFeatureEnable = spen.isFeatureEnabled(Spen.DEVICE_PEN);
        } catch (Exception e) {
            Toast.makeText(context, "此设备不支持Spen",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            //return;
        }
        tableParent = findViewById(R.id.tableParent);
        spenView = findViewById(R.id.spenView);
        mSpenSimpleSurfaceView = new SpenSurfaceView(context);
        if (mSpenSimpleSurfaceView == null) {
            Toast.makeText(context, "无法创建新的SpenView",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        spenView.addView(mSpenSimpleSurfaceView);

        //获取屏幕尺寸
        Rect rect = Tool.getScreenparameters(this);
        int myCanvasWidth = rect.width() - 80;
        int myCanvasHeigth = rect.height();
        initSpenNoteDoc(myCanvasWidth, myCanvasHeigth);

        mSpenSimpleSurfaceView.setBlankColor(ContextCompat.getColor(context, R.color.colorSpenSimpleSurfaceView));

        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
        penInfo.color = Color.RED;
        penInfo.size = PENSIZE;
        mSpenSimpleSurfaceView.setPenSettingInfo(penInfo);

        if (isSpenFeatureEnable == false) {
            //mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_STROKE);
            Toast.makeText(context, "设备不支持Spen. \n 你可以用手指画笔画", Toast.LENGTH_SHORT).show();
        }
        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
        mSpenSimpleSurfaceView.setZoomListener(new SpenZoomListener() {
            @Override
            public void onZoom(float v, float v1, float v2) {//x轴坐标 y轴坐标  缩放比例
                //最大缩放3，最小缩放0.5
            }
        });
    }

    //添加标签并设置监听
    private void addViewOrLinstener() {
        tableParent.removeAllViews();
        for (int i = 0; i < childLocations.size(); i++) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lp.leftMargin = (int) childLocations.get(i).getX();
            lp.topMargin = (int) childLocations.get(i).getY();
            final TextView tv = new TextView(context);
            tv.setTag(i);
            tv.setText("+" + childLocations.get(i).getTv());
            tv.setTextColor(getResources().getColor(R.color.colorRed));
            tv.setTextSize(30);
            tv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    index = (int) tv.getTag();
                    if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS || event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                startY = event.getRawY();
                                startX = event.getRawX();
                                stepScoreModeScore = 0;
                                for (int i = 0; i < tableParent.getChildCount(); i++) {
                                    TextView textView = (TextView) tableParent.getChildAt(i);
                                    String str = textView.getText().toString().substring(1);
                                    stepScoreModeScore += Double.valueOf(str);
                                }

                                break;
                            case MotionEvent.ACTION_UP:
                                float endY = event.getRawY();
                                float endX = event.getRawX();
                                if (startX - endX >= 50 || startX - endX <= -50) {
                                    stepScoreModeScore = 0;
                                    tableParent.removeView(tv);
                                    childLocations.remove(index);
                                    //删除一个标签后，对标签进行重新添加标识
                                    for (int j = 0; j < childLocations.size(); j++) {
                                        tableParent.getChildAt(j).setTag(j);
                                    }
                                    break;
                                } else if (startY - endY >= 50) {//判断Y轴移动的距离
                                    //+1操作
                                    if (!"5".equals(childLocations.get(index).getTv()) && !"4.5".equals(childLocations.get(index).getTv())) {
                                        if (stepScoreModeScore < stepScoreModeFullScore && stepScoreModeScore + 1 <= stepScoreModeFullScore) {
                                            tv.setText("+" + (Integer.valueOf(childLocations.get(index).getTv()) + 1));
                                            childLocations.get(index).setTv((Integer.valueOf(childLocations.get(index).getTv()) + 1) + "");
                                        } else {
                                            ToastUtils.showToast(context, "操作失败，不能超过题目总分！");
                                        }
                                    } else {
                                        ToastUtils.showToast(context, "标签最高为+5分.");
                                    }
                                } else if (startY - endY < -50) {
                                    //-1操作
                                    if (!"1".equals(childLocations.get(index).getTv()) && !"1.5".equals(childLocations.get(index).getTv())) {
                                        tv.setText("+" + (Integer.valueOf(childLocations.get(index).getTv()) - 1));
                                        childLocations.get(index).setTv((Integer.valueOf(childLocations.get(index).getTv()) - 1) + "");
                                    } else {
                                        ToastUtils.showToast(context, "标签最低为+1分.");
                                    }
                                }
                                startX = 0;
                                startY = 0;
                                break;
                        }
                        stepScoreModeScore = 0;
                        for (int j = 0; j < childLocations.size(); j++) {
                            stepScoreModeScore += Double.valueOf(childLocations.get(j).getTv());
                        }
                        if (stepScoreModeScore == (int) stepScoreModeScore) {
                            questionScore.setText(String.valueOf((int) stepScoreModeScore));
                            saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf((int) stepScoreModeScore));
                            questionNumAdapter.updateScore(minLocation, String.valueOf((int) stepScoreModeScore));
                        } else {
                            questionScore.setText(String.valueOf(stepScoreModeScore));
                            saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(stepScoreModeScore));
                            questionNumAdapter.updateScore(minLocation, String.valueOf(stepScoreModeScore));
                        }
                        questionNumAdapter.notifyDataSetChanged();
                    }
                    return true;
                }
            });
            //适配只有三星p350(samsung)和华为C5(HUAWEI)
            if ("samsung".equals(Tool.getDeviceBrand())) {
                lp.height = 40;
                lp.width = 60;
            } else if ("HUAWEI".equals(Tool.getDeviceBrand())) {
                lp.height = 70;
                lp.width = 130;
            }
            tv.setLayoutParams(lp);
            tableParent.addView(tv);
        }
        if (tableParent.getChildCount() > 0) {
            submiss.setVisibility(View.VISIBLE);
            stepScoreModeScore = 0;
            for (int j = 0; j < childLocations.size(); j++) {
                stepScoreModeScore += Double.valueOf(childLocations.get(j).getTv());
            }
            if (stepScoreModeScore == (int) stepScoreModeScore) {
                questionScore.setText(String.valueOf((int) stepScoreModeScore));
                saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf((int) stepScoreModeScore));
                questionNumAdapter.updateScore(minLocation, String.valueOf((int) stepScoreModeScore));
            } else {
                questionScore.setText(String.valueOf(stepScoreModeScore));
                saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(stepScoreModeScore));
                questionNumAdapter.updateScore(minLocation, String.valueOf(stepScoreModeScore));
            }
            questionNumAdapter.notifyDataSetChanged();
        } else {
            submiss.setVisibility(View.GONE);
        }
    }

    //初始化SpenPageView
    private void initSpenNoteDoc(int w, int h) {
        try {
            Log.e(TAG, "spenView的宽高是：" + w + "  --  " + h);
            mSpenNotDoc = new SpenNoteDoc(context, w, h);
        } catch (IOException e) {
            Toast.makeText(context, "创建NoteDoc失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        mSpenPageDoc = mSpenNotDoc.appendPage();
        mSpenPageDoc.setBackgroundImageMode(SpenPageDoc.BACKGROUND_IMAGE_MODE_FIT);
        mSpenPageDoc.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSpenSimpleSurfaceView));
        mSpenPageDoc.clearHistory();
        mSpenSimpleSurfaceView.setPageDoc(mSpenPageDoc, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        if (mSpenSimpleSurfaceView != null) {
            mSpenSimpleSurfaceView.close();
            mSpenSimpleSurfaceView = null;
        }
        if (mSpenNotDoc != null) {
            try {
                mSpenNotDoc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSpenNotDoc = null;
        }
        LoadingUtil.closeDialog();
    }

    /**
     * 根据分数得到分数列表
     *
     * @param score     当前题目得分
     * @param fullScore 当前题目总分
     * @return
     */
    private ScorePanel getScoreList(double score, double fullScore) {
        Log.e(TAG, "getScoreList: 题目分数：" + fullScore + "   题目得分：" + score + "   是否是步骤分模式" + isStepScore);
        //Math.ceil(12.2)//返回13.0
        //Math.ceil(12.7)//返回13.0
        //Math.ceil(12.0)//返回12.0
        //Math.ceil(96.1)); // 97
        //Math.floor(96.8));// 96
        //Math.round(96.1));// 96
        //Math.round(96.8));// 97
        scorePanel = new ScorePanel();
        List<String> scores = new ArrayList<>();
        List<Boolean> scoresCheck = new ArrayList<>();
        if (!isStepScore) { //不是步骤分分模式
            if (!doubleMode) { //不是双栏模式
                if (PreferencesService.getInstance(context).getPointFive()) { //0.5模式
                    if (!TextUtils.isEmpty(PreferencesService.getInstance(context).getTopScore())) {
                        String[] split = PreferencesService.getInstance(context).getTopScore().split(",");
                        List<String> stringList = new ArrayList<>();
                        List<Boolean> booleanList = new ArrayList<>();
                        for (int i = 0; i < split.length; i++) {
                            if (Double.valueOf(split[i]) <= Double.valueOf(TOTAL)) {
                                stringList.add(split[i]);
                                if (split[i].equals(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore())) {
                                    booleanList.add(true);
                                } else {
                                    booleanList.add(false);
                                }
                            }
                        }
                        scorePanel.setScores(stringList);
                        scorePanel.setScoresCheck(booleanList);
                    } else {
                        for (double i = 0; i < fullScore + 0.5; i += 0.5) {
                            if (i == (int) i) {
                                scores.add(String.valueOf((int) i));
                            } else {
                                scores.add(String.valueOf(i));
                            }
                            if (i == score) {
                                scoresCheck.add(true);
                            } else {
                                scoresCheck.add(false);
                            }
                            scorePanel.setScores(scores);
                            scorePanel.setScoresCheck(scoresCheck);
                        }
                    }
                } else { //整数模式
                    if (!TextUtils.isEmpty(PreferencesService.getInstance(context).getTopScore())) {
                        String[] split = PreferencesService.getInstance(context).getTopScore().split(",");
                        List<String> stringList = new ArrayList<>();
                        List<Boolean> booleanList = new ArrayList<>();
                        for (int i = 0; i < split.length; i++) {
                            if (Double.valueOf(split[i]) <= Double.valueOf(TOTAL)) {
                                double d = Double.valueOf(String.valueOf(split[i]));
                                if (d == (int) d) {
                                    stringList.add(split[i]);
                                    if (split[i].equals(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore())) {
                                        booleanList.add(true);
                                    } else {
                                        booleanList.add(false);
                                    }
                                }
                            }
                        }
                        scorePanel.setScores(stringList);
                        scorePanel.setScoresCheck(booleanList);
                    } else {
                        for (int i = 0; i < Math.floor(fullScore + 1.0); i++) {
                            scores.add(String.valueOf(i));
                            if (i == score) {
                                scoresCheck.add(true);
                            } else {
                                scoresCheck.add(false);
                            }
                        }
                        scorePanel.setScores(scores);
                        scorePanel.setScoresCheck(scoresCheck);
                    }
                }
            } else { //是双栏模式
                double d = Double.valueOf(TOTAL) - score; //总分减去得分的差额(用来展示右侧打分列表的数值)
                if (d > 9.5) {
                    if (PreferencesService.getInstance(context).getPointFive()) { //开启0.5模式
                        for (double i = 0; i < 10; i += 0.5) {
                            if (i == (int) i) {
                                scores.add(String.valueOf((int) i));
                            } else {
                                scores.add(String.valueOf(i));
                            }
                            if (d == i) {
                                if (Double.valueOf(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore()) - score == i) {
                                    scoresCheck.add(true);
                                } else {
                                    scoresCheck.add(false);
                                }
                            } else {
                                scoresCheck.add(false);
                            }
                        }
                        scorePanel.setScores(scores);
                        scorePanel.setScoresCheck(scoresCheck);
                    } else {
                        for (int i = 0; i < 9 + 1; i++) {
                            scores.add(String.valueOf(i));
                            if (d == i) {
                                if (Double.valueOf(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore()) - score == i) {
                                    scoresCheck.add(true);
                                } else {
                                    scoresCheck.add(false);
                                }
                            } else {
                                scoresCheck.add(false);
                            }
                        }
                        scorePanel.setScores(scores);
                        scorePanel.setScoresCheck(scoresCheck);
                    }
                } else {
                    if (PreferencesService.getInstance(context).getPointFive()) { //开启0.5模式
                        for (double i = 0; i < d + 0.5; i += 0.5) {
                            if (i == (int) i) {
                                scores.add(String.valueOf((int) i));
                            } else {
                                scores.add(String.valueOf(i));
                            }
                            if (d == i) {
                                if (Double.valueOf(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore()) - score == i) {
                                    scoresCheck.add(true);
                                } else {
                                    scoresCheck.add(false);
                                }
                            } else {
                                scoresCheck.add(false);
                            }
                        }
                        scorePanel.setScores(scores);
                        scorePanel.setScoresCheck(scoresCheck);
                    } else {
                        for (int i = 0; i < d + 1; i++) {
                            scores.add(String.valueOf(i));
                            if (d == i) {
                                if (Double.valueOf(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore()) - score == i) {
                                    scoresCheck.add(true);
                                } else {
                                    scoresCheck.add(false);
                                }
                            } else {
                                scoresCheck.add(false);
                            }
                        }
                        scorePanel.setScores(scores);
                        scorePanel.setScoresCheck(scoresCheck);
                    }
                }
            }
        } else { //是步骤分模式
            if ("英语".equals(response.getTestpaper().getPaperName())) { //是否是英语
                for (int i = 0; i < 6; i++) {
                    if (i == 1) {
                        scores.add(String.valueOf(i));
                        scoresCheck.add(false);
                        scores.add(String.valueOf(1.5));
                        scoresCheck.add(false);
                    } else {
                        scores.add(String.valueOf(i));
                        scoresCheck.add(false);
                    }
                }
            } else {
                for (int i = 0; i < 6; i++) {
                    scores.add(String.valueOf(i));
                    scoresCheck.add(false);
                }
            }
            if (!TextUtils.isEmpty(tableStepScore)) { //步骤分模式不需要刷新列表
                for (int i = 0; i < scores.size(); i++) {
                    if (tableStepScore.equals(scores.get(i))) {
                        scoresCheck.set(i, true);
                        break;
                    }
                }
            }
            scorePanel.setScores(scores);
            scorePanel.setScoresCheck(scoresCheck);
        }
        return scorePanel;
    }


    //学生数据获取成功
    public void showSueecssPage(final GetMarkNextStudentResponse nextStudentResponse) {
        imageDataList = new ArrayList<>();
        for (int i = 0; i < nextStudentResponse.getData().getImageArr().size(); i++) {
            ImageData imageData1 = new ImageData(nextStudentResponse.getData().getImageArr().get(i).getWidth(),
                    nextStudentResponse.getData().getImageArr().get(i).getHeigth());
            imageDataList.add(imageData1);
        }
        Tool.base64ToBitmap(nextStudentResponse.getData().getImageArr(), nextStudentResponse.getData().getStudentData().getQuestions().get(0).getId(), new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                mSpenPageDoc.removeAllObject();
                LocalImageData localImageData = (LocalImageData) model;
                Bitmap bitmap = BitmapFactory.decodeFile(localImageData.getPath());
                imageData = new ImageData(bitmap.getWidth(), bitmap.getHeight());
                Log.e(TAG, "本地图片的宽: " + bitmap.getWidth() + "  高:" + bitmap.getHeight());
                Log.e(TAG, "图片保存到本地：" + localImageData.getPath());

                Rect rect = Tool.getScreenparameters(MarkingActivity.this);
                Log.e(TAG, "当前屏幕的宽高是:" + rect.width() + "_" + rect.height());
                if (bitmap.getHeight() > rect.height() - Tool.getStatusBarHeight(context) || bitmap.getWidth() > rect.width() - 80) { //图片比屏幕大
                    if (bitmap.getWidth() > bitmap.getHeight()) { //宽图
                        initSpenNoteDoc(rect.width() - 80, bitmap.getHeight() * (rect.width() - 80) / bitmap.getWidth());
                    } else if (bitmap.getWidth() < bitmap.getHeight()) { //长图
                        if ("语文".equals(response.getTestpaper().getPaperName()) && bitmap.getHeight() > 2000) {
                            initSpenNoteDoc(1024 - 80, bitmap.getHeight() * (1024 - 80) / bitmap.getWidth());
                        } else {
                            initSpenNoteDoc(bitmap.getWidth() * rect.height() / bitmap.getHeight(), rect.height());
                        }
                    } else {
                        //可能存在方图，暂时不处理
                    }
                } else { //图片没有屏幕大
                    Log.e(TAG, "图片没有屏幕大 ");
                    //TODO 下面注释代码勿删
                    initSpenNoteDoc(bitmap.getWidth(), bitmap.getHeight());
                    //自动填充屏幕大小
                    if (bitmap.getWidth() > bitmap.getHeight()) { //宽图
                        mSpenSimpleSurfaceView.setZoom(0, 0, (rect.width() - 80) * 1f / bitmap.getWidth());
                        Log.e(TAG, "onSuccess1: " + (rect.width() / bitmap.getWidth()));
                    } else { //高图
                        mSpenSimpleSurfaceView.setZoom(0, 0, rect.height() * 1f / bitmap.getHeight());
                        Log.e(TAG, "onSuccess2: " + rect.height() / bitmap.getHeight());
                    }
                }
                //scale = mSpenPageDoc.getWidth() * 10 * 0.1f / bitmap.getWidth();
                scale = bitmap.getWidth() * 10 * 0.1f / mSpenPageDoc.getWidth();

                //设置背景图片
                mSpenPageDoc.setBackgroundImage(localImageData.getPath());
                mSpenSimpleSurfaceView.update();
                LoadingUtil.closeDialog();
                //业务逻辑修改，当前任务统计完成绩后依然可以随时回评修改，发布导出成绩时需要重新统计
                /*if (status == 3) {
                    ToastUtils.showTopToast(context, "当前考试已统计成绩，无法进行修改数据", R.style.Toast_Animation);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commentsP.setVisibility(View.GONE);
                            eliminateP.setVisibility(View.GONE);
                            stepScoreP.setVisibility(View.GONE);
                            collectionP.setVisibility(View.GONE);
                            goOnParent.setVisibility(View.GONE);
                            submiss.setVisibility(View.GONE);
                            if (scoreAdapter != null) {
                                scoreAdapter.clickEnable(false);
                                scoreAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }*/
            }

            @Override
            public void onFailed(String str) {
                showFailedPage(str);
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tableParent.removeAllViews();
                if (childLocations != null) {
                    childLocations.clear();
                }
            }
        });
        if (!TextUtils.isEmpty(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getCoordinate())
                && nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getCoordinate().length() > 10) {
            addStroke(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getCoordinate());
            isStepScore = false;
            comments.setChecked(true);
            comments.setTextColor(getResources().getColor(R.color.colorBlue));
            stepScore.setChecked(false);
            stepScore.setTextColor(getResources().getColor(R.color.colorScoreItem));
            mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_STROKE);
            if (reviewMode) {
                if (childLocations != null) {
                    childLocations.clear();
                }
                mSpenSimpleSurfaceView.setTouchListener(null);
            }
            PreferencesService.getInstance(context).saveAutoSubmit(true);
        } else if (!TextUtils.isEmpty(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getStepScore())
                && nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getStepScore().length() > 10) {
            addStepScore(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getStepScore());
            isStepScore = true;
            comments.setChecked(false);
            comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
            stepScore.setChecked(true);
            stepScore.setTextColor(getResources().getColor(R.color.colorBlue));
            mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
            if (reviewMode) {
                mSpenSimpleSurfaceView.setTouchListener(this);
            }
            PreferencesService.getInstance(context).saveAutoSubmit(false);
        }
        initUI(nextStudentResponse);
    }

    //初始化当前页面UI
    private void initUI(final GetMarkNextStudentResponse getMarkNextStudentResponse1) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (reviewMode) {
                    if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount()
                            && response.getTeacherTask().getTaskCount() != response.getTeacherTask().getMarkSum()) {
                        goOnParent.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(saveMarkDataBean.getQuestions().get(minLocation).getCoordinate()) && TextUtils.isEmpty(saveMarkDataBean.getQuestions().get(minLocation).getStepScore())) {
                            comments.setChecked(false);
                            comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
                            stepScore.setChecked(false);
                            stepScore.setTextColor(getResources().getColor(R.color.colorScoreItem));
                            isStepScore = false;
                        }
                    } else {
                        goOnParent.setVisibility(View.GONE);
                    }
                } else {
                    goOnParent.setVisibility(View.GONE);
                }
                strings = getQuestions(getMarkNextStudentResponse1.getData().getStudentData().getQuestions());
                doubleMode = false;
                //双栏模式开关是否开启
                if (getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getFullScore() >= 10) {
                    if (!isStepScore) {
                        soubleLayout.setVisibility(View.VISIBLE);
                        douleScoreCheckBox.setChecked(false);
                        doubleScoreLayout.setVisibility(View.GONE);
                    } else {
                        soubleLayout.setVisibility(View.GONE);
                        douleScoreCheckBox.setChecked(false);
                        doubleScoreLayout.setVisibility(View.GONE);
                    }
                } else {
                    soubleLayout.setVisibility(View.GONE);
                    douleScoreCheckBox.setChecked(false);
                    doubleScoreLayout.setVisibility(View.GONE);
                }

                //初始化题号选择列表
                questionNumAdapter = new QuestionNumAdapter(context, strings, scores);
                linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                questionNumAdapter.setmOnclickListener(new QuestionNumAdapter.MOnclickListener() {
                    @Override
                    public void OnClickLener(int positon) {

                        if (strings.size() == 1 || minLocation == positon) {
                            questionNumAdapter.setPos(positon);
                            questionNumAdapter.notifyDataSetChanged();
                            //只有一个题号不需要进行操作
                            return;
                        }

                        int loc = minLocation;
                        saveNowPageData(loc);
                        mSpenPageDoc.removeAllObject();
                        mSpenSimpleSurfaceView.update();
                        tableParent.removeAllViews();
                        if (childLocations != null) {
                            childLocations.clear();
                        }
                        stepScoreModeScore = -1;

                        // 小题切换
                        minLocation = positon;
                        questionNum.setText(strings.get(positon));

                        if ("-1".equals(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore())) {
                            questionScore.setText(String.valueOf(0));
                        } else {
                            double markScore = Double.valueOf(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore());
                            if (markScore == (int) markScore) {
                                questionScore.setText(String.valueOf((int) markScore));
                            } else {
                                questionScore.setText(String.valueOf(markScore));
                            }
                        }
                        questionNumAdapter.setPos(positon);
                        questionNumAdapter.notifyDataSetChanged();

                        double fullScore = getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getFullScore();
                        if (fullScore == (int) fullScore) {
                            TOTAL = String.valueOf((int) fullScore);
                            PreferencesService.getInstance(context).savePointFive(false);
                        } else {
                            TOTAL = String.valueOf(fullScore);
                            PreferencesService.getInstance(context).savePointFive(true);
                        }

                        doubleMode = false;
                        douleScoreCheckBox.setChecked(false);
                        if (fullScore > 9.5) {
                            soubleLayout.setVisibility(View.VISIBLE);
                            doubleScoreLayout.setVisibility(View.GONE);
                        } else {
                            soubleLayout.setVisibility(View.GONE);
                            doubleScoreLayout.setVisibility(View.GONE);
                        }

                        if (!"-1".equals(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore())) {
                            for (int i = 0; i < scorePanel.getScores().size(); i++) {
                                if (saveMarkDataBean.getQuestions().get(minLocation).getMarkScore().equals(scorePanel.getScores().get(i))) {
                                    scoreAdapter.setScoreCheck(i);
                                    break;
                                }
                            }
                            //添加笔记、步骤分
                            if (!TextUtils.isEmpty(saveMarkDataBean.getQuestions().get(minLocation).getCoordinate())) {
                                addStroke(saveMarkDataBean.getQuestions().get(minLocation).getCoordinate());
                                comments.setChecked(true);
                                comments.setTextColor(getResources().getColor(R.color.colorBlue));
                                stepScore.setChecked(false);
                                stepScore.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                isStepScore = false;
                                mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_STROKE);
                            } else if (!TextUtils.isEmpty(saveMarkDataBean.getQuestions().get(minLocation).getStepScore())) {
                                //步骤分
                                addStepScore(saveMarkDataBean.getQuestions().get(minLocation).getStepScore());
                                mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                                if (tableParent.getChildCount() > 0) {
                                    isStepScore = true;
                                    comments.setChecked(false);
                                    comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                    stepScore.setChecked(true);
                                    stepScore.setTextColor(getResources().getColor(R.color.colorBlue));
                                } else {
                                    isStepScore = false;
                                    comments.setChecked(false);
                                    comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                    stepScore.setChecked(false);
                                    stepScore.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                }
                            } else {
                                isStepScore = false;
                                mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                                comments.setChecked(false);
                                comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                stepScore.setChecked(false);
                                stepScore.setTextColor(getResources().getColor(R.color.colorScoreItem));
                            }
                            scoreAdapter.updataData(getScoreList(Double.valueOf(saveMarkDataBean.getQuestions().get(positon).getMarkScore()),
                                    getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(positon).getFullScore()));
                        } else {
                            scoreAdapter.updataData(getScoreList(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(positon).getScore(),
                                    getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(positon).getFullScore()));
                        }
                        scoreAdapter.setStepScore(isStepScore);
                        scoreAdapter.notifyDataSetChanged();
                    }
                });
                mRecyclerView.setAdapter(questionNumAdapter);
                double fullScore = getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(0).getFullScore();
                if (fullScore == (int) fullScore) {
                    TOTAL = String.valueOf((int) fullScore);
                    PreferencesService.getInstance(context).savePointFive(false);
                } else {
                    TOTAL = String.valueOf(fullScore);
                    PreferencesService.getInstance(context).savePointFive(true);
                }

                //当前小题的得分，初始化分数显示
                if ("-1.0".equals(String.valueOf(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getScore()))) {
                    SCORE = "-1";
                    String[] score = String.valueOf(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getScore()).split("\\.");
                    if ("0".equals(score[1])) {
                        if ("-1".equals(score[0])) {
                            SCORE = "-1";
                            questionScore.setText(String.valueOf(0));
                        } else {
                            SCORE = score[0];
                            questionScore.setText(score[0]);
                        }
                    } else {
                        SCORE = String.valueOf(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getScore());
                        questionScore.setText(SCORE);
                    }
                } else {
                    String[] split = String.valueOf(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getScore()).split("\\.");
                    if (split != null && split.length > 0) {
                        if ("0".equals(split[1])) {
                            SCORE = split[0];
                            questionScore.setText(split[0]);
                        } else {
                            SCORE = String.valueOf(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getScore());
                            questionScore.setText(SCORE);
                        }
                    } else {
                        SCORE = String.valueOf(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getScore());
                        questionScore.setText(SCORE);
                    }
                }

                if ("0".equals(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getSubNumber())) {
                    questionNum.setText(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getNumber() + "题");
                } else {
                    questionNum.setText(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getNumber() + "-"
                            + getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getSubNumber() + "题");
                }

                scorePanel = getScoreList(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getScore(),
                        getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getFullScore());

                scoreAdapter = new ScoreAdapter(context, scorePanel);
                scoreAdapter.setStepScore(isStepScore);

                if (!"-1".equals(SCORE) && !isStepScore) {
                    for (int i = 0; i < scorePanel.getScores().size(); i++) {
                        if (scorePanel.getScores().get(i).equals(SCORE)) {
                            scoreAdapter.setScoreCheck(i);
                            break;
                        }
                    }
                }

                scoreAdapter.setmClickListener(new ScoreAdapter.ClickListener() {
                    @Override
                    public void myOnclickListenet(final int position) {
                        if (System.currentTimeMillis() - scoreClickTime < 1000) {
                            return;
                        } else {
                            scoreClickTime = System.currentTimeMillis();
                        }
                        questionScore.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!isStepScore) {
                                    if (doubleMode) {
                                        if (position == 0) {
                                            doubleScoreTableAdapter.setPos(-1);
                                            doubleScoreTableAdapter.notifyDataSetChanged();
                                            SCORE = "0";
                                        } else {
                                            double dobl = Integer.valueOf(doubleScore) + Double.valueOf(scorePanel.getScores().get(position));
                                            if (dobl == (int) dobl) {
                                                SCORE = String.valueOf((int) dobl);
                                            } else {
                                                SCORE = String.valueOf(dobl);
                                            }
                                        }
                                        questionScore.setText(SCORE);
                                        saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(SCORE);
                                        questionNumAdapter.updateScore(minLocation, SCORE);
                                    } else {
                                        questionScore.setText(scorePanel.getScores().get(position));
                                        saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(scorePanel.getScores().get(position)));
                                        questionNumAdapter.updateScore(minLocation, scorePanel.getScores().get(position));
                                    }

                                    questionNumAdapter.notifyDataSetChanged();
                                    saveNowPageData(minLocation);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(200);
                                                //判断是否可以自动提交了
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        autoSubmitData();
                                                    }
                                                });
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                } else {
                                    if (position == 0) {
                                        saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(0));
                                        submiss.setVisibility(View.VISIBLE);
                                        tableStepScore = "0";
                                        questionNumAdapter.updateScore(minLocation, "0");
                                        questionNumAdapter.notifyDataSetChanged();
                                        saveMarkDataBean.getQuestions().get(minLocation).setStepScore("");
                                        tableParent.removeAllViews();
                                        childLocations.clear();
                                        stepScoreModeScore = 0;
                                    } else {
                                        tableStepScore = scorePanel.getScores().get(position);
                                    }
                                }
                                scoreAdapter.setScoreCheck(position);
                                scoreAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                scoreList.setAdapter(scoreAdapter);
            }
        });
    }

    //添加步骤分数据
    private void addStepScore(String stepScore) {
        tableParent.setVisibility(View.VISIBLE);
        Log.e(TAG, "addStepScore: 设置步骤分数据");
        List<StepScore> stepScores = new Gson().fromJson(stepScore, new TypeToken<List<StepScore>>() {
        }.getType());
        if (stepScores == null || stepScores.size() == 0) {
            return;
        }
        //选择步骤分的第一个标签的数据为默认显示选中的标签选项
        double d = stepScores.get(0).getParams();
        if (d != (int) d) {
            tableStepScore = String.valueOf(d);
        } else {
            tableStepScore = String.valueOf((int) d);
        }
        childLocations = new ArrayList<>();
        for (int i = 0; i < stepScores.size(); i++) {
            /**
             * 对号 tick、
             * 半对号 halfTick、
             * 错号 cross、
             * 文本  text、
             * 步骤分 stepPoints
             */
            if ("stepPoints".equals(stepScores.get(i).getField())) {
                ChildLocation location = new ChildLocation();

                float locaX = mSpenPageDoc.getWidth() * stepScores.get(i).getX() / imageData.getWidth();
                float locaY = mSpenPageDoc.getHeight() * stepScores.get(i).getY() / imageData.getHeight();

                float padImgX = locaX + (mSpenSimpleSurfaceView.getWidth() - mSpenPageDoc.getWidth()) / 2;
                float padImgY = locaY + (mSpenSimpleSurfaceView.getHeight() - mSpenPageDoc.getHeight()) / 2;

                location.setX(padImgX);
                location.setY(padImgY);
                double v = stepScores.get(i).getParams();
                if (v == (int) v) {
                    location.setTv(String.valueOf((int) v));
                } else {
                    location.setTv(String.valueOf(v));
                }
                childLocations.add(location);
            } else {
                Log.e(TAG, "addStepScore:不支持的格式类型");
            }
        }
        addViewOrLinstener();
    }

    //是否可以自动提交
    private void autoSubmitData() {
        //判断当前有没有遗漏的题
        boolean isOver = true; //是否批阅完成
        for (int i = 0; i < saveMarkDataBean.getQuestions().size(); i++) {
            if ("-1".equals(saveMarkDataBean.getQuestions().get(i).getMarkScore())) {
                isOver = false;
                minLocation = i;
                break;
            }
        }
        if (isOver) {
            //自动提交
            if (PreferencesService.getInstance(context).getAutoSubmit()) {
                submitData();
            } else {
                submiss.setVisibility(View.VISIBLE);
            }
        } else {
            submiss.setVisibility(View.GONE);
            mSpenPageDoc.removeAllObject();
            mSpenSimpleSurfaceView.update();
            mRecyclerView.scrollToPosition(minLocation);
            //moveToPosition(linearLayoutManager, minLocation);

            //更新题号列表和分数
            questionNum.setText(strings.get(minLocation));
            if ("-1".equals(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore())) {
                questionScore.setText(String.valueOf(0));
            } else {
                double markScore = Double.valueOf(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore());
                if (markScore == (int) markScore) {
                    questionScore.setText(String.valueOf((int) markScore));
                } else {
                    questionScore.setText(String.valueOf(markScore));
                }
            }
            questionNumAdapter.setPos(minLocation);
            questionNumAdapter.notifyDataSetChanged();

            if (getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore() >= 10) {
                soubleLayout.setVisibility(View.VISIBLE);
                doubleScore = "0";
                if (doubleScoreTableAdapter != null) {
                    doubleScoreTableAdapter.update(getQuestionScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore()));
                    doubleScoreTableAdapter.setPos(-1);
                    doubleScoreTableAdapter.notifyDataSetChanged();
                }
            } else {
                soubleLayout.setVisibility(View.GONE);
                doubleMode = false;
            }

            double fullScore = getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore();
            if (fullScore == (int) fullScore) {
                TOTAL = String.valueOf((int) fullScore);
            } else {
                TOTAL = String.valueOf(fullScore);
            }

            //更新分数列表
            scoreAdapter.setScoreCheck(-1);
            scoreAdapter.updataData(getScoreList(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore(),
                    getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore()));
            scoreAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 保存当前页面数据
     *
     * @param loc 当前页面小题号的位置(合并题目)
     */

    private void saveNowPageData(int loc) {
        if (!"-1".equals(saveMarkDataBean.getQuestions().get(loc).getMarkScore())) {
            if (mSpenPageDoc.getObjectList().size() != 0) {
                getPageDocObject();
                /*mSpenPageDoc.removeAllObject();
                mSpenSimpleSurfaceView.update();*/
            } else {
                if (tableParent.getChildCount() > 0) {
                    getStepScoreData();
                } else {
                    saveMarkDataBean.getQuestions().get(loc).setStepScore("");
                }
            }
        } else {
            // TODO 当前分数为-1，不对笔迹和步骤分数据进行保存
        }
    }

    //给SpenPageDoc添加笔迹数据
    private void addStroke(String s) {
        Log.e(TAG, "addStroke: 添加笔迹数据  " + s);
        List<SpenStroke> list = new Gson().fromJson(s, new TypeToken<List<SpenStroke>>() {
        }.getType());
        if (list == null || list.size() == 0) {
            return;
        }
        ArrayList<SpenObjectBase> baseList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            SpenObjectStroke base = new SpenObjectStroke();
            base.setPenName(SpenPenManager.SPEN_INK_PEN);
            base.setColor(Color.RED);
            base.setPenSize(PENSIZE);
            float[] yali = new float[list.get(i).getPointS().length];
            int[] time = new int[list.get(i).getPointS().length];
            for (int j = 0; j < list.get(i).getPointS().length; j++) {
                yali[j] = 0.3f;
                time[j] = (int) System.currentTimeMillis();
            }
            PointF[] pointS = list.get(i).getPointS();
            PointF[] pointFS = new PointF[pointS.length];
            for (int j = 0; j < pointS.length; j++) {
                PointF pointF = new PointF();
                pointF.set(pointS[j].x / scale, pointS[j].y / scale);
                pointFS[j] = pointF;
            }
            base.setPoints(pointFS, yali, time);
            baseList.add(base);
        }
        mSpenPageDoc.appendObjectList(baseList);
        mSpenSimpleSurfaceView.update();
    }

    //获取当前数据的题目列表
    private List<String> getQuestions(List<GetMarkNextStudentResponse.DataBean.StudentDataBean.QuestionsBean> questions) {
        List<String> questionNos = new ArrayList<>();
        scores = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            GetMarkNextStudentResponse.DataBean.StudentDataBean.QuestionsBean bean = questions.get(i);
            double score = bean.getScore();
            String score1;
            if (score == (int) score) {
                score1 = String.valueOf((int) score);
            } else {
                score1 = String.valueOf(score);
            }
            if (TextUtils.isEmpty(bean.getSubNumber()) || "0".equals(bean.getSubNumber())) {
                questionNos.add(bean.getNumber() + "题");
            } else {
                questionNos.add(bean.getNumber() + "-" + bean.getSubNumber() + "题");
            }
            if (reviewMode) {
                scores.add(score1);
            } else {
                scores.add("-1");
            }
        }
        return questionNos;
    }

    //任务获取失败
    public void showFailedPage(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingUtil.closeDialog();
                LogUtils.logE("showFailedPage", str);
                //failed to connect to riyun.lexuewang.cn/116.62.133.77 (port 8002) after 10000ms
                if (str.contains("failed to connect to") && str.contains("after 10000ms")) {
                    ToastUtils.showToast(context, "请求服务器超时");
                } else {
                    ToastUtils.showToast(context, str);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleNo: //小题号收起
                if (titleNo.isChecked()) {
                    titleNo.setChecked(true);
                    titleNo.setBackground(getResources().getDrawable(R.drawable.open));
                    ObjectAnimator.ofFloat(mRecyclerView, "translationX", 0, -1024)
                            .setDuration(200)
                            .start();
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    titleNo.setChecked(false);
                    titleNo.setBackground(getResources().getDrawable(R.drawable.shut));
                    ObjectAnimator.ofFloat(mRecyclerView, "translationX", -1024, 0)
                            .setDuration(100)
                            .start();
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.sideslip: //功能区收起
                if (sideslip.isChecked()) {
                    sideslip.setChecked(true);
                    sideslip.setBackground(getResources().getDrawable(R.drawable.open));
                    ObjectAnimator.ofFloat(scrollView, "translationX", 0, -1024)
                            .setDuration(200)
                            .start();
                    scrollView.setVisibility(View.GONE);
                } else {
                    sideslip.setChecked(false);
                    sideslip.setBackground(getResources().getDrawable(R.drawable.shut));
                    ObjectAnimator.ofFloat(scrollView, "translationX", -1024, 0)
                            .setDuration(100)
                            .start();
                    scrollView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.number: //id排序
                break;
            case R.id.time: //时间排序
                break;
            case R.id.score: //分数排序
                break;
            //case R.id.commentsP: //批注
            case R.id.comments: //批注
                if (stepScore.isChecked()) {
                    comments.setChecked(false);
                    ToastUtils.showToast(context, "当前模式无法开启批注");
                    return;
                }
                mSpenSimpleSurfaceView.setTouchListener(null);
                if (comments.isChecked()) {
                    ToastUtils.showToast(context, "开启批注");
                    comments.setChecked(true);
                    tableParent.removeAllViews();
                    tableParent.setVisibility(View.GONE);
                    comments.setTextColor(getResources().getColor(R.color.colorBlue));
                    if (isSpenFeatureEnable) {
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_STROKE);
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_STROKE);
                    } else {
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_STROKE);
                    }
                } else {
                    ToastUtils.showToast(context, "关闭批注");
                    comments.setChecked(false);
                    comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_NONE);
                }
                break;
            case R.id.eliminateP: //清除笔记
            case R.id.eliminate: //清除笔记
                if (mSpenPageDoc.getObjectList().size() == 0) {
                    ToastUtils.showToast(context, "未找到笔记");
                } else {
                    mSpenPageDoc.removeAllObject();
                    mSpenSimpleSurfaceView.update();
                    ToastUtils.showToast(context, "已清除");
                    saveMarkDataBean.getQuestions().get(minLocation).setCoordinate("");
                }
                break;
            //case R.id.stepScoreP: //步骤分
            case R.id.stepScore: //步骤分
                if ("语文".equals(response.getTestpaper().getPaperName())) {
                    if (reviewMode) {
                        if (getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore() >= 50) {
                            ToastUtils.showToast(context, "该题目不支持步骤分");
                            stepScore.setChecked(false);
                            return;
                        }
                    } else {
                        if (getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore() >= 50) {
                            ToastUtils.showToast(context, "该题目不支持步骤分");
                            stepScore.setChecked(false);
                            return;
                        }
                    }
                }
                if (comments.isChecked()) {
                    ToastUtils.showToast(context, "当前模式无法开启步骤分");
                    stepScore.setChecked(false);
                    return;
                }
                /*if (getMarkNextStudentResponse.getData().getStudentData().getQuestions().size() > 1) {
                    ToastUtils.showToast(context, "该题目不支持步骤分");
                    stepScore.setChecked(false);
                    return;
                }*/
                questionNumAdapter.updateScore(minLocation, String.valueOf(-1));
                questionNumAdapter.notifyDataSetChanged();
                if (stepScore.isChecked()) {
                    mSpenSimpleSurfaceView.setTouchListener(this);
                    stepScoreModeScore = 0;
                    ToastUtils.showToast(context, "开启步骤分");
                    mSpenPageDoc.removeAllObject();
                    mSpenSimpleSurfaceView.update();
                    childLocations = new ArrayList<>();
                    PreferencesService.getInstance(context).saveAutoSubmit(false);
                    isStepScore = true;
                    mSpenSimpleSurfaceView.setMaxZoomRatio(1);
                    mSpenSimpleSurfaceView.setMinZoomRatio(1);
                    mSpenSimpleSurfaceView.setZoom(0, 0, 1);
                    tableParent.setVisibility(View.VISIBLE);
                    soubleLayout.setVisibility(View.GONE);
                    submiss.setVisibility(View.GONE);
                    tableStepScore = "";
                    if (doubleScoreTableAdapter != null) {
                        doubleScoreTableAdapter.setPos(-1);
                        doubleScoreTableAdapter.notifyDataSetChanged();
                    }
                    scoreAdapter.setScoreCheck(-1);
                    scoreAdapter.notifyDataSetChanged();
                    stepScore.setChecked(true);
                    stepScore.setTextColor(getResources().getColor(R.color.colorBlue));
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                    if (reviewMode) {
                        stepScoreModeFullScore = getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore();
                        scoreAdapter.updataData(getScoreList(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore(), 0));
                    } else {
                        stepScoreModeFullScore = getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore();
                        scoreAdapter.updataData(getScoreList(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore(), 0));
                    }
                    scoreAdapter.setStepScore(true);
                    scoreAdapter.setScoreCheck(-1);
                    saveMarkDataBean.getQuestions().get(minLocation).setMarkScore("-1");
                    saveMarkDataBean.getQuestions().get(minLocation).setCoordinate("");
                    questionScore.setText(String.valueOf(0));
                } else {
                    mSpenSimpleSurfaceView.setTouchListener(null);
                    ToastUtils.showToast(context, "关闭步骤分");
                    childLocations.clear();
                    tableStepScore = null;
                    isStepScore = false;
                    if (getMarkNextStudentResponse.getData().getStudentData().getQuestions().size() == 1) {
                        PreferencesService.getInstance(context).saveAutoSubmit(true);
                    } else {
                        PreferencesService.getInstance(context).saveAutoSubmit(false);
                    }
                    submiss.setVisibility(View.GONE);
                    mSpenSimpleSurfaceView.setMaxZoomRatio(3);
                    mSpenSimpleSurfaceView.setMinZoomRatio(0.5f);
                    tableParent.setVisibility(View.GONE);
                    if (getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore() > 9) {
                        soubleLayout.setVisibility(View.VISIBLE);
                    } else {
                        soubleLayout.setVisibility(View.GONE);
                    }
                    tableParent.removeAllViews();
                    stepScore.setChecked(false);
                    saveMarkDataBean.getQuestions().get(minLocation).setStepScore("");
                    stepScore.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                    saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(-1));
                    scoreAdapter.updataData(getScoreList(-1, getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore()));
                    /*if (reviewMode) {
                        scoreAdapter.updataData(getScoreList(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore(),
                                getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore()));
                    } else {
                        scoreAdapter.updataData(getScoreList(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore(),
                                getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore()));
                    }*/
                    scoreAdapter.setStepScore(false);
                    scoreAdapter.setScoreCheck(-1);
                    questionScore.setText(String.valueOf(0));
                }
                scoreAdapter.notifyDataSetChanged();
                break;
            case R.id.collectionP: //收藏
            case R.id.collection: //收藏
                collectRequest.setStudentGuid(getMarkNextStudentResponse.getData().getStudentData().getStudentGuid());
                collectRequest.setTaskGuid(taskGuid);
                int type = 0;
                if ("0".equals(collectRequest.getValue())) {
                    collectRequest.setValue(String.valueOf(1));
                    type = 1;
                } else {
                    collectRequest.setValue(String.valueOf(0));
                    type = 0;
                }

                final int finalType = type;
                myModel.collectQuestion(context, collectRequest, new MyCallBack() {
                    @Override
                    public void onSuccess(Object model) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalType == 0) {
                                    ToastUtils.showToast(context, "取消收藏");
                                    collection.setChecked(false);
                                } else {
                                    ToastUtils.showToast(context, "收藏成功");
                                    collection.setChecked(true);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailed(String str) {
                        showFailedPage(str);
                    }
                });
                break;
            case R.id.scoringDatailsP: //评分详情
            case R.id.scoringDatails: //评分详情
                showScoreDatails();
                break;
            case R.id.historyP: //回评
            case R.id.history: //回评
                showReviewList();
                break;
            case R.id.goOn: //继续阅卷
                LoadingUtil.showDialog(context);
                tableParent.removeAllViews();
                if (childLocations != null)
                    childLocations.clear();
                getNewStudent();
                break;
            case R.id.settingP: //设置
            case R.id.setting: //设置
                showSettingPage();
                break;
            case R.id.questionSubmiss: //提交
                //是否存在没有赋分的题目
                boolean notNull = false;
                int num = 0;
                for (int i = 0; i < saveMarkDataBean.getQuestions().size(); i++) {
                    if ("-1".equals(saveMarkDataBean.getQuestions().get(i).getMarkScore())) {
                        notNull = true;
                        num = i;
                        break;
                    }
                }
                if ("-1".equals(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore())) {
                    ToastUtils.showToast(context, "请先进行赋分操作");
                    return;
                } else if (notNull) {
                    ToastUtils.showToast(context, "请先对第" + (num + 1) + "个小题进行赋分");
                    return;
                }
                submitData();
                break;
            case R.id.setting_back:
                setting_back.setVisibility(View.GONE);
                topScoreSetting.setVisibility(View.GONE);
                seting_main.setVisibility(View.VISIBLE);
                //保存设置的置顶分数
                PreferencesService.getInstance(context).saveTopScore(topScoreAdapter.getTopScoreDate());
                break;
            case R.id.pointFiveView: //0.5父布局
                if (pointFiveSwitch.isChecked()) {
                    pointFiveSwitch.setChecked(false);
                    ToastUtils.showTopToast(context, Html.fromHtml("<font color = '#FF0000'>关闭</font>0.5赋分"), R.style.Toast_Animation);
                } else {
                    pointFiveSwitch.setChecked(true);
                    ToastUtils.showTopToast(context, Html.fromHtml("<font color = '#FF0000'>开启</font>0.5赋分"), R.style.Toast_Animation);
                }
                PreferencesService.getInstance(context).savePointFive(pointFiveSwitch.isChecked());
                break;
            case R.id.pointFiveSwitch: //0.5switch
                if (pointFiveSwitch.isChecked()) {
                    pointFiveSwitch.setChecked(true);
                    ToastUtils.showTopToast(context, Html.fromHtml("<font color = '#FF0000'>开启</font>0.5赋分"), R.style.Toast_Animation);
                } else {
                    pointFiveSwitch.setChecked(false);
                    ToastUtils.showTopToast(context, Html.fromHtml("<font color = '#FF0000'>关闭</font>0.5赋分"), R.style.Toast_Animation);
                }
                PreferencesService.getInstance(context).savePointFive(pointFiveSwitch.isChecked());
                break;
            case R.id.autoSubmitSwitch: //自动提交switch
                if (isStepScore || doubleMode) {
                    ToastUtils.showToast(context, "当前模式下不支持自动提交");
                    autoSubmitSwitch.setChecked(false);
                    break;
                }
                if (autoSubmitSwitch.isChecked()) {
                    autoSubmitSwitch.setChecked(true);
                    ToastUtils.showTopToast(context, Html.fromHtml("<font color = '#FF0000'>开启</font>自动提交"), R.style.Toast_Animation);
                } else {
                    autoSubmitSwitch.setChecked(false);
                    ToastUtils.showTopToast(context, Html.fromHtml("<font color = '#FF0000'>关闭</font>自动提交"), R.style.Toast_Animation);
                }
                PreferencesService.getInstance(context).saveAutoSubmit(autoSubmitSwitch.isChecked());
                break;
            case R.id.topScoreView: //常用分数置顶
                if (getMarkNextStudentResponse == null) {
                    ToastUtils.showToast(context, "获取题目分数失败");
                    return;
                }
                seting_main.setVisibility(View.GONE);
                setting_back.setVisibility(View.VISIBLE);
                topScoreSetting.setVisibility(View.VISIBLE);
                topScoreAdapter = new TopScoreAdapter(context, initTopScoreData(PreferencesService.getInstance(context).getPointFive()));
                topScoreGridView.setAdapter(topScoreAdapter);
                if (!TextUtils.isEmpty(PreferencesService.getInstance(context).getTopScore())) {
                    if (PreferencesService.getInstance(context).getTopScore().contains(".5")
                            && PreferencesService.getInstance(context).getPointFive()) {
                        topSocre_0_5.setChecked(true);
                        topSocre_0_5.setTextColor(getResources().getColor(R.color.colorWhite));
                    } else {
                        topSocre_0_5.setChecked(false);
                        topSocre_0_5.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    }
                } else {
                    if (PreferencesService.getInstance(context).getPointFive()) {
                        topSocre_0_5.setChecked(true);
                        topSocre_0_5.setTextColor(getResources().getColor(R.color.colorWhite));
                    } else {
                        topSocre_0_5.setChecked(false);
                        topSocre_0_5.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    }
                }
                break;
            case R.id.topSocre_0_5: //自定义分数0.5开关
                if (topSocre_0_5.isChecked()) {
                    topSocre_0_5.setChecked(true);
                    topScoreAdapter.updataData(initTopScoreData(true));
                    topSocre_0_5.setTextColor(getResources().getColor(R.color.colorWhite));
                } else {
                    topSocre_0_5.setChecked(false);
                    topScoreAdapter.updataData(initTopScoreData(false));
                    topSocre_0_5.setTextColor(getResources().getColor(R.color.colorScoreItem));
                }
                topScoreAdapter.notifyDataSetChanged();
                break;
            case R.id.topScoreClear: //自定义分数选中清空
                topScoreAdapter.clearSelect();
                PreferencesService.getInstance(context).saveTopScore("");
                topScoreAdapter.notifyDataSetChanged();
                break;
            case R.id.topScoreDetermine: //自定义分数确定
                PreferencesService.getInstance(context).saveTopScore(topScoreAdapter.getTopScoreDate());
                settingDialog.cancel();
                break;
            //TODO 双栏模式开关
            case R.id.douleScoreCheckBox: //双栏模式开关
                if (douleScoreCheckBox.isChecked()) {
                    doubleMode = true;
                    PreferencesService.getInstance(context).saveAutoSubmit(false);
                    if (!"0".equals(doubleScore)) {
                        submiss.setVisibility(View.VISIBLE);
                    } else {
                        submiss.setVisibility(View.GONE);
                    }
                    doubleScoreLayout.setVisibility(View.VISIBLE);
                    doubleScoreTableAdapter = new DoubleScoreTableAdapter(context, getQuestionScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore()));
                    doubleScoreTableAdapter.setMyOnClickLisener(new DoubleScoreTableAdapter.MyOnClickLisener() {
                        @Override
                        public void MyClick(int position) {
                            submiss.setVisibility(View.VISIBLE);
                            doubleScore = String.valueOf(integers.get(position));
                            doubleScoreTableAdapter.setPos(position);
                            doubleScoreTableAdapter.notifyDataSetChanged();
                            double value = integers.get(position);
                            if (value == (int) value) {
                                saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf((int) value));
                                questionScore.setText(String.valueOf((int) value));
                                SCORE = String.valueOf((int) value);
                            } else {
                                saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(value));
                                questionScore.setText(String.valueOf(value));
                                SCORE = String.valueOf(value);
                            }
                            if ((Double.valueOf(TOTAL) - Double.valueOf(doubleScore)) > 9) {
                                scoreAdapter.updataData(getScoreList(value, 10));
                            } else {
                                scoreAdapter.updataData(getScoreList(value, Double.valueOf(TOTAL) - Double.valueOf(doubleScore) + 1));
                            }
                            scoreAdapter.setScoreCheck(-1);
                            scoreAdapter.notifyDataSetChanged();
                        }
                    });
                    doubleListView.setAdapter(doubleScoreTableAdapter);
                    //获取十位上的数
                    int a = 0;
                    if (!"-1".equals(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore())) {
                        SCORE = saveMarkDataBean.getQuestions().get(minLocation).getMarkScore();
                        double aDouble = Double.valueOf(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore());
                        //向下取整(获取当前分数的十位数)
                        double floor = Math.floor(Double.valueOf(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore()));
                        a = ((int) floor / 10) % 10 * 10;
                        for (int i = 0; i < integers.size(); i++) {
                            if (a == integers.get(i)) {
                                doubleScoreTableAdapter.setPos(i);
                                doubleScoreTableAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                        doubleScore = String.valueOf(a);
                        if (Double.valueOf(TOTAL) - a >= 9) {
                            scoreAdapter.updataData(getScoreList(a, 10));
                        } else {
                            scoreAdapter.updataData(getScoreList(a, Double.valueOf(TOTAL) - a + 1));
                        }
                        //更新打分列表
                        for (int i = 0; i < scorePanel.getScores().size(); i++) {
                            if (Double.valueOf(scorePanel.getScores().get(i)) == (aDouble - a)) {
                                scoreAdapter.setScoreCheck(i);
                                break;
                            }
                        }
                    } else {
                        doubleScore = "0";
                        if (Double.valueOf(TOTAL) - 10 >= 9) {
                            scoreAdapter.updataData(getScoreList(10, 10));
                        } else {
                            scoreAdapter.updataData(getScoreList(10, Double.valueOf(TOTAL) - 10));
                        }
                        scoreAdapter.setScoreCheck(-1);
                    }
                    scoreAdapter.notifyDataSetChanged();
                } else {
                    PreferencesService.getInstance(context).saveAutoSubmit(true);
                    doubleMode = false;
                    if (PreferencesService.getInstance(context).getAutoSubmit()) {
                        submiss.setVisibility(View.GONE);
                    } else {
                        if (isStepScore) {
                            if (childLocations != null && childLocations.size() > 0) {
                                submiss.setVisibility(View.VISIBLE);
                            } else {
                                submiss.setVisibility(View.GONE);
                            }
                        } else {
                            submiss.setVisibility(View.VISIBLE);
                        }
                    }
                    doubleScoreLayout.setVisibility(View.GONE);
                    scoreAdapter.updataData(getScoreList(Double.valueOf(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore()), Double.valueOf(TOTAL)));
                    scoreAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.double_markScore: //双栏满分
                doubleScore = String.valueOf(integers.get(integers.size() - 1));
                doubleScoreTableAdapter.setLast();
                doubleScoreTableAdapter.notifyDataSetChanged();
                saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(TOTAL);
                SCORE = TOTAL;
                questionScore.setText(TOTAL);
                if ((Double.valueOf(TOTAL) - integers.get(integers.size() - 1)) > 9) {
                    scoreAdapter.updataData(getScoreList(integers.get(integers.size() - 1), 10));
                } else {
                    scoreAdapter.updataData(getScoreList(integers.get(integers.size() - 1), Double.valueOf(TOTAL) - Double.valueOf(doubleScore) + 1));
                }
                scoreAdapter.notifyDataSetChanged();
                submiss.setVisibility(View.VISIBLE);
                break;
        }
    }

    //更新双栏模式的选项
    private List<Integer> getQuestionScore(double fullScore) {
        integers = new ArrayList<>();
        for (int i = 10; i <= fullScore; i += 10) {
            integers.add(i);
        }
        return integers;
    }

    //获取一个新的未批学生数据
    private void getNewStudent() {
        myModel.getMarkNextStudent(context, request, new MyCallBack() {
            @Override
            public void onSuccess(final Object model) {
                reviewMode = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        minLocation = 0;
                        getMarkNextStudentResponse = (GetMarkNextStudentResponse) model;
                        if (getMarkNextStudentResponse.getData() == null || getMarkNextStudentResponse.getData().getStudentData() == null
                                || getMarkNextStudentResponse.getData().getStudentData().getQuestions() == null) {
                            Looper.prepare();
                            //需要弹出弹出，进行重新获取，暂时先提示用户
                            ToastUtils.showTopToast(context, "题目获取异常，请返回主页重新获取", R.style.Toast_Animation);
                            Looper.loop();
                            return;
                        }
                        MaxScore = getMaxScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (getMarkNextStudentResponse.getData().getStudentData().isCollect()) {
                                    collectRequest.setValue(String.valueOf(1));
                                    collection.setChecked(true);
                                } else {
                                    collectRequest.setValue(String.valueOf(0));
                                    collection.setChecked(false);
                                }
                            }
                        });
                        initSaveData(getMarkNextStudentResponse);
                        showSueecssPage(getMarkNextStudentResponse);
                        submiss.setVisibility(View.GONE);
                        submiss.setEnabled(true);
                        progressTips.setText(Html.fromHtml("<font color = '#245AD3'>" + (response.getTeacherTask().getMarkNumber() + 1)
                                + "</font>/" + response.getTeacherTask().getMarkCount()));
                    }
                });
            }

            @Override
            public void onFailed(String str) {
                showFailedPage(str);
            }
        });
    }

    //获取步骤分数据
    private void getStepScoreData() {
        Log.e(TAG, "当前的缩放率是：" + scale);
        if (childLocations != null && childLocations.size() > 0) {
            List<StepScore> stepScores = new ArrayList<>();
            for (int i = 0; i < childLocations.size(); i++) {
                StepScore stepScore1 = new StepScore();
                stepScore1.setId(i);
                stepScore1.setId(0);
                stepScore1.setField("stepPoints");
                double d = Double.valueOf(childLocations.get(i).getTv());
                if (d == (int) d) {
                    stepScore1.setParams((int) d);
                } else {
                    stepScore1.setParams(d);
                }

                //针对pad上显示的相对于图片的坐标
                int padImgX = (int) (childLocations.get(i).getX() - (mSpenSimpleSurfaceView.getWidth() - mSpenPageDoc.getWidth()) / 2);
                int padImgY = (int) (childLocations.get(i).getY() - (mSpenSimpleSurfaceView.getHeight() - mSpenPageDoc.getHeight()) / 2);

                int locaX = padImgX * imageData.getWidth() / mSpenPageDoc.getWidth();
                int locaY = imageData.getHeight() * padImgY / mSpenPageDoc.getHeight();

                stepScore1.setX(locaX);
                stepScore1.setY(locaY);
                stepScores.add(stepScore1);
            }
            saveMarkDataBean.getQuestions().get(minLocation).setStepScore(new Gson().toJson(stepScores));
        }
    }

    //获取当前画布上的笔记数据
    private void getPageDocObject() {
        //坐标点要按照原图的大小坐标来计算
        ArrayList<SpenObjectBase> objectList = mSpenPageDoc.getObjectList();
        if (objectList != null || objectList.size() > 0) {
            List<SpenStroke> spenStrokes = new ArrayList<>();
            Log.e(TAG, "当前的缩放率是：" + scale);
            for (int i = 0; i < objectList.size(); i++) {
                SpenObjectStroke spenObjectStroke = (SpenObjectStroke) objectList.get(i);
                SpenStroke stroke = new SpenStroke();
                stroke.setPenSize(PENSIZE);
                //Color.RED = 0xFFFF0000
                stroke.setColor("#FF0000");
                PointF[] points = spenObjectStroke.getPoints();
                for (int j = 0; j < imageDataList.size(); j++) {
                    ImageData imageData = imageDataList.get(j);
                    if (points[0].y < imageData.getHeight() * scale) {
                        stroke.setPageName("A");
                        break;
                    } else {
                        stroke.setPageName("B");
                        break;
                    }
                }
                PointF[] pointF = new PointF[spenObjectStroke.getPoints().length];
                for (int j = 0; j < points.length; j++) {
                    PointF pointF1 = new PointF();
                    pointF1.set(points[j].x * scale, points[j].y * scale);
                    pointF[j] = pointF1;
                }
                stroke.setPointS(pointF);
                spenStrokes.add(stroke);
            }
            saveMarkDataBean.getQuestions().get(minLocation).setCoordinate(new Gson().toJson(spenStrokes));
        }
    }

    /**
     * 初始化置顶分数选择器
     *
     * @param b 是否开启0.5
     * @return
     */
    private Map<Integer, CustomBean> initTopScoreData(boolean b) {
        Map<Integer, CustomBean> data = new HashMap<>();
        Log.e(TAG, "配置文件置顶分数：" + PreferencesService.getInstance(context).getTopScore());
        if (!TextUtils.isEmpty(PreferencesService.getInstance(context).getTopScore())) {
            String[] split = PreferencesService.getInstance(context).getTopScore().split(",");
            List<String> list1 = new ArrayList<>(); //存放整数(置顶分数)
            List<String> list2 = new ArrayList<>(); //存放小数数(置顶分数)
            //判断保存的分数是否是开启0.5分功能
            for (int i = 0; i < split.length; i++) {
                if (split[i].contains(".5")) {
                    list2.add(split[i]);
                } else {
                    list1.add(split[i]);
                }
            }

            if (b) {
                int num = 0;
                for (double i = 0; i < MaxScore + 0.5; i += 0.5) {
                    for (int j = 0; j < split.length; j++) {
                        if (j == split.length - 1) {
                            CustomBean customBean = new CustomBean();
                            if (i == Double.valueOf(split[j])) {
                                customBean.setSelect(true);
                            } else {
                                customBean.setSelect(false);
                            }
                            customBean.setScore(i);
                            data.put(num, customBean);
                            num++;
                            break;
                        } else {
                            if (i == Double.valueOf(split[j])) {
                                CustomBean customBean = new CustomBean();
                                customBean.setSelect(true);
                                customBean.setScore(i);
                                data.put(num, customBean);
                                num++;
                                break;
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < MaxScore; i++) {
                    for (int j = 0; j < list1.size(); j++) {
                        if (j == list1.size() - 1) {
                            CustomBean customBean = new CustomBean();
                            if (Integer.valueOf(list1.get(j)) == i) {
                                customBean.setScore(Double.valueOf(String.valueOf(list1.get(j))));
                                customBean.setSelect(true);
                            } else {
                                customBean.setScore(Double.valueOf(String.valueOf(i)));
                                customBean.setSelect(false);
                            }
                            data.put(i, customBean);
                            break;
                        } else {
                            if (Integer.valueOf(list1.get(j)) == i) {
                                CustomBean customBean = new CustomBean();
                                customBean.setScore(Double.valueOf(String.valueOf(list1.get(j))));
                                customBean.setSelect(true);
                                data.put(i, customBean);
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            if (b) {
                int num = 0;
                for (double i = 0; i < MaxScore + 0.5; i += 0.5) {
                    CustomBean bean = new CustomBean();
                    bean.setSelect(false);
                    if ((int) i == i) {  //强制转换后来会丢失精度,如果丢源失精度的数和原数相等，说明就是整数
                        bean.setScore(Double.valueOf(String.valueOf(i)));
                        data.put(num, bean);
                    } else {
                        bean.setScore(Double.valueOf(String.valueOf(i)));
                        data.put(num, bean);
                    }
                    num++;
                }
            } else {
                for (int i = 0; i < MaxScore + 1; i++) {
                    CustomBean bean = new CustomBean();
                    bean.setSelect(false);
                    bean.setScore(Double.valueOf(String.valueOf(i)));
                    data.put(i, bean);
                }
            }
        }
        return data;
    }

    /**
     * 展示评分详情
     */
    private void showScoreDatails() {
        GetMarkAvgScoreRequest avgScoreRequest = new GetMarkAvgScoreRequest();
        avgScoreRequest.setTaskGuid(taskGuid);
        avgScoreRequest.setTeacherGuid(teacherGuid);
        myModel.getMarkAvgScore(context, avgScoreRequest, new MyCallBack() {
            @Override
            public void onSuccess(final Object model) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<GetMarkAvgScoreResponse> avgScoreResponseList = (List<GetMarkAvgScoreResponse>) model;
                        if (avgScoreResponseList != null && avgScoreResponseList.size() > 0) {
                            View view = LayoutInflater.from(context).inflate(R.layout.popupwindow_layout, null);
                            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            popupWindow.setFocusable(true);//获取焦点
                            //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
                            popupWindow.setBackgroundDrawable(new BitmapDrawable());
                            popupWindow.setOutsideTouchable(true); //点击外部消失
                            popupWindow.setTouchable(true); //设置可以点击
                            ListView datailsListView = view.findViewById(R.id.datailsListView);
                            ScoreDetailsAdapter detailsAdapter = new ScoreDetailsAdapter(context, avgScoreResponseList);
                            datailsListView.setAdapter(detailsAdapter);
                            popupWindow.setHeight(250);
                            //setAlpha(0.5f);
                            popupWindow.showAtLocation(main, Gravity.BOTTOM, 0, 0);
                            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    //setAlpha(1f);
                                }
                            });
                        } else {
                            ToastUtils.showToast(context, "暂未数据");
                        }
                    }
                });
            }

            @Override
            public void onFailed(String str) {
                showFailedPage(str);
            }
        });
    }

    /**
     * 展示回评列表
     */
    private void showReviewList() {
        ReviewStudentsRequest reviews = new ReviewStudentsRequest();
        reviews.setSearchStr("");
        reviews.setTeacherGuid(teacherGuid);
        reviews.setTaskGudi(taskGuid);
        reviews.setStatus("-1"); //0:不异常，1异常，2收藏
        myModel.reviewStudents(context, reviews, new MyCallBack() {
            @Override
            public void onSuccess(final Object model) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        studentsResponse = (ReviewStudentsResponse) model;
                        if (studentsResponse.getData() != null && studentsResponse.getData().size() > 0) {
                            reviewListView.setVisibility(View.VISIBLE);
                            nodatahint.setVisibility(View.GONE);
                            ReviewAdatper reviewAdatper = new ReviewAdatper(context, studentsResponse.getData());
                            reviewListView.setAdapter(reviewAdatper);
                            reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                    cachePool.clear();
                                    LoadingUtil.showDialog(context);
                                    reviewMode = true;
                                    LOCATION = position;
                                    drawerLayout.closeDrawer(reigth);
                                    GetStudentMarkDataRequest getStudentMarkDataRequest = new GetStudentMarkDataRequest();
                                    getStudentMarkDataRequest.setStudentGuid(studentsResponse.getData().get(position).getStudentGuid());
                                    getStudentMarkDataRequest.setTaskGuid(taskGuid);
                                    getStudentMarkDataRequest.setTeacherGuid(teacherGuid);
                                    myModel.getStudentMarkData(context, getStudentMarkDataRequest, new MyCallBack() {
                                        @Override
                                        public void onSuccess(final Object model) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    minLocation = 0;
                                                    getMarkNextStudentResponse = (GetMarkNextStudentResponse) model;
                                                    if (getMarkNextStudentResponse.getData() == null || getMarkNextStudentResponse.getData().getStudentData() == null
                                                            || getMarkNextStudentResponse.getData().getStudentData().getQuestions() == null) {
                                                        Looper.prepare();
                                                        //需要弹出弹出，进行重新获取，暂时先提示用户
                                                        ToastUtils.showTopToast(context, "题目获取异常，请返回主页重新获取", R.style.Toast_Animation);
                                                        Looper.loop();
                                                        return;
                                                    }
                                                    cachePool.add(getMarkNextStudentResponse);
                                                    if (position != studentsResponse.getData().size() - 1) { //不是最后一个回评数据
                                                        GetStudentMarkDataRequest reviewStudent = new GetStudentMarkDataRequest();
                                                        reviewStudent.setStudentGuid(studentsResponse.getData().get(position).getStudentGuid());
                                                        reviewStudent.setTaskGuid(taskGuid);
                                                        reviewStudent.setTeacherGuid(teacherGuid);
                                                        getReviewStudentCache(reviewStudent);
                                                    } else {
                                                        if (response.getTeacherTask().getMarkCount() == 0) { //自己的任务数量为0(自由阅卷)
                                                            if (response.getTeacherTask().getMarkSum() < response.getTeacherTask().getTaskCount()) { //当前任务是否所有教师没有批阅完
                                                                getNextStudentCache();
                                                            }
                                                        } else { //有自己的任务数量
                                                            if (response.getTeacherTask().getMarkNumber() < response.getTeacherTask().getMarkCount()) {
                                                                getNextStudentCache();
                                                            }
                                                        }
                                                    }
                                                    MaxScore = getMaxScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions());
                                                    if (getMarkNextStudentResponse.getData().getStudentData().isCollect()) {
                                                        collectRequest.setValue(String.valueOf(1));
                                                        collection.setChecked(true);
                                                    } else {
                                                        collectRequest.setValue(String.valueOf(0));
                                                        collection.setChecked(false);
                                                    }
                                                    initUpDateData(getMarkNextStudentResponse);
                                                    showSueecssPage(getMarkNextStudentResponse);
                                                    submiss.setEnabled(true);
                                                    progressTips.setText(Html.fromHtml("<font color = '#245AD3'>" + (position + 1)
                                                            + "</font>/" + response.getTeacherTask().getMarkCount()));
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
                        } else {
                            reviewListView.setVisibility(View.GONE);
                            nodatahint.setVisibility(View.VISIBLE);
                        }
                        drawerLayout.openDrawer(reigth);
                    }
                });
            }

            @Override
            public void onFailed(String str) {
                showFailedPage(str);
            }
        });
    }

    //获取一个新学生缓存数据
    private void getNextStudentCache() {
        if (true) {
            //临时去除缓存功能
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // LoadingUtil.showDialog(context);
            }
        });
        Log.e(TAG, "getNextStudentCache: 缓存一个未批阅学生的数据");
        GetMarkDataRequest cacheRequest = new GetMarkDataRequest();
        cacheRequest.setTaskGuid(taskGuid);
        cacheRequest.setTeacherGuid(teacherGuid);
        myModel.getMarkNextStudent(context, cacheRequest, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                GetMarkNextStudentResponse nextStudentResponse = (GetMarkNextStudentResponse) model;
                /*if (nextStudentResponse.getData().getStudentData().getQuestions().get(0).getId() ==
                        cachePool.get(0).getData().getStudentData().getQuestions().get(0).getId()) {
                    Log.e(TAG, cachePool.get(0).getData().getStudentData().getQuestions().get(0).getId() + "   cachePool: 当前缓存数据的id：" + nextStudentResponse.getData().getStudentData().getQuestions().get(0).getId());
                    getNextStudentCache();
                    return;
                }*/
                cachePool.add(nextStudentResponse);
                Log.e(TAG, "缓存数据成功，当前的数量是" + cachePool.size());
                Tool.base64ToBitmap(nextStudentResponse.getData().getImageArr(), nextStudentResponse.getData().getStudentData().getQuestions().get(0).getId(), new MyCallBack() {
                    @Override
                    public void onSuccess(Object model) {
                        LocalImageData localImageData = (LocalImageData) model;
                        Log.e(TAG, "缓存数据图片：" + localImageData.getPath());
                        /*Bitmap bitmap = BitmapFactory.decodeFile(localImageData.getPath());
                        imageData = new ImageData(bitmap.getWidth(), bitmap.getHeight());
                        Log.e(TAG, "本地图片的宽: " + bitmap.getWidth() + "  高:" + bitmap.getHeight());
                        Log.e(TAG, "图片保存到本地的地址是：" + localImageData.getPath());*/
                    }

                    @Override
                    public void onFailed(String str) {
                        showFailedPage(str);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //LoadingUtil.closeDialog();
                    }
                });
            }

            @Override
            public void onFailed(String str) {
                showFailedPage(str);
            }
        });
    }

    //获取一个已阅学生的缓存数据
    private void getReviewStudentCache(GetStudentMarkDataRequest reviewStudent) {
        if (true) {
            //临时去除缓存功能
            return;
        }
        Log.e(TAG, "getReviewStudentCache: 缓存已批阅学生的数据");
        myModel.getStudentMarkData(context, reviewStudent, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                GetMarkNextStudentResponse reviewStudentResponse = (GetMarkNextStudentResponse) model;
                cachePool.add(reviewStudentResponse);
                Tool.base64ToBitmap(reviewStudentResponse.getData().getImageArr(), reviewStudentResponse.getData().getStudentData().getQuestions().get(0).getId(), new MyCallBack() {
                    @Override
                    public void onSuccess(Object model) {
                        LocalImageData localImageData = (LocalImageData) model;
                        /*Bitmap bitmap = BitmapFactory.decodeFile(localImageData.getPath());
                        imageData = new ImageData(bitmap.getWidth(), bitmap.getHeight());
                        Log.e(TAG, "本地图片的宽: " + bitmap.getWidth() + "  高:" + bitmap.getHeight());
                        Log.e(TAG, "图片保存到本地的地址是：" + localImageData.getPath());*/
                    }

                    @Override
                    public void onFailed(String str) {
                        showFailedPage(str);
                    }
                });
            }

            @Override
            public void onFailed(String str) {
                showFailedPage(str);
            }
        });
    }

    /**
     * 提交数据
     */
    private void submitData() {
        LoadingUtil.showDialog(context);
        submiss.setEnabled(false);
        if (!isStepScore) {
            if (mSpenPageDoc.getObjectList().size() != 0) {
                getPageDocObject();
            }
        } else {
            if (tableParent.getChildCount() > 0) {
                getStepScoreData();
            }
        }
        LogUtils.logE(TAG, "提交的数据是: " + new GsonBuilder().serializeNulls().create().toJson(saveMarkDataBean));
        if (true) {
            //return;
        }
        if (reviewMode) {
            Log.e(TAG, "onClick: 回评模式");
            //回评模式
            myModel.upDateMarkData(context, saveMarkDataBean, new MyCallBack() {
                @Override
                public void onSuccess(Object model) {
                    saveResponse = (SavaDataResponse) model;
                    if (cachePool != null && cachePool.size() > 0) {
                        cachePool.remove(0); //清除第一个缓存数据
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LOCATION++;
                            if (childLocations != null) {
                                childLocations.clear();
                            }
                            stepScoreModeScore = 0;
                            tableParent.removeAllViews();
                            doubleScore = "0";
                            if (doubleMode) {
                                doubleScoreTableAdapter.setPos(-1);
                                doubleScoreTableAdapter.notifyDataSetChanged();
                            }
                            //判断后面是否有回评数据
                            if (LOCATION == studentsResponse.getData().size()) { //回评结束了
                                if (response.getTeacherTask().getMarkSum() < response.getTeacherTask().getTaskCount()) { //总任务没完成
                                    if (response.getTeacherTask().getMarkCount() == 0) { //自己任务数量为0
                                        getNewStudent();
                                        if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount() - 1) {
                                            //获取下一个缓存数据
                                            getNextStudentCache();
                                        }
                                    }
                                    if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount()) {
                                        getNewStudent();
                                    } else { //自己的任务已经完成
                                        myTaskOverDialog();
                                    }
                                } else {
                                    //阅卷结束
                                    myTaskOverDialog();
                                }
                            } else { //还有回评数据
                                GetStudentMarkDataRequest getStudentMarkDataRequest = new GetStudentMarkDataRequest();
                                getStudentMarkDataRequest.setStudentGuid(studentsResponse.getData().get(LOCATION).getStudentGuid());
                                getStudentMarkDataRequest.setTaskGuid(taskGuid);
                                getStudentMarkDataRequest.setTeacherGuid(teacherGuid);
                                if (cachePool.size() != 0) {
                                    loadCacheData();
                                } else {
                                    myModel.getStudentMarkData(context, getStudentMarkDataRequest, new MyCallBack() {
                                        @Override
                                        public void onSuccess(final Object model) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    minLocation = 0;
                                                    getMarkNextStudentResponse = (GetMarkNextStudentResponse) model;
                                                    if (getMarkNextStudentResponse.getData() == null || getMarkNextStudentResponse.getData().getStudentData() == null
                                                            || getMarkNextStudentResponse.getData().getStudentData().getQuestions() == null) {
                                                        //需要弹出弹出，进行重新获取，暂时先提示用户
                                                        ToastUtils.showTopToast(context, "题目获取异常，请返回主页重新获取", R.style.Toast_Animation);
                                                        LoadingUtil.closeDialog();
                                                        return;
                                                    }
                                                    MaxScore = getMaxScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions());
                                                    if (getMarkNextStudentResponse.getData().getStudentData().isCollect()) {
                                                        collectRequest.setValue(String.valueOf(1));
                                                        collection.setChecked(true);
                                                    } else {
                                                        collectRequest.setValue(String.valueOf(0));
                                                        collection.setChecked(false);
                                                    }
                                                    initUpDateData(getMarkNextStudentResponse);
                                                    showSueecssPage(getMarkNextStudentResponse);
                                                    submiss.setEnabled(true);
                                                    progressTips.setText(Html.fromHtml("<font color = '#245AD3'>" + (LOCATION + 1)
                                                            + "</font>/" + response.getTeacherTask().getMarkCount()));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailed(String str) {
                                            showFailedPage(str);
                                        }
                                    });
                                }
                            }
                        }
                    });
                }

                @Override
                public void onFailed(String str) {
                    showFailedPage(str);
                }
            });
        } else {
            Log.e(TAG, "onClick: 正常模式");
            //正常批阅模式
            myModel.saveMarkData(context, saveMarkDataBean, new MyCallBack() {
                @Override
                public void onSuccess(Object model) {
                    if (cachePool != null && cachePool.size() > 0) {
                        cachePool.remove(0); //清除第一个数据
                        Log.e(TAG, "提交数据成功，清除第一个缓存数据后的长度是：" + cachePool.size());
                    }
                    saveResponse = (SavaDataResponse) model;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (childLocations != null) {
                                childLocations.clear();
                            }
                            tableParent.removeAllViews();
                            doubleScore = "0";
                            stepScoreModeScore = 0;
                            if (doubleMode) {
                                doubleScoreTableAdapter.setPos(-1);
                                doubleScoreTableAdapter.notifyDataSetChanged();
                            }
                            LOCATION = saveResponse.getMyNumber();
                            //修改一下原先获取的试卷数据(已阅、未阅)
                            response.getTeacherTask().setMarkNumber(saveResponse.getMyNumber());
                            response.getTeacherTask().setMarkCount(saveResponse.getMyCount());
                            response.getTeacherTask().setTaskCount(saveResponse.getTaskCount());
                            response.getTeacherTask().setMarkSum(saveResponse.getTaskNumber());
                            if (saveResponse.getTaskNumber() < saveResponse.getTaskCount()) { //任务已阅量 < 任务总量
                                if (saveResponse.getMyCount() != 0) { //有自己的任务
                                    if (saveResponse.getMyNumber() < saveResponse.getMyCount()) {
                                        if (cachePool.size() > 0) {
                                            loadCacheData();
                                        } else {
                                            getNewStudent();
                                        }
                                        if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount() - 1) {
                                            //获取下一个缓存数据
                                            getNextStudentCache();
                                            //getNewStudent();
                                        }
                                    } else {
                                        if (saveResponse.getMyNumber() == saveResponse.getMyCount()) {
                                            myTaskOverDialog();
                                            return;
                                        }
                                        if (style == 1) {
                                            if (saveResponse.getTaskNumber() < saveResponse.getTaskCount()) {
                                                if (cachePool.size() > 0) {
                                                    loadCacheData();
                                                } else {
                                                    getNewStudent();
                                                }
                                                getNextStudentCache();
                                            } else {
                                                myTaskOverDialog();
                                            }
                                        } else {
                                            myTaskOverDialog();
                                        }
                                    }
                                } else { //帮阅模式
                                    if (cachePool.size() > 0) {
                                        loadCacheData();
                                    } else {
                                        getNewStudent();
                                    }
                                    if (saveResponse.getTaskNumber() != saveResponse.getTaskCount() - 1) {
                                        //获取下一个缓存数据
                                        getNextStudentCache();
                                        //getNewStudent();
                                    }
                                }
                            } else {
                                //阅卷结束
                                myTaskOverDialog();
                            }
                        }
                    });
                }

                @Override
                public void onFailed(String str) {
                    //{"success":506,"message":"当前学生已被批阅,请刷新页面"}
                    if (str.contains("{\"success\":506,\"message\":\"当前学生已被批阅,请刷新页面\"}")) {
                        myModel.getMarkData(context, request, new MyCallBack() {
                            @Override
                            public void onSuccess(final Object model) {
                                final GetMarkDataResponse getMarkDataResponse = (GetMarkDataResponse) model;
                                final GetMarkDataResponse.TeacherTaskBean teacherTask = getMarkDataResponse.getTeacherTask();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (teacherTask.getMarkSum() < teacherTask.getTaskCount()) { //任务还没有完成
                                            getNewStudent();
                                        } else {
                                            if (teacherTask.getMarkNumber() > 0) {
                                                myTaskOverDialog();
                                            } else {
                                                myTaskOverDialog2();
                                            }
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailed(String str) {

                            }
                        });
                    } else {
                        showFailedPage(str);
                    }
                }
            });
        }
    }

    //加载缓存数据
    private void loadCacheData() {
        Log.e(TAG, "loadCacheData: 加载缓存数据");
        minLocation = 0;
        collectRequest = new CollectRequest();
        //加载缓存数据
        getMarkNextStudentResponse = cachePool.get(0); //获取第一个缓存数据
        if (getMarkNextStudentResponse.getData() == null || getMarkNextStudentResponse.getData().getStudentData() == null
                || getMarkNextStudentResponse.getData().getStudentData().getQuestions() == null) {
            //需要弹出弹出，进行重新获取，暂时先提示用户
            ToastUtils.showTopToast(context, "题目获取异常，请返回主页重新获取", R.style.Toast_Animation);
            LoadingUtil.closeDialog();
            return;
        }
        MaxScore = getMaxScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions());
        if (getMarkNextStudentResponse.getData().getStudentData().isCollect()) {
            collectRequest.setValue(String.valueOf(1));
            collection.setChecked(true);
        } else {
            collectRequest.setValue(String.valueOf(0));
            collection.setChecked(false);
        }
        initUpDateData(getMarkNextStudentResponse);
        loadCacheImg(Tool.IMAGEPATH + "/" + getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(0).getId() + ".jpeg");
        submiss.setEnabled(true);
        progressTips.setText(Html.fromHtml("<font color = '#245AD3'>" + (LOCATION + 1)
                + "</font>/" + response.getTeacherTask().getMarkCount()));
        initUI(getMarkNextStudentResponse);
    }

    /**
     * 加载缓存数据的图片
     *
     * @param imgPath
     */
    private void loadCacheImg(String imgPath) {
        Log.e(TAG, "加载缓存图片：" + imgPath);
        mSpenPageDoc.removeAllObject();
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        if (bitmap == null) {
            ToastUtils.showToast(context, "获取图片异常");
            Log.e(TAG, "loadCacheImg: 获取图片异常");
            return;
        }
        imageData = new ImageData(bitmap.getWidth(), bitmap.getHeight());
        Log.e(TAG, "本地图片的宽: " + bitmap.getWidth() + "  高:" + bitmap.getHeight());

        Rect rect = Tool.getScreenparameters(MarkingActivity.this);
        if (bitmap.getHeight() > rect.height() - Tool.getStatusBarHeight(context) || bitmap.getWidth() > rect.width() - 80) { //图片比屏幕大
            if (bitmap.getWidth() > bitmap.getHeight()) { //宽图
                initSpenNoteDoc(rect.width() - 80, bitmap.getHeight() * (rect.width() - 80) / bitmap.getWidth());
            } else if (bitmap.getWidth() < bitmap.getHeight()) { //长图
                if ("语文".equals(response.getTestpaper().getPaperName()) && bitmap.getHeight() > 2000) {
                    initSpenNoteDoc(1024 - 80, bitmap.getHeight() * (1024 - 80) / bitmap.getWidth());
                } else {
                    initSpenNoteDoc(bitmap.getWidth() * rect.height() / bitmap.getHeight(), rect.height());
                }

            } else {
                //可能存在方图，暂时不处理
            }
        } else { //图片没有屏幕大
            initSpenNoteDoc(bitmap.getWidth(), bitmap.getHeight());
        }

        //设置背景图片
        mSpenPageDoc.setBackgroundImage(imgPath);

        if (!TextUtils.isEmpty(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getCoordinate())
                && getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getCoordinate().length() > 10) {
            addStroke(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getCoordinate());
            isStepScore = false;
        } else if (!TextUtils.isEmpty(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getStepScore())
                && getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getStepScore().length() > 10) {
            addStepScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getStepScore());
            isStepScore = true;
        }
        mSpenSimpleSurfaceView.update();
        LoadingUtil.closeDialog();
    }

    //自己的任务已经阅完提示
    private void myTaskOverDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingUtil.closeDialog();
            }
        });
        builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("任务已完成，您可以如下操作");
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "回评", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LOCATION--;
                showReviewList();
                submiss.setEnabled(true);
                dialog.cancel();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                MarkingActivity.this.finish();
            }
        });
        dialog.show();
    }

    private void myTaskOverDialog2() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoadingUtil.closeDialog();
            }
        });
        builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("任务已完成，您可以如下操作");
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                MarkingActivity.this.finish();
            }
        });
        dialog.show();
    }

    /**
     * 展示设置页面
     */
    private void showSettingPage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.setting_page, null);
        builder.setView(view);
        settingDialog = builder.create();
        settingDialog.show();
        settingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (getMarkNextStudentResponse == null) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (reviewMode) {
                            if (doubleMode) {
                                if ("0".equals(SCORE)) {
                                    scoreAdapter.updataData(getScoreList(Double.valueOf(SCORE), 10));
                                } else {
                                    scoreAdapter.updataData(getScoreList(Double.valueOf(SCORE), Double.valueOf(SCORE) - Double.valueOf(doubleScore)));
                                }
                            } else {
                                scoreAdapter.updataData(getScoreList(Double.valueOf(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore()),
                                        getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore()));
                            }
                        } else {
                            if (doubleMode) {
                                if ("0".equals(doubleScore)) {
                                    scoreAdapter.updataData(getScoreList(Double.valueOf(SCORE), 10));
                                } else {
                                    if (Double.valueOf(TOTAL) - Double.valueOf(doubleScore) >= 9) {
                                        scoreAdapter.updataData(getScoreList(Double.valueOf(doubleScore), 10));
                                    } else {
                                        scoreAdapter.updataData(getScoreList(Double.valueOf(doubleScore), Double.valueOf(TOTAL) - Double.valueOf(doubleScore)));
                                    }
                                }
                            } else {
                                scoreAdapter.updataData(getScoreList(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore(),
                                        getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore()));
                            }
                        }
                        scoreAdapter.notifyDataSetChanged();
                        if (PreferencesService.getInstance(context).getAutoSubmit()) {
                            submiss.setVisibility(View.GONE);
                        } else {
                            if (isStepScore) {
                                if ("-1".equals(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore())) {
                                    submiss.setVisibility(View.GONE);
                                } else {
                                    submiss.setVisibility(View.VISIBLE);
                                }
                            } else {
                                boolean b = true; //是否全部都打分了
                                for (int i = 0; i < saveMarkDataBean.getQuestions().size(); i++) {
                                    if ("-1".equals(saveMarkDataBean.getQuestions().get(i).getMarkScore())) {
                                        b = false;
                                        break;
                                    }
                                }
                                if (b) {
                                    submiss.setVisibility(View.VISIBLE);
                                } else {
                                    submiss.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });
            }
        });
        WindowManager.LayoutParams params = settingDialog.getWindow().getAttributes();
        //TODO 设置成平面高度的0.75倍
        params.height = (int) (Tool.getScreenparameters(MarkingActivity.this).height() * 0.75);
        settingDialog.getWindow().setAttributes(params);
        seting_main = view.findViewById(R.id.setting_main);
        topScoreSetting = view.findViewById(R.id.topScoreSetting);
        setting_back = view.findViewById(R.id.setting_back);
        pointFiveView = view.findViewById(R.id.pointFiveView);
        pointFiveSwitch = view.findViewById(R.id.pointFiveSwitch);
        autoSubmitView = view.findViewById(R.id.autoSubmitView);
        autoSubmitSwitch = view.findViewById(R.id.autoSubmitSwitch);
        topScoreView = view.findViewById(R.id.topScoreView);
        topScoreGridView = view.findViewById(R.id.topScoreGridView);
        topSocre_0_5 = view.findViewById(R.id.topSocre_0_5);
        topScoreClear = view.findViewById(R.id.topScoreClear);
        topScoreDetermine = view.findViewById(R.id.topScoreDetermine);

        setting_back.setOnClickListener(this);
        pointFiveView.setOnClickListener(this);
        pointFiveSwitch.setOnClickListener(this);
        autoSubmitSwitch.setOnClickListener(this);
        autoSubmitSwitch.setEnabled(true);
        topScoreView.setOnClickListener(this);
        topSocre_0_5.setOnClickListener(this);
        topScoreClear.setOnClickListener(this);
        topScoreDetermine.setOnClickListener(this);
        topScoreGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        //初始化控件
        if (PreferencesService.getInstance(context).getPointFive()) {
            pointFiveSwitch.setChecked(true);
        } else {
            pointFiveSwitch.setChecked(false);
        }
        if (PreferencesService.getInstance(context).getAutoSubmit()) {
            autoSubmitSwitch.setChecked(true);
        } else {
            autoSubmitSwitch.setChecked(false);
        }
    }

    //背景变透明方法
    public void setAlpha(float alpha) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = alpha; //0.0-1.0
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (settingDialog != null && settingDialog.isShowing()) {
                settingDialog.cancel();
            } else {
                builder = new AlertDialog.Builder(context);
                builder.setTitle("提示");
                builder.setMessage("是否退出当前任务？");
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        MarkingActivity.this.finish();
                    }
                });
                dialog.show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //RecyclerView使item移动到指定的位置
    public static void moveToPosition(LinearLayoutManager manager, int pos) {
        manager.scrollToPositionWithOffset(pos, 0);
        //manager.scrollToPosition(pos);
        manager.setStackFromEnd(true);
    }

    boolean next = false;//是否继续执行ACTION_UP操作

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "onTouch: 点击了");
                next = true;
                //第一个触摸是电子笔
                if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS || event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                    if (TextUtils.isEmpty(tableStepScore)) {
                        //没有选择步骤分
                        ToastUtils.showToast(context, "请先选择一个非0的标签分数");
                        next = false;
                    } else {
                        if (tableParent.getChildCount() > 0) {
                            stepScoreModeScore = 0;
                            for (int i = 0; i < tableParent.getChildCount(); i++) {
                                TextView tv = (TextView) tableParent.getChildAt(i);
                                stepScoreModeScore += Double.valueOf(tv.getText().toString().substring(1));
                            }
                        } else {
                            if (Double.valueOf(tableStepScore) > Double.valueOf(TOTAL)) {
                                ToastUtils.showToast(context, "请选择一个小一点的分值");
                                stepScoreModeScore = -1;
                                next = false;
                            }
                        }
                        if (stepScoreModeScore + (Double.valueOf(tableStepScore)) > Double.valueOf(TOTAL)) {
                            Log.e(TAG, "onTouch: 得分" + stepScoreModeScore);
                            Log.e(TAG, "onTouch: 当前选中的标签" + tableStepScore);
                            Log.e(TAG, "onTouch: 当前题目总分" + TOTAL);
                            ToastUtils.showToast(context, "不能超过题目总分");
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (next) {
                    if ((event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS || event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER)
                            && !TextUtils.isEmpty(tableStepScore) && ((stepScoreModeScore + Double.valueOf(tableStepScore)) <= Double.valueOf(TOTAL))) {
                        if (isStepScore && !TextUtils.isEmpty(tableStepScore) && !"0".equals(tableStepScore)) {
                            if (childLocations == null) {
                                return true;
                            }
                            float zoomRatio = mSpenSimpleSurfaceView.getZoomRatio(); //当前缩放率
                            //判断是否在有效范围内
                            if (event.getRawX() >= (event.getRawX() - event.getX())
                                    && event.getRawX() <= ((event.getRawX() - event.getX()) + mSpenPageDoc.getWidth() * zoomRatio)
                                    && event.getRawY() >= (mSpenSimpleSurfaceView.getHeight() - mSpenPageDoc.getHeight()) / 2
                                    && event.getRawY() <= (((mSpenSimpleSurfaceView.getHeight() - mSpenPageDoc.getHeight()) / 2) + mSpenPageDoc.getHeight() * zoomRatio)) {
                                ChildLocation location = new ChildLocation();
                                //判断添加的标签是否超出了边界(标签的宽高)
                                if (event.getRawX() + 60 > ((event.getRawX() - event.getX()) + mSpenPageDoc.getWidth() * zoomRatio)
                                        || event.getRawY() + 40 > ((event.getRawY() - event.getY()) + mSpenPageDoc.getHeight() * zoomRatio)) {
                                    float x, y;
                                    if (event.getRawX() + 60 > ((event.getRawX() - event.getX()) + mSpenPageDoc.getWidth() * zoomRatio)) {
                                        x = ((event.getRawX() - event.getX()) + mSpenPageDoc.getWidth() * zoomRatio) - 60;
                                    } else {
                                        x = event.getRawX();
                                    }
                                    if (event.getRawY() + 40 > (((mSpenSimpleSurfaceView.getHeight() - mSpenPageDoc.getHeight()) / 2) + mSpenPageDoc.getHeight() * zoomRatio)) {
                                        y = ((mSpenSimpleSurfaceView.getHeight() - mSpenPageDoc.getHeight()) / 2 + mSpenPageDoc.getHeight() * zoomRatio) - 40;
                                    } else {
                                        y = event.getRawY();
                                    }
                                    location.setX(x);
                                    location.setY(y);
                                } else {
                                    location.setX(event.getRawX());
                                    location.setY(event.getRawY());
                                }
                                location.setTv(tableStepScore);
                                childLocations.add(location);
                                stepScoreModeScore = 0;
                                for (int i = 0; i < childLocations.size(); i++) {
                                    stepScoreModeScore += Double.valueOf(childLocations.get(i).getTv());
                                }
                                if (stepScoreModeScore == (int) stepScoreModeScore) {
                                    questionScore.setText(String.valueOf((int) stepScoreModeScore));
                                    saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf((int) stepScoreModeScore));
                                } else {
                                    questionScore.setText(String.valueOf(stepScoreModeScore));
                                    saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(stepScoreModeScore));
                                }
                                submiss.setVisibility(View.VISIBLE);
                                addViewOrLinstener();
                            } else {
                                Log.e(TAG, "onTouch: 超出了有效范围");
                            }
                        }
                    }
                }
                break;
        }
        return false;
    }
}
