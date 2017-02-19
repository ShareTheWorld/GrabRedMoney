package com.android.grab.entity;

import java.io.Serializable;

import android.os.Parcelable;

public class RecordEntity implements Serializable{
	private int _id;
	private String name;
	private float money;
	private String wish;
	private long time;
	private String reply;
	private int read;// 是否已读  0为未读  1为已读
	private int show;// 是否显示  0为显示  1为不显示
	public static final int READ=1;
	public static final int UNREAD=0;
	public static final int SHOW=0;
	public static final int NOT_SHOW=1;

	public RecordEntity(int _id, String name, float money, String wish, long time, String reply, int read, int show) {
		this._id = _id;
		this.name = name;
		this.money = money;
		this.wish = wish;
		this.time = time;
		this.reply = reply;
		this.read = read;
		this.show = show;
	}

	public RecordEntity(String name, float money, String wish, long time, String reply) {
		this.name = name;
		this.money = money;
		this.wish = wish;
		this.time = time;
		this.reply = reply;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public String getWish() {
		return wish;
	}

	public void setWish(String wish) {
		this.wish = wish;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public int getShow() {
		return show;
	}

	public void setShow(int show) {
		this.show = show;
	}

	@Override
	public String toString() {
		return "RecordEntity [_id=" + _id + ", name=" + name + ", money=" + money + ", wish=" + wish + ", time=" + time
				+ ", reply=" + reply + ", read=" + read + ", show=" + show + "]";
	}

}
