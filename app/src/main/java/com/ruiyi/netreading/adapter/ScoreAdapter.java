package com.ruiyi.netreading.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.ruiyi.netreading.activity.R;
import com.ruiyi.netreading.bean.ScorePanel;

import java.util.List;

public class ScoreAdapter extends BaseAdapter {
    private String TAG = "ScoreAdapter";
    private Context context;
    private LayoutInflater inflater;
    private List<String> scores;
    private List<Boolean> scoreCheck;
    private boolean status = true; //当前状态是否可以进行修改分数操作
    private boolean isStepScore; //是否是步骤分模式
    private boolean isReduce; //是否为步骤分减分模式

    public ScoreAdapter(Context cont, ScorePanel data) {
        this.context = cont;
        inflater = LayoutInflater.from(cont);
        this.scores = data.getScores();
        this.scoreCheck = data.getScoresCheck();
    }

    @Override
    public int getCount() {
        return scores == null ? 0 : scores.size();
    }

    @Override
    public Object getItem(int position) {
        return scores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.score_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (status) {
            holder.scoreItem.setEnabled(true);
        } else {
            holder.scoreItem.setEnabled(false);
        }

        if (scoreCheck.get(position)) {
            holder.scoreItem.setBackground(ContextCompat.getDrawable(context, R.drawable.score_item_down_bg));
            holder.scoreItem.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        } else {
            holder.scoreItem.setBackground(ContextCompat.getDrawable(context, R.drawable.score_item_bg));
            holder.scoreItem.setTextColor(ContextCompat.getColor(context, R.color.colorScoreItem));
        }

        if (isStepScore) {
            if (position == 0) {
                holder.scoreItem.setText(scores.get(position));
            } else {
                if (isReduce) {
                    holder.scoreItem.setText("-" + scores.get(position));
                } else {
                    holder.scoreItem.setText("+" + scores.get(position));
                }
            }
        } else {
            holder.scoreItem.setText(scores.get(position));
        }
        holder.scoreItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.myOnclickListenet(position);
            }
        });

        return convertView;
    }

    private ClickListener mClickListener;

    public interface ClickListener {
        void myOnclickListenet(int position);
    }

    public void setmClickListener(ClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    class ViewHolder {
        Button scoreItem;

        public ViewHolder(View view) {
            scoreItem = view.findViewById(R.id.scoreItem);
        }
    }

    //修改选中的选项
    public void setScoreCheck(int position) {
        for (int i = 0; i < scoreCheck.size(); i++) {
            if (i == position) {
                scoreCheck.set(i, true);
            } else {
                scoreCheck.set(i, false);
            }
        }
    }

    //更新数据源操作
    public void updataData(ScorePanel scorePanel) {
        this.scores = scorePanel.getScores();
        this.scoreCheck = scorePanel.getScoresCheck();
    }

    //是否是步骤分模式
    public void setStepScore(boolean b) {
        this.isStepScore = b;
    }

    //是否开启步骤分减分模式
    public void setStepMode(boolean b) {
        this.isReduce = b;
    }

    //得到当前选中的值
    public double getChectValue() {
        for (int i = 0; i < scoreCheck.size(); i++) {
            if (scoreCheck.get(i)) {
                return Double.valueOf(scores.get(i));
            }
        }
        return -1.0;
    }

    public void clickEnable(boolean enabled) {
        this.status = enabled;
    }
}
