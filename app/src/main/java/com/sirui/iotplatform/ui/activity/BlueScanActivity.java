package com.sirui.iotplatform.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sirui.iotplatform.R;
import com.sirui.iotplatform.ui.adapter.DeviceAdapter;
import com.vise.baseble.ViseBluetooth;
import com.vise.baseble.callback.scan.PeriodScanCallback;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.BluetoothLeDeviceStore;
import com.vise.baseble.utils.BleUtil;
import com.wx.android.common.util.SystemUtils;
import com.wx.android.common.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 暂时不用这个BlueScanActivity------用Dialog代替
 */
public class BlueScanActivity extends Activity {

    private static final String TAG = BlueScanActivity.class.getSimpleName();
    public static final String EXTRA_BLE_DEVICE = "extra_ble_device";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2;

    private TextView txtGoInTo;
    private ImageButton btnClear;
    private ImageView bleRefresh;

    private ListView deviceLv;
    private BluetoothLeDeviceStore bluetoothLeDeviceStore;
    private volatile List<BluetoothLeDevice> bluetoothLeDeviceList = new ArrayList<>();
    private DeviceAdapter adapter;

    // 扫描设备回调
    private PeriodScanCallback periodScanCallback = new PeriodScanCallback() {
        @Override
        public void scanTimeout() {
            Log.i(TAG, "scan timeout");
            stopScan();// 停止扫描
        }

        @Override
        public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {
            if (bluetoothLeDeviceStore != null) {
                bluetoothLeDeviceStore.addDevice(bluetoothLeDevice);
                bluetoothLeDeviceList = bluetoothLeDeviceStore.getDeviceList();
            }

            runOnUiThread(new Runnable() {
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
                if (BleUtil.isBleEnable(this)) {
                    startScan();
                } else {
                    BleUtil.enableBluetooth(this, 1);
                }
            } else {
                // Permission Denied
                Log.e(TAG, "LOCATION Permission Denied");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        ViseBluetooth.getInstance().init(getApplicationContext());
        init();

        /**
         * 6.0的sdk要请求权限
         */
        Log.i(TAG, "SDK Version--->" + String.valueOf(SystemUtils.getVersionSDK()));
        if (SystemUtils.getVersionSDK() >= 23) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                if (BleUtil.isBleEnable(this)) {
                    startScan();
                } else {
                    BleUtil.enableBluetooth(this, 1);
                }
            }
        } else {
            if (BleUtil.isBleEnable(this)) {
                startScan();
            } else {
                BleUtil.enableBluetooth(this, 1);
            }
        }

    }

    private void init() {
        deviceLv = (ListView) findViewById(R.id.ble_scan_listview);
        txtGoInTo = (TextView) findViewById(R.id.ble_txt_gointo);
        btnClear = (ImageButton) findViewById(R.id.ble_btn_clear);
        bleRefresh = (ImageView) findViewById(R.id.ble_scan_refresh);

        bluetoothLeDeviceStore = new BluetoothLeDeviceStore();
        adapter = new DeviceAdapter(this);
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

                // 进入小车控制界面
                Intent intent = new Intent(BlueScanActivity.this, SmartCar2.class);
                intent.putExtra(EXTRA_BLE_DEVICE, device);
                startActivity(intent);

                bleRefresh.clearAnimation();
            }
        });

        txtGoInTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast("进入小车控制界面");
                startActivity(new Intent(BlueScanActivity.this, SmartCar2.class));

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast("退出");
                BlueScanActivity.this.finish();
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

    @Override
    protected void onResume() {
        super.onResume();
        boolean isSupport = BleUtil.isSupportBle(this);
        boolean isOpenBle = BleUtil.isBleEnable(this);
//        if(isSupport){
//            supportTv.setText(getString(R.string.supported));
//        } else{
//            supportTv.setText(getString(R.string.not_supported));
//        }
//        if (isOpenBle) {
//            statusTv.setText(getString(R.string.on));
//        } else{
//            statusTv.setText(getString(R.string.off));
//        }
//        invalidateOptionsMenu();
        if (BleUtil.isBleEnable(this)) {
            startScan();
        } else {
            BleUtil.enableBluetooth(this, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // 请求打开蓝牙回复
            if (requestCode == 1) {
                startScan();
            }
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


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
        Animation circle_anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.ble_refresh);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        circle_anim.setInterpolator(interpolator);
        if (circle_anim != null) {
            bleRefresh.startAnimation(circle_anim);  //开始动画
        }
    }

    private void stopScan() {
        ViseBluetooth.getInstance().stopScan(periodScanCallback);// 停止扫描

        bleRefresh.clearAnimation();// 关闭动画
    }


}
