package com.sirui.iotplatform.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;

/**
 * @descripiton 通用工具类，下面的加static的原因是静态方法可以直接用类名.方法调用
 */
public class CommonUtils {


	/**
	 * 检测网络是否可用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}

		return false;
	}

	/**
	 * 检测Sdcard是否存在
	 *
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * 获取当前应用程序的版本号
	 */
	public static String getVersion(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo packinfo = pm.getPackageInfo(context.getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "版本号错误";
		}
	}

	/**
	 * @descripiton 获取本机Mac 地址方法：
	 * @return String mac
	 */
	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String mac = info.getMacAddress();
		return mac;
	}








}
