package com.ruiyi.netreading.activity;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.ruiyi.netreading.adapter.QuestionNumAdapter;
import com.ruiyi.netreading.adapter.ReviewAdatper;
import com.ruiyi.netreading.adapter.ScoreAdapter;
import com.ruiyi.netreading.adapter.TopScoreAdapter;
import com.ruiyi.netreading.bean.CustomBean;
import com.ruiyi.netreading.bean.ImageData;
import com.ruiyi.netreading.bean.LocalImageData;
import com.ruiyi.netreading.bean.SaveMarkDataBean;
import com.ruiyi.netreading.bean.ScorePanel;
import com.ruiyi.netreading.bean.SpenStroke;
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
import com.ruiyi.netreading.controller.MyCallBack;
import com.ruiyi.netreading.controller.MyModel;
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
import com.samsung.android.sdk.pen.pen.SpenPenManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MarkingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PENSIZE = 3;

    private String TAG = "MarkingActivity";
    private Context context;
    private Handler handler;
    private MyModel myModel;

    private AlertDialog.Builder builder;

    private DrawerLayout drawerLayout;
    private RelativeLayout main; //主布局
    private LinearLayout reigth; //侧滑布局
    //当前可绘画笔记区域的宽高
    private int myCanvasWidth;
    private int myCanvasHeigth;

    private TextView number, time, score; //回评列表筛选
    private ListView reviewListView; //回评列表

    private String teacherGuid;//教师guid
    private String taskGuid;//任务guid

    private int LOCATION = 0; //当前题目的进度(12/100中的12，回评模式用的到),是集合的下标
    private int minLocation = 0; //当前显示题目的位置(合并题：当前第几个小题；非合并题：当前位置为0)
    private boolean reviewMode; //是否是回评模式
    private String SCORE; //当前题目的得分
    private double MaxScore;//当前题目的最高分数(合并题目的最高分数)

    private RelativeLayout spenView;
    private SpenNoteDoc mSpenNotDoc;
    private SpenPageDoc mSpenPageDoc;
    private SpenSimpleSurfaceView mSpenSimpleSurfaceView;

    private TextView questionScore;//题目得分
    private TextView questionNum;//题号
    private ListView scoreList;//打分面板
    private Button submiss;//提交按钮

    private ScoreAdapter scoreAdapter; //赋分列表适配器
    private ScorePanel scorePanel; //打分面板模型(分数值，是否选中)

    private CheckBox titleNo; //折合开关
    private RecyclerView mRecyclerView; //小题号列表
    private TextView progressTips;//阅卷进度提示

    //底部功能区
    private CheckBox sideslip; //功能区折叠按钮
    private HorizontalScrollView scrollView; //功能区
    private LinearLayout commentsP, eliminateP, stepScoreP, collectionP, historyP, scoringDatailsP, settingP;
    private CheckBox comments; //批注
    private TextView eliminate;//清除笔记
    private CheckBox stepScore; //步骤分
    private CheckBox collection; //收藏
    private TextView history; //回评
    private TextView scoringDatails; //评分详情
    private TextView setting; //设置
    private AlertDialog settingDialog; //设置dialog

    private List<ImageData> imageDataList; //存放每张图片的宽高

    private GetMarkDataResponse response; //获取试卷试卷
    private GetMarkDataRequest request; //获取新数据请求模型
    private GetMarkNextStudentResponse getMarkNextStudentResponse; //获取新数据请求结果模型
    private SaveMarkDataBean saveMarkDataBean; //新数据提交模型
    private SavaDataResponse saveResponse; //提交成功返回结果
    private List<SaveMarkDataBean.QuestionsBean> questionsBeanList; //新数据提交里的questions字段
    private GetMarkNextStudentResponse getMarkUpdataStudentResponse; //获取已阅数据请求结果模型

    private ReviewStudentsResponse studentsResponse; //回评列表数

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mark);
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
        myModel.getMarkData(context, request, new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                response = (GetMarkDataResponse) model;
                if (response.getTeacherTask().getTaskCount() == response.getTeacherTask().getMarkSum()) { //回评
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
                            getStudentMarkDataRequest.setStudentGuid(studentsResponse.getData().get(dataBeanList.size() - 1).getStudentGuid());
                            getStudentMarkDataRequest.setTaskGuid(taskGuid);
                            getStudentMarkDataRequest.setTeacherGuid(teacherGuid);
                            LOCATION = dataBeanList.size() - 1;
                            myModel.getStudentMarkData(context, getStudentMarkDataRequest, new MyCallBack() {
                                @Override
                                public void onSuccess(final Object model) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            collectRequest = new CollectRequest();
                                            getMarkUpdataStudentResponse = (GetMarkNextStudentResponse) model;
                                            if (getMarkUpdataStudentResponse.getData() == null || getMarkUpdataStudentResponse.getData().getStudentData() == null
                                                    || getMarkUpdataStudentResponse.getData().getStudentData().getQuestions() == null) {
                                                Looper.prepare();
                                                //需要弹出弹出，进行重新获取，暂时先提示用户
                                                ToastUtils.showTopToast(context, "题目获取异常，请返回主页重新获取", R.style.Toast_Animation);
                                                Looper.loop();
                                                return;
                                            }
                                            MaxScore = getMaxScore(getMarkUpdataStudentResponse.getData().getStudentData().getQuestions());
                                            String col = getMarkUpdataStudentResponse.getData().getStudentData().isCollect() == true ? "1" : "0";
                                            collectRequest.setValue(col);
                                            initUpDateData(getMarkUpdataStudentResponse);
                                            showSueecssPage(getMarkUpdataStudentResponse);
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
                } else {
                    progressTips.post(new Runnable() {
                        @Override
                        public void run() {
                            progressTips.setText(Html.fromHtml("<font color = '#245AD3'>"
                                    + (response.getTeacherTask().getMarkNumber() + 1) + "</font>/" + response.getTeacherTask().getMarkCount()));
                        }
                    });
                    myModel.getMarkNextStudent(context, request, new MyCallBack() {
                        @Override
                        public void onSuccess(Object model) {
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
                            String col = getMarkNextStudentResponse.getData().getStudentData().isCollect() == true ? "1" : "0";
                            collectRequest.setValue(col);
                            initSaveData(getMarkNextStudentResponse);
                            showSueecssPage(getMarkNextStudentResponse);
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
            bean.setMarkScore(String.valueOf(questionsBean.getScore()));
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
        scoringDatailsP = findViewById(R.id.scoringDatailsP);
        scoringDatailsP.setOnClickListener(this);
        scoringDatails = findViewById(R.id.scoringDatails);
        scoringDatails.setOnClickListener(this);
        settingP = findViewById(R.id.settingP);
        settingP.setOnClickListener(this);
        setting = findViewById(R.id.setting);
        setting.setOnClickListener(this);

        //初始化SpenView
        boolean isSpenFeatureEnable; //是否支持手写笔
        Spen spen = new Spen();
        try {
            spen.initialize(context);
            isSpenFeatureEnable = spen.isFeatureEnabled(Spen.DEVICE_PEN);
        } catch (Exception e) {
            Toast.makeText(context, "此设备不支持Spen",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }
        spenView = findViewById(R.id.spenView);
        mSpenSimpleSurfaceView = new SpenSimpleSurfaceView(context);
        if (mSpenSimpleSurfaceView == null) {
            Toast.makeText(context, "无法创建新的SpenView",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        spenView.addView(mSpenSimpleSurfaceView);

        //获取屏幕尺寸
        Rect rect = Tool.getScreenparameters(this);
        myCanvasWidth = rect.width() - 80;
        myCanvasHeigth = rect.height() - Tool.getStatusBarHeight(context);
        try {
            Log.e(TAG, "spenView的宽高是：" + myCanvasWidth + "  --  " + myCanvasHeigth);
            mSpenNotDoc = new SpenNoteDoc(context, myCanvasWidth, myCanvasHeigth);
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
        mSpenSimpleSurfaceView.setBlankColor(ContextCompat.getColor(context, R.color.colorSpenSimpleSurfaceView));

        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
        penInfo.color = Color.RED;
        penInfo.size = PENSIZE;
        mSpenSimpleSurfaceView.setPenSettingInfo(penInfo);

        if (isSpenFeatureEnable == false) {
            mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_STROKE);
            Toast.makeText(context, "设备不支持Spen. \n 你可以用手指画笔画", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    }

    /**
     * 根据分数得到分数列表
     *
     * @param score     当前题目得分
     * @param fullScore 当前题目总分
     * @return
     */
    private ScorePanel getScoreList(double score, double fullScore) {
        Log.e(TAG, "getScoreList: 题目分数：" + fullScore + "   题目得分：" + score);

        //获取配置文件中的数据
        //PreferencesService.getInstance(context)


        //Math.ceil(12.2)//返回13.0
        //Math.ceil(12.7)//返回13.0
        //Math.ceil(12.0)//返回12.0
        ScorePanel scorePanel = new ScorePanel();
        List<String> scores = new ArrayList<>();
        List<Boolean> scoresCheck = new ArrayList<>();
        //是否0.5模式
        if (PreferencesService.getInstance(context).getPointFive()) { //0.5模式
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
        } else { //整数模式
            for (int i = 0; i < Math.ceil(fullScore + 1); i++) {
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
        return scorePanel;
    }


    //学生数据获取成功
    public void showSueecssPage(final GetMarkNextStudentResponse nextStudentResponse) {
        Tool.base64ToBitmap(nextStudentResponse.getData().getImageArr(), nextStudentResponse.getData().getStudentData().getQuestions().get(0).getId(), new MyCallBack() {
            @Override
            public void onSuccess(Object model) {
                mSpenPageDoc.removeAllObject();
                LocalImageData localImageData = (LocalImageData) model;
                Bitmap bitmap = BitmapFactory.decodeFile(localImageData.getPath());
                Log.e(TAG, "本地图片的宽: " + bitmap.getWidth() + "  高:" + bitmap.getHeight());
                Log.e(TAG, "图片保存到本地的地址是：" + localImageData.getPath());
                //设置背景图片
                mSpenPageDoc.setBackgroundImage(localImageData.getPath());

                if (!TextUtils.isEmpty(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getCoordinate())
                        && !"[]".equals(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getCoordinate())) {
                    addStroke(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getCoordinate());
                }
                mSpenSimpleSurfaceView.update();
            }

            @Override
            public void onFailed(String str) {
                showFailedPage(str);
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final List<String> strings = getQuestions(nextStudentResponse.getData().getStudentData().getQuestions());
                //初始化题号选择列表
                final QuestionNumAdapter questionNumAdapter = new QuestionNumAdapter(context, strings);
                LinearLayoutManager manager = new LinearLayoutManager(context);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mRecyclerView.setLayoutManager(manager);
                questionNumAdapter.setmOnclickListener(new QuestionNumAdapter.MOnclickListener() {
                    @Override
                    public void OnClickLener(int positon) {
                        int loc = minLocation;
                        saveNowPageData(loc);

                        // 小题切换
                        minLocation = positon;
                        questionNum.setText(strings.get(positon));
                        questionNumAdapter.setPos(positon);
                        questionNumAdapter.notifyDataSetChanged();


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
                            } else {
                                //步骤分未开启
                            }

                        } else {
                            scoreAdapter.setScoreCheck(-1);
                        }
                        scoreAdapter.notifyDataSetChanged();

                    }
                });
                mRecyclerView.setAdapter(questionNumAdapter);

                //当前小题的得分，初始化分数显示
                if ("-1.0".equals(String.valueOf(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore()))) {
                    SCORE = "-1";
                    String[] score = String.valueOf(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore()).split("\\.");
                    if ("0".equals(score[1])) {
                        if ("-1".equals(score[0])) {
                            SCORE = "-1";
                            questionScore.setText(String.valueOf(0));
                        } else {
                            SCORE = score[0];
                            questionScore.setText(score[0]);
                        }
                    } else {
                        SCORE = String.valueOf(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore());
                        questionScore.setText(SCORE);
                    }
                } else {
                    String[] split = String.valueOf(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore()).split("\\.");
                    if (split != null && split.length > 0) {
                        if ("0".equals(split[1])) {
                            SCORE = split[0];
                            questionScore.setText(split[0]);
                        } else {
                            SCORE = String.valueOf(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore());
                            questionScore.setText(SCORE);
                        }
                    } else {
                        SCORE = String.valueOf(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore());
                        questionScore.setText(SCORE);
                    }
                }

                if ("0".equals(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getSubNumber())) {
                    questionNum.setText(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getNumber() + "题");
                } else {
                    questionNum.setText(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getNumber() + "-"
                            + nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getSubNumber() + "题");
                }

                scorePanel = getScoreList(nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore(),
                        nextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore());

                scoreAdapter = new ScoreAdapter(context, scorePanel);

                if (!"-1".equals(SCORE)) {
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
                        questionScore.post(new Runnable() {
                            @Override
                            public void run() {
                                questionScore.setText(scorePanel.getScores().get(position));
                                saveMarkDataBean.getQuestions().get(minLocation).setMarkScore(String.valueOf(scorePanel.getScores().get(position)));
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

    /**
     * 保存当前页面数据
     *
     * @param loc 当前页面小题号的位置(合并题目)
     */

    private void saveNowPageData(int loc) {
        if (!"-1".equals(saveMarkDataBean.getQuestions().get(loc).getMarkScore())) {
            if (mSpenPageDoc.getObjectList().size() != 0) {
                getPageDocObject();
                mSpenPageDoc.removeAllObject();
                mSpenSimpleSurfaceView.update();
            } else {
                //saveMarkDataBean.getQuestions().get(loc).setStepScore();
            }
        } else {
            // TODO 当前分数为-1，不对笔迹和步骤分数据进行保存
        }
    }

    //给SpenPageDoc添加笔迹数据
    private void addStroke(String s) {
        List<SpenStroke> list = new Gson().fromJson(s, new TypeToken<List<SpenStroke>>() {
        }.getType());
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
            base.setPoints(list.get(i).getPointS(), yali, time);
            baseList.add(base);
        }
        mSpenPageDoc.appendObjectList(baseList);
        mSpenSimpleSurfaceView.update();
    }

    //获取当前数据的题目列表
    private List<String> getQuestions(List<GetMarkNextStudentResponse.DataBean.StudentDataBean.QuestionsBean> questions) {
        List<String> questionNos = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            GetMarkNextStudentResponse.DataBean.StudentDataBean.QuestionsBean bean = questions.get(i);
            if (TextUtils.isEmpty(bean.getSubNumber()) || "0".equals(bean.getSubNumber())) {
                questionNos.add(bean.getNumber() + "题");
            } else {
                questionNos.add(bean.getNumber() + "-" + bean.getSubNumber() + "题");
            }
        }
        return questionNos;
    }

    //任务获取失败
    public void showFailedPage(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtils.logE("showFailedPage", str);
                ToastUtils.showToast(context, str);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleNo:
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
            case R.id.sideslip:
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
            case R.id.commentsP: //批注
            case R.id.comments: //批注
                break;
            case R.id.eliminateP: //清除笔记
            case R.id.eliminate: //清除笔记
                if (mSpenPageDoc.getObjectList().size() == 0) {
                    ToastUtils.showToast(context, "未找到笔记");
                } else {
                    mSpenPageDoc.removeAllObject();
                    mSpenSimpleSurfaceView.update();
                    ToastUtils.showToast(context, "已清除");
                }
                break;
            case R.id.stepScoreP: //步骤分
            case R.id.stepScore: //步骤分
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
            case R.id.settingP: //设置
            case R.id.setting: //设置
                showSettingPage();
                break;
            case R.id.questionSubmiss: //提交
                if ("-1".equals(saveMarkDataBean.getQuestions().get(minLocation).getMarkScore())) {
                    ToastUtils.showToast(context, "请先进行赋分操作");
                    return;
                }
                if (mSpenPageDoc.getObjectList().size() != 0) {
                    getPageDocObject();
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
            case R.id.autoSubmitView: //自动提交父布局
                if (autoSubmitSwitch.isChecked()) {
                    autoSubmitSwitch.setChecked(false);
                    ToastUtils.showTopToast(context, Html.fromHtml("<font color = '#FF0000'>关闭</font>自动提交"), R.style.Toast_Animation);
                } else {
                    autoSubmitSwitch.setChecked(true);
                    ToastUtils.showTopToast(context, Html.fromHtml("<font color = '#FF0000'>开启</font>自动提交"), R.style.Toast_Animation);
                }
                PreferencesService.getInstance(context).saveAutoSubmit(autoSubmitSwitch.isChecked());
                break;
            case R.id.autoSubmitSwitch: //自动提交switch
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
                    } else {
                        topSocre_0_5.setChecked(false);
                    }
                } else {
                    topSocre_0_5.setChecked(false);
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
        }
    }

    //获取当前画布上的笔记数据
    private void getPageDocObject() {
        ArrayList<SpenObjectBase> objectList = mSpenPageDoc.getObjectList();
        if (objectList != null || objectList.size() > 0) {
            List<SpenStroke> spenStrokes = new ArrayList<>();
            for (int i = 0; i < objectList.size(); i++) {
                SpenObjectStroke spenObjectStroke = (SpenObjectStroke) objectList.get(i);
                SpenStroke stroke = new SpenStroke();
                stroke.setPointS(spenObjectStroke.getPoints());
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
            public void onSuccess(Object model) {
                List<GetMarkAvgScoreResponse> avgScoreResponseList = (List<GetMarkAvgScoreResponse>) model;
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
                        ReviewAdatper reviewAdatper = new ReviewAdatper(context, studentsResponse.getData());
                        reviewListView.setAdapter(reviewAdatper);
                        reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
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
                                                getMarkUpdataStudentResponse = (GetMarkNextStudentResponse) model;
                                                if (getMarkUpdataStudentResponse.getData() == null || getMarkUpdataStudentResponse.getData().getStudentData() == null
                                                        || getMarkUpdataStudentResponse.getData().getStudentData().getQuestions() == null) {
                                                    Looper.prepare();
                                                    //需要弹出弹出，进行重新获取，暂时先提示用户
                                                    ToastUtils.showTopToast(context, "题目获取异常，请返回主页重新获取", R.style.Toast_Animation);
                                                    Looper.loop();
                                                    return;
                                                }
                                                MaxScore = getMaxScore(getMarkUpdataStudentResponse.getData().getStudentData().getQuestions());
                                                String col = getMarkUpdataStudentResponse.getData().getStudentData().isCollect() == true ? "1" : "0";
                                                collectRequest.setValue(col);
                                                initUpDateData(getMarkUpdataStudentResponse);
                                                showSueecssPage(getMarkUpdataStudentResponse);
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
     * 提交数据
     */
    private void submitData() {
        submiss.setEnabled(false);
        myModel.Submitdata();
        LogUtils.logE(TAG, "提交的数据是: " + new GsonBuilder().serializeNulls().create().toJson(saveMarkDataBean));
        if (reviewMode) {
            Log.e(TAG, "onClick: 回评模式");
            //回评模式
            myModel.upDateMarkData(context, saveMarkDataBean, new MyCallBack() {
                @Override
                public void onSuccess(Object model) {
                    LOCATION++;
                    saveResponse = (SavaDataResponse) model;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //判断后面是否有回评数据
                            if (LOCATION >= studentsResponse.getData().size()) { //回评结束了
                                if (response.getTeacherTask().getTaskCount() != response.getTeacherTask().getMarkSum()) {
                                    //是否继续阅卷(帮阅)
                                    builder = new AlertDialog.Builder(context);
                                    builder.setTitle("提示");
                                    builder.setMessage("您的任务已经结束，是否继续阅卷(帮助其他没有完成任务的教师批阅)？");
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            myModel.getMarkNextStudent(context, request, new MyCallBack() {
                                                @Override
                                                public void onSuccess(final Object model) {
                                                    reviewMode = false;
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
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
                                                            String col = getMarkNextStudentResponse.getData().getStudentData().isCollect() == true ? "1" : "0";
                                                            collectRequest.setValue(col);
                                                            initSaveData(getMarkNextStudentResponse);
                                                            showSueecssPage(getMarkNextStudentResponse);
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
                                    });
                                    builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            MarkingActivity.this.finish();
                                        }
                                    });
                                    builder.create().show();
                                } else {
                                    //阅卷结束
                                    builder = new AlertDialog.Builder(context);
                                    builder.setTitle("提示");
                                    builder.setMessage("任务已完成，您可以如下操作");
                                    AlertDialog dialog = builder.create();
                                    dialog.setCancelable(false);
                                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, "回评", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
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
                            } else { //还有回评数据
                                GetStudentMarkDataRequest getStudentMarkDataRequest = new GetStudentMarkDataRequest();
                                getStudentMarkDataRequest.setStudentGuid(studentsResponse.getData().get(LOCATION).getStudentGuid());
                                getStudentMarkDataRequest.setTaskGuid(taskGuid);
                                getStudentMarkDataRequest.setTeacherGuid(teacherGuid);
                                myModel.getStudentMarkData(context, getStudentMarkDataRequest, new MyCallBack() {
                                    @Override
                                    public void onSuccess(final Object model) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                getMarkUpdataStudentResponse = (GetMarkNextStudentResponse) model;
                                                if (getMarkUpdataStudentResponse.getData() == null || getMarkUpdataStudentResponse.getData().getStudentData() == null
                                                        || getMarkUpdataStudentResponse.getData().getStudentData().getQuestions() == null) {
                                                    //需要弹出弹出，进行重新获取，暂时先提示用户
                                                    ToastUtils.showTopToast(context, "题目获取异常，请返回主页重新获取", R.style.Toast_Animation);
                                                    return;
                                                }
                                                MaxScore = getMaxScore(getMarkUpdataStudentResponse.getData().getStudentData().getQuestions());
                                                String col = getMarkUpdataStudentResponse.getData().getStudentData().isCollect() == true ? "1" : "0";
                                                collectRequest.setValue(col);
                                                initUpDateData(getMarkUpdataStudentResponse);
                                                showSueecssPage(getMarkUpdataStudentResponse);
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
                    LOCATION = -1;
                    saveResponse = (SavaDataResponse) model;

                    //修改一下原先获取的试卷数据(已阅、未阅)
                    response.getTeacherTask().setMarkNumber(saveResponse.getMyNumber());
                    response.getTeacherTask().setMarkCount(saveResponse.getMyCount());

                    if (saveResponse.getMyNumber() < saveResponse.getMyCount()) {
                        reviewMode = false;
                        myModel.getMarkNextStudent(context, request, new MyCallBack() {
                            @Override
                            public void onSuccess(Object model) {
                                getMarkNextStudentResponse = (GetMarkNextStudentResponse) model;
                                if (getMarkNextStudentResponse.getData() == null || getMarkNextStudentResponse.getData().getStudentData() == null
                                        || getMarkNextStudentResponse.getData().getStudentData().getQuestions() == null) {
                                    //需要弹出弹出，进行重新获取，暂时先提示用户
                                    ToastUtils.showTopToast(context, "题目获取异常，请返回主页重新获取", R.style.Toast_Animation);
                                    return;
                                }
                                mSpenPageDoc.removeAllObject();
                                mSpenSimpleSurfaceView.update();
                                MaxScore = getMaxScore(getMarkNextStudentResponse.getData().getStudentData().getQuestions());
                                String col = getMarkNextStudentResponse.getData().getStudentData().isCollect() == true ? "1" : "0";
                                collectRequest.setValue(col);
                                initSaveData(getMarkNextStudentResponse);
                                showSueecssPage(getMarkNextStudentResponse);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        submiss.setEnabled(true);
                                        progressTips.setText(Html.fromHtml("<font color = '#245AD3'>" +
                                                (saveResponse.getMyNumber() + 1) + "</font>/" + saveResponse.getMyCount()));
                                    }
                                });
                            }

                            @Override
                            public void onFailed(String str) {
                                showFailedPage(str);
                            }
                        });
                    } else if (saveResponse.getMyNumber() >= saveResponse.getMyCount()) {
                        //可能存在帮阅模式
                    }
                }

                @Override
                public void onFailed(String str) {
                    showFailedPage(str);
                }
            });
        }
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
        WindowManager.LayoutParams params = settingDialog.getWindow().getAttributes();
        //设置成平面高度的0.75倍
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
        autoSubmitView.setOnClickListener(this);
        autoSubmitSwitch.setOnClickListener(this);
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

        settingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (reviewMode) {
                    scoreAdapter.updataData(getScoreList(getMarkUpdataStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore(),
                            getMarkUpdataStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore()));
                } else {
                    scoreAdapter.updataData(getScoreList(getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getScore(),
                            getMarkNextStudentResponse.getData().getStudentData().getQuestions().get(minLocation).getFullScore()));
                }
                scoreAdapter.notifyDataSetChanged();
            }
        });
    }
}
