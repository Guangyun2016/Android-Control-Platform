package com.sirui.smartcar.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sirui.smartcar.R;
import com.sirui.smartcar.adapter.DeviceViewPagerAdapter;
import com.sirui.smartcar.ui.activity.SmartCar2;
import com.stx.xhb.xbanner.transformers.ZoomPageTransformer;
import com.wx.android.common.util.ToastUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ygy on 2016/9/29 0029.
 */
public class Device extends Fragment {

    // 圆形头像
    private SimpleDraweeView cm_image_view;

    private Button btnConnect;
    private TextView txtKnowMore;
    private int connectIndex = 0;
    private static final int DEVICE_CAR = 0;
    private static final int DEVICE_HAND_HOLD = 1;
//    private static final int DEVICE_ZIPAI = 2;

    private int[] imgs = {R.drawable.pic_car, R.drawable.pic_hand_hold_stabler};
    private String[] titles = {"智能二代摄影小车", "智能手持稳定器"};
    /**图片集合**/
    private List<ImageView> vData = new ArrayList<ImageView>();
    /**游标集合**/
    private ArrayList<ImageView> pointViews = new ArrayList<ImageView>();
    private ViewPager mViewPager;
    private DeviceViewPagerAdapter adapter;
    private LinearLayout pointContainer;
    // 去除ViewPager滑动到边沿时颜色显示
    private EdgeEffectCompat leftEdge;
    private EdgeEffectCompat rightEdge;

    private TextView deviceTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.device_layout, null);
        initViews(rootView);


        return rootView;
    }

    public void initViews(View rootView) {
        // 显示圆角图片
//        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
//        draweeView.setBackgroundResource(R.drawable.ic_head_logo);
//        draweeView.setImageURI(uri);

        btnConnect = (Button) rootView.findViewById(R.id.btn_how_to_connect_device);
        txtKnowMore = (TextView) rootView.findViewById(R.id.txt_device_know_more);
        // 侧边栏
        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        // 点击主界面圆角图片
        cm_image_view = (SimpleDraweeView) rootView.findViewById(R.id.dl_image_view);
        deviceTitle = (TextView) rootView.findViewById(R.id.txt_device_title);

        pointContainer = (LinearLayout) rootView.findViewById(R.id.device_point_ll);
        mViewPager = (ViewPager) rootView.findViewById(R.id.device_viewpager);
        initViewPager();
        getInitViewPager();

        cm_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(drawer);
            }
        });

        /**
         * 点击连接设备按钮
         */
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ToastUtils.showToast("这个是-->" + String.valueOf(connectIndex) + "--" + titles[connectIndex]);

                switch (connectIndex) {
                    case DEVICE_CAR:
                        startActivity(new Intent(getContext(), SmartCar2.class));
                        break;
                    case DEVICE_HAND_HOLD:
                        break;

                }
            }
        });

        /**
         * 点击了解更多
         */
        txtKnowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ToastUtils.showToast("这个是-->" + String.valueOf(connectIndex) + "--" + titles[connectIndex]);

                switch (connectIndex) {
                    case DEVICE_CAR:
                        break;
                    case DEVICE_HAND_HOLD:
                        break;
                }
            }
        });
    }

    public void getInitViewPager() {


        for (int i = 0; i < imgs.length; i++) {

            // ===========================图片
            ImageView img = new ImageView(getActivity());
            img.setBackgroundResource(imgs[i]);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            img.setLayoutParams(layoutParams);
            vData.add(img);

            // ===========================游标
            ImageView pImg = new ImageView(getActivity());
            pImg.setBackgroundResource(R.drawable.point_normal_d);
            LinearLayout.LayoutParams layoutParams_pImg = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams_pImg.leftMargin = 20;
            layoutParams_pImg.rightMargin = 20;
            layoutParams_pImg.width = 20;
            layoutParams_pImg.height = 20;
            pImg.setLayoutParams(layoutParams_pImg);
            pointContainer.addView(pImg);// 添加到容器

            // 初始显示游标
            if (i == 0) {
                pImg.setBackgroundResource(R.drawable.point_selected_d);
            } else {
                pImg.setBackgroundResource(R.drawable.point_normal_d);
            }

            pointViews.add(pImg);// 这里是游标点集合
        }

        adapter = new DeviceViewPagerAdapter(getActivity(), vData);
        mViewPager.setAdapter(adapter);
        mViewPager.setPageTransformer(true, new ZoomPageTransformer());// 页面切换动画
        // ViewPager和title初始化显示内容
        deviceTitle.setText(titles[connectIndex]);// 显示设备名称和型号
        mViewPager.setCurrentItem(0);// 显示第一个元素



        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 去除ViewPager滑动到边沿时颜色显示
                if (leftEdge != null && rightEdge != null) {
                    leftEdge.finish();
                    rightEdge.finish();
                    leftEdge.setSize(0, 0);
                    rightEdge.setSize(0, 0);
                }
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < pointViews.size(); i++) {
                    if (position == i) {
                        pointViews.get(i).setBackgroundResource(R.drawable.point_selected_d);
                    } else {
                        pointViews.get(i).setBackgroundResource(R.drawable.point_normal_d);
                    }
                }


                connectIndex = position;// 索引
                deviceTitle.setText(titles[connectIndex]);// 显示设备名称和型号
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


    }

    /**
     * // 去除ViewPager滑动到边沿时颜色显示
     */
    private void initViewPager() {
        try {
            Field leftEdgeField = mViewPager.getClass().getDeclaredField("mLeftEdge");
            Field rightEdgeField = mViewPager.getClass().getDeclaredField("mRightEdge");
            if (leftEdgeField != null && rightEdgeField != null) {
                leftEdgeField.setAccessible(true);
                rightEdgeField.setAccessible(true);
                leftEdge = (EdgeEffectCompat) leftEdgeField.get(mViewPager);
                rightEdge = (EdgeEffectCompat) rightEdgeField.get(mViewPager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
