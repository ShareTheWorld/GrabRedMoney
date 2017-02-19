package com.android.grab.activity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.android.grabredmoney.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void open(View view) {
		Log.i("hongtao.fu", "**********");
		
		// Drawable d=getDrawable(R.drawable.grab_logo);
		// d.set
		// dialog.setIcon(Drawable);
	}

	public boolean getByteByInputStream(InputStream inputStream) {

		byte[] buffer = null;
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int len;
			byte b[] = new byte[100];
			while ((len = inputStream.read(b)) != -1) {
				byteArrayOutputStream.write(b, 0, len);
			}
			buffer = byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
