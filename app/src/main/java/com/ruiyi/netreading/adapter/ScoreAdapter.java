package com.ruiyi.netreading.adapter;

import android.content.Context;
import android.util.Log;
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
    private boolean isStepScore; //是否是步骤分模式

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
                holder.scoreItem.setText("+" + scores.get(position));
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

    //获取当前选中的值
    public int getPositon() {
        int pos = -1;
        for (int i = 0; i < scoreCheck.size(); i++) {
            if (scoreCheck.get(i)) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    public void updataData(ScorePanel scorePanel) {
        //更新数据源操作
        this.scores = scorePanel.getScores();
        this.scoreCheck = scorePanel.getScoresCheck();
    }

    public void setStepScore(boolean b) {
        this.isStepScore = b;
    }
}