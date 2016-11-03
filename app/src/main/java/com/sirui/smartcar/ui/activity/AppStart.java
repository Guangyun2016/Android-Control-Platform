package com.sirui.smartcar.ui.activity;

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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.sirui.smartcar.R;
import com.sirui.smartcar.utils.ActivityManager;
import com.sirui.smartcar.utils.AppDatas;
import com.wx.android.common.util.ToastUtils;

import java.io.File;

public class AppStart extends Activity {

    private SharedPreferences sp;// 存储一次显示导航页
    private String is_first;
    private static final int sleepTime = 1000;// 睡眠1s
    private long costTime;
    private String TAG = "AppStart";

    private final static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                ToastUtils.showToast("Permission Granted");

                Log.i(TAG, "----------before initFolder()----------");
                initFolder();// 生成app文件目录 ，一些缓存或者其他文件都可以放在这里
                createSDCardDir();
                Log.i(TAG, "----------after initFolder()----------");

            } else {
                // Permission Denied
                ToastUtils.showToast("Permission Denied");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_start_layout);

        /**
         * 动态申请权限
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }







        System.out.println("------------------------AppStart onCreate()----------------------");
        ActivityManager.getActivityManager().addActivity(AppStart.this);// 把当前activity增加到activity栈队列中



		/* 这里用到轻量级数据库，每次调试都卸载在源文件.apk，因为数据会写入数据库 */
        // 第一个参数是存储时的名字，第二个参数是文件的打开方式（键值对的方式）
        sp = getSharedPreferences("is_first_login", Context.MODE_PRIVATE);
        // 在数据库中检索is_first，没有就默认返回true，这里is_first=true
        is_first = sp.getString("is_first", "true");



/*        ToastUtils.showToast("LeanCloud测试开始");
        // 测试 LeanCloud SDK 是否正常工作的代码
        AVObject testObject = new AVObject("TestObject");
        testObject.put("words","Hello World!");
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    Log.d("saved","测试 LeanCloud SDK success!");
                    ToastUtils.showToast("测试 LeanCloud SDK success!");
                } else {
                    ToastUtils.showToast("测试nono");
                }
            }
        });*/


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


    private boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void intiFolders() {
        for (int i = 0; i < AppDatas.dirs.length; i++) {
            File dir = new File(AppDatas.dirs[i]);
            // 如果不存在就创建
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }


    /**
     * 在SD卡创建目录
     */
    private void initFolder() {




        Log.i(TAG, "----------before new----------");

        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/aaaFloder");
        Log.i(TAG, "----------after new----------" + dir.toString());

        // 如果不存在就创建
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void createSDCardDir() {
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}