package com.android.grab.db.opt;

import com.android.grab.entity.RecordEntity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DBRecordInsertHandler {
	public static final int SELECT_ALL_DATA = 0;
	public static final int INSERT = 1;
	public static final int UPDATE = 2;
	public static final int DELEATE_ID = 3;
	public static final int DELEATE_ALL = 4;
	private Context context;
	private static DBRecordInsertHandler t;
	private DBRecordInsertHandler(Context context) {
		this.context = context;
	}
	public static DBRecordInsertHandler getInstance(Context context){
		if(t==null){
			synchronized (DBRecordInsertHandler.class) {
				if(t==null){
					t=new DBRecordInsertHandler(context);
				}
			}
		}
		return t;
	}
	//红包记录的插入操作
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			final Object o = msg.obj;
			new Thread() {
				public void run() {
					if (context == null)
						return;
					if(o instanceof RecordEntity){
						RecordEntity recordEntity=(RecordEntity)o;
						RedMoneyRecord r=new RedMoneyRecord(context);
						r.insert(recordEntity);
					}
				};
			}.start();
		};
	};
}
