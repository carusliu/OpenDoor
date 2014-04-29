/**
 * SharedPreferencesHelper.java
 * 2012-4-11
 */
package com.carusliu.opendoor.tool;


import com.carusliu.opendoor.application.AppApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesLastingHelper {

	public static final String APP_SHARD = "permanentcarserver";
	private static SharedPreferences mSharedPreferences;
	private static Editor mEditor;

	public static SharedPreferences getSharedPreferences() {

		if (mSharedPreferences == null) {
			mSharedPreferences = AppApplication.getAppContext().getSharedPreferences(APP_SHARD, Context.MODE_PRIVATE);
		}
		return mSharedPreferences;
	}

	public static Editor getEditor() {

		if (mEditor == null) {
			mEditor = getSharedPreferences().edit();
		}
		return mEditor;
	}

}
