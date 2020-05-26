package com.ruiyi.netreading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ruiyi.netreading.activity.R;
import com.ruiyi.netreading.bean.response.GetMarkAvgScoreResponse;

import java.util.List;

//评分详情适配器
public class ScoreDetailsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<GetMarkAvgScoreResponse> datas;

    public ScoreDetailsAdapter(Context context, List<GetMarkAvgScoreResponse> list) {
        this.mContext = context;
        this.datas = list;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.details_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GetMarkAvgScoreResponse scoreResponse = datas.get(position);

        if ("0".equals(scoreResponse.getSubNumber())) {
            holder.details_questionNo.setText(scoreResponse.getNumber() + "题");
        } else {
            holder.details_questionNo.setText(scoreResponse.getNumber() + "_" + scoreResponse.getSubNumber() + "题");
        }
        holder.details_avgScore.setText(String.format("%.2f", scoreResponse.getAvgScore()));
        holder.details_myAvgScore.setText(String.format("%.2f", scoreResponse.getMyAvgScore()));

        return convertView;
    }

    class ViewHolder {
        TextView details_questionNo;
        TextView details_avgScore;
        TextView details_myAvgScore;

        public ViewHolder(View view) {
            details_questionNo = view.findViewById(R.id.details_questionNo);
            details_avgScore = view.findViewById(R.id.details_avgScore);
            details_myAvgScore = view.findViewById(R.id.details_myAvgScore);
        }
    }
}
