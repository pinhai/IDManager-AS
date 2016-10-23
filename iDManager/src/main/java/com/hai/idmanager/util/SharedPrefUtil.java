package com.hai.idmanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.hai.idmanager.BaseApplication;
import com.hai.idmanager.constant.PrefConstants;

/**
 * android SharedPreference 轻量级缓存
 * 
 */
public class SharedPrefUtil {
	private static SharedPrefUtil instance;
	private Context context;
	private SharedPreferences mPreferences;

	public static SharedPrefUtil getInstance() {
		if (instance == null) {
			instance = new SharedPrefUtil();
		}
		return instance;
	}

	private SharedPrefUtil() {
		context = BaseApplication.getInstance();
		if (context != null) {
			mPreferences = context.getSharedPreferences(
					PrefConstants.NAME, Context.MODE_PRIVATE);
		}
	}

	public void putString(String key, String value) {
		Editor editor = mPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getString(String key, String defaultVal) {
		return mPreferences.getString(key, defaultVal);
	}

	public void putBoolean(String key, boolean value){
		Editor editor = mPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getBoolean(String key, boolean defValue){
		return mPreferences.getBoolean(key, defValue);
	}

	public void remove(String info) {
		SharedPreferences preferences = context.getSharedPreferences(
				PrefConstants.NAME, Context.MODE_PRIVATE);
		Editor ed = preferences.edit();
		ed.remove(info);
		ed.commit();
	}

}
