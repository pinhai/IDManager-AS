package com.hai.idmanager.view.base;

import android.app.Application;
import android.content.Context;

import com.hai.securitylock.utils.PreferenceUtil;

public class BaseApplication extends Application {
	
	public static final boolean debug = true;

	private static BaseApplication instance;

	public static Context getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		PreferenceUtil.init(instance);
		com.hai.idmanager.utils.PreferenceUtil.init(instance);
	}
}
