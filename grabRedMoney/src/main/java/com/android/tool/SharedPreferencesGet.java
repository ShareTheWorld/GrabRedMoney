package com.android.tool;

import com.android.grabredmoney.STATIC;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharedPreferencesGet {
	public static boolean getActiviteState(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		long activiteTime = sp.getLong(STATIC.ACTIVITE_TIME, 0);
		long currentTime = System.currentTimeMillis();
		return (currentTime - activiteTime) < STATIC.ACTIVITE_TIME_LONG_ONE;
	}

	public static void setShowAdTimeRecently(Context context, long showAdTimeRecently) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putLong(STATIC.SHOW_AD_TIME_RECENTLY, showAdTimeRecently);
		editor.commit();
	}

	public static long getShowAdTimeRecently(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		long showAdTimeRecently = sp.getLong(STATIC.SHOW_AD_TIME_RECENTLY, 0);
		return showAdTimeRecently;
	}

}
