/**
 * 
 */
package com.hai.idmanager.utils;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class AppUtil {
    
	/**
     * 获取屏幕分辨率
     * @param context
     * @return
     */
    public static int[] getScreenDispaly(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = windowManager.getDefaultDisplay().getWidth();// 手机屏幕的宽度
		int height = windowManager.getDefaultDisplay().getHeight();// 手机屏幕的高度
		int result[] = { width, height };
		return result;
	}

	/**
	 * 开关输入法
	 */
	public static void toggleInputMethod(Context context) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 隐藏软键盘
	 */
	public static void hideSoftInput(View view) {
		if(view == null) return;
		InputMethodManager mIMM = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		mIMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 显示软键盘,调用该方法后,会在onPause时自动隐藏软键盘
	 */
	public static void showSoftInput(Context context, final View view) {
		if (view == null) return;
		final InputMethodManager mIMM = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		view.requestFocus();
		view.postDelayed(new Runnable() {
			@Override
			public void run() {
				mIMM.showSoftInput(view, InputMethodManager.SHOW_FORCED);
			}
		}, 500);
	}
    
}
