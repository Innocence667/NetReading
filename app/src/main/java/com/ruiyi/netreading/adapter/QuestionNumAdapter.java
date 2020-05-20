package com.ruiyi.netreading.adapter;

import android.content.Context;
import android.util.Log;
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

    private List<String> data;
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


    public QuestionNumAdapter(Context context, List<String> list) {
        this.data = list;
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
        holder.questionNo.setText(data.get(position));
        if (pos == position) {
            holder.questionNo.setChecked(true);
            holder.questionNo.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlue));
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
}
