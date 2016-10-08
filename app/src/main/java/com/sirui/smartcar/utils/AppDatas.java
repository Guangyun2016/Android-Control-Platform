package com.sirui.smartcar.utils;

import android.os.Environment;

/**
 * Created by ygy on 2016/10/8 0008.
 */

public class AppDatas {

    public static final String sdcard = Environment.getExternalStorageDirectory().toString();

    public static final String PATH_APK = "/sdcard/sisui/apk";
    public static final String PATH_AUDIO = "/sdcard/sisui/audio";
    public static final String PATH_VIDEO = "/sdcard/sisui/video";
    public static final String PATH_IMAGE = "/sdcard/sisui/images";
    public static final String PATH_DATA = "/sdcard/sisui/data";
    public static final String PATH_LOG = "/sdcard/sisui/log";
    public static final String PATH_CACHE = "/sdcard/sisui/cache";
//    public static final String PATH_APK = sdcard + "/sisui/apk";
//    public static final String PATH_AUDIO = sdcard + "/sisui/audio";
//    public static final String PATH_VIDEO = sdcard + "/sisui/video";
//    public static final String PATH_IMAGE = sdcard + "/sisui/images";
//    public static final String PATH_DATA = sdcard + "/sisui/data";
//    public static final String PATH_LOG = sdcard + "/sisui/log";
//    public static final String PATH_CACHE = sdcard + "/sisui/cache";

    public static final String dirs[] = {
            PATH_APK,
            PATH_AUDIO,
            PATH_VIDEO,
            PATH_IMAGE,
            PATH_DATA,
            PATH_LOG,
            PATH_CACHE
    };


}
