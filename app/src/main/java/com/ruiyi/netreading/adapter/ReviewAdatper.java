package com.ruiyi.netreading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruiyi.netreading.activity.R;
import com.ruiyi.netreading.bean.response.ReviewStudentsResponse;

import java.util.List;

public class ReviewAdatper extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<ReviewStudentsResponse.DataBean> dataBeans;

    public ReviewAdatper(Context context, List<ReviewStudentsResponse.DataBean> data) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        this.dataBeans = data;
    }

    @Override
    public int getCount() {
        return dataBeans == null ? 0 : dataBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return dataBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.review_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (dataBeans.get(position).isCollect()) {
            holder.flag.setVisibility(View.VISIBLE);
        } else {
            holder.flag.setVisibility(View.GONE);
        }

        holder.number.setText(dataBeans.get(position).getTestCode());
        holder.time.setText(dataBeans.get(position).getTime().split("T")[0]);
        String[] split = String.valueOf(dataBeans.get(position).getScore()).split("\\.");
        if (split != null && split.length > 0) {
            if ("0".equals(split[1])) {
                holder.score.setText(split[0]);
            } else {
                holder.score.setText(String.valueOf(dataBeans.get(position).getScore()));
            }
        } else {
            holder.score.setText(String.valueOf(dataBeans.get(position).getScore()));
        }

        return convertView;
    }

    class ViewHolder {
        TextView number;
        TextView time;
        TextView score;
        TextView flag;

        public ViewHolder(View view) {
            number = view.findViewById(R.id.questionNum);
            time = view.findViewById(R.id.questionTime);
            score = view.findViewById(R.id.questionScore);
            flag = view.findViewById(R.id.flag);
        }
    }
}
