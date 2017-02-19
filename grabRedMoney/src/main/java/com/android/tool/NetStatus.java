package com.android.tool;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * 通过连接百度判断网络是否可用
 * 
 * @author user
 *
 */
public class NetStatus {
	private static final String key = "<!--STATUS OK-->";

	public static int getNetStatus() {
		try {
			URL url = new URL("http://www.baidu.com");
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setConnectTimeout(2 * 1000);
			if (!(httpURLConnection.getResponseCode() == 200)) {
				throw new Exception("网络异常");
			}
			InputStream inputStream = httpURLConnection.getInputStream();
			boolean isOk = getByteByInputStream(inputStream);
			Log.i("hongtao.fu", "网络 " + isOk);
			return isOk ? 1 : -1;
		} catch (Exception e) {
			Log.i("hongtao.fu", "网络 " + false);
			return -1;
		}
	}

	public static boolean getByteByInputStream(InputStream inputStream) {

		try {
			int len;
			byte b[] = new byte[100];
			while ((len = inputStream.read(b)) != -1) {
				String str = new String(b, 0, len);
				boolean isOk = str.contains(key);
				return isOk;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
