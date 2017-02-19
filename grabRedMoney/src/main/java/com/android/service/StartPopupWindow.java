package com.android.service;

import com.android.grab.activity.PayActivity;
import com.android.grab.activity.PopupWindow;
import com.android.grabredmoney.MainActivity;
import com.android.grabredmoney.STATIC;
import com.android.tool.SharedPreferencesGet;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class StartPopupWindow extends Service {
	private PopupWindow popupWindow;
	public static final int WE_CHAT_GRAB_RED_MONEY_START_VALUE = 725019;
	public static final String WE_CHAT_GRAB_RED_MONEY_START_KEY = "who_start_activity";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		popupWindow = new PopupWindow();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//delete by hongtao.fu for add pay Activity begin
//		long activiteTime = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
//				.getLong(STATIC.ACTIVITE_TIME, 0);
//		long currentTime = System.currentTimeMillis();
//		Log.i("hongtao.fu", "startService");
//		boolean isActivite = SharedPreferencesGet.getActiviteState(getApplicationContext());
//		if (!isActivite) {// 没有激活就弹出广告
//			Intent activiteAdActivity = new Intent(getApplicationContext(), MainActivity.class);
//			activiteAdActivity.setPackage("com.android.grabredmoney");
//			activiteAdActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
//			edit.putInt(WE_CHAT_GRAB_RED_MONEY_START_KEY, WE_CHAT_GRAB_RED_MONEY_START_VALUE);
//			edit.commit();
//			this.startActivity(activiteAdActivity);
//		}
		//delete by hongtao.fu for add pay Activity end
		
//		Intent payActiviy=new Intent(getApplicationContext(),PayActivity.class);
//		payActiviy.setPackage("com.android.grabredmoney");
//		payActiviy.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		payActiviy.putExtra("record", intent.getStringExtra("record"));
//		getApplicationContext().startActivity(payActiviy);

		return super.onStartCommand(intent, flags, startId);
	}

}
