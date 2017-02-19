package com.android.tool;

import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class PhoneApplicationManager {
	public void startApplication(Context context, String packageName) {

	}

	/**
	 * 得到当前正在运行的Activity的包名和类名
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentApplicationPackageAndClassName(Context context) {
		String currentApplicationPackageName = null;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Service.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = activityManager.getRunningTasks(1);
		if (list.size() >= 1) {
			RunningTaskInfo runningTaskInfo = list.get(0);
			currentApplicationPackageName = runningTaskInfo.topActivity
					.getClassName();
		}
//		Log.i("hongtao.fu", "当前运行的程序的包名为: " + currentApplicationPackageName);

		return currentApplicationPackageName;
	}

	public void getCurrentApplicationActivity() {

	}
}
