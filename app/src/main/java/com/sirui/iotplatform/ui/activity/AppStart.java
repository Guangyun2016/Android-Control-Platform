package com.sirui.iotplatform.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.sirui.iotplatform.R;
import com.sirui.iotplatform.utils.ActivityManager;
import com.sirui.iotplatform.utils.AppDatas;
import com.wx.android.common.util.SystemUtils;

import java.io.File;

public class AppStart extends Activity {

    private SharedPreferences.Editor editor;
    private SharedPreferences sp;// 存储一次显示导航页
    private String is_first;
    private static final int sleepTime = 1000;// 睡眠1s
    private long costTime;
    private String TAG = "AppStart";

    private final static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    CustomToast.showToast(getApplicationContext(), "免登录成功！", 1000);// 线程中不可以用Toast，会报错
                    break;
                case 1:

                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i(TAG, "Received response permission request.");
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Log.i(TAG, "WRITE_EXTERNAL_STORAGE Permission Granted");
                Log.i(TAG, "----------before initFolder()----------");
                initFolders();
                Log.i(TAG, "----------after initFolder()----------");

            } else {
                // Permission Denied
                Log.i(TAG, "WRITE_EXTERNAL_STORAGE Permission Denied");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.app_start_layout);

        System.out.println("------------------------AppStart onCreate()----------------------");
        ActivityManager.getActivityManager().addActivity(AppStart.this);// 把当前activity增加到activity栈队列中


		/* 这里用到轻量级数据库，每次调试都卸载在源文件.apk，因为数据会写入数据库 */
        // 第一个参数是存储时的名字，第二个参数是文件的打开方式（键值对的方式）
        sp = getSharedPreferences("is_first_login", Context.MODE_PRIVATE);
        // 在数据库中检索is_first，没有就默认返回true，这里is_first=true
        is_first = sp.getString("is_first", "true");
        editor = sp.edit();

        /**
         * 6.0的sdk要请求权限
         */
        Log.i(TAG, "SDK Version--->" + String.valueOf(SystemUtils.getVersionSDK()));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            initFolders();
        }


        // ==================推送服务===============start

        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
        // 参数依次为：当前的 context、频道名称、回调对象的类
        PushService.subscribe(this, "public", ShopJingDong.class);
//        PushService.subscribe(this, "private", Callback1.class);
//        PushService.subscribe(this, "protected", Callback2.class);

        /**
         * 推送，保存 Installation
         */
        AVInstallation.getCurrentInstallation().saveInBackground();
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                }
            }
        });

        // 设置默认打开的 Activity
        PushService.setDefaultPushCallback(this, ShopJingDong.class);

        // ==================推送服务===============end


    }// onCreate()

    public void startNewActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 判断（首次为true，所以执行引导页，然后设置为false，第二次执行跳过引导页）
                if (is_first.equals("true")) {
                    //            startActivity(new Intent(AppStart.this, AppStartGuide.class));
                    //                    startActivity(new Intent(AppStart.this, MainActivity.class));
                    startActivity(new Intent(AppStart.this, AppAdvertisement.class));// 广告倒计时
                    AppStart.this.finish();// 结束当前Activity
                } else {
                    // 进入登录界面
//                    startActivity(new Intent(AppStart.this, MainActivity.class));
                    startActivity(new Intent(AppStart.this, AppAdvertisement.class));// 广告倒计时
                    AppStart.this.finish();// 结束当前Activity
                }
            }
        }, 1000);
    }


    private boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void initFolders() {

        // 判断外部存储是否已挂载
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            // 创建一个文件夹对象，赋值为外部存储器的目录
            File sdcardDir = Environment.getExternalStorageDirectory();
            for (int i = 0; i < AppDatas.dirs.length; i++) {
                File dir = new File(sdcardDir.getPath() + AppDatas.dirs[i]);
                // 如果不存在就创建
                if (!dir.exists()) {
                    dir.mkdirs();
                    Log.i(TAG, "----------create Folder----------" + sdcardDir.getPath() + AppDatas.dirs[i]);
                } else {
                    Log.i(TAG, "----------Folder is exist----------" + sdcardDir.getPath() + AppDatas.dirs[i]);
                }
            }
            startNewActivity();
        } else {
            return;
        }
    }


    /**
     * 在SD卡创建目录
     */
    public void testCreateSDCardDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // 创建一个文件夹对象，赋值为外部存储器的目录
            File sdcardDir = Environment.getExternalStorageDirectory();
            //得到一个路径，内容是sdcard的文件夹路径和名字
            String path = sdcardDir.getPath() + "/aaaDir";
            Log.i(TAG, "----------createSDCardDir----------" + path);
            File path1 = new File(path);
            if (!path1.exists()) {
                //若不存在，创建目录，可以在应用启动的时候创建
                path1.mkdirs();
            }
        } else {
            return;
        }
    }

    /**
     * 登录耗时等待
     */
    private void delayTime() {
        if (sleepTime - costTime > 0) {
            try {
                Thread.sleep(sleepTime - costTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}