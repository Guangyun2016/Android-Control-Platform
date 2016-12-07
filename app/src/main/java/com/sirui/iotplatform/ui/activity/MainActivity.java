package com.sirui.iotplatform.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sirui.iotplatform.R;
import com.sirui.iotplatform.ui.fragment.Device;
import com.sirui.iotplatform.ui.fragment.News;
import com.sirui.iotplatform.ui.fragment.Shop;
import com.wx.android.common.util.ToastUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // 装载fragment容器
    private RelativeLayout tab_container;
    // fragments
    private Fragment[] fragments;
    private Device deviceFrag;
    private News newsFrag;
    private Shop marketFrag;
    // 索引
    private int currentFragIndex = 0;
    private int index = 0;
    private ImageView[] imageViews;
    private TextView[] textViews;

    // headerLayout
    private SimpleDraweeView headImageView;
    private TextView txtName, txtSign;
    private SharedPreferences sharedPreferences = null;

    // 在按一次退出Time
    private long firstTime;
    private long secondTime;
    private long spaceTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // -----------------------------------------------------------------fragment start
        tab_container = (RelativeLayout) findViewById(R.id.tab_container);
        deviceFrag = new Device();
        newsFrag = new News();
        marketFrag = new Shop();
        fragments = new Fragment[]{deviceFrag, newsFrag, marketFrag};

        imageViews = new ImageView[3];
        imageViews[0] = (ImageView) findViewById(R.id.tab1_iv);
        imageViews[1] = (ImageView) findViewById(R.id.tab2_iv);
        imageViews[2] = (ImageView) findViewById(R.id.tab3_iv);
        imageViews[0].setSelected(true);

        textViews = new TextView[3];
        textViews[0] = (TextView) findViewById(R.id.tab1_tv);
        textViews[1] = (TextView) findViewById(R.id.tab2_tv);
        textViews[2] = (TextView) findViewById(R.id.tab3_tv);
        textViews[0].setTextColor(getResources().getColor(R.color.black));

        // 添加Fragments，并显示第一个
        getSupportFragmentManager().beginTransaction()
                .add(R.id.tab_container, deviceFrag)
                .add(R.id.tab_container, newsFrag)
                .add(R.id.tab_container, marketFrag)
                .hide(newsFrag)
                .hide(marketFrag)
                .show(deviceFrag)
                .commit();
        // -----------------------------------------------------------------fragment end


        sharedPreferences = getApplicationContext().getSharedPreferences("wxUserInfo", 0);

        // 侧边栏点击监听onNavigationItemSelected
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // xml should remove app:headerLayout="@layout/nav_header_main"
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        // get NavigationView widget id
        headImageView = (SimpleDraweeView) headerLayout.findViewById(R.id.nav_header_main_imageView);
        txtName = (TextView) headerLayout.findViewById(R.id.nav_header_main_name);
        txtSign = (TextView) headerLayout.findViewById(R.id.nav_header_main_sign);

//        // 加载圆形头像
        Uri uri = Uri.parse(sharedPreferences.getString("headimgurl", ""));
        headImageView.setImageURI(uri);
        txtName.setText(sharedPreferences.getString("nickname", "未登录"));
        txtSign.setText(sharedPreferences.getString("signature", ""));

        headImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PersonalInfo.class));
            }
        });

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share_app) {
            ToastUtils.showToast(getString(R.string.drawer_left_share));
        } else if (id == R.id.nav_info) {
            ToastUtils.showToast(getString(R.string.drawer_left_info));
            startActivity(new Intent(MainActivity.this, PersonalInfo.class));
        } else if (id == R.id.nav_production) {
            ToastUtils.showToast(getString(R.string.drawer_left_production));
        } else if (id == R.id.nav_service) {
            ToastUtils.showToast(getString(R.string.drawer_left_service));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 点击事件
    public void tabBtnOnClicked(View view) {
        switch (view.getId()) {
            case R.id.tab1_rl:
                index = 0;
                break;
            case R.id.tab2_rl:
                index = 1;
                break;
            case R.id.tab3_rl:
                index = 2;
                break;
        }

        if (currentFragIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentFragIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.tab_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }

        imageViews[currentFragIndex].setSelected(false);
        imageViews[index].setSelected(true);
        textViews[currentFragIndex].setTextColor(getResources().getColor(R.color.gray_C8C7C8));
        textViews[index].setTextColor(getResources().getColor(R.color.black));
        currentFragIndex = index;
    }

    /**
     * 跳到京东商城页面
     * @param view
     */
    public void GoToJingDongShop(View view) {
        startActivity(new Intent(this, ShopJingDong.class));

    }

    /**
     * 跳到天猫商城页面
     * @param view
     */
    public void goToTianMaoShop(View view) {
        startActivity(new Intent(this, ShopTianMao.class));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            firstTime = System.currentTimeMillis();
            spaceTime = firstTime - secondTime;
            secondTime = firstTime;

            if (spaceTime > 2000) {
                ToastUtils.showToast("再按一次退出" + getString(R.string.app_name));
                return false;
            } else {
                System.exit(0);
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}

























