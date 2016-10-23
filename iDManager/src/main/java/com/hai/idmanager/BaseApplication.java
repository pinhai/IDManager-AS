package com.hai.idmanager;

import android.app.Application;
import android.content.Context;

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
	}
}
