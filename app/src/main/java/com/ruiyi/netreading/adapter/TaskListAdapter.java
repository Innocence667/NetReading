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
import com.ruiyi.netreading.util.ToastUtils;

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
        switch (childs.get(childPosition).getIdentity()) {
            case 0:
                chileViewHolder.identity.setText("单评");
                break;
            case 1:
                chileViewHolder.identity.setText("一评");
                break;
            case 2:
                chileViewHolder.identity.setText("二评");
                break;
            case 3:
                chileViewHolder.identity.setText("仲裁");
                break;
            default:
                chileViewHolder.identity.setText("单评");
                break;
        }
        if (childs.get(childPosition).getTeacherData() != null) {
            chileViewHolder.myMission.setText(Html.fromHtml("我的任务：<font color = '#245AD3'>" + childs.get(childPosition).getTeacherData().getTeacherNumber()
                    + "</font>/" + childs.get(childPosition).getTeacherData().getTeacherCount()));
            chileViewHolder.progress.setMax(childs.get(childPosition).getTaskCount());
            chileViewHolder.progress.setProgress(childs.get(childPosition).getTeacherData().getTeacherNumber());
            if (childs.get(childPosition).getStyle() != 2) { //不是双评
                chileViewHolder.totalTasks.setText("任务总量：" + childs.get(childPosition).getMarkNumber() + "/" + childs.get(childPosition).getTaskCount());
                chileViewHolder.progress.setSecondaryProgress(childs.get(childPosition).getMarkNumber());
            } else {
                if (childs.get(childPosition).getIdentity() == 3) {
                    chileViewHolder.myMission.setText(Html.fromHtml("我的任务：<font color = '#245AD3'>" + childs.get(childPosition).getTeacherData().getTeacherNumber()
                            + "</font>/" + (childs.get(childPosition).getTeacherData().getTeacherNumber() + childs.get(childPosition).getArbCount())));
                    chileViewHolder.progress.setMax((childs.get(childPosition).getArbCount() + childs.get(childPosition).getTeacherData().getTeacherNumber()));
                    chileViewHolder.totalTasks.setText("任务总量：" + childs.get(childPosition).getTeacherData().getTeacherNumber() + "/" + (childs.get(childPosition).getArbCount() + childs.get(childPosition).getTeacherData().getTeacherNumber()));
                    chileViewHolder.progress.setSecondaryProgress(0);
                    chileViewHolder.progress.setProgress(childs.get(childPosition).getTeacherData().getTeacherNumber());
                } else {
                    chileViewHolder.totalTasks.setText("任务总量：" + childs.get(childPosition).getMarkNum() + "/" + childs.get(childPosition).getTaskCount());
                    chileViewHolder.progress.setSecondaryProgress(childs.get(childPosition).getMarkNum());
                }
            }
        } else {
            chileViewHolder.myMission.setText(Html.fromHtml("我的任务：<font color = '#245AD3'>" + (0) + "</font>"));
            chileViewHolder.progress.setMax(childs.get(childPosition).getTaskCount());
            chileViewHolder.progress.setProgress(0);
            if (childs.get(childPosition).getStyle() != 2) { //不是双评
                chileViewHolder.totalTasks.setText("任务总量：" + childs.get(childPosition).getMarkNumber() + "/" + childs.get(childPosition).getTaskCount());
                chileViewHolder.progress.setSecondaryProgress(childs.get(childPosition).getMarkNumber());
            } else {
                if (childs.get(childPosition).getIdentity() == 3) {
                    chileViewHolder.myMission.setText(Html.fromHtml("我的任务：<font color = '#245AD3'>0</font>"));
                    chileViewHolder.progress.setMax(100);
                    chileViewHolder.totalTasks.setText("任务总量：0/0");
                    chileViewHolder.progress.setSecondaryProgress(0);
                    chileViewHolder.progress.setProgress(0);
                } else {
                    chileViewHolder.totalTasks.setText("任务总量：" + childs.get(childPosition).getMarkNum() + "/" + childs.get(childPosition).getTaskCount());
                    chileViewHolder.progress.setSecondaryProgress(childs.get(childPosition).getMarkNum());
                }
            }
            chileViewHolder.progress.setProgress(0);
        }

        if (childs.get(childPosition).getStyle() != 2) {
            chileViewHolder.percentage.setText(String.format("%.1f", ((double) childs.get(childPosition).getMarkNumber() * 100 / childs.get(childPosition).getTaskCount())) + "%");
        } else {
            if (childs.get(childPosition).getTeacherData() != null) {
                if (childs.get(childPosition).getIdentity() == 3) {
                    if (childs.get(childPosition).getTeacherData().getTeacherNumber() != 0) {
                        chileViewHolder.percentage.setText(String.format("%.1f", ((double) childs.get(childPosition).getTeacherData().getTeacherNumber() * 100 / (childs.get(childPosition).getArbCount() + childs.get(childPosition).getTeacherData().getTeacherNumber()))) + "%");
                    } else {
                        chileViewHolder.percentage.setText("0%");
                    }
                } else {
                    chileViewHolder.percentage.setText(String.format("%.1f", ((double) childs.get(childPosition).getMarkNum() * 100 / childs.get(childPosition).getTaskCount())) + "%");
                }
            } else {
                chileViewHolder.percentage.setText("0%");
            }
        }

        if (childs.get(childPosition).getMarkNumber() != childs.get(childPosition).getTaskCount()) { //任务没有完成
            if (!childs.get(childPosition).isPublish()) {
                if (childs.get(childPosition).getTeacherData() != null) {
                    if (childs.get(childPosition).getStyle() == 2) {
                        if (childs.get(childPosition).getIdentity() != 3) {
                            if (childs.get(childPosition).getMarkNum() != childs.get(childPosition).getTaskCount()) {
                                if (childs.get(childPosition).getTeacherData().getTeacherNumber() == 0) {
                                    chileViewHolder.startTask.setText("开始阅卷");
                                } else {
                                    chileViewHolder.startTask.setText("继续阅卷");
                                }
                                chileViewHolder.startTask.setEnabled(true);
                                chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                                chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                            } else {
                                if (childs.get(childPosition).getTeacherData().getTeacherNumber() == 0) {
                                    chileViewHolder.startTask.setText("已阅完");
                                    chileViewHolder.startTask.setEnabled(false);
                                    chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_enable_style));
                                    chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorProgress));
                                } else {
                                    chileViewHolder.startTask.setText("回评");
                                    chileViewHolder.startTask.setEnabled(true);
                                    chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                                    chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                                }
                            }
                        } else {
                            if (childs.get(childPosition).getArbCount() != 0) {
                                chileViewHolder.startTask.setText("开始阅卷");
                                chileViewHolder.startTask.setEnabled(true);
                                chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                                chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                            } else {
                                if (childs.get(childPosition).getTeacherData().getTeacherNumber() != 0) {
                                    chileViewHolder.startTask.setText("回评");
                                    chileViewHolder.startTask.setEnabled(true);
                                    chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                                    chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                                } else {
                                    chileViewHolder.startTask.setText("开始阅卷");
                                    chileViewHolder.startTask.setEnabled(true);
                                    chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                                    chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                                }
                            }
                        }
                    } else {
                        if (childs.get(childPosition).getTeacherData().getTeacherNumber() == 0) {
                            chileViewHolder.startTask.setText("开始阅卷");
                        } else {
                            chileViewHolder.startTask.setText("继续阅卷");
                        }
                        chileViewHolder.startTask.setEnabled(true);
                        chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                        chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    }
                } else {
                    chileViewHolder.startTask.setText("开始阅卷");
                    chileViewHolder.startTask.setEnabled(true);
                    chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                    chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                }
            } else {
                chileViewHolder.startTask.setText("已发布");
                chileViewHolder.startTask.setEnabled(false);
                chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            }
        } else { //任务已完成
            if (!childs.get(childPosition).isPublish()) {
                if (childs.get(childPosition).getTeacherData() != null) {
                    if (childs.get(childPosition).getTeacherData().getTeacherNumber() > 0) {
                        chileViewHolder.startTask.setText("回评");
                        chileViewHolder.startTask.setEnabled(true);
                        chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                        chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    } else {
                        chileViewHolder.startTask.setText("已阅完");
                        chileViewHolder.startTask.setEnabled(false);
                        chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_enable_style));
                        chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorProgress));
                    }
                } else {
                    chileViewHolder.startTask.setText("已阅完");
                    chileViewHolder.startTask.setEnabled(false);
                    chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_enable_style));
                    chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorProgress));
                }
            } else {
                chileViewHolder.startTask.setText("已发布");
                chileViewHolder.startTask.setEnabled(false);
                chileViewHolder.startTask.setBackground(ContextCompat.getDrawable(mContext, R.drawable.begin_btn_style));
                chileViewHolder.startTask.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
            }
        }

        chileViewHolder.startTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (childs.get(childPosition).getStyle() == 2 && childs.get(childPosition).getIdentity() == 3
                        && childs.get(childPosition).getArbCount() == 0) {
                    if (childs.get(childPosition).getTeacherData() == null || childs.get(childPosition).getTeacherData().getTeacherNumber() == 0) {
                        ToastUtils.showTopToast(mContext, "当前任务不存在需要仲裁的试卷，不能进入阅卷！", R.style.Toast_Animation);
                    } else {
                        myClickListener.myOnclickListenet(childPosition);
                    }
                } else {
                    myClickListener.myOnclickListenet(childPosition);
                }
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
        TextView identity; //身份
        TextView myMission; //我的任务
        TextView totalTasks; //任务总量
        ProgressBar progress; //任务进度
        TextView percentage; //任务百分比
        Button startTask; //进入任务
        View line; //虚线

        public ChileViewHolder(View view) {
            questionName = view.findViewById(R.id.questionName);
            identity = view.findViewById(R.id.identity);
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
    }

    public void clearChilds() {
        this.childs.clear();
    }
}
