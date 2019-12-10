package com.hai.idmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.hai.idmanager.view.base.BaseApplication;
import com.hai.idmanager.constant.PrefConstants;

/**
 * android SharedPreference 轻量级缓存
 * 
 */
public class SharedPrefUtil {
	private static SharedPrefUtil instance;
	private Context context;
	private static SharedPreferences mPreferences;

	private static void newInstance() {
		if (instance == null) {
			instance = new SharedPrefUtil();
		}
	}

	private SharedPrefUtil() {
		context = BaseApplication.getInstance();
		if (context != null) {
			mPreferences = context.getSharedPreferences(
					PrefConstants.NAME, Context.MODE_PRIVATE);
		}
	}

	public static void putString(String key, String value) {
		newInstance();
		Editor editor = mPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(String key, String defaultVal) {
		newInstance();
		return mPreferences.getString(key, defaultVal);
	}

	public static void putBoolean(String key, boolean value){
		newInstance();
		Editor editor = mPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean getBoolean(String key, boolean defValue){
		newInstance();
		return mPreferences.getBoolean(key, defValue);
	}

	public static void remove(String info) {
		newInstance();
		Editor ed = mPreferences.edit();
		ed.remove(info);
		ed.commit();
	}

}
