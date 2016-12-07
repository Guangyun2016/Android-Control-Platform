package com.sirui.iotplatform.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sirui.iotplatform.R;

public class DeviceKnowMore extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_know_more);
    }



    public void goBackView(View view) {
        DeviceKnowMore.this.finish();
    }
}
