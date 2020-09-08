package com.ruiyi.netreading.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ruiyi.netreading.activity.MainActivity;
import com.ruiyi.netreading.activity.R;
import com.ruiyi.netreading.bean.response.GetExamContextResponse;
import com.ruiyi.netreading.bean.response.GetExamListResponse;

import java.util.List;

public class TaskListAdapter extends BaseExpandableListAdapter {

    private MainActivity mContext;
    private List<GetExamListResponse.ExamListBean> datas; //父数据源
    private List<GetExamContextResponse.TaskListBean> childs; //子数据源
    private LayoutInflater inflater;
    private boolean isOver; //自己的任务手全部阅完

    public TaskListAdapter(Context context, List<GetExamListResponse.ExamListBean> list) {
        this.mContext = (MainActivity) context;
        this.datas = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childs == null ? 0 : childs.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return datas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childs.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们
    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.task_item, parent, false);
            groupViewHolder = new GroupViewHolder(convertView);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.examName.setText(datas.get(groupPosition).getExamName());
        if (isExpanded) {
            groupViewHolder.examName.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext, R.drawable.down), null);
        } else {
            groupViewHolder.examName.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext, R.drawable.right), null);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChileViewHolder chileViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.task_context_item, parent, false);
            chileViewHolder = new ChileViewHolder(convertView);
            convertView.setTag(chileViewHolder);
        } else {
            chileViewHolder = (ChileViewHolder) convertView.getTag();
        }

        chileViewHolder.questionName.setText(childs.get(childPosition).getTaskName());
        if (childs.get(childPosition).getTeacherData() != null) {
            chileViewHolder.myMission.setText(Html.fromHtml("我的任务：<font color = '#245AD3'>" + childs.get(childPosition).getTeacherData().getTeacherNumber()
                    + "</font>/" + childs.get(childPosition).getTeacherData().getTeacherCount()));
            if (childs.get(childPosition).getStyle() != 2) { //不是双评
                chileViewHolder.totalTasks.setText("任务总量：" + childs.get(childPosition).getMarkNumber() + "/" + childs.get(childPosition).getTaskCount());
                chileViewHolder.progress.setSecondaryProgress(childs.get(childPosition).getMarkNumber());
            } else {
                chileViewHolder.totalTasks.setText("任务总量：" + childs.get(childPosition).getMarkNum() + "/" + childs.get(childPosition).getTaskCount());
                chileViewHolder.progress.setSecondaryProgress(childs.get(childPosition).getMarkNum());
            }
            chileViewHolder.progress.setMax(childs.get(childPosition).getTaskCount());
            chileViewHolder.progress.setProgress(childs.get(childPosition).getTeacherData().getTeacherNumber());
            if (childs.get(childPosition).getMarkNumber() != 0) {
                //TODO 保留一位小数
                chileViewHolder.percentage.setText(String.format("%.1f", ((double) childs.get(childPosition).getMarkNumber() * 100 / childs.get(childPosition).getTaskCount())) + "%");
            } else {
                if (childs.get(childPosition).getStyle() != 2) {
                    chileViewHolder.percentage.setText("0%");
                } else {
                    if (childs.get(childPosition).getMarkNum() != 0) {
                        chileViewHolder.percentage.setText(String.format("%.1f", ((double) childs.get(childPosition).getMarkNum() * 100 / childs.get(childPosition).getTaskCount())) + "%");
                    } else {
                        chileViewHolder.percentage.setText("0%");
                    }
                }
            }
            if (childs.get(childPosition).getTaskCount() == childs.get(childPosition).getMarkNumber()) { //当前任务已经完成
                if (childs.get(childPosition).getTeacherData().getTeacherNumber() == 0) { //自己没有任务
                    chileViewHolder.startTask.setText("已阅完");
                    chileViewHolder.startTask.setEnabled(false);
                } else {
                    chileViewHolder.startTask.setText("回 评");
                    chileViewHolder.startTask.setEnabled(true);
                }
                chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_enable_style));
                chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorProgress));
            } else { //任务没完成
                if (childs.get(childPosition).getTeacherData().getTeacherCount() == 0) { //自由阅卷模式
                    if (childs.get(childPosition).getTeacherData().getTeacherNumber() == 0) {
                        if (childs.get(childPosition).getStyle() == 3) {
                            chileViewHolder.startTask.setText("无阅卷任务");
                            chileViewHolder.startTask.setEnabled(false);
                            chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_enable_style));
                            chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorProgress));
                        } else {
                            chileViewHolder.startTask.setText("开始阅卷");
                            chileViewHolder.startTask.setEnabled(true);
                            chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                            chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                        }
                    } else {
                        chileViewHolder.startTask.setText("继续阅卷");
                        chileViewHolder.startTask.setEnabled(true);
                        chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                        chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    }
                } else { //有自己的任务
                    if (childs.get(childPosition).getTeacherData().getTeacherNumber() == 0) {
                        chileViewHolder.startTask.setText("开始阅卷");
                        chileViewHolder.startTask.setEnabled(true);
                        chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                        chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    } else {
                        if (childs.get(childPosition).getStyle() != 2) {
                            if (childs.get(childPosition).getTeacherData().getTeacherNumber() != childs.get(childPosition).getTeacherData().getTeacherCount()) {
                                chileViewHolder.startTask.setText("继续阅卷");
                                chileViewHolder.startTask.setEnabled(true);
                                chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                                chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                            } else {
                                //阅卷类型(1单评，3按班)，单评阅完自己的可以帮阅，按班只能批阅自己的任务，不可帮阅
                            /*if (childs.get(childPosition).getStyle() == 1) {
                                chileViewHolder.startTask.setText("继续阅卷");
                            } else if (childs.get(childPosition).getStyle() == 3) {
                                chileViewHolder.startTask.setText("回 评");
                            }*/
                                if (isOver) {
                                    chileViewHolder.startTask.setText("继续阅卷");
                                } else {
                                    chileViewHolder.startTask.setText("回 评");
                                }
                                chileViewHolder.startTask.setEnabled(true);
                                chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                                chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                            }
                        } else {
                            if (childs.get(childPosition).getTeacherData().getTeacherNumber() != childs.get(childPosition).getTeacherData().getTeacherCount()) {
                                chileViewHolder.startTask.setText("继续阅卷");
                                chileViewHolder.startTask.setEnabled(true);
                                chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                                chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                            } else {
                                    chileViewHolder.startTask.setText("回 评");
                                chileViewHolder.startTask.setEnabled(true);
                                chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                                chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                            }
                        }
                    }
                }
            }
        } else {
            chileViewHolder.myMission.setText(Html.fromHtml("我的任务：<font color = '#245AD3'>" + (0) + "</font>"));
            if (childs.get(childPosition).getStyle() != 2) { //不是双评
                chileViewHolder.totalTasks.setText("任务总量：" + childs.get(childPosition).getMarkNumber() + "/" + childs.get(childPosition).getTaskCount());
                chileViewHolder.progress.setSecondaryProgress(childs.get(childPosition).getMarkNumber());
            } else {
                chileViewHolder.totalTasks.setText("任务总量：" + childs.get(childPosition).getMarkNum() + "/" + childs.get(childPosition).getTaskCount());
                chileViewHolder.progress.setSecondaryProgress(childs.get(childPosition).getMarkNum());
            }
            chileViewHolder.progress.setProgress(0);
            if (childs.get(childPosition).getMarkNumber() != 0) {
                chileViewHolder.percentage.setText(String.format("%.1f", ((double) childs.get(childPosition).getMarkNumber() * 100 / childs.get(childPosition).getTaskCount())) + "%");
            } else {
                if (childs.get(childPosition).getStyle() != 2) {
                    chileViewHolder.percentage.setText("0%");
                } else {
                    if (childs.get(childPosition).getMarkNum() != 0) {
                        chileViewHolder.percentage.setText(String.format("%.1f", ((double) childs.get(childPosition).getMarkNum() * 100 / childs.get(childPosition).getTaskCount())) + "%");
                    } else {
                        chileViewHolder.percentage.setText("0%");
                    }
                }
            }
            if (childs.get(childPosition).getIdentity() != 3) {
                if (childs.get(childPosition).getMarkNumber() == childs.get(childPosition).getTaskCount()) {
                    chileViewHolder.startTask.setText("已阅完");
                    chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_enable_style));
                    chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorProgress));
                    chileViewHolder.startTask.setEnabled(false);
                } else {
                    chileViewHolder.startTask.setText("开始阅卷");
                    chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                    chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    chileViewHolder.startTask.setEnabled(true);
                }
            } else {
                if (childs.get(childPosition).getArbCount() == 0) {
                    chileViewHolder.totalTasks.setText("任务总量：" + childs.get(childPosition).getMarkNumber() + "/" + childs.get(childPosition).getArbCount());
                    chileViewHolder.startTask.setText("无阅卷任务");
                    chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_enable_style));
                    chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorProgress));
                    chileViewHolder.startTask.setEnabled(false);
                } else {
                    chileViewHolder.totalTasks.setText("任务总量：" + childs.get(childPosition).getTeacherData() + "/" + childs.get(childPosition).getArbCount());
                    chileViewHolder.startTask.setText("开始阅卷");
                    chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                    chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    chileViewHolder.startTask.setEnabled(true);
                }
            }
        }

        chileViewHolder.startTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.myOnclickListenet(childPosition);
            }
        });
        if (childPosition != childs.size() - 1) {
            chileViewHolder.line.setVisibility(View.VISIBLE);
        } else {
            chileViewHolder.line.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private OnclickListener myClickListener;

    public interface OnclickListener {
        void myOnclickListenet(int position);
    }

    public void setMyClickListener(OnclickListener clickListener) {
        this.myClickListener = clickListener;
    }

    //指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class GroupViewHolder {
        TextView examName;

        public GroupViewHolder(View view) {
            examName = view.findViewById(R.id.examName);
        }
    }

    class ChileViewHolder {
        TextView questionName; //题目名称
        TextView myMission; //我的任务
        TextView totalTasks; //任务总量
        ProgressBar progress; //任务进度
        TextView percentage; //任务百分比
        Button startTask; //进入任务
        View line; //虚线

        public ChileViewHolder(View view) {
            questionName = view.findViewById(R.id.questionName);
            myMission = view.findViewById(R.id.myMission);
            totalTasks = view.findViewById(R.id.totalTasks);
            progress = view.findViewById(R.id.progress);
            percentage = view.findViewById(R.id.percentage);
            startTask = view.findViewById(R.id.startTask);
            line = view.findViewById(R.id.item_line);
        }
    }

    //设置父数据源
    public void setParentData(List<GetExamListResponse.ExamListBean> datas1) {
        this.datas = datas1;
        this.childs.clear();
    }

    //设置子数据源
    public void setChilds(List<GetExamContextResponse.TaskListBean> childs1) {
        this.childs = childs1;
        //判断自己的任务是否全部阅完
        isOver = true;
        for (int i = 0; i < childs.size(); i++) {
            if (childs.get(i).isCanMark()) { //是自己的任务
                if (childs.get(i).getTeacherData() != null) {
                    if (childs.get(i).getTeacherData().getTeacherCount() != childs.get(i).getTeacherData().getTeacherNumber()) {
                        isOver = false;
                        break;
                    }
                } else {
                    isOver = false;
                    break;
                }
            }
        }

        /*if (isOver) {
            boolean isOv = true;//任务是否完成
            //调用mymodel.getTaskContext()方法
            for (int i = 0; i < childs.size(); i++) {
                if (childs.get(i).getTaskCount() != childs.get(i).getMarkNumber()) {
                    isOv = false;
                    break;
                }
            }
            if (!isOv) {
                mContext.updataChildData();
            }
        }*/
    }

    public void clearChilds() {
        this.childs.clear();
    }
}
