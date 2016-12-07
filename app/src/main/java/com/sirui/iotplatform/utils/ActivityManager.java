package com.sirui.iotplatform.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.sirui.iotplatform.ui.activity.MainActivity;

import java.util.Stack;

/**
 * @ClassName: AppManager
 * @Description: 管理应用的Activity生命周期
 */
public class ActivityManager {

	private static Stack<Activity> activityStack;// 存放Activity栈
	private static Stack<MainActivity> mainActivityStack;// 存放APP的主界面mainactivity到栈

	// 定义本类的一个对象
	private static ActivityManager instance;

	//构造函数
	public ActivityManager() {
	}

	/**
	 * 实例化本类instance对象(new一个对象)
	 */
	public static ActivityManager getActivityManager(){
		if (instance == null) {
			instance = new ActivityManager();
		}
		return instance;
	}

	/**
	 * 增加MainActivity到栈中
	 */
	public void addMainActivity(MainActivity mainActivity) {
		if (mainActivityStack == null) {
			mainActivityStack = new Stack<MainActivity>();
		}
		mainActivityStack.add(mainActivity);
	}

	/**
	 * 遍历查找Activity
	 */
	public Activity getMainActivity(Class<?> cls) {
		for (Activity activity : mainActivityStack) {
			if (activity.getClass().equals(cls)) {
				return activity;
			}
		}
		return null;
	}

	/**
	 * 增加Activity到栈中
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 遍历查找包含某个Class的Activity
	 */
	public Activity getActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				return activity;
			}
		}
		return null;
	}

	/**
	 * 栈顶的activity
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束栈顶的activity
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);// 移除
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束包含某个Class类的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有的mainActivity
	 */
	public void finishAllMainActivity() {
		for (int i = 0, size = mainActivityStack.size(); i < size; i++) {
			if (null != mainActivityStack.get(i)) {
				mainActivityStack.get(i).finish();
			}
		}
		mainActivityStack.clear();

	}

	/**
	 * 结束所有的Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 结束homeActivity前面的Activity，即定位到homeActivity
	 */
	public void finishToHome() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (MainActivity.class != activityStack.get(i).getClass()) {
				activityStack.get(i).finish();
			}
		}
	}

	/**
	 * 比如在登录界面点击忘记密码时，到修改密码成功和切换账号界面后在跳转到登录界面，此时用finishToCurrent(Activity
	 * activity)结束栈中上面的几个Activity回到登录Activity
	 */
//	public void finishToLogin() {
//		for (int i = 0, size = activityStack.size(); i < size; i++) {
//			if (WXEntryActivity.class != activityStack.get(i).getClass()) {
//				activityStack.get(i).finish();
//			}
//		}
//	}

	/**
	 * @return boolean isFinished = true;
	 */
	public boolean exitAllActivity() {
		boolean isFinished = true;
		for (Activity act : activityStack) {
			if (act != null) {
				act.finish();
			}
		}
		for (Activity act : activityStack) {
			if (!act.isFinishing()) {
				isFinished = false;
			}
		}
		return isFinished;
	}

	/**
	 * 结束APP所有Activity的生命周期，退出应用
	 */
	public void AppExit(Context context) {
		try {
			// AppContext.getInstance().stopTackingService();//没用
			// finishAllMainActivity();
			finishAllActivity();
			// ActivityManager activityMgr = (ActivityManager)
			// context.getSystemService(Context.ACTIVITY_SERVICE); //加上这个会产生异常
			if (exitAllActivity()) {
				// activityMgr.killBackgroundProcesses(context.getPackageName());
				System.exit(0);
			}

		} catch (Exception e) {

			// 堆栈跟踪中的元素，它由 Throwable.getStackTrace() 返回，创建对应于堆栈跟踪的 throwable 的点
			StackTraceElement stacks[] = e.getStackTrace();
			StringBuffer sb = new StringBuffer();
			for (StackTraceElement stack : stacks) {
				sb.append(stack.toString() + "\n");
			}
			Log.e("AppManager", sb.toString());

		}
	}
}