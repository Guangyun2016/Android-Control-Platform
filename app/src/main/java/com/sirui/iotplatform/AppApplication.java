package com.sirui.iotplatform;

import android.app.Application;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.SaveCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.wx.android.common.util.ToastUtils;

/**
 * Created by ygy 2016-9-28 15:42:10
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 在加载图片之前，你必须初始化Fresco类。你只需要调用Fresco.initialize一次即可完成初始化，
         * 在 Application 里面做这件事再适合不过了（如下面的代码），注意多次的调用初始化是无意义的。
         */
        Fresco.initialize(this.getApplicationContext());

        /**
         * Utils工具类
         */
        ToastUtils.init(getApplicationContext());

        /**
         * LeanCloud初始化参数依次为 this, AppId, AppKey
         */
        AVOSCloud.initialize(this, "5YpgpCvuf3HCaxR6v5hTtSWI-gzGzoHsz", "m6AQs84XgRzNRVxHry6dJWDH");




    }
}
