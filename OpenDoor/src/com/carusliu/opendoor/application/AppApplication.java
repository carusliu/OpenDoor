package com.carusliu.opendoor.application;

import java.util.LinkedList;
import java.util.List;

import com.carusliu.opendoor.tool.DebugLog;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Process;

public class AppApplication extends Application {
	private static Context mContext;
	public static String TAG = "car_cube";

	/**
	 * Singleton pattern
	 */
	private static AppApplication instance;
	private List<Activity> mActivityList = new LinkedList<Activity>();
	public String mDistrict;

	public static AppApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		mContext = this;
		super.onCreate();
		instance = this;
		DebugLog.logw("   ...AppApplication OnCreate    Application onCreate... pid="
				+ Process.myPid());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	public static Context getAppContext() {
		return mContext;
	}

	/*
	 * 娣诲姞Activity鍒板鍣ㄤ腑
	 */
	public synchronized void addActivity(Activity activity) {
		mActivityList.add(activity);
	}

	/**
	 * Activity 姝ｅ紡鍚姩
	 * */
	public void applicationStart() {

	}

	public static String getKey() {
		return "8a28a27a9a523bc557053054bb5a19c3";
	}

	/*
	 * 遍历所有Activity并finish
	 */
	public void applicationExit() {
		while (mActivityList.size() > 0) {
			Activity activity = mActivityList.get(mActivityList.size() - 1);
			mActivityList.remove(mActivityList.size() - 1);
			activity.finish();
		}
	}

	public synchronized void delActivity(Activity activity) {
		int size = mActivityList.size();

		for (int i = size - 1; i >= 0; i--) {
			if (activity == mActivityList.get(i)) {
				mActivityList.remove(i);
				break;
			}
		}
	}

	public Activity getStackTopActivity() {

		if (mActivityList != null && mActivityList.size() > 0) {
			return mActivityList.get(mActivityList.size() - 1);
		}
		return null;
	}

	/**
	 * finish all activities except the activity
	 */
	public void finishExcept(Activity activity) {
		if (mActivityList != null) {
			int cnt = mActivityList.size();
			for (int i = 0; i < cnt; i++) {
				if (activity != mActivityList.get(i)) {
					mActivityList.get(i).finish();
				}
			}
			mActivityList.clear();
			mActivityList.add(activity);
		}
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	/**
	 * Logout
	 */
	public void logoutApp() {
		// TODO Auto-generated method stub
	}
}
