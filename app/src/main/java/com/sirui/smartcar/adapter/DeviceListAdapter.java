package com.sirui.smartcar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sirui.smartcar.R;

import java.util.List;
import java.util.Map;

/**
 * Created by ygy on 2016/9/30 0030.
 */

public class DeviceListAdapter extends BaseAdapter {

    LayoutInflater mInflater = null;
    List<Map<String, Object>> datas;

    public DeviceListAdapter(Context context, List<Map<String, Object>> datas) {
        this.mInflater = LayoutInflater.from(context);
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
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
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.device_list_layout, null);
            holder.img = (ImageView)convertView.findViewById(R.id.iv);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.comment = (TextView) convertView.findViewById(R.id.comment);
            holder.favour = (TextView) convertView.findViewById(R.id.favour);
            // 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
            convertView.setTag(holder);// 绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
        }

        holder.img.setBackgroundResource((Integer) datas.get(position).get("img"));
        holder.name.setText((String) datas.get(position).get("name"));
        holder.comment.setText(String.valueOf((Integer) datas.get(position).get("comment")) + "评论");
        holder.favour.setText(String.valueOf((Integer)datas.get(position).get("favour")) + "赞");

        return convertView;
    }

    class ViewHolder {
        public ImageView img;
        public TextView name;
        public TextView comment;
        public TextView favour;
    }

}
