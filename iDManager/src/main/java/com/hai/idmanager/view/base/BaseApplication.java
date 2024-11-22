package com.hai.idmanager.view.base;

import android.app.Application;
import android.content.Context;

import com.hai.idmanager.utils.PreferenceUtil;
import com.hai.idmanager.utils.ScreenUtil;

public class BaseApplication extends Application {
	
	private static BaseApplication instance;

	public static Context getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		PreferenceUtil.init(instance);
		ScreenUtil.INSTANCE.init(instance);
	}
}
