package com.sirui.iotplatform.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by ygy on 2016/9/30 0030.
 */

public class DeviceViewPagerAdapter extends PagerAdapter {

    private List<ImageView> imgs = null;

    public DeviceViewPagerAdapter(Context context, List<ImageView> imgs) {
        this.imgs = imgs;
    }

    @Override
    public int getCount() {
        return this.imgs.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(imgs.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(imgs.get(position));
        return imgs.get(position);
    }

}
