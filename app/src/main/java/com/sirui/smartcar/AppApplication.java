package com.sirui.smartcar;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.sirui.smartcar.utils.AppDatas;
import com.wx.android.common.util.ToastUtils;

import java.io.File;

/**
 * Created by ygy 2016-9-28 15:42:10
 */
public class AppApplication extends Application {

//    public static String cacheDir = "/siruiapp";


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

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 在加载图片之前，你必须初始化Fresco类。你只需要调用Fresco.initialize一次即可完成初始化，
         * 在 Application 里面做这件事再适合不过了（如下面的代码），注意多次的调用初始化是无意义的。
         */
        Fresco.initialize(this.getApplicationContext());

        ToastUtils.init(getApplicationContext());


//        if (AppDatas.sdcard != null) {
//            Log.i("AppApplication", AppDatas.sdcard);
//            intiFolders();
//        }

        /**
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */
/*        try {
            if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
                cacheDir = getApplicationContext().getExternalCacheDir().toString();
//                LogUtil.d("sd卡", cacheDir);
            } else {
                cacheDir = getApplicationContext().getCacheDir().toString();
            }
        } catch (Exception e) {
            cacheDir = getApplicationContext().getCacheDir().toString();
        }*/
    }
}
