package com.sirui.smartcar.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * @description 监测网络状态
 * 
 */
public class NetWorkHelper {
	
	//定义常量来标识几种网络类型
	/** 没有网络 */
	public static final int NETWORKTYPE_INVALID = 0;
	/** wap网络 */
	public static final int NETWORKTYPE_WAP = 1;
	/** 2G网络 */
	public static final int NETWORKTYPE_2G = 2;
	/** 3G和3G以上网络，或统称为快速网络 */
	public static final int NETWORKTYPE_3G = 3;
	/** wifi网络 */
	public static final int NETWORKTYPE_WIFI = 4;

	/**
	 * @description 检测网络是否连接
	 * @param context 上下文, this
	 * @return true表示连接, false表示没连接
	 */
	public static boolean isNetworkAvailable(Context context) {
		// //网络管理类，可以判断是否能上网，以及网络类型
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo info[] = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 *	网络名字 
	 * @param context
	 * @return
	 */
	public static String getNetworkName(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String SSID = null;
		String wifi = null;// 08-01 15:45:38.660:
							// V/**************************wifiInfo(30101):
							// SSID: FAST_13D176, BSSID: d8:15:0d:13:d1:76, MAC:
							// C4:6A:B7:2D:D9:92, Supplicant state: COMPLETED,
							// RSSI: -56, Link speed: 65, Net ID: 12, Explicit
							// connect: true

		if (wifiInfo != null) {
			SSID = wifiInfo.getSSID();
			wifi = wifiInfo.toString();
		}
		Log.i("NetWorkHelper", "########################wifiInfo----->" + wifiInfo.toString());
		Log.i("NetWorkHelper", "########################SSID----->" + wifiInfo.getSSID());
		return SSID;
	}

	public void getBetworkName(Context context) {

	}
	
	/**
	 * 判断是否是FastMobileNetWork，将3G或者3G以上的网络称为快速网络
	 * @param context
	 * @return
	 */
	private static boolean isFastMobileNetwork(Context context) {  
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);  
		switch (telephonyManager.getNetworkType()) {  
		       case TelephonyManager.NETWORK_TYPE_1xRTT:  
		           return false; // ~ 50-100 kbps  
		       case TelephonyManager.NETWORK_TYPE_CDMA:  
		           return false; // ~ 14-64 kbps  
		       case TelephonyManager.NETWORK_TYPE_EDGE:  
		           return false; // ~ 50-100 kbps  
		       case TelephonyManager.NETWORK_TYPE_EVDO_0:  
		           return true; // ~ 400-1000 kbps  
		       case TelephonyManager.NETWORK_TYPE_EVDO_A:  
		           return true; // ~ 600-1400 kbps  
		       case TelephonyManager.NETWORK_TYPE_GPRS:  
		           return false; // ~ 100 kbps  
		       case TelephonyManager.NETWORK_TYPE_HSDPA:  
		           return true; // ~ 2-14 Mbps  
		       case TelephonyManager.NETWORK_TYPE_HSPA:  
		           return true; // ~ 700-1700 kbps  
		       case TelephonyManager.NETWORK_TYPE_HSUPA:  
		           return true; // ~ 1-23 Mbps  
		       case TelephonyManager.NETWORK_TYPE_UMTS:  
		           return true; // ~ 400-7000 kbps  
		       case TelephonyManager.NETWORK_TYPE_EHRPD:  
		           return true; // ~ 1-2 Mbps  
		       case TelephonyManager.NETWORK_TYPE_EVDO_B:  
		           return true; // ~ 5 Mbps  
		       case TelephonyManager.NETWORK_TYPE_HSPAP:  
		           return true; // ~ 10-20 Mbps  
		       case TelephonyManager.NETWORK_TYPE_IDEN:  
		           return false; // ~25 kbps  
		       case TelephonyManager.NETWORK_TYPE_LTE:  
		           return true; // ~ 10+ Mbps  
		       case TelephonyManager.NETWORK_TYPE_UNKNOWN:  
		           return false;  
		       default:  
		           return false;  
		    }  
		}  
	
	/** 
     * 获取网络状态，wifi,wap,2g,3g. 
     * @param context 上下文 
     * @return int 网络状态 
     */  
  
    public static int getNetWorkType(Context context) {  
  
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();  
        int mNetWorkType = 0;// 网络状态局部变量
  
        if (networkInfo != null && networkInfo.isConnected()) {  
            String type = networkInfo.getTypeName();  
  
            if (type.equalsIgnoreCase("WIFI")) {  
                mNetWorkType = NETWORKTYPE_WIFI;  
            } else if (type.equalsIgnoreCase("MOBILE")) {  
                String proxyHost = android.net.Proxy.getDefaultHost();  
                mNetWorkType = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORKTYPE_3G : NETWORKTYPE_2G)  : NETWORKTYPE_WAP;  
            }  
        } else {  
            mNetWorkType = NETWORKTYPE_INVALID;  
        }  
  
        return mNetWorkType;  
    }   
    
    /**
     * 得到String网络类型
     * @param index
     * @return
     */
    public static String getNetWorkTypeString(int index) {
    	switch (index) {
		case NETWORKTYPE_INVALID:
			return "没有网络";
			
		case NETWORKTYPE_WAP:
			return "wap网络";
			
		case NETWORKTYPE_2G:
			return "2G网络";
			
		case NETWORKTYPE_3G:
			return "3G和3G以上网络，或统称为快速网络";
			
		case NETWORKTYPE_WIFI:
			return "WIFI网络";

		default:
			return null;
		}
    }
	
}
