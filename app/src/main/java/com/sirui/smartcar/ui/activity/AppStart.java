package com.sirui.smartcar.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.sirui.smartcar.R;
import com.sirui.smartcar.utils.ActivityManager;

import java.io.File;

public class AppStart extends Activity {

    private SharedPreferences sp;// 存储一次显示导航页
    private String is_first;
    private static final int sleepTime = 1000;// 睡眠1s
    private long costTime;

    public static final String sdcard = Environment.getExternalStorageDirectory().toString();

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    CustomToast.showToast(getApplicationContext(), "免登录成功！", 1000);// 线程中不可以用Toast，会报错
                    break;
                case 1:
//                    Intent intent = new Intent(AppStart.this, MainActivity.class);// 跳到主界面
//                    startActivity(intent);
//                    AppStart.this.finish();// 要结束当前界面
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_start_layout);

        System.out.println("------------------------AppStart onCreate()----------------------");
        ActivityManager.getActivityManager().addActivity(AppStart.this);// 把当前activity增加到activity栈队列中

        initFolder();// 生成app文件目录 ，一些缓存或者其他文件都可以放在这里

		/* 这里用到轻量级数据库，每次调试都卸载在源文件.apk，因为数据会写入数据库 */
        // 第一个参数是存储时的名字，第二个参数是文件的打开方式（键值对的方式）
        sp = getSharedPreferences("is_first_login", Context.MODE_PRIVATE);
        // 在数据库中检索is_first，没有就默认返回true，这里is_first=true
        is_first = sp.getString("is_first", "true");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 判断（首次为true，所以执行引导页，然后设置为false，第二次执行跳过引导页）
                if (is_first.equals("true")) {
//            startActivity(new Intent(AppStart.this, AppStartGuide.class));
                    startActivity(new Intent(AppStart.this, MainActivity.class));
                    AppStart.this.finish();// 结束当前Activity
                } else {
                    // 进入登录界面
                    startActivity(new Intent(AppStart.this, MainActivity.class));
//            startActivity(new Intent(AppStart.this, Login.class));
                    AppStart.this.finish();// 结束当前Activity
                }
            }
        }, 1500);

    }// onCreate()


    /**
     * 在SD卡创建目录
     */
    private void initFolder() {

            File dir = new File(Environment.getExternalStorageDirectory().toString() + "/myFloder");
            // 如果不存在就创建
            if (!dir.exists()) {
                dir.mkdirs();
            }

//        for (int i = 0; i < AppDatas.dirs.length; i++) {
//            File dir = new File(sdcard + AppDatas.dirs[i]);
//            // 如果不存在就创建
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//        }
    }

    /**
     * 登录耗时等待
     */
    private void delayTime() {
        if (sleepTime - costTime > 0) {
            try {
                Thread.sleep(sleepTime - costTime);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}