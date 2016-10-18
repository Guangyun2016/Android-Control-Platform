package com.sirui.smartcar.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.sirui.smartcar.R;
import com.sirui.smartcar.widget.MyView;
import com.wx.android.common.util.ToastUtils;

public class SmartCar2 extends AppCompatActivity {

    CheckBox sc_pating, sc_del, sc_graph, sc_ruler, sc_speed, sc_spot, sc_picture_taking, sc_bluetooth;
    MyView myView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_smart_car2);

        initView();

    }

    private void initView() {
        myView = (MyView) findViewById(R.id.sc_draw_path_view);

        sc_pating = (CheckBox) findViewById(R.id.sc_painting);
        sc_del = (CheckBox) findViewById(R.id.sc_del);
        sc_graph = (CheckBox) findViewById(R.id.sc_graph);
        sc_ruler = (CheckBox) findViewById(R.id.sc_ruler);
        sc_speed = (CheckBox) findViewById(R.id.sc_speed);
        sc_spot = (CheckBox) findViewById(R.id.sc_spot);
        sc_picture_taking = (CheckBox) findViewById(R.id.sc_picture_taking);
        sc_bluetooth = (CheckBox) findViewById(R.id.sc_bluetooth);

        sc_pating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (true == isChecked) {
                    ToastUtils.showToast("sc_pating ischecked");
                } else {
                    ToastUtils.showToast("sc_pating");
                }
            }
        });
        sc_del.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ToastUtils.showToast("sc_del");
                myView.reset();
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
            }
        });
        sc_ruler.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (true == isChecked) {
                    ToastUtils.showToast("sc_ruler ischecked");
                } else {
                    ToastUtils.showToast("sc_ruler");
                }
            }
        });
        sc_speed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (true == isChecked) {
                    ToastUtils.showToast("sc_speed ischecked");
                } else {
                    ToastUtils.showToast("sc_speed");
                }
            }
        });
        sc_spot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (true == isChecked) {
                    ToastUtils.showToast("sc_spot ischecked");
                } else {
                    ToastUtils.showToast("sc_spot");
                }
            }
        });
        sc_picture_taking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (true == isChecked) {
                    ToastUtils.showToast("sc_picture_taking ischecked");
                } else {
                    ToastUtils.showToast("sc_picture_taking");
                }
            }
        });
        sc_bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ToastUtils.showToast("sc_bluetooth");
            }
        });

    }

    public void tabBtnOnClicked(View view) {
        SmartCar2.this.finish();
    }

}
