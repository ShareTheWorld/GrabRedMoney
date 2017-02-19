package com.android.service;

import com.android.grabredmoney.MainActivity;
import com.android.grabredmoney.STATIC;
import com.android.tool.SharedPreferencesGet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * 当用户安装了软件过后激活应用
 * 
 * @author fht
 *
 */
public class GrabBroadcastReceivert extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
			// boolean isActiviteApp =
			// PreferenceManager.getDefaultSharedPreferences(context)
			// .getBoolean(STATIC.IS_ACTIVITE_APP, false);
			long activiteTime = PreferenceManager.getDefaultSharedPreferences(context).getLong(STATIC.ACTIVITE_TIME, 0);
			long currentTime = System.currentTimeMillis();
			long showAdTimeRecently = SharedPreferencesGet.getShowAdTimeRecently(context);
			// 90*24*60*60*1000 三个月的时间
			if ((currentTime - activiteTime) > STATIC.ACTIVITE_TIME_LONG_ONE) {
				// PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(STATIC.IS_ACTIVITE_APP,
				// true)
				// .commit();
				//激活时间和最近显示广告的时间之差在规定的时间以内
				if ((currentTime - showAdTimeRecently) < STATIC.ACTIVITE_TIME_LONG_IN) {
					PreferenceManager.getDefaultSharedPreferences(context).edit()
							.putLong(STATIC.ACTIVITE_TIME, System.currentTimeMillis()).commit();
					Toast.makeText(context, "抢红包应用已经\"激活成功\"", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

}
