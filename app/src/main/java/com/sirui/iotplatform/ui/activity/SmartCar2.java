package com.sirui.iotplatform.ui.activity;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sirui.iotplatform.R;
import com.sirui.iotplatform.widget.DrawCarTrackPath;
import com.vise.baseble.ViseBluetooth;
import com.vise.baseble.callback.IBleCallback;
import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.exception.BleException;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.utils.BleLog;
import com.vise.baseble.utils.HexUtil;
import com.wx.android.common.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class SmartCar2 extends AppCompatActivity {

    private static final String TAG = SmartCar2.class.getSimpleName();

    private CheckBox sc_pating, sc_del, sc_graph, sc_ruler, sc_speed;
    private DrawCarTrackPath drawCarTrackPath;
    private HorizontalScrollView horizontalScrollView;
    int horizontalScrollViewMeasuredWidth = 0;

    // 设置小车速度
    private SeekBar speedSeekBar;
    private RelativeLayout speedSeekBarRL;
    private TextView speedTxt;
    private Button sendBtn = null;

    private RelativeLayout parentView;
    private LinearLayout graphLayout = null;
    private TextView pCircle, pTrangle, pRectangle;

    //===============BLE================
    private BluetoothLeDevice mDevice; // ble device-->is LeDevice
    private BluetoothGattCharacteristic mCharacteristic;
    private StringBuilder mOutputInfo = new StringBuilder();
    private List<List<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_smart_car2);

        initView();
    }

    private void initView() {

        // get BLE object
        mDevice = getIntent().getParcelableExtra(BlueScanActivity.EXTRA_BLE_DEVICE);
        if(mDevice != null){
//            ((TextView) findViewById(R.id.device_address)).setText(mDevice.getAddress());
            Log.i(TAG, "BluetoothLeDevice--mDevice.getAddress()--->" + mDevice.getAddress());
        }

        drawCarTrackPath = (DrawCarTrackPath) findViewById(R.id.sc_draw_path_view);
        drawCarTrackPath.setOnCarPathViewClickListener(new myOnDrawCarPathViewClickListener());

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.sc_hsView);
        // 匹配屏幕尺寸得到HorizontalScrollView宽度
        ViewTreeObserver vto = horizontalScrollView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                horizontalScrollViewMeasuredWidth = horizontalScrollView.getMeasuredWidth();
//                Log.i(TAG, "horizontalScrollViewMeasuredWidth---> " + horizontalScrollViewMeasuredWidth);
                return true;
            }
        });

        sendBtn = (Button) findViewById(R.id.sc_send_to_ble);
        speedTxt = (TextView) findViewById(R.id.txt_show_car_speed);
        speedSeekBar = (SeekBar) findViewById(R.id.car_speed_seekBar);
        speedSeekBarRL = (RelativeLayout) findViewById(R.id.car_speed_seekBar_rl);
        sc_pating = (CheckBox) findViewById(R.id.sc_painting);
        sc_del = (CheckBox) findViewById(R.id.sc_del);
        sc_graph = (CheckBox) findViewById(R.id.sc_graph);
        sc_ruler = (CheckBox) findViewById(R.id.sc_ruler);
        sc_speed = (CheckBox) findViewById(R.id.sc_speed);

        parentView = (RelativeLayout) findViewById(R.id.layout_smart_car2);
        graphLayout = (LinearLayout) findViewById(R.id.graph_layout);
        pCircle = (TextView) findViewById(R.id.p_circle);
        pRectangle = (TextView) findViewById(R.id.p_rectangle);
        pTrangle = (TextView) findViewById(R.id.p_triangle);

        sc_pating.setChecked(true);
        // 自由路径
        sc_pating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (true == isChecked) {
                    ToastUtils.showToast("sc_pating ischecked");
//                    drawCarTrackPath.setWhetherDraw(true);
                    drawCarTrackPath.setPaintType(DrawCarTrackPath.PAINT_TYPE_LINE);
                } else {
                    ToastUtils.showToast("sc_pating");
//                    drawCarTrackPath.setWhetherDraw(false);
                }

                setHorizontalScrollViewMoveToLeft();
            }
        });
        sc_del.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ToastUtils.showToast("sc_del");
                scDelDeal();
                setHorizontalScrollViewMoveToLeft();
            }
        });
        sc_graph.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                sendBtn.setVisibility(View.GONE);
                drawCarTrackPath.reset();
                drawCarTrackPath.setWhetherDraw(false);// 选择图形画笔后可以画图

                if (true == isChecked) {
                    ToastUtils.showToast("sc_graph ischecked");
                    graphLayout.setVisibility(View.VISIBLE);

                } else {
                    graphLayout.setVisibility(View.GONE);
                    ToastUtils.showToast("sc_graph");
                }
                setHorizontalScrollViewMoveToReght();
            }
        });
        sc_ruler.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (true == isChecked) {
                    ToastUtils.showToast("sc_ruler ischecked");
                    drawCarTrackPath.setWhetherDrawRuler(true);
                } else {
                    ToastUtils.showToast("sc_ruler");
                    drawCarTrackPath.setWhetherDrawRuler(false);
                }
                setHorizontalScrollViewMoveToReght();
            }
        });
        sc_speed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (true == isChecked) {
                    ToastUtils.showToast("sc_speed ischecked");

                    speedSeekBarRL.setVisibility(View.VISIBLE);

                } else {
                    speedSeekBarRL.setVisibility(View.GONE);
                    ToastUtils.showToast("sc_speed");
                }
            }
        });

        /*****速度进度条控制*****/
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /*
            * seekbar改变时的事件监听处理
            * */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedTxt.setText(progress + "%");
                Log.i(TAG, "onProgressChanged--->" + String.valueOf(seekBar.getId()));
            }

            /*
            * 按住seekbar时的事件监听处理
            * */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                ToastUtils.showToast("按住SeekBar");
            }

            /*
            * 放开seekbar时的时间监听处理
            * */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ToastUtils.showToast("放开SeekBar");
            }
        });

        /**圆形画笔
         */
        pCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sc_pating.setChecked(false);
                sc_graph.setChecked(false);
                sc_speed.setChecked(false);

                drawCarTrackPath.reset();
                drawCarTrackPath.setWhetherDraw(true);
                sendBtn.setVisibility(View.GONE);

                drawCarTrackPath.setPaintType(DrawCarTrackPath.PAINT_TYPE_CIRCLE);
            }
        });
        /**
         * 矩形画笔
         */
        pRectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sc_pating.setChecked(false);
                sc_graph.setChecked(false);
                sc_speed.setChecked(false);

                drawCarTrackPath.reset();
                drawCarTrackPath.setWhetherDraw(true);
                sendBtn.setVisibility(View.GONE);
                drawCarTrackPath.setPaintType(DrawCarTrackPath.PAINT_TYPE_RECTANGLE);
            }
        });
        /**
         * 三角形画笔
         */
        pTrangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sc_pating.setChecked(false);
                sc_graph.setChecked(false);
                sc_speed.setChecked(false);

                drawCarTrackPath.reset();
                drawCarTrackPath.setWhetherDraw(true);
                sendBtn.setVisibility(View.GONE);
                drawCarTrackPath.setPaintType(DrawCarTrackPath.PAINT_TYPE_TRIANGLE);
            }
        });

        /**
         * 发送数据给BLE
         */
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCharacteristic == null) {
                    ToastUtils.showToast("Please select enable write characteristic!");
                    return;
                }

                Log.i("send", TAG +  "--->send--->" + mCharacteristic.toString());

//                if (mInput.getText() == null || mInput.getText().toString() == null) {
//                    Toast.makeText(DeviceControlActivity.this, "Please input command!", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                /**
                 * 原来的--->发送数据HexUtil.decodeHex(mInput.getText().toString().toCharArray())
                 * 改成发送字符串后再转成16进制数组
                 */
//                ViseBluetooth.getInstance().writeCharacteristic(mCharacteristic,
//                        HexUtil.decodeHex(str2HexStr(mInput.getText().toString()).toCharArray()), new IBleCallback<BluetoothGattCharacteristic>() {
//                            @Override
//                            public void onSuccess(BluetoothGattCharacteristic characteristic, int type) {
//                                BleLog.i("Send onSuccess!");
//                                ToastUtils.showToast("Send onSuccess!");
//                            }
//
//                            @Override
//                            public void onFailure(BleException exception) {
//                                BleLog.i("Send onFail!");
//                            }
//                        });
            }
        });

    }

    /**
     * 字符串转成十六进制字符串
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    /**
     * 清除动作
     */
    public void scDelDeal() {
        drawCarTrackPath.reset();
        drawCarTrackPath.setWhetherDraw(true);
//        sc_pating.setChecked(true);
        sc_graph.setChecked(false);
        sc_speed.setChecked(false);
        sendBtn.setVisibility(View.GONE);
    }

    /**        drawCarTrackPath.setWhetherDraw(true);

     * HorizontalScrollView滑动到最右边
     */
    public void setHorizontalScrollViewMoveToReght() {
//        horizontalScrollView.smoothScrollBy(horizontalScrollViewMeasuredWidth, 0);// 也可以
        horizontalScrollView.fullScroll(View.FOCUS_RIGHT);

    }

    /**
     * HorizontalScrollView滑动到最左边
     */
    public void setHorizontalScrollViewMoveToLeft() {
        horizontalScrollView.fullScroll(View.FOCUS_LEFT);// 可以
//        horizontalScrollView.fullScroll(0);// 可以
    }

    public void tabBtnOnClicked(View view) {
        SmartCar2.this.finish();
    }

    /**
     * DrawCarTrackPath回调
     */
    class myOnDrawCarPathViewClickListener implements DrawCarTrackPath.onDrawCarPathViewClickListener {

        @Override
        public void onDrawPathViewClick() {

        }

        @Override
        public void onDrawPathMotionEventUp() {
            sendBtn.setVisibility(View.VISIBLE);
            drawCarTrackPath.setWhetherDraw(false);
            drawCarTrackPath.setWhetherDrawXYLine(false);

        }

        @Override
        public void onDrawPathMotionEventDown() {
//            sc_graph.setChecked(false);
            sc_speed.setChecked(false);
        }
    }

    /**
     * 连接设备回调
     */
    private IConnectCallback connectCallback = new IConnectCallback() {
        @Override
        public void onConnectSuccess(BluetoothGatt gatt, int status) {
            BleLog.i(TAG +  "--->Connect Success!");

            /**
             * 连接成功之后，在这里getBluttoothGattService()--->Characteristic
             * 连接成功之后，在这里getBluttoothGattService()--->Characteristic
             */
            ToastUtils.showToast("Connect Success!");
//            mConnectionState.setText("true");
//            if(gatt != null){
//                simpleExpandableListAdapter = displayGattServices(gatt.getServices());
//            }
        }

        @Override
        public void onConnectFailure(BleException exception) {
            BleLog.i(TAG +  "--->Connect Failure!");
//            Toast.makeText(DeviceControlActivity.this, "Connect Failure!", Toast.LENGTH_SHORT).show();
//            mConnectionState.setText("false");
//            invalidateOptionsMenu();
//            clearUI();
        }

        @Override
        public void onDisconnect() {
            BleLog.i(TAG +  "--->Disconnect!");
//            Toast.makeText(DeviceControlActivity.this, "Disconnect!", Toast.LENGTH_SHORT).show();
//            mConnectionState.setText("false");
//            invalidateOptionsMenu();
//            clearUI();
        }
    };

    /**
     * 通知服务
     */
    private IBleCallback bleCallback = new IBleCallback() {
        @Override
        public void onSuccess(Object o, int type) {
            if (o == null) {
                return;
            }
            if(o instanceof BluetoothGattCharacteristic){
                if (((BluetoothGattCharacteristic)o).getValue() == null) {
                    return;
                }

//                BleLog.i("notify success:"+HexUtil.encodeHexStr(((BluetoothGattCharacteristic)o).getValue()));
//                mOutputInfo.append(HexUtil.encodeHexStr(((BluetoothGattCharacteristic)o).getValue())).append("\n");
//                mOutput.setText(mOutputInfo.toString());
            }
        }

        @Override
        public void onFailure(BleException exception) {
            if (exception == null) {
                return;
            }
            BleLog.i(TAG +  "--->notify fail:"+exception.getDescription());
        }
    };


    // copy
    @Override
    protected void onResume() {
        super.onResume();
        ViseBluetooth.getInstance().connect(mDevice, false, connectCallback);// 连接
    }

    // copy
    @Override
    protected void onStop() {
        super.onStop();
        ViseBluetooth.getInstance().disconnect();// 断开
    }

    // copy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViseBluetooth.getInstance().clear();// 释放资源
    }


}
