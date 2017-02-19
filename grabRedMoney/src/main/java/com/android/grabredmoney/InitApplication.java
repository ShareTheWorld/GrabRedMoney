package com.android.grabredmoney;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class InitApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("hongtao.fu", "第一次启动应用的时候运行次方法");
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor=sp.edit();
		editor.putBoolean(STATIC.IS_RUNNING_CORE_SERVECE, false);
		editor.commit();
	}
}
