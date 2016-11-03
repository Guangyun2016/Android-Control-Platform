package com.sirui.smartcar.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sirui.smartcar.R;
import com.sirui.smartcar.widget.DrawCarTrackPath;
import com.wx.android.common.util.ToastUtils;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_smart_car2);

        initView();

    }

    private void initView() {
        drawCarTrackPath = (DrawCarTrackPath) findViewById(R.id.sc_draw_path_view);

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

        speedTxt = (TextView) findViewById(R.id.txt_show_car_speed);
        speedSeekBar = (SeekBar) findViewById(R.id.car_speed_seekBar);
        speedSeekBarRL = (RelativeLayout) findViewById(R.id.car_speed_seekBar_rl);
        sc_pating = (CheckBox) findViewById(R.id.sc_painting);
        sc_del = (CheckBox) findViewById(R.id.sc_del);
        sc_graph = (CheckBox) findViewById(R.id.sc_graph);
        sc_ruler = (CheckBox) findViewById(R.id.sc_ruler);
        sc_speed = (CheckBox) findViewById(R.id.sc_speed);

        sc_pating.setChecked(true);
        sc_pating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (true == isChecked) {
                    ToastUtils.showToast("sc_pating ischecked");
                    drawCarTrackPath.setWhetherDraw(true);
                } else {
                    ToastUtils.showToast("sc_pating");
                    drawCarTrackPath.setWhetherDraw(false);
                }

                setHorizontalScrollViewMoveToLeft();
            }
        });
        sc_del.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ToastUtils.showToast("sc_del");
                drawCarTrackPath.reset();
                drawCarTrackPath.setWhetherDraw(true);
                sc_pating.setChecked(true);

                setHorizontalScrollViewMoveToLeft();
            }
        });
        sc_graph.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (true == isChecked) {
                    ToastUtils.showToast("sc_graph ischecked");
                } else {
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

                    drawCarTrackPath.setWhetherDraw(false);
                    speedSeekBarRL.setVisibility(View.VISIBLE);

                } else {
                    drawCarTrackPath.setWhetherDraw(true);
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
    }

    /**
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

}
