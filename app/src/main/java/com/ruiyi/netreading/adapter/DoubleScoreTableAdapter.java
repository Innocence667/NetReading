package com.ruiyi.netreading.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.ruiyi.netreading.activity.R;

import java.util.List;

public class DoubleScoreTableAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<Integer> datas;
    private int pos = -1;//选中的位置

    public DoubleScoreTableAdapter(Context context, List<Integer> list) {
        this.mContext = context;
        this.datas = list;
        inflater = LayoutInflater.from(context);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.doublescore_table_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.doublescore_table_checkBox.setText(datas.get(position) + "+");
        if (position == pos) {
            holder.doublescore_table_checkBox.setChecked(true);
            holder.doublescore_table_checkBox.setTextColor(Color.RED);
        } else {
            holder.doublescore_table_checkBox.setChecked(false);
            holder.doublescore_table_checkBox.setTextColor(Color.WHITE);
        }

        if (position == datas.size() - 1) {
            holder.lineView.setVisibility(View.GONE);
        } else {
            holder.lineView.setVisibility(View.VISIBLE);
        }

        holder.doublescore_table_checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myOnClickLisener.MyClick(position);
            }
        });

        return convertView;
    }

    private MyOnClickLisener myOnClickLisener;

    public interface MyOnClickLisener {
        void MyClick(int position);
    }

    public void setMyOnClickLisener(MyOnClickLisener myOnClickLisener) {
        this.myOnClickLisener = myOnClickLisener;
    }

    class ViewHolder {
        CheckBox doublescore_table_checkBox;
        View lineView;

        public ViewHolder(View view) {
            doublescore_table_checkBox = view.findViewById(R.id.doublescore_table_checkBox);
            lineView = view.findViewById(R.id.lineView);
        }
    }

    //更新数据
    public void update(List<Integer> list) {
        this.datas = list;
    }

    //更新位置
    public void setPos(int p) {
        this.pos = p;
    }

    //满分选中最后一个
    public void setLast() {
        this.pos = datas.size() - 1;
    }
}
