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
public class PreferenceUtil {

	private static SharedPreferences.Editor editor;
	private static SharedPreferences sharedPreferences;

	private PreferenceUtil(){
	}

	public static void init(Context context){
		//获取sharedPreferences对象
		sharedPreferences = context.getSharedPreferences(PrefConstants.NAME, Context.MODE_PRIVATE);
		//获取editor对象
		editor = sharedPreferences.edit();//获取编辑器
	}

	public static void putString(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(String key, String defaultVal) {
		return sharedPreferences.getString(key, defaultVal);
	}

	public static void putBoolean(String key, boolean value){
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean getBoolean(String key, boolean defValue){
		return sharedPreferences.getBoolean(key, defValue);
	}

	public static void remove(String info) {
		editor.remove(info);
		editor.commit();
	}

}
