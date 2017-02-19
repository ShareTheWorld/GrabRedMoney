package com.android.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.android.grab.activity.PayActivity;
import com.android.grabredmoney.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import c.b.BP;
import c.b.PListener;

public class PayUtil {
	private Activity mContext;
	// 此为测试Appid,请将Appid改成你自己的Bmob AppId
	String APPID = "b2cf2c8ed07cc792fdc82cc7bd8c8e37";
	// String APPID = "11e93b5e5fad3225bebec3fde45839e7";
	// 此为支付插件的官方最新版本号,请在更新时留意更新说明
	int PLUGINVERSION = 7;

	public void init(Activity context) {
		mContext = context;
		BP.init(mContext, APPID);
	}

	public void pay(String title, String description, double money, boolean aliOrWechat) {
		// // 第4个参数为true时调用支付宝支付，为false时调用微信支付
		// showDialog("加载中...");
		BP.pay(title, description, money, aliOrWechat, new BmobPListener());
	}

	public boolean installPayPlug() {
//		int pluginVersion = BP.getPluginVersion();
//		if (pluginVersion < PLUGINVERSION) {// 为0说明未安装支付插件, 否则就是支付插件的版本低于官方最新版
//			installBmobPayPlugin("bp.db");
//			return true;
//		}
		return false;
	}

	void installBmobPayPlugin(String fileName) {
		try {
//			InputStream is = mContext.getAssets().open(fileName);
//			File file = new File(mContext.getExternalFilesDir(null) + File.separator + fileName + ".apk");
//			if (file.exists())
//				file.delete();
//			file.createNewFile();
//			FileOutputStream fos = new FileOutputStream(file);
//			byte[] temp = new byte[1024];
//			int i = 0;
//			while ((i = is.read(temp)) > 0) {
//				fos.write(temp, 0, i);
//			}
//			fos.close();
//			is.close();
//
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			intent.setDataAndType(Uri.parse("file://" + file), "application/vnd.android.package-archive");
//			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class BmobPListener implements PListener {
		@Override
		public void orderId(String s) {
			Log.i("hongtao.fu", "// 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询");
			PayActivity.dismissDialog();
		}

		@Override
		public void succeed() {
			popupToast("感谢你的支持，我们将更加努力!");
			mContext.finish();

		}

		@Override
		public void fail(int code, String s) {
			// 当code为-2,意味着用户中断了操作
			// code为-3意味着没有安装BmobPlugin插件
			if (code == -3) {
				popupToast("监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付");

				installBmobPayPlugin("bp.db");
			} else {
				popupToast("支付中断!");
			}
			PayActivity.dismissDialog();
		}

		@Override
		public void unknow() {
			popupToast("支付结果未知,请稍后手动查询");
			PayActivity.dismissDialog();
		}
	};
	public void popupToast(String msg){
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.toast_layout, null);// 得到加载view
		((TextView)v.findViewById(R.id.toast_text)).setText(msg);
		Toast toast = new Toast(mContext);
		toast.setView(v);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}

}
