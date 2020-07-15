package com.ruiyi.netreading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ruiyi.netreading.activity.R;

import java.util.List;

public class QuestionNumAdapter extends RecyclerView.Adapter<QuestionNumAdapter.ViewHolder> {

    private List<String> data; //题号
    private List<String> scores; //得分
    private Context mContext;
    private int pos = 0; //当前选中的item下标
    private LayoutInflater inflater;

    public interface MOnclickListener {
        void OnClickLener(int positon);
    }

    private MOnclickListener mOnclickListener;

    public void setmOnclickListener(MOnclickListener listener) {
        this.mOnclickListener = listener;
    }


    public QuestionNumAdapter(Context context, List<String> list, List<String> mScores) {
        this.data = list;
        this.scores = mScores;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public QuestionNumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.question_num_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (!"-1".equals(scores.get(position))) {
            holder.questionNo.setText(data.get(position) + "(" + scores.get(position) + "分)");
        } else {
            holder.questionNo.setText(data.get(position));
        }
        if (pos == position) {
            holder.questionNo.setChecked(true);
            holder.questionNo.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        } else {
            holder.questionNo.setChecked(false);
            holder.questionNo.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
        }
        holder.questionNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnclickListener.OnClickLener(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox questionNo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNo = itemView.findViewById(R.id.questionNo);
        }
    }

    //修改选中的item
    public void setPos(int pos1) {
        this.pos = pos1;
    }

    //更新数据
    public void udataeData(List<String> data1) {
        this.data = data1;
    }

    //更新指定位置的分数
    public void updateScore(int pos, String score) {
        scores.set(pos, score);
    }
}
