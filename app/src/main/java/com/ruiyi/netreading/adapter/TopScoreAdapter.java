package com.ruiyi.netreading.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import androidx.core.content.ContextCompat;

import com.ruiyi.netreading.activity.R;
import com.ruiyi.netreading.bean.CustomBean;

import java.util.Map;

public class TopScoreAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private Map<Integer, CustomBean> data;

    public TopScoreAdapter(Context context, Map<Integer, CustomBean> list) {
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.data = list;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (data.get(position).getScore() == (int) data.get(position).getScore()) {
            holder.topScore_item.setText((int) data.get(position).getScore() + "");
        } else {
            holder.topScore_item.setText(data.get(position).getScore() + "");
        }

        if (data.get(position).isSelect()) {
            holder.topScore_item.setChecked(true);
            holder.topScore_item.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        } else {
            holder.topScore_item.setChecked(false);
            holder.topScore_item.setTextColor(ContextCompat.getColor(mContext, R.color.colorScoreItem));
        }

        holder.topScore_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.topScore_item.isChecked()) {
                    holder.topScore_item.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    data.get(position).setSelect(true);
                } else {
                    holder.topScore_item.setTextColor(ContextCompat.getColor(mContext, R.color.colorScoreItem));
                    data.get(position).setSelect(false);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        CheckBox topScore_item;

        public ViewHolder(View view) {
            topScore_item = view.findViewById(R.id.scoreItem);
        }
    }

    //更新数据源
    public void updataData(Map<Integer, CustomBean> datas) {
        this.data = datas;
    }

    //清空选中
    public void clearSelect() {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setSelect(false);
        }
    }


    //获取设置的置顶分数
    public String getTopScoreDate() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            CustomBean customBean = data.get(i);
            if (customBean.isSelect()) {
                if (customBean.getScore() == (int) customBean.getScore()) {
                    stringBuilder.append((int) customBean.getScore() + ",");
                } else {
                    stringBuilder.append(customBean.getScore() + ",");
                }
            }
        }
        String string = stringBuilder.toString();
        if (TextUtils.isEmpty(string)) {
            return "";
        } else {
            return string.substring(0, string.length() - 1);
        }
    }
}
