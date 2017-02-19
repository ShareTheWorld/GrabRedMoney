package com.android.grab.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private final static String SQLITE_NAME = "grab.db";
	private static SQLiteOpenHelper sqlite;

	public static SQLiteOpenHelper getInstance(Context context) {
		if (sqlite == null) {
			synchronized (DBHelper.class) {
				if (sqlite == null) {
					sqlite = new DBHelper(context);
				}
			}
		}
		return sqlite;
	}

	public DBHelper(Context context) {
		super(context, SQLITE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建一张红包记录表
		String create_record_table = "CREATE TABLE record(" + "_id integer primary key autoincrement,"// 自增id
				+ "name varchar(50) , "// 抢的谁的红包
				+ "money float, "// 抢了多少金额
				+ "wish varchar(200), "// 祝福语是什么 也就是留言
				+ "time interger, "// 抢到红包的时间
				+ "start_time interger, "// 抢到红包的时间
				+ "reply varchar(200), "// 抢到红包回复的是什么
				+ "read integer, "// 是否已读
				+ "show integer "// 是否显示
				+ ");";
		db.execSQL(create_record_table);
		String create_reply_table = "CREATE TABLE reply(" + "_id integer primary key autoincrement,"
				+ "reply varchar(200) ," + "type integer " + ");";
		db.execSQL(create_reply_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
