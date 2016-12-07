package com.sirui.iotplatform.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.sirui.iotplatform.R;
import com.sirui.iotplatform.wxapi.WXEntryActivity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * TODO:这里还要加个图片缓存
 */
public class AppAdvertisement extends AppCompatActivity {

    private final static String TAG = "AppAdvertisement";
    private ImageView imageView = null;
    private TextView textView = null;
    private final int SHOW_ADVERTISEMENT_CODE = 1;
    private String url = null;
    private String link = null;

    private TimeCount timeCount;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_ADVERTISEMENT_CODE:
                    Glide.with(getApplicationContext()).load(url).into(imageView);
                    timeCount.start();// 开始计时
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_app_advertisement);

        imageView = (ImageView) findViewById(R.id.advertisement_img);
        textView = (TextView) findViewById(R.id.txt_tick);

        getImages();

        // 参数依次为总时长60s, 和计时的时间间隔1s
        timeCount = new TimeCount(5000, 1000);// 构造CountDownTimer对象

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeCount.onFinish();
                startActivity(new Intent(AppAdvertisement.this, WXEntryActivity.class));
                AppAdvertisement.this.finish();
            }
        });


    }

    public void getImages() {
        AVQuery<AVObject> avQuery = new AVQuery<>("SRAvertisement");
        avQuery.getInBackground("583d2d03a22b9d006c1c322f", new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                // object 就是 id 为 558e20cbe4b060308e3eb36c 的 类 对象实例
                try {
                    JSONArray array = avObject.getJSONArray("content");
                    Log.i(TAG, "JSONArray---->" + array.toString());
                    Log.i(TAG, "JSONArray---->" + array.length());

//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject jb = (JSONObject) array.get(i);
//                        String url = jb.getString("url");
//                        Log.i(TAG, "url---->" + url);
//                        imgesUrls.add(url);
//                    }
                    JSONObject jb = (JSONObject) array.get(0);
                    url = jb.getString("url");
                    link = jb.getString("link");
                    Log.i(TAG, "url---->" + url);
                    Log.i(TAG, "link---->" + link);

                    mHandler.sendEmptyMessage(SHOW_ADVERTISEMENT_CODE);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    /**
     * Created by sr06 on 2016/12/5.
     */
    class TimeCount extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            textView.setText("跳过 " + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            startActivity(new Intent(AppAdvertisement.this, WXEntryActivity.class));
            AppAdvertisement.this.finish();
        }
    }

}


