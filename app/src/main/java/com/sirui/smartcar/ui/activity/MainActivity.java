package com.sirui.smartcar.ui.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sirui.smartcar.R;
import com.sirui.smartcar.ui.fragment.Device;
import com.sirui.smartcar.ui.fragment.Market;
import com.sirui.smartcar.ui.fragment.News;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // 装载fragment容器
    private RelativeLayout tab_container;
    // fragments
    private Fragment[] fragments;
    private Device deviceFrag;
    private News newsFrag;
    private Market marketFrag;
    // 索引
    private int currentFragIndex = 0;
    private int index = 0;
    private ImageView[] imageViews;
    private TextView[] textViews;

//    private LinearLayout tab_botton_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        tab_botton_ll = (LinearLayout) findViewById(tab_botton_ll);

        // 侧边栏点击监听onNavigationItemSelected
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tab_container = (RelativeLayout) findViewById(R.id.tab_container);
        deviceFrag = new Device();
        newsFrag = new News();
        marketFrag = new Market();
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
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share_app) {
            Toast.makeText(this, R.string.drawer_left_share, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_info) {
            Toast.makeText(this, R.string.drawer_left_info, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_production) {
            Toast.makeText(this, R.string.drawer_left_production, Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_service) {
            Toast.makeText(this, R.string.drawer_left_service, Toast.LENGTH_SHORT).show();
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
}
