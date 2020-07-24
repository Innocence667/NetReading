package com.ruiyi.netreading.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.ruiyi.netreading.activity.R;

import java.util.List;

public class SpenColorSAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<String> colors;
    private int pos = 10;//选中的位置

    public SpenColorSAdapter(Context context, List<String> list) {
        this.inflater = LayoutInflater.from(context);
        this.colors = list;
    }

    @Override
    public int getCount() {
        return colors == null ? 0 : colors.size();
    }

    @Override
    public Object getItem(int position) {
        return colors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spen_color_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.parent.setBackgroundColor(Color.parseColor(colors.get(position)));
        if (pos == position) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.myOnclickListenet(position);
            }
        });

        return convertView;
    }

    private MyClickListener myClickListener;

    public interface MyClickListener {
        void myOnclickListenet(int position);
    }

    public void setMyClickListener(MyClickListener listener) {
        this.myClickListener = listener;
    }

    private class ViewHolder {
        LinearLayout parent;
        CheckBox checkBox;

        public ViewHolder(View view) {
            parent = view.findViewById(R.id.spenColorParent);
            checkBox = view.findViewById(R.id.spenColoeCheck);
        }
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
