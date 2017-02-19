package com.android.grab.db.opt;

import java.util.ArrayList;
import java.util.List;

import com.android.grab.db.DBHelper;
import com.android.grab.entity.RecordEntity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RedMoneyRecord {
	private SQLiteDatabase db;
	private final static String TABLE_NAME = "record";
	private Context context;

	public RedMoneyRecord(Context context) {
		this.context = context;
	}

	public List<RecordEntity> selectAllData() {
		db = DBHelper.getInstance(context).getReadableDatabase();
		try {
			String sql = "select * from " + TABLE_NAME + " ORDER BY time DESC ; ";
			Cursor cursor = db.rawQuery(sql, null);
			List<RecordEntity> list = new ArrayList<RecordEntity>();
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("_id"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				float money = cursor.getFloat(cursor.getColumnIndex("money"));
				String wish = cursor.getString(cursor.getColumnIndex("wish"));
				long time = cursor.getLong(cursor.getColumnIndex("time"));
				String reply = cursor.getString(cursor.getColumnIndex("reply"));
				int read = cursor.getInt(cursor.getColumnIndex("read"));
				int show = cursor.getInt(cursor.getColumnIndex("show"));
				RecordEntity record = new RecordEntity(id, name, money, wish, time, reply, read, show);
				if (show == RecordEntity.SHOW) {
					list.add(record);
				}
			}
			return list;
		} catch (Exception e) {
			Log.e("hongtao.fu", "数据查询出异常");
			e.printStackTrace();
		} finally {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
		}
		return null;
	}

	public boolean insert(RecordEntity record) {
		db = DBHelper.getInstance(context).getReadableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("name", record.getName());
			values.put("money", record.getMoney());
			values.put("wish", record.getWish());
			values.put("time", record.getTime());
			values.put("reply", record.getReply());
			db.insert(TABLE_NAME, null, values);
			Log.i("db", "插入数据:" + record);
			return true;
		} catch (Exception e) {
			Log.e("hongtao.fu", "数据插入出异常");
		} finally {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
		}
		return false;
	}

	/**
	 * 更新的是是否已读
	 * 
	 * @param record
	 * @return
	 */
	public synchronized boolean setReadStatus(String _id, int readStatus) {
		db = DBHelper.getInstance(context).getReadableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("read", readStatus);
			Log.i("hongtao.fu", "_id=" + _id);
			db.update(TABLE_NAME, values, " _id = ? ", new String[] { _id });
			return true;
		} catch (Exception e) {
			Log.e("hongtao.fu", "设置阅读状态异常");
		} finally {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
		}
		return false;
	}

	/**
	 * 设置显示状态
	 * 
	 * @param _id
	 * @param showStatus
	 * @return
	 */
	public synchronized boolean setShowStatus(String _id, int showStatus) {
		db = DBHelper.getInstance(context).getReadableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("show", showStatus);
			db.update(TABLE_NAME, values, " _id = ? ", new String[] { _id });
			return true;
		} catch (Exception e) {
			Log.e("hongtao.fu", "设置显示状态异常");
		} finally {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
		}
		return false;
	}

	/**
	 * 设置为不显示
	 * 
	 * @param _id
	 * @param showStatus
	 * @return
	 */
	public synchronized boolean setShowStatusNotShow(List<Integer> _ids) {
		Log.i("hongtao.fu", _ids.toString());
		db = DBHelper.getInstance(context).getReadableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("show", RecordEntity.NOT_SHOW);
			for (int id : _ids) {
				db.update(TABLE_NAME, values, " _id = ? ", new String[] { String.valueOf(id) });
			}
			return true;
		} catch (Exception e) {
			Log.e("hongtao.fu", "设置显示状态异常");
		} finally {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
		}
		return false;
	}
	/**
	 * 得到当前时间到制定之间的一个总收益 
	 * @param time
	 * @return
	 */
	public synchronized float getMoneyInTime(long time) {
		db = DBHelper.getInstance(context).getReadableDatabase();
		Cursor cursor=null;
		float m = 0;
		try {
			String sql = "select * from " + TABLE_NAME + "; ";
			 cursor= db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				float money = cursor.getFloat(cursor.getColumnIndex("money"));
				long t = cursor.getLong(cursor.getColumnIndex("time"));
				if (t >= time) {
					m += money;
				}
			}
		} catch (Exception e) {
			Log.e("hongtao.fu", "计算收益出错");
			e.printStackTrace();
		} finally {
			if(cursor!=null){
				if(!cursor.isClosed()){
					cursor.close();
				}
			}
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
			
		}
		return m;
	}
}
