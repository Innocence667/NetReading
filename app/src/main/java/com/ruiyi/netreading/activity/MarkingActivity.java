package com.ruiyi.netreading.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import com.ruiyi.netreading.MyApplication;
import com.ruiyi.netreading.adapter.DoubleScoreTableAdapter;
import com.ruiyi.netreading.adapter.QuestionNumAdapter;
import com.ruiyi.netreading.adapter.ReviewAdatper;
import com.ruiyi.netreading.adapter.ScoreAdapter;
import com.ruiyi.netreading.adapter.ScoreDetailsAdapter;
import com.ruiyi.netreading.adapter.SpenColorSAdapter;
import com.ruiyi.netreading.adapter.TopScoreAdapter;
import com.ruiyi.netreading.bean.CustomBean;
import com.ruiyi.netreading.bean.GradeData;
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
import com.ruiyi.netreading.util.Interfaces;
import com.ruiyi.netreading.util.LoadingUtil;
import com.ruiyi.netreading.util.LogUtils;
import com.ruiyi.netreading.util.PreferencesService;
import com.ruiyi.netreading.util.ToastUtils;
import com.ruiyi.netreading.util.Tool;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenObjectBase;
import com.samsung.android.sdk.pen.document.SpenObjectImage;
import com.samsung.android.sdk.pen.document.SpenObjectStroke;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;
import com.samsung.android.sdk.pen.pen.SpenPenManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MarkingActivity extends AppCompatActivity implements View.OnClickListener, DrawerLayout.DrawerListener, RadioGroup.OnCheckedChangeListener {

    private String TAG = "MarkingActivity";
    //标签类型
    private String tabType = TYPE_NONE;
    private static final String TYPE_NONE = "null"; //无
    private static final String TYPE_TICK = "tick"; //对
    private static final String TYPE_HALFTICK = "halfTick"; //半对
    private static final String TYPE_CROSS = "cross"; //错
    private static final String TYPE_STEPPOINTS = "stepPoints"; //标签
    private Context context;
    private Handler handler;
    private MyModel myModel;

    private AlertDialog.Builder builder;
    private PopupWindow popupWindow;

    private DrawerLayout drawerLayout;
    private RelativeLayout main; //主布局
    private LinearLayout reigth; //侧滑布局

    private TextView gobackTimeIcon, gobackScoreIcon; //回评列表筛选
    private int timeType = 0, scoreType = 0; //记录条件筛选的模式(0默认、1升序、2降序)
    private LinearLayout time, score; //排序
    private ListView reviewListView; //回评列表
    private TextView nodatahint; //没有回评数据提示

    private String teacherGuid;//教师guid
    private String taskGuid;//任务guid

    private int LOCATION = 0; //当前题目的进度(12/100中的12，回评模式用的到),是集合的下标
    private int minLocation = 0; //当前显示题目的位置(合并题：当前第几个小题；非合并题：当前位置为0)
    private boolean reviewMode; //是否是回评模式
    private boolean doubleMode; //双栏模式
    private String doubleScore = "0";//双栏模式选中的分值
    private String SCORE; //当前题目的得分
    private String TOTAL; //当前题目的总分
    private double MaxScore;//当前题目的最高分数(合并题目的最高分数)
    private String tableStepScore; //当前步骤分选中的标签
    private double stepScoreModeScore = 0;//步骤分模式得分
    private long scoreClickTime = 0; //打分点击的时间

    //笔迹模式
    private Map<Integer, String> strokeColor = new HashMap<>();//存放每个笔画的颜色

    //步骤分模式
    private Map<Integer, Double> tab = new HashMap<>();//存放标签中的分值
    private Map<Integer, StepScore> stepDatas = new HashMap<>();//标签数据

    private RelativeLayout spenView;
    private SpenNoteDoc mSpenNotDoc;
    private SpenPageDoc mSpenPageDoc;
    private SpenSurfaceView mSpenSimpleSurfaceView;
    private boolean next = false;//是否继续执行ACTION_UP操作
    private boolean isDoubleFinger = false;
    private boolean isSpenFeatureEnable = false;//是否支持手写笔
    private float scale; //图片缩放的倍数

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
    private Bitmap bitmap = null; //添加标签时的对象
    private CheckBox sideslip; //功能区折叠按钮
    private LinearLayout spenLayoutShow; //手写笔选中后的界面
    private RadioGroup radioGroup; //标识功能组
    private RadioButton spenRadio; //批注
    //对、半对、错、选择
    private RadioButton identification_dui, identification_bandui, identification_cuo;

    private LinearLayout clearParent;//清除父布局
    private CheckBox identification_objSelect; //选择
    private TextView clearAll;//清空全部

    private LinearLayout labelParent;//收藏异常父布局
    private CheckBox collection; //收藏
    private CheckBox abnormal; //异常
    private ImageView collectionImg;
    private TextView abnormalTv;

    private LinearLayout imageLocation; //图片选择父布局

    private HorizontalScrollView scrollView; //功能区
    //步骤分、批注、清除笔迹、收藏、回评、继续阅卷、评分详情、设置
    private RelativeLayout commentsP, rotateP, eliminateP, stepScoreP, collectionP, historyP, goOnParent, scoringDatailsP, settingP;
    private CheckBox stepScore; //步骤分
    private CheckBox rotateTv; //旋转
    private CheckBox comments; //批注
    private CheckBox eliminate;//清除笔记
    private CheckBox labe;//标签
    private TextView history; //回评
    private TextView goOn; //继续阅卷
    private TextView scoringDatails; //评分详情
    private TextView setting; //设置
    private AlertDialog settingDialog; //设置dialog
    private AlertDialog rotateDialog; //旋转图片dialog
    private boolean _stepMode = false;//点击设置之前当前的步骤分模式

    private List<ImageData> imageDataList; //存放每张图片的宽高
    private ImageData imageData;
    private List<String> imgPath; //存放线上考试选择图片时用到的图片url

    private GetMarkDataResponse response; //获取试卷试卷
    private GetMarkDataRequest request; //获取新数据请求模型
    private GetMarkNextStudentResponse getMarkNextStudentResponse; //获取新数据请求结果模型
    private SaveMarkDataBean saveMarkDataBean; //数据提交模型
    private SavaDataResponse saveResponse; //提交成功返回结果
    private List<SaveMarkDataBean.QuestionsBean> questionsBeanList; //新数据提交里的questions字段
    private List<GetMarkNextStudentResponse> cachePool = new ArrayList<>(); //存放数据的缓存池

    private ReviewStudentsResponse studentsResponse; //回评列表数据
    private ReviewAdatper reviewAdatper; //回评列表适配器

    private CollectRequest collectRequest;//收藏(取消收藏)请求模型
    private CollectRequest AbnormalRequest;//试卷异常(取消异常)请求模型

    //设置布局控件
    private LinearLayout seting_main;
    private ImageView setting_back;
    private LinearLayout pointFiveView;
    private Switch pointFiveSwitch;
    private LinearLayout stepModeView;
    private Switch stepModeSwitch;
    private LinearLayout autoSubmitView;
    private Switch autoSubmitSwitch;
    private LinearLayout topScoreView, penSetView;
    private CheckBox horizontalCheckBox, verticalCheckBox;

    private RelativeLayout topScoreSetting;//置顶分数页面
    private GridView topScoreGridView;
    private CheckBox topSocre_0_5;
    private TextView topScoreClear;
    private TextView topScoreDetermine;

    private LinearLayout penSetting; //绘画笔设置界面
    private TextView spenSize;
    private SeekBar spenSeekBar;
    private GridView spenColorGridView;

    private TopScoreAdapter topScoreAdapter;//置顶分数适配器
    private SpenColorSAdapter spenColorSAdapter;//手写笔颜色设置适配器

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
                            if (response.getTeacherTask().getMarkSum() != response.getTeacherTask().getTaskCount()) { //任务没有完成
                                if (response.getTeacherTask().getStyle() != 2) { //不是双评
                                    if (response.getTeacherTask().getStyle() == 1) { //单评
                                        normalMOde(); //正常阅卷
                                    } else { //按班
                                        if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount()) {
                                            normalMOde(); //正常阅卷
                                        } else {
                                            goBackMode(); //回评
                                        }
                                    }
                                } else { //双评
                                    if (response.getTeacherTask().getMarkNum() < response.getTeacherTask().getTaskCount()) {
                                        normalMOde();
                                    } else {
                                        goBackMode(); //回评
                                    }
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
                if (response.getTeacherTask().getMarkCount() == 0 || response.getTeacherTask().getIdentity() == 3) {
                    progressTips.setText(Html.fromHtml("<font color = '#245AD3'>第" + (response.getTeacherTask().getMarkNumber() + 1)
                            + "份</font>"));
                } else {
                    progressTips.setText(Html.fromHtml("<font color = '#245AD3'>" + (response.getTeacherTask().getMarkNumber() + 1)
                            + "</font>/" + response.getTeacherTask().getMarkCount()));
                }
            }
        });
        myModel.getMarkNextStudent(context, request, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                minLocation = 0;
                collectRequest = new CollectRequest();
                AbnormalRequest = new CollectRequest();
                getMarkNextStudentResponse = (GetMarkNextStudentResponse) model;
                cachePool.add(getMarkNextStudentResponse);
                //第一次进来缓存2张，之后每次提交缓存一张
                if (response.getTeacherTask().getMarkCount() == 0) { //自由阅卷
                    if (response.getTeacherTask().getMarkSum() < response.getTeacherTask().getTaskCount()) { //当前任务没有阅完
                        if (response.getTeacherTask().getTaskCount() - response.getTeacherTask().getMarkSum() >= 2) {
                            getNextStudentCache(true);
                        } else {
                            getNextStudentCache(false);
                        }
                    }
                } else { //有任务
                    if (response.getTeacherTask().getMarkNumber() < response.getTeacherTask().getMarkCount()) { //自己的任务没有阅完
                        if (response.getTeacherTask().getMarkCount() - response.getTeacherTask().getMarkNumber() >= 2) {
                            getNextStudentCache(true);
                        } else {
                            getNextStudentCache(false);
                        }
                    } else {
                        if (response.getTeacherTask().getStyle() == 1) {
                            if (response.getTeacherTask().getMarkSum() < response.getTeacherTask().getTaskCount()) {
                                if (response.getTeacherTask().getTaskCount() - response.getTeacherTask().getMarkSum() >= 2) {
                                    getNextStudentCache(true);
                                } else {
                                    getNextStudentCache(false);
                                }
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
                            collection.setTextColor(getResources().getColor(R.color.colorWhite));
                            collectionImg.setVisibility(View.VISIBLE);
                        } else {
                            collectRequest.setValue(String.valueOf(0));
                            collection.setChecked(false);
                            collection.setTextColor(getResources().getColor(R.color.colorScoreItem));
                            collectionImg.setVisibility(View.GONE);
                        }
                        if (getMarkNextStudentResponse.getData().getStudentData().isAbnormal()) {
                            AbnormalRequest.setValue(String.valueOf(1));
                            abnormal.setChecked(true);
                            abnormal.setTextColor(getResources().getColor(R.color.colorWhite));
                            abnormalTv.setVisibility(View.VISIBLE);
                        } else {
                            AbnormalRequest.setValue(String.valueOf(0));
                            abnormal.setChecked(false);
                            abnormal.setTextColor(getResources().getColor(R.color.colorScoreItem));
                            abnormalTv.setVisibility(View.GONE);
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
        Log.i("goBackMode", "goBackMode: 回评阅卷");
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
                    Looper.prepare();
                    ToastUtils.showToast(context, "暂无数据");
                    Looper.loop();
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
                                AbnormalRequest = new CollectRequest();
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
                                    collection.setTextColor(getResources().getColor(R.color.colorWhite));
                                    collectionImg.setVisibility(View.VISIBLE);
                                } else {
                                    collectRequest.setValue(String.valueOf(0));
                                    collection.setChecked(false);
                                    collection.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                    collectionImg.setVisibility(View.GONE);
                                }
                                if (getMarkNextStudentResponse.getData().getStudentData().isAbnormal()) {
                                    AbnormalRequest.setValue(String.valueOf(1));
                                    abnormal.setChecked(true);
                                    abnormal.setTextColor(getResources().getColor(R.color.colorWhite));
                                    abnormalTv.setVisibility(View.VISIBLE);
                                } else {
                                    AbnormalRequest.setValue(String.valueOf(0));
                                    abnormal.setChecked(false);
                                    abnormal.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                    abnormalTv.setVisibility(View.GONE);
                                }
                                initUpDateData(getMarkNextStudentResponse);
                                showSueecssPage(getMarkNextStudentResponse);
                                submiss.setEnabled(true);
                                if (response.getTeacherTask().getMarkCount() == 0 || response.getTeacherTask().getIdentity() == 3) {
                                    progressTips.setText(Html.fromHtml("<font color = '#245AD3'>第" + dataBeanList.size()
                                            + "份</font>"));
                                } else {
                                    progressTips.setText(Html.fromHtml("<font color = '#245AD3'>" + dataBeanList.size()
                                            + "</font>/" + response.getTeacherTask().getMarkCount()));
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
            bean.setGradeData(questionsBean.getGradeData());
            questionsBeanList.add(bean);
        }
        saveMarkDataBean.setQuestions(questionsBeanList);
    }

    private void initView() {
        drawerLayout = findViewById(R.id.drawerLayou);
        drawerLayout.addDrawerListener(this);
        //禁止侧滑
        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        main = findViewById(R.id.main);
        reigth = findViewById(R.id.reight);
        gobackTimeIcon = findViewById(R.id.gobackTimeIcon);
        gobackScoreIcon = findViewById(R.id.gobackScoreIcon);
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
        spenLayoutShow = findViewById(R.id.spenLayoutShow);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(this);
        spenRadio = findViewById(R.id.spenRadio);
        identification_dui = findViewById(R.id.identification_dui);
        identification_bandui = findViewById(R.id.identification_bandui);
        identification_cuo = findViewById(R.id.identification_cuo);
        identification_objSelect = findViewById(R.id.objsearch);
        clearParent = findViewById(R.id.clearParent);
        clearAll = findViewById(R.id.clear_all);
        identification_objSelect.setOnClickListener(this);
        clearAll.setOnClickListener(this);

        labelParent = findViewById(R.id.labelParent);
        abnormal = findViewById(R.id.abnormal);
        abnormal.setOnClickListener(this);
        collectionImg = findViewById(R.id.collectionImg);
        abnormalTv = findViewById(R.id.abnormalTv);

        imageLocation = findViewById(R.id.imageLocation);

        scrollView = findViewById(R.id.function);

        commentsP = findViewById(R.id.commentsP);
        commentsP.setOnClickListener(this);
        rotateP = findViewById(R.id.rotateP);
        rotateP.setOnClickListener(this);
        comments = findViewById(R.id.comments);
        comments.setOnClickListener(this);
        eliminateP = findViewById(R.id.eliminateP);
        eliminateP.setOnClickListener(this);
        eliminate = findViewById(R.id.eliminate);
        eliminate.setOnClickListener(this);
        labe = findViewById(R.id.label);
        labe.setOnClickListener(this);
        stepScoreP = findViewById(R.id.stepScoreP);
        stepScoreP.setOnClickListener(this);
        stepScore = findViewById(R.id.stepScore);
        stepScore.setOnClickListener(this);
        rotateTv = findViewById(R.id.rotateTv);
        rotateTv.setOnClickListener(this);
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
        Spen spen = new Spen();
        try {
            spen.initialize(context);
            isSpenFeatureEnable = spen.isFeatureEnabled(Spen.DEVICE_PEN);
        } catch (Exception e) {
            Toast.makeText(context, "此设备不支持Spen",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        spenView = findViewById(R.id.spenView);
        mSpenSimpleSurfaceView = new SpenSurfaceView(context);
        if (mSpenSimpleSurfaceView == null) {
            Toast.makeText(context, "无法创建新的SpenView",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        spenView.addView(mSpenSimpleSurfaceView);

        //获取屏幕尺寸
        final Rect rect = Tool.getScreenparameters(this);
        int myCanvasWidth = rect.width() - 80;
        int myCanvasHeigth = rect.height();
        initSpenNoteDoc(myCanvasWidth, myCanvasHeigth);

        mSpenSimpleSurfaceView.setBlankColor(ContextCompat.getColor(context, R.color.colorSpenSimpleSurfaceView));

        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
        penInfo.color = Color.parseColor(PreferencesService.getInstance(context).getSpenColor());
        penInfo.size = PreferencesService.getInstance(context).getSpenSize();
        mSpenSimpleSurfaceView.setPenSettingInfo(penInfo);

        if (!isSpenFeatureEnable) {
            mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_NONE);
            //Toast.makeText(context, "设备不支持Spen. \n 你可以用手指画笔画", Toast.LENGTH_SHORT).show();
        }
        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
        //mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_PEN_BUTTON, SpenSimpleSurfaceView.ACTION_ERASER);
        mSpenSimpleSurfaceView.setTouchListener(new SpenTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        isDoubleFinger = false;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        isDoubleFinger = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                }
                if (stepScore.isChecked()) { //步骤分模式
                    Log.i(TAG, "onTouch: 步骤分 ");
                    if (tabType.equals(TYPE_STEPPOINTS) && !isDoubleFinger) {
                        boolean isReduce = PreferencesService.getInstance(context).getStepMode();
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                next = true;
                                //第一个触摸是电子笔
                                if (event.getToolType(0) == MotionEvent.TOOL_TYPE_STYLUS || event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) {
                                    if (TextUtils.isEmpty(tableStepScore)) {
                                        //没有选择步骤分
                                        ToastUtils.showToast(context, "请先选择一个非0的标签分数");
                                        next = false;
                                    } else {
                                        stepScoreModeScore = 0;
                                        for (Map.Entry<Integer, Double> map : tab.entrySet()) {
                                            stepScoreModeScore += map.getValue();
                                        }
                                        if (PreferencesService.getInstance(context).getStepMode()) {
                                            Log.i(TAG, "onTouch当前得分是: " + (Double.parseDouble(TOTAL) - stepScoreModeScore));
                                            if (Double.parseDouble(TOTAL) + (stepScoreModeScore + (Double.parseDouble("-" + tableStepScore))) < 0) {
                                                if (mSpenSimpleSurfaceView.getToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN) != SpenSimpleSurfaceView.ACTION_SELECTION
                                                        && mSpenSimpleSurfaceView.getToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER) != SpenSimpleSurfaceView.ACTION_SELECTION) {
                                                    ToastUtils.showToast(context, "不能不能小于0分");
                                                }
                                                next = false;
                                            }
                                        } else {
                                            if (stepScoreModeScore + (Double.parseDouble(tableStepScore)) > Double.parseDouble(TOTAL)) {
                                                if (mSpenSimpleSurfaceView.getToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN) != SpenSimpleSurfaceView.ACTION_SELECTION
                                                        && mSpenSimpleSurfaceView.getToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER) != SpenSimpleSurfaceView.ACTION_SELECTION) {
                                                    ToastUtils.showToast(context, "不能超过题目总分");
                                                }
                                                next = false;
                                            }
                                        }
                                        if (mSpenSimpleSurfaceView.getToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN) == SpenSimpleSurfaceView.ACTION_SELECTION) {
                                            next = false;
                                        }
                                    }
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                if (next) {
                                    if (event.getRawX() >= (event.getRawX() - event.getX())
                                            && event.getRawX() <= ((event.getRawX() - event.getX()) + mSpenPageDoc.getWidth() * mSpenSimpleSurfaceView.getZoomRatio())
                                            && event.getRawY() >= (mSpenSimpleSurfaceView.getHeight() - mSpenPageDoc.getHeight()) / 2
                                            && event.getRawY() <= (((mSpenSimpleSurfaceView.getHeight() - mSpenPageDoc.getHeight()) / 2) + mSpenPageDoc.getHeight() * mSpenSimpleSurfaceView.getZoomRatio())) {
                                        SpenObjectImage objectImage = new SpenObjectImage();
                                        switch (String.valueOf(scoreAdapter.getChectValue())) {
                                            case "0.5":
                                                Log.i(TAG, "onTouch: 0.5");
                                                if (isReduce) {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce0_5);
                                                } else {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus0_5);
                                                }
                                                break;
                                            case "1.0":
                                                Log.i(TAG, "onTouch: 1.0");
                                                if (isReduce) {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce1);
                                                } else {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus1);
                                                }
                                                break;
                                            case "1.5":
                                                Log.i(TAG, "onTouch: 1.5");
                                                if (isReduce) {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce1_5);
                                                } else {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus1_5);
                                                }
                                                break;
                                            case "2.0":
                                                Log.i(TAG, "onTouch: 2.0");
                                                if (isReduce) {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce2);
                                                } else {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus2);
                                                }
                                                break;
                                            case "3.0":
                                                Log.i(TAG, "onTouch: 3.0");
                                                if (isReduce) {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce3);
                                                } else {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus3);
                                                }
                                                break;
                                            case "4.0":
                                                Log.i(TAG, "onTouch: 4.0");
                                                if (isReduce) {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce4);
                                                } else {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus4);
                                                }
                                                break;
                                            case "5.0":
                                                Log.i(TAG, "onTouch: 5.0");
                                                if (isReduce) {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce5);
                                                } else {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus5);
                                                }
                                                break;
                                            case "6.0":
                                                Log.i(TAG, "onTouch: 6.0");
                                                if (isReduce) {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce6);
                                                } else {
                                                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus6);
                                                }
                                                break;
                                        }
                                        if (bitmap == null) {
                                            Log.i(TAG, "onTouch: 标签图片是空的");
                                            return false;
                                        }
                                        objectImage.setImage(bitmap);
                                        //获取点击位置
                                        float x = event.getX();
                                        float y = event.getY();
                                        //获取画布的位移量
                                        float panX = mSpenSimpleSurfaceView.getPan().x;
                                        float panY = mSpenSimpleSurfaceView.getPan().y;
                                        //获取当前的缩放率
                                        float zoom = mSpenSimpleSurfaceView.getZoomRatio();
                                        float imgWidth = bitmap.getWidth() * zoom;
                                        float imgHeight = bitmap.getHeight() * zoom;
                                        RectF rectF = new RectF();
                                        rectF.set((x - imgWidth / 2) / zoom + panX,
                                                (y - imgHeight / 2) / zoom + panY,
                                                (x + imgWidth / 2) / zoom + panX,
                                                (y + imgHeight / 2) / zoom + panY);
                                        objectImage.setRect(rectF, true);
                                        objectImage.setResizeOption(SpenObjectBase.RESIZE_OPTION_DISABLE);
                                        objectImage.setRotatable(false);//不可旋转
                                        objectImage.setMovable(false);//不可移动
                                        mSpenPageDoc.appendObject(objectImage);
                                        mSpenSimpleSurfaceView.update();
                                        stepScoreModeScore = 0;
                                        for (Map.Entry<Integer, Double> map : tab.entrySet()) {
                                            stepScoreModeScore += map.getValue();
                                        }
                                        if (PreferencesService.getInstance(context).getStepMode()) {
                                            stepScoreModeScore = getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore() + stepScoreModeScore;
                                        }
                                        if (stepScoreModeScore == (int) stepScoreModeScore) {
                                            questionScore.setText(String.valueOf(((int) stepScoreModeScore)));
                                            saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(((int) stepScoreModeScore)));
                                            questionNumAdapter.updateScore(minLocation, String.valueOf(((int) stepScoreModeScore)));
                                        } else {
                                            questionScore.setText(String.valueOf(stepScoreModeScore));
                                            saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(stepScoreModeScore));
                                            questionNumAdapter.updateScore(minLocation, String.valueOf(stepScoreModeScore));
                                        }
                                        questionNumAdapter.notifyDataSetChanged();
                                        for (Map.Entry<Integer, Double> map : tab.entrySet()) {
                                            if (map.getValue() != 0) {
                                                submiss.setVisibility(View.VISIBLE);
                                                break;
                                            } else {
                                                submiss.setVisibility(View.GONE);
                                            }
                                        }
                                        return false;
                                    } else {
                                        Log.i(TAG, "onTouch: 超出了有效范围");
                                    }
                                }
                                break;
                        }
                    } else if (!TYPE_STEPPOINTS.equals(tabType) && !TYPE_NONE.equals(tabType)) {
                        if (event.getAction() == MotionEvent.ACTION_UP && !isDoubleFinger) {
                            SpenObjectImage objectImage = new SpenObjectImage();
                            objectImage.setImage(bitmap);
                            //获取点击位置
                            float x = event.getX();
                            float y = event.getY();
                            //获取画布的位移量
                            float panX = mSpenSimpleSurfaceView.getPan().x;
                            float panY = mSpenSimpleSurfaceView.getPan().y;
                            //获取当前的缩放率
                            float zoom = mSpenSimpleSurfaceView.getZoomRatio();
                            float imgWidth = bitmap.getWidth() * zoom;
                            float imgHeight = bitmap.getHeight() * zoom;
                            RectF rectF = new RectF();
                            rectF.set((x - imgWidth / 2) / zoom + panX,
                                    (y - imgHeight / 2) / zoom + panY,
                                    (x + imgWidth / 2) / zoom + panX,
                                    (y + imgHeight / 2) / zoom + panY);
                            objectImage.setRect(rectF, true);
                            objectImage.setResizeOption(SpenObjectBase.RESIZE_OPTION_DISABLE);
                            objectImage.setRotatable(false);//不可旋转
                            objectImage.setMovable(false);//不可移动
                            mSpenPageDoc.appendObject(objectImage);
                            mSpenSimpleSurfaceView.update();
                        }
                    }

                } else if (comments.isChecked()) { //是批注模式
                    if (event.getAction() == MotionEvent.ACTION_UP && !TYPE_STEPPOINTS.equals(tabType)
                            && !TYPE_NONE.equals(tabType) && !isDoubleFinger) {
                        SpenObjectImage objectImage = new SpenObjectImage();
                        objectImage.setImage(bitmap);
                        //获取点击位置
                        float x = event.getX();
                        float y = event.getY();
                        //获取画布的位移量
                        float panX = mSpenSimpleSurfaceView.getPan().x;
                        float panY = mSpenSimpleSurfaceView.getPan().y;
                        //获取当前的缩放率
                        float zoom = mSpenSimpleSurfaceView.getZoomRatio();
                        float imgWidth = bitmap.getWidth() * zoom;
                        float imgHeight = bitmap.getHeight() * zoom;
                        RectF rectF = new RectF();
                        rectF.set((x - imgWidth / 2) / zoom + panX,
                                (y - imgHeight / 2) / zoom + panY,
                                (x + imgWidth / 2) / zoom + panX,
                                (y + imgHeight / 2) / zoom + panY);
                        objectImage.setRect(rectF, true);
                        objectImage.setResizeOption(SpenObjectBase.RESIZE_OPTION_DISABLE);
                        objectImage.setRotatable(false);//不可旋转
                        objectImage.setMovable(false);//不可移动
                        mSpenPageDoc.appendObject(objectImage);
                        mSpenSimpleSurfaceView.update();
                    }
                }
                return false;
            }
        });
    }

    //初始化SpenPageView
    private void initSpenNoteDoc(int w, int h) {
        try {
            Log.i(TAG, "spenView的宽高是：" + w + "  --  " + h);
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
        mSpenPageDoc.setObjectListener(new SpenPageDoc.ObjectListener() {
            @Override
            public void onObjectAdded(SpenPageDoc spenPageDoc, ArrayList<SpenObjectBase> arrayList, int i1) {
                //type类型1=stroke、3=image
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getType() == SpenObjectBase.TYPE_IMAGE) {
                        if (isAddMode) { //是否是回评添加数据(切换小题)
                            tab.put(arrayList.get(i).getRuntimeHandle(), objectValue);
                        } else {
                            if (TYPE_STEPPOINTS.equals(tabType)) {
                                if (PreferencesService.getInstance(context).getStepMode()) {
                                    tab.put(arrayList.get(i).getRuntimeHandle(),
                                            scoreAdapter.getChectValue() == (int) scoreAdapter.getChectValue() ? (int) -scoreAdapter.getChectValue() : -scoreAdapter.getChectValue());
                                } else {
                                    tab.put(arrayList.get(i).getRuntimeHandle(),
                                            scoreAdapter.getChectValue() == (int) scoreAdapter.getChectValue() ? (int) scoreAdapter.getChectValue() : scoreAdapter.getChectValue());
                                }
                            } else {
                                tab.put(arrayList.get(i).getRuntimeHandle(), 0.0);
                            }
                        }
                        StepScore stepScore = new StepScore();
                        stepScore.setId(arrayList.get(i).getRuntimeHandle());
                        stepScore.setIndex(0);
                        if (isAddMode) {
                            stepScore.setField(field);
                            stepScore.setParams(objectValue);
                        } else {
                            stepScore.setField(tabType);
                            if (TYPE_STEPPOINTS.equals(tabType)) {
                                if (PreferencesService.getInstance(context).getStepMode()) {
                                    stepScore.setParams(-scoreAdapter.getChectValue());
                                } else {
                                    stepScore.setParams(scoreAdapter.getChectValue());
                                }
                            } else {
                                stepScore.setParams(0);
                            }
                        }
                        RectF drawnRect = arrayList.get(i).getDrawnRect();
                        stepScore.setX((drawnRect.left + ((drawnRect.right - drawnRect.left) / 2f)) / scale);
                        stepScore.setY((drawnRect.top + ((drawnRect.bottom - drawnRect.top) / 2f)) / scale);
                        stepDatas.put(arrayList.get(i).getRuntimeHandle(), stepScore);
                    } else if (arrayList.get(i).getType() == SpenObjectBase.TYPE_STROKE) {
                        arrayList.get(i).setSelectable(true); //可以选中
                        arrayList.get(i).setMovable(false); //禁止移动
                        arrayList.get(i).setRotatable(false); //禁止旋转
                        arrayList.get(i).setResizeOption(SpenObjectBase.RESIZE_OPTION_DISABLE);//禁止缩放
                        Log.e(TAG, "onObjectAdded: " + arrayList.get(i).getRect().toString());
                        if (addStrokeMode) {
                            strokeColor.put(arrayList.get(i).getRuntimeHandle(), "");
                        } else {
                            strokeColor.put(arrayList.get(i).getRuntimeHandle(), PreferencesService.getInstance(context).getSpenColor());
                        }
                    }
                }
            }

            @Override
            public void onObjectRemoved(SpenPageDoc spenPageDoc, ArrayList<SpenObjectBase> arrayList, int i) {
                for (int j = 0; j < arrayList.size(); j++) {
                    if (arrayList.get(i).getType() == 3) {
                        if (tab != null && tab.size() > 0) {
                            tab.remove(arrayList.get(i).getRuntimeHandle());
                            stepDatas.remove(arrayList.get(i).getRuntimeHandle());
                        }
                    }
                }
                if (stepScore.isChecked()) {
                    if (PreferencesService.getInstance(context).getStepMode()) {
                        stepScoreModeScore = Double.valueOf(TOTAL);
                    } else {
                        stepScoreModeScore = 0;
                    }
                    if (arrayList.size() > 1) {
                        tab.clear();
                        stepDatas.clear();
                    } else {
                        for (Map.Entry<Integer, Double> map : tab.entrySet()) {
                            stepScoreModeScore += map.getValue();
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
                }
            }

            @Override
            public void onObjectChanged(SpenPageDoc spenPageDoc, SpenObjectBase spenObjectBase, int i) {

            }
        });
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
     */
    private ScorePanel getScoreList(double score, double fullScore) {
        if (scoreAdapter != null) {
            scoreAdapter.setStepMode(PreferencesService.getInstance(context).getStepMode());
            scoreAdapter.notifyDataSetChanged();
        }
        Log.i(TAG, "getScoreList: 题目分数：" + fullScore + "   题目得分：" + score + "   是否是步骤分模式:" + stepScore.isChecked() + "  是否是步骤分减分模式？" + PreferencesService.getInstance(context).getStepMode());
        Log.i(TAG, "置顶分数: " + PreferencesService.getInstance(context).getTopScore());
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
        if (!stepScore.isChecked()) { //不是步骤分分模式
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
                        for (int i = 0; i < d + 0.5; i++) {
                            scores.add(String.valueOf(i));
                            if (d == i) {
                                if (Double.valueOf(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore()) - score == i) {
                                    if (i == 0) {
                                        scoresCheck.add(false);
                                    } else {
                                        scoresCheck.add(true);
                                    }
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
            for (int i = 0; i < 9; i++) {
                if (i == 0) {
                    scores.add(String.valueOf(0));
                } else if (i == 1) {
                    scores.add(String.valueOf((double) (i - 0.5)));
                } else if (i == 2) {
                    scores.add(String.valueOf((i - 1)));
                } else if (i == 3) {
                    scores.add(String.valueOf((double) (i - 1.5)));
                } else {
                    scores.add(String.valueOf((i - 2)));
                }
                scoresCheck.add(false);
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
        Tool.base64ToBitmap(nextStudentResponse, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                mSpenPageDoc.removeAllObject();
                final LocalImageData localImageData = (LocalImageData) model;
                Bitmap bitmap = BitmapFactory.decodeFile(localImageData.getPath());
                imageData = new ImageData(bitmap.getWidth(), bitmap.getHeight());
                Log.i(TAG, "本地图片的宽: " + bitmap.getWidth() + "  高:" + bitmap.getHeight());
                Log.i(TAG, "图片保存到本地：" + localImageData.getPath());

                Rect rect = Tool.getScreenparameters(MarkingActivity.this);
                Log.i(TAG, "当前屏幕的宽高是:" + rect.width() + "_" + rect.height());
                if (bitmap.getHeight() > rect.height() - Tool.getStatusBarHeight(context) || bitmap.getWidth() > rect.width() - 80) { //图片比屏幕大
                    scale = mSpenPageDoc.getWidth() * 1f / bitmap.getWidth();
                    if (bitmap.getWidth() > bitmap.getHeight()) { //宽图
                        initSpenNoteDoc(rect.width() - 80, bitmap.getHeight() * (rect.width() - 80) / bitmap.getWidth());
                    } else if (bitmap.getWidth() < bitmap.getHeight()) { //长图
                        scale = mSpenPageDoc.getWidth() * 1f / bitmap.getWidth();
                        if ("语文".equals(response.getTestpaper().getPaperName()) && bitmap.getHeight() > 2000) {
                            initSpenNoteDoc(1024 - 80, bitmap.getHeight() * (1024 - 80) / bitmap.getWidth());
                        } else {
                            initSpenNoteDoc(bitmap.getWidth() * rect.height() / bitmap.getHeight(), rect.height());
                        }
                    } else {
                        //可能存在方图，暂时不处理
                    }
                } else { //图片没有屏幕大
                    Log.i(TAG, "图片没有屏幕大 ");
                    initSpenNoteDoc(bitmap.getWidth(), bitmap.getHeight());
                    //自动填充屏幕大小
                    if (bitmap.getWidth() > bitmap.getHeight()) { //宽图
                        mSpenSimpleSurfaceView.setZoom(0, 0, (rect.width() - 80) * 1f / bitmap.getWidth());
                    } else { //高图
                        mSpenSimpleSurfaceView.setZoom(0, 0, rect.height() * 1f / bitmap.getHeight());
                    }
                    scale = bitmap.getWidth() * 1f / mSpenPageDoc.getWidth();
                }
                Log.i(TAG, "onSuccess当前缩放率是: " + scale);
                bitmap.recycle();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            //设置背景图片
                            mSpenPageDoc.setBackgroundImage(localImageData.getPath());
                            mSpenSimpleSurfaceView.update();
                            LoadingUtil.closeDialog();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }

            @Override
            public void onFailed(String str) {
                showFailedPage(str);
            }
        });

        //先判断是否有笔迹，再判断是否有步骤分
        if (!TextUtils.isEmpty(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getCoordinate())
                && nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getCoordinate().length() > 10) {
            addStroke(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getCoordinate());
            comments.setChecked(false);
            comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
            stepScore.setChecked(false);
            spenLayoutShow.setVisibility(View.GONE);
            stepScore.setTextColor(getResources().getColor(R.color.colorScoreItem));
            stepScore.setText("总 分");
            mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
            mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_GESTURE);
            PreferencesService.getInstance(context).saveAutoSubmit(true);
        }
        if (!TextUtils.isEmpty(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getStepScore())) {
            comments.setChecked(false);
            comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
            spenLayoutShow.setVisibility(View.GONE);
            if (nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getGradeData().contains("\"gradeMode\":3")) {
                tabType = TYPE_STEPPOINTS;
                stepScore.setChecked(true);
                stepScore.setText("步骤分");
                stepScore.setTextColor(getResources().getColor(R.color.colorBlue));
                PreferencesService.getInstance(context).saveAutoSubmit(false);
                submiss.setVisibility(View.VISIBLE);
            } else {
                tabType = TYPE_NONE;
                stepScore.setChecked(false);
                stepScore.setText("总 分");
                stepScore.setTextColor(getResources().getColor(R.color.colorScoreItem));
                PreferencesService.getInstance(context).saveAutoSubmit(true);
                submiss.setVisibility(View.GONE);
            }
            addStepScore(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getStepScore(),
                    nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getGradeData());
            mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
            mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_GESTURE);
        } else {
            if (comments.isChecked() && spenRadio.isChecked()) {
                mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_STROKE);
                mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_STROKE);
            } else {
                mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_GESTURE);
            }
        }
        initUI(nextStudentResponse);
    }

    //初始化当前页面UI
    private void initUI(final GetMarkNextStudentResponse getMarkNextStudentResponse1) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ("0".equals(getMarkNextStudentResponse1.getData().getIsOnline())) {
                    rotateP.setVisibility(View.GONE);
                } else {
                    rotateP.setVisibility(View.VISIBLE);
                }
                if (reviewMode) {
                    if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount()
                            && response.getTeacherTask().getTaskCount() != response.getTeacherTask().getMarkSum()) {
                        goOnParent.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(saveMarkDataBean.getQuestions().get(minLocation).getCoordinate())
                                && TextUtils.isEmpty(saveMarkDataBean.getQuestions().get(minLocation).getStepScore())) {
                            comments.setChecked(false);
                            comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
                            stepScore.setChecked(false);
                            stepScore.setTextColor(getResources().getColor(R.color.colorScoreItem));
                            stepScore.setText("总 分");
                            tabType = TYPE_NONE;
                        }
                    } else {
                        goOnParent.setVisibility(View.GONE);
                    }
                    eliminate.setChecked(false);
                    eliminate.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    labe.setChecked(false);
                    labe.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    spenLayoutShow.setVisibility(View.GONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_GESTURE);
                } else {
                    goOnParent.setVisibility(View.GONE);
                }

                //题号数据源
                strings = getQuestions(getMarkNextStudentResponse1.getData().getStudentData().getQuestions());
                doubleMode = false;
                //双栏模式开关是否开启
                if (getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getFullScore() >= 10) {
                    if (!stepScore.isChecked()) {
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
                        if (eliminate.isChecked() && identification_objSelect.isChecked()) {
                            identification_objSelect.setChecked(false);
                            identification_objSelect.setTextColor(getResources().getColor(R.color.colorScoreItem));
                            mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_NONE);
                            mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_FINGER, SpenSurfaceView.ACTION_GESTURE);
                        }
                        int loc = minLocation;
                        saveNowPageData(loc);
                        tab.clear();
                        stepDatas.clear();
                        mSpenPageDoc.removeAllObject();
                        mSpenSimpleSurfaceView.update();
                        stepScoreModeScore = 0;
                        // 小题切换
                        minLocation = positon;
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
                        double fullScore = getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getFullScore();
                        if (fullScore == (int) fullScore) {
                            TOTAL = String.valueOf((int) fullScore);
                        } else {
                            TOTAL = String.valueOf(fullScore);
                        }
                        doubleMode = false;
                        douleScoreCheckBox.setChecked(false);
                        if (fullScore > 9.5 && !stepScore.isChecked()) {
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
                            if (TextUtils.isEmpty(saveMarkDataBean.getQuestions().get(minLocation).getCoordinate())
                                    && TextUtils.isEmpty(saveMarkDataBean.getQuestions().get(minLocation).getStepScore())) {
                                mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                                comments.setChecked(false);
                                comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                stepScore.setChecked(false);
                                stepScore.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                stepScore.setText("总 分");
                            } else {
                                //添加步骤分
                                if (!TextUtils.isEmpty(saveMarkDataBean.getQuestions().get(minLocation).getStepScore())
                                        && saveMarkDataBean.getQuestions().get(minLocation).getStepScore().trim().length() > 10) {
                                    addStepScore(saveMarkDataBean.getQuestions().get(minLocation).getStepScore(),
                                            saveMarkDataBean.getQuestions().get(minLocation).getGradeData());
                                    if (saveMarkDataBean.getQuestions().get(minLocation).getGradeData().contains("\"gradeMode\":3")) {
                                        stepScore.setChecked(true);
                                        stepScore.setTextColor(getResources().getColor(R.color.colorBlue));
                                        stepScore.setText("步骤分");
                                    }
                                    if (saveMarkDataBean.getQuestions().get(minLocation).getGradeData().contains("true")) {
                                        PreferencesService.getInstance(context).saveStepMode(false);
                                    } else {
                                        PreferencesService.getInstance(context).saveStepMode(true);
                                    }
                                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                                }
                                //添加笔记
                                if (!TextUtils.isEmpty(saveMarkDataBean.getQuestions().get(minLocation).getCoordinate())) {
                                    addStroke(saveMarkDataBean.getQuestions().get(minLocation).getCoordinate());
                                    /*comments.setChecked(true);
                                    comments.setTextColor(getResources().getColor(R.color.colorBlue));
                                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_STROKE);*/
                                }
                            }
                            scoreAdapter.updataData(getScoreList(Double.valueOf(saveMarkDataBean.getQuestions().get(positon).getMarkScore()),
                                    getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(positon).getFullScore()));
                        } else {
                            scoreAdapter.updataData(getScoreList(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(positon).getScore(),
                                    getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(positon).getFullScore()));
                        }
                        if (stepScore.isChecked()) {
                            scoreAdapter.setStepScore(true);
                        } else {
                            scoreAdapter.setStepScore(false);
                        }
                        scoreAdapter.notifyDataSetChanged();
                    }
                });
                mRecyclerView.setAdapter(questionNumAdapter);
                double fullScore = getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(0).getFullScore();
                if (fullScore == (int) fullScore) {
                    TOTAL = String.valueOf((int) fullScore);
                } else {
                    TOTAL = String.valueOf(fullScore);
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
                //初始化题号
                if ("0".equals(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getSubNumber())) {
                    questionNum.setText(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getNumber() + "题");
                } else {
                    questionNum.setText(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getNumber() + "-"
                            + getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getSubNumber() + "题");
                }
                //初始化打分面板
                scorePanel = getScoreList(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getScore(),
                        getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getFullScore());
                scoreAdapter = new ScoreAdapter(context, scorePanel);
                if (!TextUtils.isEmpty(getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getGradeData())) {
                    if (getMarkNextStudentResponse1.getData().getStudentData().getQuestions().get(minLocation).getGradeData().contains("\"stepModeAdd\":true")) {
                        PreferencesService.getInstance(context).saveStepMode(false);
                        scoreAdapter.setStepMode(false);
                    } else {
                        PreferencesService.getInstance(context).saveStepMode(true);
                        scoreAdapter.setStepMode(true);
                    }
                } else {
                    scoreAdapter.setStepMode(PreferencesService.getInstance(context).getStepMode());
                }
                scoreAdapter.notifyDataSetChanged();
                if (stepScore.isChecked()) {
                    scoreAdapter.setStepScore(true);
                } else {
                    scoreAdapter.setStepScore(false);
                }
                if (!"-1".equals(SCORE) && !stepScore.isChecked()) {
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
                                if (eliminate.isChecked() && identification_objSelect.isChecked()) {
                                    identification_objSelect.setChecked(false);
                                    identification_objSelect.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_NONE);
                                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_FINGER, SpenSurfaceView.ACTION_GESTURE);
                                }

                                if (stepScore.isChecked()) {
                                    if (position == 0) {
                                        saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(0));
                                        submiss.setVisibility(View.VISIBLE);
                                        tableStepScore = "";
                                        questionNumAdapter.updateScore(minLocation, "0");
                                        questionNumAdapter.notifyDataSetChanged();
                                        saveMarkDataBean.getQuestions().get(minLocation).setStepScore("");
                                        stepScoreModeScore = 0;
                                        tab.clear();
                                        stepDatas.clear();
                                        strokeColor.clear();
                                        mSpenPageDoc.removeAllObject();
                                        mSpenSimpleSurfaceView.update();
                                        questionScore.setText("0");
                                    } else {
                                        tableStepScore = scorePanel.getScores().get(position);
                                    }
                                } else {
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

    //只有在回评或者修改小题时才会调用addStepScore方法，如果是true则不执行mSpenPageDoc的添加对象监听
    private boolean isAddMode = false;
    //当前标签上的分值(对勾类型默认为0分)
    private double objectValue = 0.0;
    //当前标签类型(对、半对、错、标签)
    private String field;

    /**
     * 添加步骤分数据
     *
     * @param stepScore 步骤分数据
     * @param gradeData 当前题目的操作模式
     */
    private void addStepScore(String stepScore, String gradeData) {
        Log.i(TAG, "addStepScore: 设置步骤分数据");
        Log.i(TAG, "stepScore: " + stepScore);
        Log.i(TAG, "gradeData: " + gradeData);
        List<StepScore> stepScores = new Gson().fromJson(stepScore, new TypeToken<List<StepScore>>() {
        }.getType());
        if (stepScores == null || stepScores.size() == 0) {
            return;
        }
        //选择步骤分提交时的标签为默认显示选中的标签选项
        GradeData gradeData1 = new Gson().fromJson(gradeData, GradeData.class);
        double d = Double.parseDouble(gradeData1.getStepLength().substring(1));
        if (d != (int) d) {
            tableStepScore = String.valueOf(d);
        } else {
            tableStepScore = String.valueOf((int) d);
        }
        isAddMode = true;
        for (int i = 0; i < stepScores.size(); i++) {
            //对号 tick、半对号 halfTick、错号 cross、文本  text、步骤分 stepPoints
            SpenObjectImage objectImage = new SpenObjectImage();
            Bitmap bitmap1 = null;
            if ("stepPoints".equals(stepScores.get(i).getField())) {
                if (gradeData.contains("true")) {
                    switch (String.valueOf(stepScores.get(i).getParams())) {
                        case "0.5":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus0_5);
                            break;
                        case "1":
                        case "1.0":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus1);
                            break;
                        case "1.5":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus1_5);
                            break;
                        case "2":
                        case "2.0":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus2);
                            break;
                        case "3":
                        case "3.0":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus3);
                            break;
                        case "4":
                        case "4.0":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus4);
                            break;
                        case "5":
                        case "5.0":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus5);
                            break;
                        case "6":
                        case "6.0":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.plus6);
                            break;
                        default:
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.correct);
                            break;
                    }
                } else {
                    switch (String.valueOf(stepScores.get(i).getParams())) {
                        case "-0.5":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce0_5);
                            break;
                        case "-1":
                        case "-1.0":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce1);
                            break;
                        case "-1.5":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce1_5);
                            break;
                        case "-2":
                        case "-2.0":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce2);
                            break;
                        case "-3":
                        case "-3.0":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce3);
                            break;
                        case "-4":
                        case "-4.0":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce4);
                            break;
                        case "-5":
                        case "-5.0":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce5);
                            break;
                        case "-6":
                        case "-6.0":
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.reduce6);
                            break;
                        default:
                            bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.correct);
                            break;
                    }
                }
            } else { //添加对、半对、错标签
                switch (stepScores.get(i).getField()) {
                    case "tick": //对
                        bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.correct);
                        break;
                    case "halfTick": //半对
                        bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.bandui);
                        break;
                    case "cross": //错
                        bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.erroe);
                        break;
                    case "": //文本
                        Log.i(TAG, "步骤分暂不支持文本格式");
                        break;
                }
            }
            objectValue = stepScores.get(i).getParams();
            //tab.put(stepScores.get(i).getId(), stepScores.get(i).getParams());
            //stepDatas.put(stepScores.get(i).getId(), stepScores.get(i));
            objectImage.setImage(bitmap1);
            float x = stepScores.get(i).getX() * scale;
            float y = stepScores.get(i).getY() * scale;
            float imgWidth = bitmap1.getWidth();
            float imgHeight = bitmap1.getHeight();
            RectF rectF = new RectF();
            rectF.set((x - imgWidth / 2f),
                    (y - imgHeight / 2f),
                    (x + imgWidth / 2f),
                    (y + imgHeight / 2f));
            objectImage.setRect(rectF, true);
            objectImage.setResizeOption(SpenObjectBase.RESIZE_OPTION_DISABLE);
            objectImage.setRotatable(false);//不可旋转
            objectImage.setMovable(false);//不可移动
            field = stepScores.get(i).getField();
            mSpenPageDoc.appendObject(objectImage);
            mSpenSimpleSurfaceView.update();
        }
        isAddMode = false;
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
                /*if (doubleMode && (scoreAdapter.getChectValue() == 0 || Double.parseDouble(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore())
                        == getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore())) {
                    submitData();
                } else {
                    submiss.setVisibility(View.VISIBLE);
                }*/
                submiss.setVisibility(View.VISIBLE);
            }
        } else {
            submiss.setVisibility(View.GONE);
            mSpenPageDoc.removeAllObject();
            mSpenSimpleSurfaceView.update();
            mRecyclerView.scrollToPosition(minLocation);
            tab.clear();
            stepDatas.clear();
            strokeColor.clear();
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
        if (mSpenPageDoc.getObjectList().size() != 0) {
            getPageDocObject(loc);
        }
    }

    private boolean addStrokeMode = false;
    //当前笔迹的颜色
    private List<String> colors;

    //给SpenPageDoc添加笔迹数据
    private void addStroke(String s) {
        Log.i(TAG, "addStroke: 添加笔迹数据  " + s);
        List<SpenStroke> list = new Gson().fromJson(s, new TypeToken<List<SpenStroke>>() {
        }.getType());
        if (list == null || list.size() == 0) {
            return;
        }
        ArrayList<SpenObjectBase> baseList = new ArrayList<>();
        addStrokeMode = true;
        colors = new ArrayList<>();
        strokeColor.clear();
        for (int i = 0; i < list.size(); i++) {
            SpenObjectStroke base = new SpenObjectStroke();
            base.setPenName(SpenPenManager.SPEN_INK_PEN);
            colors.add(list.get(i).getColor());
            base.setColor(Color.parseColor(list.get(i).getColor()));
            base.setPenSize(list.get(i).getPenSize());
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
                pointF.set(pointS[j].x * scale, pointS[j].y * scale);
                pointFS[j] = pointF;
            }
            base.setPoints(pointFS, yali, time);
            baseList.add(base);
        }
        mSpenPageDoc.appendObjectList(baseList);
        mSpenSimpleSurfaceView.update();
        addStrokeMode = false;
        int i = 0;
        for (Map.Entry<Integer, String> map : strokeColor.entrySet()) {
            strokeColor.put(map.getKey(), colors.get(i));
            i++;
        }
        Log.i(TAG, "addStroke结束后: " + new Gson().toJson(strokeColor));
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
                LogUtils.logI("showFailedPage", str);
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
            case R.id.titleNo: //小题列表收起
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
                    spenLayoutShow.setVisibility(View.GONE);
                } else {
                    sideslip.setChecked(false);
                    sideslip.setBackground(getResources().getDrawable(R.drawable.shut));
                    ObjectAnimator.ofFloat(scrollView, "translationX", -1024, 0)
                            .setDuration(100)
                            .start();
                    scrollView.setVisibility(View.VISIBLE);
                    if (comments.isChecked() || eliminate.isChecked() || labe.isChecked()) {
                        spenLayoutShow.setVisibility(View.VISIBLE);
                    } else {
                        spenLayoutShow.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.time: //时间排序
                if (reviewAdatper == null) {
                    return;
                }
                scoreType = 0;
                gobackScoreIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.order), null);
                if (timeType == 0) {
                    timeType = 1;
                    gobackTimeIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.order_by), null);
                    Collections.sort(studentsResponse.getData(), new Comparator<ReviewStudentsResponse.DataBean>() {
                        @Override
                        public int compare(ReviewStudentsResponse.DataBean o1, ReviewStudentsResponse.DataBean o2) {
                            if (o1.getTime().compareTo(o2.getTime()) < 0) {
                                return -1;
                            } else if (o1.getTime().compareTo(o2.getTime()) == 0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    });
                } else if (timeType == 1) {
                    timeType = 2;
                    gobackTimeIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.order_by_desc), null);
                    Collections.sort(studentsResponse.getData(), new Comparator<ReviewStudentsResponse.DataBean>() {
                        @Override
                        public int compare(ReviewStudentsResponse.DataBean o1, ReviewStudentsResponse.DataBean o2) {
                            if (o1.getTime().compareTo(o2.getTime()) < 0) {
                                return 1;
                            } else if (o1.getTime().compareTo(o2.getTime()) == 0) {
                                return 0;
                            } else {
                                return -1;
                            }
                        }
                    });
                } else {
                    timeType = 1;
                    gobackTimeIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.order_by), null);
                    Collections.sort(studentsResponse.getData(), new Comparator<ReviewStudentsResponse.DataBean>() {
                        @Override
                        public int compare(ReviewStudentsResponse.DataBean o1, ReviewStudentsResponse.DataBean o2) {
                            if (o1.getTime().compareTo(o2.getTime()) < 0) {
                                return -1;
                            } else if (o1.getTime().compareTo(o2.getTime()) == 0) {
                                return 0;
                            } else {
                                return 1;
                            }
                        }
                    });
                }
                reviewAdatper.notifyDataSetChanged();
                break;
            case R.id.score: //分数排序
                if (reviewAdatper == null) {
                    return;
                }
                timeType = 0;
                gobackTimeIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.order), null);
                if (scoreType == 0) {
                    scoreType = 1;
                    gobackScoreIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.order_by), null);
                    Collections.sort(studentsResponse.getData());
                } else if (scoreType == 1) {
                    scoreType = 2;
                    gobackScoreIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.order_by_desc), null);
                    Collections.sort(studentsResponse.getData());
                    Collections.reverse(studentsResponse.getData());
                } else {
                    scoreType = 1;
                    gobackScoreIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.order_by), null);
                    Collections.sort(studentsResponse.getData());
                }
                reviewAdatper.updateDatas(studentsResponse.getData());
                reviewAdatper.notifyDataSetChanged();
                break;
            case R.id.stepScore: //步骤分
                questionNumAdapter.updateScore(minLocation, String.valueOf(-1));
                questionNumAdapter.notifyDataSetChanged();
                if (stepScore.isChecked()) {
                    if (TYPE_NONE.equals(tabType)) {
                        tabType = TYPE_STEPPOINTS;
                    }
                    stepScoreModeScore = 0;
                    ToastUtils.showToast(context, "开启步骤分");
                    stepScore.setText("步骤分");
                    PreferencesService.getInstance(context).saveAutoSubmit(false);
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
                    stepScore.setText("步骤分");
                    if (comments.isChecked() && spenRadio.isChecked()) {
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_STROKE);
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_STROKE);
                    } else {
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_GESTURE);
                    }
                    scoreAdapter.updataData(getScoreList(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore(), 0));
                    scoreAdapter.setStepScore(true);
                    scoreAdapter.setScoreCheck(-1);
                    saveMarkDataBean.getQuestions().get(minLocation).setMarkScore("-1");
                    saveMarkDataBean.getQuestions().get(minLocation).setCoordinate("");
                    questionScore.setText(String.valueOf(0));
                } else {
                    ToastUtils.showToast(context, "关闭步骤分");
                    stepScore.setChecked(false);
                    stepScore.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    stepScore.setText("总 分");
                    if (comments.isChecked()) {
                        if (spenRadio.isChecked()) {
                            tabType = TYPE_NONE;
                        } else {
                            if (identification_dui.isChecked()) {
                                tabType = TYPE_TICK;
                            }
                            if (identification_bandui.isChecked()) {
                                tabType = TYPE_HALFTICK;
                            }
                            if (identification_cuo.isChecked()) {
                                tabType = TYPE_CROSS;
                            }
                        }
                    } else {
                        tabType = TYPE_NONE;
                    }
                    tableStepScore = null;
                    if (getMarkNextStudentResponse.getData().getStudentData().getQuestions().size() == 1) {
                        PreferencesService.getInstance(context).saveAutoSubmit(true);
                    } else {
                        PreferencesService.getInstance(context).saveAutoSubmit(false);
                    }
                    submiss.setVisibility(View.GONE);
                    if (getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore() > 9) {
                        soubleLayout.setVisibility(View.VISIBLE);
                    } else {
                        soubleLayout.setVisibility(View.GONE);
                    }
                    tab.clear();
                    stepDatas.clear();
                    strokeColor.clear();
                    mSpenPageDoc.removeAllObject();
                    mSpenSimpleSurfaceView.update();
                    saveMarkDataBean.getQuestions().get(minLocation).setStepScore("");
                    if (comments.isChecked() && spenRadio.isChecked()) {
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_STROKE);
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_STROKE);
                    } else {
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_GESTURE);
                    }
                    saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(-1));
                    scoreAdapter.updataData(getScoreList(-1, getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore()));
                    scoreAdapter.setStepScore(false);
                    scoreAdapter.setScoreCheck(-1);
                    questionScore.setText(String.valueOf(0));
                }
                scoreAdapter.notifyDataSetChanged();
                break;
            case R.id.rotateP:
            case R.id.rotateTv: //图片旋转
                if (rotateTv.isChecked()) {
                    rotateTv.setChecked(true);
                    rotateTv.setTextColor(getResources().getColor(R.color.colorBlue));
                    spenLayoutShow.setVisibility(View.VISIBLE);
                    radioGroup.setVisibility(View.GONE);
                    clearParent.setVisibility(View.GONE);
                    labelParent.setVisibility(View.GONE);
                    imageLocation.setVisibility(View.VISIBLE);
                    comments.setChecked(false);
                    eliminate.setChecked(false);
                    labe.setChecked(false);
                    comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    eliminate.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    labe.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_NONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_FINGER, SpenSurfaceView.ACTION_GESTURE);
                    tabType = TYPE_NONE;
                    imgPath = new ArrayList<>();
                    for (int i = 0; i < getMarkNextStudentResponse.getData().getImageArr().size(); i++) {
                        String str = getMarkNextStudentResponse.getData().getImageArr().get(i).getSrc();
                        imgPath.add(str.substring(str.indexOf("YunExam") + 7));
                    }
                    imageLocation.removeAllViews();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view = LayoutInflater.from(context).inflate(R.layout.rotateimg_layout, null);
                    builder.setView(view);
                    rotateDialog = builder.create();
                    LinearLayout linearLayout = view.findViewById(R.id.imgLocation);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(40, 40);
                    params.setMargins(5, 0, 5, 0);
                    for (int i = 0; i < getMarkNextStudentResponse.getData().getImageArr().size(); i++) {
                        final TextView tv = new TextView(context);
                        tv.setText(String.valueOf((getMarkNextStudentResponse.getData().getImageArr().get(i).getIndex() + 1)));
                        tv.setTextColor(getResources().getColor(R.color.colorWhite));
                        tv.setTextSize(18);
                        tv.setTag(i);
                        tv.setGravity(Gravity.CENTER);
                        tv.setBackground(ContextCompat.getDrawable(context, R.drawable.begin_btn_style));
                        tv.setLayoutParams(params);
                        //linearLayout.addView(tv);
                        imageLocation.addView(tv);
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LoadingUtil.showDialog(context);
                                RotatePicture((Integer) tv.getTag());
                                rotateDialog.dismiss();
                            }
                        });
                    }
                    //rotateDialog.show();
                } else {
                    rotateTv.setChecked(false);
                    rotateTv.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    spenLayoutShow.setVisibility(View.GONE);
                    if (stepScore.isChecked()) {
                        tabType = TYPE_STEPPOINTS;
                    } else {
                        tabType = TYPE_NONE;
                    }
                }
                break;
            case R.id.comments: //批注
                if (comments.isChecked()) {
                    comments.setChecked(true);
                    comments.setTextColor(getResources().getColor(R.color.colorBlue));
                    eliminate.setChecked(false);
                    eliminate.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    labe.setChecked(false);
                    labe.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    rotateTv.setChecked(false);
                    rotateTv.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    spenLayoutShow.setVisibility(View.VISIBLE);
                    radioGroup.setVisibility(View.VISIBLE);
                    clearParent.setVisibility(View.GONE);
                    labelParent.setVisibility(View.GONE);
                    imageLocation.setVisibility(View.GONE);
                    identification_objSelect.setChecked(false);
                    spenRadio.setChecked(true);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_STROKE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_FINGER, SpenSurfaceView.ACTION_STROKE);
                    tabType = TYPE_NONE;
                } else {
                    comments.setChecked(false);
                    comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    spenLayoutShow.setVisibility(View.GONE);
                    if (stepScore.isChecked()) {
                        tabType = TYPE_STEPPOINTS;
                    } else {
                        tabType = TYPE_NONE;
                    }
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_NONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_FINGER, SpenSurfaceView.ACTION_GESTURE);
                }
                break;
            case R.id.eliminateP:
            case R.id.eliminate:
                if (eliminate.isChecked()) {
                    comments.setChecked(false);
                    comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    eliminate.setChecked(true);
                    eliminate.setTextColor(getResources().getColor(R.color.colorBlue));
                    labe.setChecked(false);
                    labe.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    rotateTv.setChecked(false);
                    rotateTv.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    identification_objSelect.setChecked(false);
                    identification_objSelect.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    clearAll.setText("清除所有");
                    spenLayoutShow.setVisibility(View.VISIBLE);
                    radioGroup.setVisibility(View.GONE);
                    clearParent.setVisibility(View.VISIBLE);
                    labelParent.setVisibility(View.GONE);
                    imageLocation.setVisibility(View.GONE);
                    identification_objSelect.setChecked(false);
                    //spenRadio.setChecked(false);
                    tabType = TYPE_NONE;
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_NONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_FINGER, SpenSurfaceView.ACTION_GESTURE);
                } else {
                    eliminate.setChecked(false);
                    eliminate.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    spenLayoutShow.setVisibility(View.GONE);
                    if (stepScore.isChecked()) {
                        tabType = TYPE_STEPPOINTS;
                    }
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_NONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_FINGER, SpenSurfaceView.ACTION_GESTURE);
                }
                break;
            case R.id.collectionP:
            case R.id.label:
                if (labe.isChecked()) {
                    comments.setChecked(false);
                    comments.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    eliminate.setChecked(false);
                    eliminate.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    labe.setChecked(true);
                    labe.setTextColor(getResources().getColor(R.color.colorBlue));
                    rotateTv.setChecked(false);
                    rotateTv.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    spenLayoutShow.setVisibility(View.VISIBLE);
                    radioGroup.setVisibility(View.GONE);
                    clearParent.setVisibility(View.GONE);
                    labelParent.setVisibility(View.VISIBLE);
                    imageLocation.setVisibility(View.GONE);
                    identification_objSelect.setChecked(false);
                    //spenRadio.setChecked(false);
                    tabType = TYPE_NONE;
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_NONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_FINGER, SpenSurfaceView.ACTION_GESTURE);
                } else {
                    labe.setChecked(false);
                    labe.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    spenLayoutShow.setVisibility(View.GONE);
                    if (stepScore.isChecked()) {
                        tabType = TYPE_STEPPOINTS;
                    } else {
                        tabType = TYPE_NONE;
                    }
                }
                break;
            case R.id.clear_all: //清除笔记
                if (mSpenPageDoc.getObjectList().size() == 0) {
                    ToastUtils.showToast(context, "页面没有数据");
                } else {
                    Log.i(TAG, "onClick: " + mSpenSimpleSurfaceView.getToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN));
                    if (mSpenSimpleSurfaceView.getToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN) == SpenSimpleSurfaceView.ACTION_SELECTION) {
                        mSpenPageDoc.removeSelectedObject();
                        mSpenSimpleSurfaceView.closeControl();
                        mSpenSimpleSurfaceView.update();
                    } else {
                        mSpenPageDoc.removeAllObject();
                        mSpenSimpleSurfaceView.update();
                        if (stepScore.isChecked()) {
                            tab.clear();
                            stepDatas.clear();
                            stepScoreModeScore = 0;
                            questionScore.setText("0");
                            questionNumAdapter.updateScore(minLocation, "0");
                            questionNumAdapter.notifyDataSetChanged();
                            saveMarkDataBean.getQuestions().get(minLocation).setMarkScore("-1");
                            saveMarkDataBean.getQuestions().get(minLocation).setStepScore(null);
                        }
                        saveMarkDataBean.getQuestions().get(minLocation).setCoordinate(null);
                        ToastUtils.showToast(context, "清除成功");
                        if (stepScore.isChecked()) {
                            submiss.setVisibility(View.GONE);
                        }
                    }
                }
                break;
            case R.id.objsearch: //选择对象
                if (identification_objSelect.isChecked()) {
                    identification_objSelect.setChecked(true);
                    identification_objSelect.setTextColor(getResources().getColor(R.color.colorWhite));
                    clearAll.setText("删 除");
                    bitmap = null;
                    identification_dui.setChecked(false);
                    identification_bandui.setChecked(false);
                    identification_cuo.setChecked(false);
                    tabType = TYPE_NONE;
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_SELECTION);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_FINGER, SpenSurfaceView.ACTION_SELECTION);
                } else {
                    identification_objSelect.setChecked(false);
                    identification_objSelect.setTextColor(getResources().getColor(R.color.colorScoreItem));
                    clearAll.setText("清除所有");
                    bitmap = null;
                    tabType = TYPE_NONE;
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_NONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_FINGER, SpenSurfaceView.ACTION_GESTURE);
                }
                break;
            case R.id.collection: //收藏
                collectRequest.setStudentGuid(getMarkNextStudentResponse.getData().getStudentData().getStudentGuid());
                collectRequest.setTaskGuid(taskGuid);
                collectRequest.setIdentity(response.getTeacherTask().getIdentity());
                int type = 0;
                if ("0".equals(collectRequest.getValue())) {
                    collectRequest.setValue(String.valueOf(1));
                    type = 1;
                } else {
                    collectRequest.setValue(String.valueOf(0));
                    type = 0;
                }

                final int finalType = type;
                myModel.collectQuestion(context, collectRequest, 1, new MyCallBack() {
                    @Override
                    public void onSuccess(Object model) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalType == 0) {
                                    ToastUtils.showToast(context, "取消收藏");
                                    collectionImg.setVisibility(View.GONE);
                                    collection.setChecked(false);
                                    collection.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                } else {
                                    ToastUtils.showToast(context, "收藏成功");
                                    collectionImg.setVisibility(View.VISIBLE);
                                    collection.setChecked(true);
                                    collection.setTextColor(getResources().getColor(R.color.colorWhite));
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
            case R.id.abnormal: //异常
                AbnormalRequest.setStudentGuid(getMarkNextStudentResponse.getData().getStudentData().getStudentGuid());
                AbnormalRequest.setTaskGuid(taskGuid);
                AbnormalRequest.setIdentity(response.getTeacherTask().getIdentity());
                int type1 = 0;
                if ("0".equals(AbnormalRequest.getValue())) {
                    AbnormalRequest.setValue(String.valueOf(1));
                    type1 = 1;
                } else {
                    AbnormalRequest.setValue(String.valueOf(0));
                    type1 = 0;
                }

                final int finalType1 = type1;
                myModel.collectQuestion(context, AbnormalRequest, 2, new MyCallBack() {
                    @Override
                    public void onSuccess(Object model) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalType1 == 0) {
                                    ToastUtils.showToast(context, "取消异常");
                                    abnormalTv.setVisibility(View.GONE);
                                    abnormal.setChecked(false);
                                    abnormal.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                } else {
                                    ToastUtils.showToast(context, "试卷异常");
                                    abnormalTv.setVisibility(View.VISIBLE);
                                    abnormal.setChecked(true);
                                    abnormal.setTextColor(getResources().getColor(R.color.colorWhite));
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
                timeType = scoreType = 0;
                gobackTimeIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.order), null);
                gobackScoreIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.order), null);
                showReviewList();
                break;
            case R.id.goOn: //继续阅卷
                LoadingUtil.showDialog(context);
                stepScoreModeScore = 0;
                tab.clear();
                stepDatas.clear();
                strokeColor.clear();
                getNewStudent();
                break;
            case R.id.settingP: //设置
            case R.id.setting: //设置
                _stepMode = PreferencesService.getInstance(context).getStepMode();
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
                if (topScoreSetting.getVisibility() == View.VISIBLE) {
                    //保存设置的置顶分数
                    PreferencesService.getInstance(context).saveTopScore(topScoreAdapter.getTopScoreDate());
                }
                setting_back.setVisibility(View.GONE);
                topScoreSetting.setVisibility(View.GONE);
                penSetting.setVisibility(View.GONE);
                seting_main.setVisibility(View.VISIBLE);
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
            case R.id.stepModeView:
                if (stepModeSwitch.isChecked()) {
                    stepModeSwitch.setChecked(false);
                    ToastUtils.showTopToast(context, Html.fromHtml("<font color = '#FF0000'>关闭</font>步骤分减分模式"), R.style.Toast_Animation);
                } else {
                    stepModeSwitch.setChecked(true);
                    ToastUtils.showTopToast(context, Html.fromHtml("<font color = '#FF0000'>开启</font>步骤分减分模式"), R.style.Toast_Animation);
                }
                PreferencesService.getInstance(context).saveStepMode(stepModeSwitch.isChecked());
                break;
            case R.id.stepModeSwitch:
                if (stepModeSwitch.isChecked()) {
                    stepModeSwitch.setChecked(true);
                    ToastUtils.showTopToast(context, Html.fromHtml("<font color = '#FF0000'>开启</font>步骤分减分模式"), R.style.Toast_Animation);
                } else {
                    stepModeSwitch.setChecked(false);
                    ToastUtils.showTopToast(context, Html.fromHtml("<font color = '#FF0000'>关闭</font>步骤分减分模式"), R.style.Toast_Animation);
                }
                PreferencesService.getInstance(context).saveStepMode(stepModeSwitch.isChecked());
                break;
            case R.id.autoSubmitSwitch: //自动提交switch
                if (stepScore.isChecked() || doubleMode) {
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
            case R.id.penSetView:
                spenSeekBar.setProgress(PreferencesService.getInstance(context).getSpenSize());
                seting_main.setVisibility(View.GONE);
                setting_back.setVisibility(View.VISIBLE);
                topScoreSetting.setVisibility(View.GONE);
                penSetting.setVisibility(View.VISIBLE);
                spenColorSAdapter = new SpenColorSAdapter(context, MyApplication.getColors());
                spenColorGridView.setAdapter(spenColorSAdapter);
                for (int i = 0; i < MyApplication.getColors().size(); i++) {
                    if (PreferencesService.getInstance(context).getSpenColor().equals(MyApplication.getColors().get(i))) {
                        spenColorSAdapter.setPos(i);
                        spenColorSAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                spenColorSAdapter.setMyClickListener(new SpenColorSAdapter.MyClickListener() {
                    @Override
                    public void myOnclickListenet(int position) {
                        spenColorSAdapter.setPos(position);
                        spenColorSAdapter.notifyDataSetChanged();
                        PreferencesService.getInstance(context).saveSpenColor(MyApplication.getColors().get(position));
                    }
                });
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
            case R.id.horizontalCheckBox:
                if (horizontalCheckBox.isChecked()) {
                    verticalCheckBox.setChecked(false);
                    MarkingActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    verticalCheckBox.setChecked(true);
                    MarkingActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                PreferencesService.getInstance(context).saveScreenType(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case R.id.verticalCheckBox:
                if (verticalCheckBox.isChecked()) {
                    horizontalCheckBox.setChecked(false);
                    MarkingActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    horizontalCheckBox.setChecked(true);
                    MarkingActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                PreferencesService.getInstance(context).saveScreenType(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
                                questionNumAdapter.updateScore(minLocation, String.valueOf((int) value));
                                questionNumAdapter.notifyDataSetChanged();
                                SCORE = String.valueOf((int) value);
                            } else {
                                saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(value));
                                questionScore.setText(String.valueOf(value));
                                questionNumAdapter.updateScore(minLocation, String.valueOf(value));
                                questionNumAdapter.notifyDataSetChanged();
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
                        if (stepScore.isChecked()) {

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
                questionNumAdapter.updateScore(minLocation, TOTAL);
                questionNumAdapter.notifyDataSetChanged();
                saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(TOTAL);
                SCORE = TOTAL;
                questionScore.setText(TOTAL);
                if ((Double.valueOf(TOTAL) - integers.get(integers.size() - 1)) > 9) {
                    scoreAdapter.updataData(getScoreList(integers.get(integers.size() - 1), 10));
                } else {
                    scoreAdapter.updataData(getScoreList(integers.get(integers.size() - 1), Double.valueOf(TOTAL) - Double.valueOf(doubleScore) + 1));
                }
                scoreAdapter.notifyDataSetChanged();
                double_markScore.setEnabled(false);
                //submiss.setVisibility(View.VISIBLE);
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
                //防止连续点击
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    double_markScore.setEnabled(true);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }

    //选择图片请求
    private void RotatePicture(int index) {
        myModel.RotateAnswerImg(Interfaces.getInstance(context).ROTOTEANSWERIMG, "?imgUrl=" + imgPath.get(index) + "&angle=-90", new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                String json = (String) model;
                if (json.contains("\"code\":200") && json.contains("\"data\":true")) {
                    Tool.base64ToBitmap(getMarkNextStudentResponse, new MyCallBack() {
                        @Override
                        public void onSuccess(Object model) {
                            mSpenPageDoc.removeAllObject();
                            LocalImageData localImageData = (LocalImageData) model;
                            Bitmap bitmap = BitmapFactory.decodeFile(localImageData.getPath());
                            imageData = new ImageData(bitmap.getWidth(), bitmap.getHeight());
                            Log.i(TAG, "本地图片的宽: " + bitmap.getWidth() + "  高:" + bitmap.getHeight());
                            Log.i(TAG, "图片保存到本地：" + localImageData.getPath());

                            Rect rect = Tool.getScreenparameters(MarkingActivity.this);
                            Log.i(TAG, "当前屏幕的宽高是:" + rect.width() + "_" + rect.height());
                            if (bitmap.getHeight() > rect.height() - Tool.getStatusBarHeight(context) || bitmap.getWidth() > rect.width() - 80) { //图片比屏幕大
                                scale = mSpenPageDoc.getWidth() * 1f / bitmap.getWidth();
                                if (bitmap.getWidth() > bitmap.getHeight()) { //宽图
                                    initSpenNoteDoc(rect.width() - 80, bitmap.getHeight() * (rect.width() - 80) / bitmap.getWidth());
                                } else if (bitmap.getWidth() < bitmap.getHeight()) { //长图
                                    scale = mSpenPageDoc.getWidth() * 1f / bitmap.getWidth();
                                    if ("语文".equals(response.getTestpaper().getPaperName()) && bitmap.getHeight() > 2000) {
                                        initSpenNoteDoc(1024 - 80, bitmap.getHeight() * (1024 - 80) / bitmap.getWidth());
                                    } else {
                                        initSpenNoteDoc(bitmap.getWidth() * rect.height() / bitmap.getHeight(), rect.height());
                                    }
                                } else {
                                    //可能存在方图，暂时不处理
                                }
                            } else { //图片没有屏幕大
                                Log.i(TAG, "图片没有屏幕大 ");
                                initSpenNoteDoc(bitmap.getWidth(), bitmap.getHeight());
                                //自动填充屏幕大小
                                if (bitmap.getWidth() > bitmap.getHeight()) { //宽图
                                    mSpenSimpleSurfaceView.setZoom(0, 0, (rect.width() - 80) * 1f / bitmap.getWidth());
                                } else { //高图
                                    mSpenSimpleSurfaceView.setZoom(0, 0, rect.height() * 1f / bitmap.getHeight());
                                }
                                scale = bitmap.getWidth() * 1f / mSpenPageDoc.getWidth();
                            }
                            Log.i(TAG, "onSuccess当前缩放率是: " + scale);

                            //设置背景图片
                            mSpenPageDoc.setBackgroundImage(localImageData.getPath());
                            mSpenSimpleSurfaceView.update();
                            saveMarkDataBean.getQuestions().get(minLocation).setGradeData(null);
                            saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(0));
                            saveMarkDataBean.getQuestions().get(minLocation).setStepScore(null);
                            saveMarkDataBean.getQuestions().get(minLocation).setCoordinate(null);
                            /*new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                        LoadingUtil.closeDialog();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();*/
                        }

                        @Override
                        public void onFailed(final String str) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showFailedPage(str);
                                    LoadingUtil.closeDialog();
                                }
                            });
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(context, "操作失败");
                            LoadingUtil.closeDialog();
                        }
                    });
                }
            }

            @Override
            public void onFailed(String str) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(context, "操作失败");
                        LoadingUtil.closeDialog();
                    }
                });
            }
        });
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
                            //需要弹出弹出，进行重新获取，暂时先提示用户
                            ToastUtils.showTopToast(context, "题目获取异常，请返回主页重新获取", R.style.Toast_Animation);
                            return;
                        }
                        MaxScore = getMaxScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (getMarkNextStudentResponse.getData().getStudentData().isCollect()) {
                                    collectRequest.setValue(String.valueOf(1));
                                    collection.setChecked(true);
                                    collection.setTextColor(getResources().getColor(R.color.colorWhite));
                                    collectionImg.setVisibility(View.VISIBLE);
                                } else {
                                    collectRequest.setValue(String.valueOf(0));
                                    collection.setChecked(false);
                                    collection.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                    collectionImg.setVisibility(View.GONE);
                                }
                                if (getMarkNextStudentResponse.getData().getStudentData().isAbnormal()) {
                                    AbnormalRequest.setValue(String.valueOf(1));
                                    abnormal.setChecked(true);
                                    abnormal.setTextColor(getResources().getColor(R.color.colorWhite));
                                    abnormalTv.setVisibility(View.VISIBLE);
                                } else {
                                    AbnormalRequest.setValue(String.valueOf(0));
                                    abnormal.setChecked(false);
                                    abnormal.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                    abnormalTv.setVisibility(View.GONE);
                                }
                            }
                        });
                        initSaveData(getMarkNextStudentResponse);
                        showSueecssPage(getMarkNextStudentResponse);
                        submiss.setVisibility(View.GONE);
                        submiss.setEnabled(true);
                        if (response.getTeacherTask().getMarkCount() == 0 || response.getTeacherTask().getIdentity() == 3) {
                            progressTips.setText(Html.fromHtml("<font color = '#245AD3'>第" + (response.getTeacherTask().getMarkNumber() + 1)
                                    + "份</font>"));
                        } else {
                            progressTips.setText(Html.fromHtml("<font color = '#245AD3'>" + (response.getTeacherTask().getMarkNumber() + 1)
                                    + "</font>/" + response.getTeacherTask().getMarkCount()));
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

    //获取当前画布上的笔记数据
    private void getPageDocObject(int loc) {
        //坐标点要按照原图的大小坐标来计算
        ArrayList<SpenObjectBase> objectList = mSpenPageDoc.getObjectList();
        if (objectList != null || objectList.size() > 0) {
            List<SpenStroke> spenStrokes = new ArrayList<>();
            List<StepScore> stepScores = new ArrayList<>();
            //添加笔迹
            for (int i = 0; i < objectList.size(); i++) {
                if (objectList.get(i).getType() == SpenObjectBase.TYPE_STROKE) {
                    SpenObjectStroke spenObjectStroke = (SpenObjectStroke) objectList.get(i);
                    SpenStroke stroke = new SpenStroke();
                    stroke.setPenSize(spenObjectStroke.getPenSize());
                    //Color.RED = 0xFFFF0000
                    stroke.setColor(strokeColor.get(objectList.get(i).getRuntimeHandle()));
                    PointF[] points = spenObjectStroke.getPoints();
                    if (getMarkNextStudentResponse.getData().getImageArr().size() == 1) {
                        stroke.setPageName(getMarkNextStudentResponse.getData().getImageArr().get(0).getPageName());
                    } else {
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
                    }
                    PointF[] pointF = new PointF[spenObjectStroke.getPoints().length];
                    for (int j = 0; j < points.length; j++) {
                        PointF pointF1 = new PointF();
                        pointF1.set(points[j].x / scale, points[j].y / scale);
                        pointF[j] = pointF1;
                    }
                    stroke.setPointS(pointF);
                    spenStrokes.add(stroke);
                }
            }
            if (spenStrokes.size() > 0) {
                saveMarkDataBean.getQuestions().get(loc).setCoordinate(new Gson().toJson(spenStrokes));
            }

            //添加标签、步骤分
            for (Map.Entry<Integer, StepScore> map : stepDatas.entrySet()) {
                stepScores.add(map.getValue());
            }
            if (stepScores.size() == 0) {
                saveMarkDataBean.getQuestions().get(loc).setStepScore(null);
            } else {
                String str = new Gson().toJson(stepScores);
                saveMarkDataBean.getQuestions().get(loc).setStepScore(str);
            }
            GradeData gradeData = new GradeData();
            if (stepScore.isChecked()) {
                gradeData.setGradeMode(3);
            } else {
                gradeData.setGradeMode(1);
            }
            if (PreferencesService.getInstance(context).getStepMode()) {
                gradeData.setStepModeAdd(false);
                if (scoreAdapter.getChectValue() == (int) scoreAdapter.getChectValue()) {
                    gradeData.setStepLength("-" + (int) scoreAdapter.getChectValue());
                } else {
                    gradeData.setStepLength("-" + scoreAdapter.getChectValue());
                }
            } else {
                gradeData.setStepModeAdd(true);
                if (scoreAdapter.getChectValue() == (int) scoreAdapter.getChectValue()) {
                    gradeData.setStepLength("+" + (int) scoreAdapter.getChectValue());
                } else {
                    gradeData.setStepLength("+" + scoreAdapter.getChectValue());
                }
            }
            saveMarkDataBean.getQuestions().get(minLocation).setGradeData(new Gson().toJson(gradeData));
        }
        Log.i(TAG, "getPageDocObject: " + new Gson().toJson(saveMarkDataBean));
    }

    /**
     * 初始化置顶分数选择器
     *
     * @param b 是否开启0.5
     * @return
     */
    private Map<Integer, CustomBean> initTopScoreData(boolean b) {
        Map<Integer, CustomBean> data = new HashMap<>();
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
                            if (i == Double.parseDouble(split[j])) {
                                customBean.setSelect(true);
                            } else {
                                customBean.setSelect(false);
                            }
                            customBean.setScore(i);
                            data.put(num, customBean);
                            num++;
                            break;
                        } else {
                            if (i == Double.parseDouble(split[j])) {
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
                                customBean.setScore(Double.parseDouble(String.valueOf(list1.get(j))));
                                customBean.setSelect(true);
                            } else {
                                customBean.setScore(Double.parseDouble(String.valueOf(i)));
                                customBean.setSelect(false);
                            }
                            data.put(i, customBean);
                            break;
                        } else {
                            if (Integer.valueOf(list1.get(j)) == i) {
                                CustomBean customBean = new CustomBean();
                                customBean.setScore(Double.parseDouble(String.valueOf(list1.get(j))));
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
                        bean.setScore(Double.parseDouble(String.valueOf(i)));
                        data.put(num, bean);
                    } else {
                        bean.setScore(Double.parseDouble(String.valueOf(i)));
                        data.put(num, bean);
                    }
                    num++;
                }
            } else {
                for (int i = 0; i <= MaxScore; i++) {
                    CustomBean bean = new CustomBean();
                    bean.setSelect(false);
                    bean.setScore(Double.parseDouble(String.valueOf(i)));
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
                            reviewAdatper = new ReviewAdatper(context, studentsResponse.getData());
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
                                                                getNextStudentCache(false);
                                                            }
                                                        } else { //有自己的任务数量
                                                            if (response.getTeacherTask().getMarkNumber() < response.getTeacherTask().getMarkCount()) {
                                                                getNextStudentCache(false);
                                                            }
                                                        }
                                                    }
                                                    MaxScore = getMaxScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions());
                                                    if (getMarkNextStudentResponse.getData().getStudentData().isCollect()) {
                                                        collectRequest.setValue(String.valueOf(1));
                                                        collection.setChecked(true);
                                                        collection.setTextColor(getResources().getColor(R.color.colorWhite));
                                                        collectionImg.setVisibility(View.VISIBLE);
                                                    } else {
                                                        collectRequest.setValue(String.valueOf(0));
                                                        collection.setChecked(false);
                                                        collection.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                                        collectionImg.setVisibility(View.GONE);
                                                    }
                                                    if (getMarkNextStudentResponse.getData().getStudentData().isAbnormal()) {
                                                        AbnormalRequest.setValue(String.valueOf(1));
                                                        abnormal.setChecked(true);
                                                        abnormal.setTextColor(getResources().getColor(R.color.colorWhite));
                                                        abnormalTv.setVisibility(View.VISIBLE);
                                                    } else {
                                                        AbnormalRequest.setValue(String.valueOf(0));
                                                        abnormal.setChecked(false);
                                                        abnormal.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                                        abnormalTv.setVisibility(View.GONE);
                                                    }
                                                    initUpDateData(getMarkNextStudentResponse);
                                                    showSueecssPage(getMarkNextStudentResponse);
                                                    submiss.setEnabled(true);
                                                    if (response.getTeacherTask().getMarkCount() == 0 || response.getTeacherTask().getIdentity() == 3) {
                                                        progressTips.setText(Html.fromHtml("<font color = '#245AD3'>第" + (position + 1)
                                                                + "份</font>"));
                                                    } else {
                                                        progressTips.setText(Html.fromHtml("<font color = '#245AD3'>" + (position + 1)
                                                                + "</font>/" + response.getTeacherTask().getMarkCount()));
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

    /**
     * 获取一个新学生缓存数据
     *
     * @param isNext 是否再缓存一个
     */
    private void getNextStudentCache(final boolean isNext) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // LoadingUtil.showDialog(context);
            }
        });
        Log.i(TAG, "getNextStudentCache: 缓存一个未批阅学生的数据");
        GetMarkDataRequest cacheRequest = new GetMarkDataRequest();
        cacheRequest.setTaskGuid(taskGuid);
        cacheRequest.setTeacherGuid(teacherGuid);
        myModel.getMarkNextStudent(context, cacheRequest, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                GetMarkNextStudentResponse nextStudentResponse = (GetMarkNextStudentResponse) model;
                cachePool.add(nextStudentResponse);
                Log.i(TAG, "缓存数据成功，当前的数量是" + cachePool.size());
                Tool.base64ToBitmap(nextStudentResponse, new MyCallBack() {
                    @Override
                    public void onSuccess(Object model) {
                        LocalImageData localImageData = (LocalImageData) model;
                        Log.i(TAG, "缓存数据图片：" + localImageData.getPath());
                        /*Bitmap bitmap = BitmapFactory.decodeFile(localImageData.getPath());
                        imageData = new ImageData(bitmap.getWidth(), bitmap.getHeight());
                        Log.i(TAG, "本地图片的宽: " + bitmap.getWidth() + "  高:" + bitmap.getHeight());
                        Log.i(TAG, "图片保存到本地的地址是：" + localImageData.getPath());*/
                        if (isNext) {
                            getNextStudentCache(false);
                        }
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
        Log.i(TAG, "getReviewStudentCache: 缓存已批阅学生的数据");
        myModel.getStudentMarkData(context, reviewStudent, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                GetMarkNextStudentResponse reviewStudentResponse = (GetMarkNextStudentResponse) model;
                cachePool.add(reviewStudentResponse);
                Tool.base64ToBitmap(reviewStudentResponse, new MyCallBack() {
                    @Override
                    public void onSuccess(Object model) {
                        LocalImageData localImageData = (LocalImageData) model;
                        /*Bitmap bitmap = BitmapFactory.decodeFile(localImageData.getPath());
                        imageData = new ImageData(bitmap.getWidth(), bitmap.getHeight());
                        Log.i(TAG, "本地图片的宽: " + bitmap.getWidth() + "  高:" + bitmap.getHeight());
                        Log.i(TAG, "图片保存到本地的地址是：" + localImageData.getPath());*/
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
        if (mSpenPageDoc.getObjectList().size() != 0) {
            getPageDocObject(minLocation);
        }
        LogUtils.logI(TAG, "提交的数据是: " + new GsonBuilder().serializeNulls().create().toJson(saveMarkDataBean));
        if (reviewMode) {
            Log.i(TAG, "onClick: 回评模式");
            //回评模式
            myModel.upDateMarkData(context, saveMarkDataBean, new MyCallBack() {
                @Override
                public void onSuccess(Object model) {
                    saveResponse = (SavaDataResponse) model;
                    tab.clear();
                    stepDatas.clear();
                    if (cachePool != null && cachePool.size() > 0) {
                        cachePool.remove(0); //清除第一个缓存数据
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LOCATION++;
                            stepScoreModeScore = 0;
                            doubleScore = "0";
                            if (doubleMode) {
                                doubleScoreTableAdapter.setPos(-1);
                                doubleScoreTableAdapter.notifyDataSetChanged();
                            }
                            //判断后面是否有回评数据
                            if (LOCATION == studentsResponse.getData().size()) { //回评结束了
                                if (response.getTeacherTask().getMarkSum() != response.getTeacherTask().getTaskCount()) { //总任务没完成
                                    if (response.getTeacherTask().getStyle() == 2) {
                                        if (response.getTeacherTask().getMarkNum() != response.getTeacherTask().getTaskCount()) {
                                            getNewStudent();
                                            if (response.getTeacherTask().getMarkSum() != response.getTeacherTask().getTaskCount() - 1) {
                                                //获取下一个缓存数据
                                                getNextStudentCache(false);
                                            }
                                        } else {
                                            myTaskOverDialog();
                                        }
                                    } else {
                                        if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount()) {
                                            getNewStudent();
                                            if (response.getTeacherTask().getMarkSum() != response.getTeacherTask().getTaskCount() - 1) {
                                                //获取下一个缓存数据
                                                getNextStudentCache(false);
                                            }
                                        } else { //自己的任务已经完成
                                            myTaskOverDialog();
                                        }
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
                                                        collection.setTextColor(getResources().getColor(R.color.colorWhite));
                                                        collectionImg.setVisibility(View.VISIBLE);
                                                    } else {
                                                        collectRequest.setValue(String.valueOf(0));
                                                        collection.setChecked(false);
                                                        collection.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                                        collectionImg.setVisibility(View.GONE);
                                                    }
                                                    if (getMarkNextStudentResponse.getData().getStudentData().isAbnormal()) {
                                                        AbnormalRequest.setValue(String.valueOf(1));
                                                        abnormal.setChecked(true);
                                                        abnormal.setTextColor(getResources().getColor(R.color.colorWhite));
                                                        abnormalTv.setVisibility(View.VISIBLE);
                                                    } else {
                                                        AbnormalRequest.setValue(String.valueOf(0));
                                                        abnormal.setChecked(false);
                                                        abnormal.setTextColor(getResources().getColor(R.color.colorScoreItem));
                                                        abnormalTv.setVisibility(View.GONE);
                                                    }
                                                    initUpDateData(getMarkNextStudentResponse);
                                                    showSueecssPage(getMarkNextStudentResponse);
                                                    submiss.setEnabled(true);
                                                    if (response.getTeacherTask().getMarkCount() == 0 || response.getTeacherTask().getIdentity() == 3) {
                                                        progressTips.setText(Html.fromHtml("<font color = '#245AD3'>第" + (LOCATION + 1)
                                                                + "份</font>"));
                                                    } else {
                                                        progressTips.setText(Html.fromHtml("<font color = '#245AD3'>" + (LOCATION + 1)
                                                                + "</font>/" + response.getTeacherTask().getMarkCount()));
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
            Log.i(TAG, "onClick: 正常模式");
            //正常批阅模式
            myModel.saveMarkData(context, saveMarkDataBean, new MyCallBack() {
                @Override
                public void onSuccess(Object model) {
                    tab.clear();
                    stepDatas.clear();
                    strokeColor.clear();
                    if (cachePool != null && cachePool.size() > 0) {
                        cachePool.remove(0); //清除第一个数据
                        Log.i(TAG, "提交数据成功，清除第一个缓存数据后的长度是：" + cachePool.size());
                    }
                    saveResponse = (SavaDataResponse) model;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                            response.getTeacherTask().setArbCount(saveResponse.getArbCount());
                            if (saveResponse.getTaskNumber() != saveResponse.getTaskCount()) { //任务没有完成
                                if (response.getTeacherTask().getStyle() == 2) { //双评模式
                                    if (response.getTeacherTask().getIdentity() != 3) {
                                        if (response.getTeacherTask().getMarkCount() == response.getTeacherTask().getMarkNumber()
                                                || response.getTeacherTask().getTaskCount() == response.getTeacherTask().getMarkSum()) {
                                            //阅卷结束
                                            myTaskOverDialog();
                                        } else {
                                            if (cachePool.size() > 0) {
                                                loadCacheData();
                                                if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount() - 1) {
                                                    //获取下一个缓存数据
                                                    getNextStudentCache(false);
                                                }
                                            } else {
                                                getNewStudent();
                                                if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount() - 1) {
                                                    //获取下一个缓存数据
                                                    getNextStudentCache(false);
                                                }
                                            }
                                        }
                                    } else {
                                        if (saveResponse.getTaskNumber() != (saveResponse.getTaskNumber() + saveResponse.getArbCount())) {
                                            if (cachePool.size() > 0) {
                                                loadCacheData();
                                                if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount() - 1) {
                                                    //获取下一个缓存数据
                                                    getNextStudentCache(false);
                                                }
                                            } else {
                                                getNewStudent();
                                                if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount() - 1) {
                                                    //获取下一个缓存数据
                                                    getNextStudentCache(false);
                                                }
                                            }
                                        } else {
                                            myTaskOverDialog();
                                        }
                                    }
                                } else {
                                    if (response.getTeacherTask().isFree()) {
                                        if (response.getTeacherTask().getTaskCount() != response.getTeacherTask().getMarkSum()) {
                                            if (cachePool.size() > 0) {
                                                loadCacheData();
                                                if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount() - 1) {
                                                    //获取下一个缓存数据
                                                    getNextStudentCache(false);
                                                }
                                            } else {
                                                getNewStudent();
                                                if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount() - 1) {
                                                    //获取下一个缓存数据
                                                    getNextStudentCache(false);
                                                }
                                            }
                                        }
                                    } else {
                                        if (response.getTeacherTask().getMarkCount() == response.getTeacherTask().getMarkNumber()) {
                                            //阅卷结束
                                            myTaskOverDialog();
                                        } else {
                                            if (cachePool.size() > 0) {
                                                loadCacheData();
                                                if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount() - 1) {
                                                    //获取下一个缓存数据
                                                    getNextStudentCache(false);
                                                }
                                            } else {
                                                getNewStudent();
                                                if (response.getTeacherTask().getMarkNumber() != response.getTeacherTask().getMarkCount() - 1) {
                                                    //获取下一个缓存数据
                                                    getNextStudentCache(false);
                                                }
                                            }
                                        }
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
        Log.i(TAG, "loadCacheData: 加载缓存数据");
        minLocation = 0;
        collectRequest = new CollectRequest();
        AbnormalRequest = new CollectRequest();
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
            collection.setTextColor(getResources().getColor(R.color.colorWhite));
            collectionImg.setVisibility(View.VISIBLE);
        } else {
            collectRequest.setValue(String.valueOf(0));
            collection.setChecked(false);
            collection.setTextColor(getResources().getColor(R.color.colorScoreItem));
            collectionImg.setVisibility(View.GONE);
        }
        if (getMarkNextStudentResponse.getData().getStudentData().isAbnormal()) {
            AbnormalRequest.setValue(String.valueOf(1));
            abnormal.setChecked(true);
            abnormal.setTextColor(getResources().getColor(R.color.colorWhite));
            abnormalTv.setVisibility(View.VISIBLE);
        } else {
            AbnormalRequest.setValue(String.valueOf(0));
            abnormal.setChecked(false);
            abnormal.setTextColor(getResources().getColor(R.color.colorScoreItem));
            abnormalTv.setVisibility(View.GONE);
        }
        initUpDateData(getMarkNextStudentResponse);
        loadCacheImg(Tool.IMAGEPATH + "/" + getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(0).getId() + ".jpeg");
        submiss.setEnabled(true);
        if (response.getTeacherTask().getMarkCount() == 0 || response.getTeacherTask().getIdentity() == 3) {
            progressTips.setText(Html.fromHtml("<font color = '#245AD3'>第" + (LOCATION + 1)
                    + "份</font>"));
        } else {
            progressTips.setText(Html.fromHtml("<font color = '#245AD3'>" + (LOCATION + 1)
                    + "</font>/" + response.getTeacherTask().getMarkCount()));
        }
        initUI(getMarkNextStudentResponse);
    }

    /**
     * 加载缓存数据的图片
     *
     * @param imgPath
     */
    private void loadCacheImg(String imgPath) {
        Log.i(TAG, "加载缓存图片：" + imgPath);
        mSpenPageDoc.removeAllObject();
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        if (bitmap == null) {
            ToastUtils.showToast(context, "获取图片异常");
            Log.i(TAG, "loadCacheImg: 获取图片异常");
            return;
        }
        imageData = new ImageData(bitmap.getWidth(), bitmap.getHeight());
        Log.i(TAG, "本地图片的宽: " + bitmap.getWidth() + "  高:" + bitmap.getHeight());

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
        } else if (!TextUtils.isEmpty(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getStepScore())
                && getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getStepScore().length() > 10
                && getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getGradeData().contains("\"gradeMode\":3")) {
            addStepScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getStepScore(),
                    getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getGradeData());
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
                if (_stepMode != PreferencesService.getInstance(context).getStepMode()) {
                    saveMarkDataBean.getQuestions().get(minLocation).setStepScore("");
                    saveMarkDataBean.getQuestions().get(minLocation).setMarkScore("-1");
                    saveMarkDataBean.getQuestions().get(minLocation).setGradeData(null);
                    questionScore.setText("0");
                    tab.clear();
                    stepDatas.clear();
                    mSpenPageDoc.removeAllObject();
                    mSpenSimpleSurfaceView.update();
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
                            if (stepScore.isChecked()) {
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
                        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
                        penInfo.color = Color.parseColor(PreferencesService.getInstance(context).getSpenColor());
                        penInfo.size = (float) (PreferencesService.getInstance(context).getSpenSize() * 1.5);
                        mSpenSimpleSurfaceView.setPenSettingInfo(penInfo);
                    }
                });
            }
        });
        WindowManager.LayoutParams params = settingDialog.getWindow().getAttributes();
        //TODO 设置成平面高度的0.75倍
        params.height = (int) (Tool.getScreenparameters(MarkingActivity.this).height() * 0.75);
        settingDialog.getWindow().setAttributes(params);
        seting_main = view.findViewById(R.id.setting_main);
        setting_back = view.findViewById(R.id.setting_back);
        pointFiveView = view.findViewById(R.id.pointFiveView);
        pointFiveSwitch = view.findViewById(R.id.pointFiveSwitch);
        stepModeView = view.findViewById(R.id.stepModeView);
        stepModeSwitch = view.findViewById(R.id.stepModeSwitch);
        autoSubmitView = view.findViewById(R.id.autoSubmitView);
        autoSubmitSwitch = view.findViewById(R.id.autoSubmitSwitch);
        topScoreView = view.findViewById(R.id.topScoreView);
        penSetView = view.findViewById(R.id.penSetView);
        horizontalCheckBox = view.findViewById(R.id.horizontalCheckBox);
        verticalCheckBox = view.findViewById(R.id.verticalCheckBox);

        topScoreSetting = view.findViewById(R.id.topScoreSetting);
        topScoreGridView = view.findViewById(R.id.topScoreGridView);
        topSocre_0_5 = view.findViewById(R.id.topSocre_0_5);
        topScoreClear = view.findViewById(R.id.topScoreClear);
        topScoreDetermine = view.findViewById(R.id.topScoreDetermine);

        penSetting = view.findViewById(R.id.penSetting);
        spenSize = view.findViewById(R.id.spensize);
        spenSeekBar = view.findViewById(R.id.spenSeekBar);
        spenColorGridView = view.findViewById(R.id.spenColors);
        spenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    spenSeekBar.setProgress(1);
                    spenSize.setText("1");
                    PreferencesService.getInstance(context).saveSpenSize(progress);
                } else {
                    spenSeekBar.setProgress(progress);
                    spenSize.setText(progress + "");
                    PreferencesService.getInstance(context).saveSpenSize(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() == 0) {
                    spenSeekBar.setProgress(1);
                    spenSize.setText("1");
                }
            }
        });

        setting_back.setOnClickListener(this);
        pointFiveView.setOnClickListener(this);
        stepModeView.setOnClickListener(this);
        pointFiveSwitch.setOnClickListener(this);
        stepModeSwitch.setOnClickListener(this);
        autoSubmitSwitch.setOnClickListener(this);
        autoSubmitSwitch.setEnabled(true);
        topScoreView.setOnClickListener(this);
        penSetView.setOnClickListener(this);
        topSocre_0_5.setOnClickListener(this);
        topScoreClear.setOnClickListener(this);
        topScoreDetermine.setOnClickListener(this);
        horizontalCheckBox.setOnClickListener(this);
        verticalCheckBox.setOnClickListener(this);

        //初始化控件
        if (PreferencesService.getInstance(context).getPointFive()) {
            pointFiveSwitch.setChecked(true);
        } else {
            pointFiveSwitch.setChecked(false);
        }
        if (PreferencesService.getInstance(context).getStepMode()) {
            stepModeSwitch.setChecked(true);
        } else {
            stepModeSwitch.setChecked(false);
        }
        if (PreferencesService.getInstance(context).getAutoSubmit()) {
            autoSubmitSwitch.setChecked(true);
        } else {
            autoSubmitSwitch.setChecked(false);
        }
        if (PreferencesService.getInstance(context).getScreenType() == 0) {
            horizontalCheckBox.setChecked(true);
            verticalCheckBox.setChecked(false);
        } else {
            horizontalCheckBox.setChecked(false);
            verticalCheckBox.setChecked(true);
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

    @Override //滑动中
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override //打开
    public void onDrawerOpened(@NonNull View drawerView) {
        gobackScoreIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.order), null);
        gobackTimeIcon.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.order), null);
        showReviewList();
    }

    @Override //关闭
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override  //状态改变
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getCheckedRadioButtonId()) {
            case R.id.spenRadio:
                if (spenRadio.isChecked()) {
                    spenRadio.setChecked(true);
                    identification_dui.setChecked(false);
                    identification_bandui.setChecked(false);
                    identification_cuo.setChecked(false);
                    tabType = TYPE_NONE;
                    spenLayoutShow.setVisibility(View.VISIBLE);
                    ToastUtils.showToast(context, "开启批注");
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_STROKE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_STROKE);
                } else {
                    spenRadio.setChecked(false);
                    if (spenRadio.isChecked()) {
                        spenLayoutShow.setVisibility(View.VISIBLE);
                        tabType = TYPE_STEPPOINTS;
                    } else {
                        spenLayoutShow.setVisibility(View.GONE);
                        tabType = TYPE_NONE;
                    }
                    ToastUtils.showToast(context, "关闭批注");
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_GESTURE);
                }
                break;
            case R.id.identification_dui:
                if (identification_dui.isChecked()) {
                    identification_dui.setChecked(true);
                    identification_bandui.setChecked(false);
                    identification_cuo.setChecked(false);
                    identification_objSelect.setChecked(false);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_GESTURE);
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.correct);
                    tabType = TYPE_TICK;
                } else {
                    identification_dui.setChecked(false);
                    bitmap = null;
                    if (spenRadio.isChecked()) {
                        tabType = TYPE_NONE;
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_STROKE);
                    }
                    if (stepScore.isChecked()) {
                        tabType = TYPE_STEPPOINTS;
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_NONE);
                    }
                }
                break;
            case R.id.identification_bandui:
                if (identification_bandui.isChecked()) {
                    identification_dui.setChecked(false);
                    identification_bandui.setChecked(true);
                    identification_cuo.setChecked(false);
                    identification_objSelect.setChecked(false);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_GESTURE);
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bandui);
                    tabType = TYPE_HALFTICK;
                } else {
                    identification_bandui.setChecked(false);
                    bitmap = null;
                    if (spenRadio.isChecked()) {
                        tabType = TYPE_NONE;
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_STROKE);
                    }
                    if (stepScore.isChecked()) {
                        tabType = TYPE_STEPPOINTS;
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_NONE);
                    }
                }
                break;
            case R.id.identification_cuo:
                if (identification_cuo.isChecked()) {
                    identification_dui.setChecked(false);
                    identification_bandui.setChecked(false);
                    identification_cuo.setChecked(true);
                    identification_objSelect.setChecked(false);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSimpleSurfaceView.ACTION_NONE);
                    mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_GESTURE);
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.erroe);
                    tabType = TYPE_CROSS;
                } else {
                    identification_cuo.setChecked(false);
                    bitmap = null;
                    if (spenRadio.isChecked()) {
                        tabType = TYPE_NONE;
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_STROKE);
                    }
                    if (stepScore.isChecked()) {
                        tabType = TYPE_STEPPOINTS;
                        mSpenSimpleSurfaceView.setToolTypeAction(SpenSurfaceView.TOOL_SPEN, SpenSurfaceView.ACTION_NONE);
                    }
                }
                break;
        }
    }
}
