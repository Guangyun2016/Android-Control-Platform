package com.sirui.smartcar.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sirui.smartcar.R;
import com.sirui.smartcar.adapter.DeviceListAdapter;
import com.sirui.smartcar.ui.activity.ScrollingActivity;
import com.sirui.smartcar.ui.activity.SmartCar2;
import com.wx.android.common.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ygy on 2016/9/29 0029.
 */
public class Device extends Fragment {

    private SimpleDraweeView cm_image_view;
    private ListView lv;
    private List<Map<String, Object>> data; // 这里是引用还没有实例化

    // 显示侧边栏
    void toggle(DrawerLayout mDrawerLayout) {
        int drawerLockMode = mDrawerLayout.getDrawerLockMode(GravityCompat.START);
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)
                && (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    // 获取数据集
    public List<Map<String, Object>> getDatas() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        map = new HashMap<String, Object>();
        map.put("img", R.drawable.car_pic);
        map.put("name", "思锐智能云台");
        map.put("comment", 999);
        map.put("favour", 999);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("img", R.drawable.car_pic);
        map.put("name", "思锐智能二代小车");
        map.put("comment", 999);
        map.put("favour", 999);
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("img", R.drawable.car_pic);
        map.put("name", "思锐智能自拍杆");
        map.put("comment", 999);
        map.put("favour", 999);
        list.add(map);

        return list;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.device_layout, null);

        lv = (ListView) rootView.findViewById(R.id.dl_lv);
        data = getDatas();
        DeviceListAdapter adapter = new DeviceListAdapter(getActivity(), data);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ToastUtils.showToast((String) data.get(position).get("name"));

                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), ScrollingActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(getActivity(), SmartCar2.class));
                        break;
                    case 2:
                        startActivity(new Intent(getActivity(), ScrollingActivity.class));
                        break;
                }
            }
        });


        // 侧边栏
        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);

        // 显示圆角图片
//        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
//        draweeView.setBackgroundResource(R.drawable.ic_head_logo);
//        draweeView.setImageURI(uri);
        // 点击主界面圆角图片
        cm_image_view = (SimpleDraweeView) rootView.findViewById(R.id.dl_image_view);
        cm_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(drawer);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
