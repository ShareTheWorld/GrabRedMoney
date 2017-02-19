package com.android.grab.activity;

import com.android.grabredmoney.R;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class PopupWindow {
	private static Context context;
	private static WindowManager.LayoutParams params;
	private static WindowManager wm;
	private static LinearLayout float_layout;
	private static Button releaseLock;

	public void popuWindow(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams localLaoutParams = new WindowManager.LayoutParams();
		params = localLaoutParams;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;;
		params.format = PixelFormat.RGBA_8888;
		params.flags = 1280;
		params.width = 500;// WindowManager.LayoutParams.MATCH_PARENT;
		params.height = 500;// WindowManager.LayoutParams.MATCH_PARENT;
		params.gravity = Gravity.CENTER;
		float_layout = ((LinearLayout) LayoutInflater.from(context).inflate(R.layout.popup_activite_ad, null));// main=2130903040
		float_layout.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
		releaseLock = (Button) float_layout.findViewById(R.id.releaseLock);
		releaseLock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				releaseScreen();
			}
		});
		Log.i("hongtao.fu","*********************************");
	}

	public void releaseScreen() {
		wm.removeView(float_layout);
	}
}
