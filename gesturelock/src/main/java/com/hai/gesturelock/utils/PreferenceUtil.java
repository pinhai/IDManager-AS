package com.hai.gesturelock.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.hai.gesturelock.constant.Constants;

public class PreferenceUtil {

	private static final String PREFERENCES_NAME = "name_preferences_gesture_lock";

	private static Editor editor;
	private static SharedPreferences sharedPreferences;

	private PreferenceUtil(){
	}

	public static void init(Context context){
		//获取sharedPreferences对象
		sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
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

	/**
	 * 保存手势密码
	 * @param psw
	 */
	public static void putGesturePsw(String psw){
		editor.putString(Constants.SHARED_GESTURE_LOCK_PSW_KEY, psw);
		editor.commit();//提交修改
	}

	public static String getGesturePsw(){
		return sharedPreferences.getString(Constants.SHARED_GESTURE_LOCK_PSW_KEY, "");
	}

	public static void removeGesturePsw(){
		editor.remove(Constants.SHARED_GESTURE_LOCK_PSW_KEY);
		editor.commit();//提交修改
	}

	public static boolean hasGesturePsw(){
		return !TextUtils.isEmpty(getGesturePsw());
	}

	/**
	 * 打开指纹解锁
	 */
	public static void openFingerprintLock(){
		putBoolean(Constants.SHARED_FINGERPRINT_LOCK_KEY, true);
	}

	/**
	 * 关闭指纹解锁
	 */
	public static void closeFingerprintLock(){
		putBoolean(Constants.SHARED_FINGERPRINT_LOCK_KEY, false);
	}

	/**
	 * 是否打开了指纹解锁
	 */
	public static boolean isFingerprintLockOpened(){
		return getBoolean(Constants.SHARED_FINGERPRINT_LOCK_KEY, false);
	}

}
