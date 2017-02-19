package com.android.grab.db.opt;

import java.util.ArrayList;
import java.util.List;

import com.android.grab.db.DBHelper;
import com.android.grab.entity.ReplyEntity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RedMoneyReply {
	private SQLiteDatabase db;
	private final static String TABLE_NAME = "reply";
	private Context context;

	public RedMoneyReply(Context context) {
		this.context = context;
	}

	public List<ReplyEntity> selectAllData() {
		db = DBHelper.getInstance(context).getReadableDatabase();
		try {
			String sql = "select * from " + TABLE_NAME + " ;";
			Cursor cursor = db.rawQuery(sql, null);
			List<ReplyEntity> list = new ArrayList<ReplyEntity>();
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("_id"));
				String reply = cursor.getString(cursor.getColumnIndex("reply"));
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				ReplyEntity replyEntity = new ReplyEntity(id, reply, type);
				Log.i("hongtao.fu", replyEntity.toString());
				list.add(replyEntity);
			}
			return list;
		} catch (Exception e) {
			Log.e("hongtao.fu", "Reply数据查询出异常");
		} finally {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
		}
		return null;
	}
	/**
	 * 根据短信回复的类型进行挑选
	 * @param type
	 * @return
	 */
	public List<ReplyEntity> selectAllDataByType(int type) {
		db = DBHelper.getInstance(context).getReadableDatabase();
		try {
			String sql = "select * from " + TABLE_NAME + " ;";
			Cursor cursor = db.rawQuery(sql, null);
			List<ReplyEntity> list = new ArrayList<ReplyEntity>();
			while (cursor.moveToNext()) {
				if (type != cursor.getInt(cursor.getColumnIndex("type"))) {
					continue;
				}
				int id = cursor.getInt(cursor.getColumnIndex("_id"));
				String reply = cursor.getString(cursor.getColumnIndex("reply"));

				ReplyEntity replyEntity = new ReplyEntity(id, reply, type);
				Log.i("hongtao.fu", replyEntity.toString());
				list.add(replyEntity);
			}
			return list;
		} catch (Exception e) {
			Log.e("hongtao.fu", "Reply数据查询出异常");
		} finally {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
		}
		return null;
	}

	public long insert(ReplyEntity reply) {
		db = DBHelper.getInstance(context).getReadableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("reply", reply.getReply());
			values.put("type", reply.getType());
			long l = db.insert(TABLE_NAME, null, values);
			Log.i("db", "Reply：插入：" + reply.toString());
			return l;
		} catch (Exception e) {
			Log.e("hongtao.fu", "Reply表数据插入出异常");
		} finally {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
		}
		return -1;
	}

	public boolean update(ReplyEntity reply) {
		db = DBHelper.getInstance(context).getReadableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("reply", reply.getReply());
			values.put("type", reply.getType());
			db.update(TABLE_NAME, values, " _id = ? ", new String[] { reply.get_id() + "" });
			Log.i("db", "Reply:update:" + reply);
			return true;
		} catch (Exception e) {
			Log.e("hongtao.fu", "Reply表数据更新出异常");
		} finally {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
		}
		return false;
	}

	public boolean delete(int id) {
		db = DBHelper.getInstance(context).getReadableDatabase();
		try {
			db.delete(TABLE_NAME, "_id = ? ", new String[] { id + "" });
			Log.i("db", "Reply:delete:" + id);
			return true;
		} catch (Exception e) {
			Log.e("hongtao.fu", "Reply表数据删除出异常");
		} finally {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
		}
		return false;
	}

	public boolean updateType(ReplyEntity reply) {
		db = DBHelper.getInstance(context).getReadableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("type", reply.getType());
			db.update(TABLE_NAME, values, " _id = ? ", new String[] { reply.get_id() + "" });
			Log.i("db", "Reply:update:" + reply);
			return true;
		} catch (Exception e) {
			Log.e("hongtao.fu", "Reply表数据更新出异常");
		} finally {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
		}
		return false;
	}

	public boolean updateReply(ReplyEntity reply) {
		db = DBHelper.getInstance(context).getReadableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("reply", reply.getReply());
			db.update(TABLE_NAME, values, " _id = ? ", new String[] { reply.get_id() + "" });
			Log.i("db", "Reply:update:" + reply);
			return true;
		} catch (Exception e) {
			Log.e("hongtao.fu", "Reply表数据更新出异常");
		} finally {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
				}
			}
		}
		return false;
	}
}
