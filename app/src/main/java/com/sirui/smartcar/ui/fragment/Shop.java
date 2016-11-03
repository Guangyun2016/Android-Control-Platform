package com.sirui.smartcar.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.sirui.smartcar.R;
import com.stx.xhb.xbanner.XBanner;
import com.wx.android.common.util.ToastUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ygy on 2016/9/29 0029.
 */

public class Shop extends Fragment {

    private Activity activity;
    private XBanner mBannerNet;
    private List<String> imgesUrls = new ArrayList<String>();

    List<ImageView> listImgs = new ArrayList<ImageView>();

    private static final String TAG = "Shop";

    private static final int HANDLER_SHOWDISPLAY = 0;

    private android.os.Handler mHandler = new android.os.Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case HANDLER_SHOWDISPLAY:{
                    showImageLoopDisplay();
                }
            }
        }
    };

    public void showImageLoopDisplay() {

        Log.i(TAG, "imgesUrl.size()--->" + imgesUrls.size());
        if (imgesUrls.size() > 0) {
            //添加广告数据
            mBannerNet.setData(imgesUrls, null);//第二个参数为提示文字资源集合
            mBannerNet.setmAdapter(new MyLoadBannerAdapter(imgesUrls));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.shop_layout, null);

        activity = getActivity();

        mBannerNet = (XBanner) rootView.findViewById(R.id.shop_xbanner);
        mBannerNet.setPageChangeDuration(3000); // 2s
        getImages();

        mBannerNet.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, int position) {
                ToastUtils.showToast("你点了-->" + position);
            }
        });

        return rootView;
    }

    public void getImages() {
        AVQuery<AVObject> avQuery = new AVQuery<>("advertisement");
        avQuery.getInBackground("58009e35a34131005fe30921", new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                // object 就是 id 为 558e20cbe4b060308e3eb36c 的 类 对象实例
                try {
                    JSONArray array = avObject.getJSONArray("contants");
                    Log.i(TAG, "JSONArray---->" + array.toString());
                    Log.i(TAG, "JSONArray---->" + array.length());

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jb = (JSONObject) array.get(i);
                        String url = jb.getString("url");
                        Log.i(TAG, "url---->" + url);
                        imgesUrls.add(url);
                    }

                    mHandler.sendEmptyMessage(HANDLER_SHOWDISPLAY);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    class MyLoadBannerAdapter implements XBanner.XBannerAdapter {

        List<String> urlDatas;

        public MyLoadBannerAdapter(List<String> datas) {
            this.urlDatas = datas;
        }

        @Override
        public void loadBanner(XBanner banner, View view, int position) {
            // 图片加载库Glide
            Glide.with(activity).load(urlDatas.get(position)).placeholder(R.drawable.pic_car).error(R.drawable.pic_car).into((ImageView) view);
            Log.i(TAG, "Glide load--->" + position);
        }
    }

    // 不能再Fragment中创建OnClick事件分发，只能在Activity中，这里把下面两个方法放到了MainActivity
    //    public void goToTianMaoShop(View view) {
    //        ToastUtils.showToast("打开天猫商城页面");
    //    }
    //    public void goToJingDongShop(View view) {
    //        ToastUtils.showToast("打开京东商城页面");
    //    }

    @Override
    public void onResume() {
        super.onResume();
//         开启图片轮播
        mBannerNet.startAutoPlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止图片轮播
        mBannerNet.stopAutoPlay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
