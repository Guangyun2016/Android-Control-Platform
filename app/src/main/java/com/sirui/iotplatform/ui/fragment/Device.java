package com.sirui.iotplatform.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sirui.iotplatform.R;
import com.sirui.iotplatform.ui.activity.BlueScanActivity;
import com.sirui.iotplatform.ui.activity.CameraControl;
import com.sirui.iotplatform.ui.activity.DeviceKnowMore;
import com.sirui.iotplatform.ui.activity.SmartCar2;
import com.sirui.iotplatform.ui.adapter.DeviceAdapter;
import com.sirui.iotplatform.ui.adapter.DeviceViewPagerAdapter;
import com.stx.xhb.xbanner.transformers.ZoomPageTransformer;
import com.vise.baseble.ViseBluetooth;
import com.vise.baseble.callback.scan.PeriodScanCallback;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.BluetoothLeDeviceStore;
import com.vise.baseble.utils.BleUtil;
import com.wx.android.common.util.SystemUtils;
import com.wx.android.common.util.ToastUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ygy on 2016/9/29 0029.
 */
public class Device extends Fragment {

    private Activity activity;
    private final String TAG = "Device";
    private View mLayout = null;
    // 圆形头像
    private SimpleDraweeView cm_image_view;

    private Button btnConnect;
    private TextView txtKnowMore;
    private int connectIndex = 0;
    private static final int DEVICE_CAR = 0;
    private static final int DEVICE_HAND_HOLD = 1;

    private int[] imgs = {R.drawable.pic_car, R.drawable.pic_hand_hold_stabler};
    private String[] titles = {"智能二代摄影小车", "智能手持稳定器"};
    /**
     * 图片集合
     **/
    private List<ImageView> vData = new ArrayList<ImageView>();
    /**
     * 游标集合
     **/
    private ArrayList<ImageView> pointViews = new ArrayList<ImageView>();
    private ViewPager mViewPager;
    private DeviceViewPagerAdapter deviceAdapter;
    private LinearLayout pointContainer;
    // 去除ViewPager滑动到边沿时颜色显示
    private EdgeEffectCompat leftEdge;
    private EdgeEffectCompat rightEdge;

    private TextView deviceTitle;

    private SharedPreferences sharedPreferences = null;


    // ==================================================================ble
    public static final String EXTRA_BLE_DEVICE = "extra_ble_device";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 102;

    private TextView txtGoInTo;
    private ImageButton btnClear;
    private ImageView bleRefresh;

    private ListView deviceLv;
    private BluetoothLeDeviceStore bluetoothLeDeviceStore;
    private volatile List<BluetoothLeDevice> bluetoothLeDeviceList = new ArrayList<>();
    private DeviceAdapter adapter;
    private AlertDialog dialog;


    // 扫描设备回调
    private PeriodScanCallback periodScanCallback = new PeriodScanCallback() {
        @Override
        public void scanTimeout() {
            Log.i(TAG, "scan timeout");
            stopScan();// 停止扫描
//            dialog.dismiss();
        }

        @Override
        public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {
            if (bluetoothLeDeviceStore != null) {
                bluetoothLeDeviceStore.addDevice(bluetoothLeDevice);
                bluetoothLeDeviceList = bluetoothLeDeviceStore.getDeviceList();
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setDeviceList(bluetoothLeDeviceList);// 给Adapter数据集
                }
            });
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (BleUtil.isBleEnable(getActivity())) {
                    showDeviceDialog();
                    startScan();
                } else {
                    BleUtil.enableBluetooth(getActivity(), 1);
                }
            } else {
                // Permission Denied
                Log.e(TAG, "LOCATION Permission Denied");
                Snackbar.make(mLayout, getString(R.string.app_name) + "被禁止读取位置信息", Snackbar.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.device_layout, null);
        initViews(rootView);
        activity = getActivity();
        mLayout = rootView.findViewById(R.id.device_relativeLayout);

        ViseBluetooth.getInstance().init(getActivity());

        return rootView;
    }


    public void initViews(View rootView) {

        sharedPreferences = getActivity().getSharedPreferences("wxUserInfo", 0);

        // 显示圆角图片
//        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
//        draweeView.setBackgroundResource(R.drawable.ic_head_logo);
//        draweeView.setImageURI(uri);// 网络加载
        // 网络加载主界面圆角图片头像
        cm_image_view = (SimpleDraweeView) rootView.findViewById(R.id.dl_image_view);
        Uri uri = Uri.parse(sharedPreferences.getString("headimgurl", ""));
        cm_image_view.setImageURI(uri);

        btnConnect = (Button) rootView.findViewById(R.id.btn_how_to_connect_device);
        txtKnowMore = (TextView) rootView.findViewById(R.id.txt_device_know_more);
        // 侧边栏
        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);

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
         * Click connect Device button
         */
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 6.0的sdk要请求权限
                 */
                Log.i(TAG, "SDK Version--->" + String.valueOf(SystemUtils.getVersionSDK()));
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    if (BleUtil.isBleEnable(getActivity())) {
                        showDeviceDialog();
                        startScan();
                    } else {
                        BleUtil.enableBluetooth(getActivity(), 1);
                    }
                }

                ToastUtils.showToast("这个是-->" + String.valueOf(connectIndex) + "--" + titles[connectIndex]);
//                switch (connectIndex) {
//                    case DEVICE_CAR:
//                        ToastUtils.showToast("小车");
//                        startActivity(new Intent(getContext(), BlueScanActivity.class));
//                        startActivity(new Intent(getContext(), SmartCar2.class));
//                        break;
//                    case DEVICE_HAND_HOLD:
//                        ToastUtils.showToast("手持稳定器");
//                        break;
//                }
            }
        });

        /**
         * click know more
         */
        txtKnowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ToastUtils.showToast("这个是-->" + String.valueOf(connectIndex) + "--" + titles[connectIndex]);

                // 跳到了解更多页面
                startActivity(new Intent(getActivity(), DeviceKnowMore.class));

                switch (connectIndex) {
                    case DEVICE_CAR:
                        break;
                    case DEVICE_HAND_HOLD:
                        break;
                }
            }
        });
    }

    /**
     * Open Dialog to choose ble device
     */
    public void showDeviceDialog() {
        dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.activity_device_scan);

        deviceLv = (ListView) window.findViewById(R.id.ble_scan_listview);
        txtGoInTo = (TextView) window.findViewById(R.id.ble_txt_gointo);
        btnClear = (ImageButton) window.findViewById(R.id.ble_btn_clear);
        bleRefresh = (ImageView) window.findViewById(R.id.ble_scan_refresh);

        bluetoothLeDeviceStore = new BluetoothLeDeviceStore();
        adapter = new DeviceAdapter(getActivity());
        deviceLv.setAdapter(adapter);

        deviceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothLeDevice device = (BluetoothLeDevice) adapter.getItem(position);// 得到设备对象
                if (device == null) {
                    Log.i(TAG, "device == null!!!!!!!!!!!!");
                    return;
                }

                Log.i(TAG, device.getName());

                if (connectIndex == DEVICE_CAR) {
                    // SmartCart2 Control
                    Intent intent = new Intent(activity, SmartCar2.class);
                    intent.putExtra(EXTRA_BLE_DEVICE, device);
                    startActivity(intent);
                } else if (connectIndex == DEVICE_HAND_HOLD){
                    // HandHold Device Camera Control
                    Intent intent = new Intent(activity, CameraControl.class);
                    startActivity(intent);
                }

                dialog.dismiss();
                bleRefresh.clearAnimation();
            }
        });

        txtGoInTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectIndex == DEVICE_CAR) {
                    // 进入小车控制界面
                    startActivity(new Intent(activity, SmartCar2.class));
                } else if (connectIndex == DEVICE_HAND_HOLD){
                    // 相机控制
                    startActivity(new Intent(activity, CameraControl.class));
                }

                dialog.dismiss();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast("退出");
                dialog.dismiss();
            }
        });

        bleRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast("刷新");
                startScan();
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

        deviceAdapter = new DeviceViewPagerAdapter(getActivity(), vData);
        mViewPager.setAdapter(deviceAdapter);
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
//        boolean isSupport = BleUtil.isSupportBle(getActivity());
//        boolean isOpenBle = BleUtil.isBleEnable(getActivity());
//
//        if (BleUtil.isBleEnable(getActivity())) {
//            startScan();
//        } else {
//            BleUtil.enableBluetooth(getActivity(), 1);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        stopScan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            // 请求打开蓝牙回复
            if (requestCode == 1) {
                startScan();
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            /**
             * dialog dissmiss
             */
//            dialog.dismiss();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Start scan ble device
     */
    private void startScan() {
        if (bluetoothLeDeviceStore != null) {
            bluetoothLeDeviceStore.clear();
        }
        if (adapter != null && bluetoothLeDeviceList != null) {
            bluetoothLeDeviceList.clear();
            adapter.setDeviceList(bluetoothLeDeviceList);
        }
        ViseBluetooth.getInstance().setScanTimeout(5000).startScan(periodScanCallback);// 开始扫描5s


        // 刷新动画
        Animation circle_anim = AnimationUtils.loadAnimation(getActivity(),
                R.anim.ble_refresh);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        circle_anim.setInterpolator(interpolator);
        if (circle_anim != null) {
            bleRefresh.startAnimation(circle_anim);  //开始动画
        }
    }

    /**
     * Stop scan ble device
     */
    private void stopScan() {
        ViseBluetooth.getInstance().stopScan(periodScanCallback);// 停止扫描

        bleRefresh.clearAnimation();// 关闭动画
    }



}
