package com.ruiyi.netreading.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import androidx.core.content.ContextCompat;

import com.ruiyi.netreading.activity.R;

import java.util.List;

public class TopScoreAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<String> data;

    public TopScoreAdapter(Context context, List<String> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.topScore_item.setText(data.get(position));

        holder.topScore_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.topScore_item.isChecked()) {
                    holder.topScore_item.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                } else {
                    holder.topScore_item.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlue));
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

    public void updataData(List<String> datas) {
        this.data = datas;
    }

}
