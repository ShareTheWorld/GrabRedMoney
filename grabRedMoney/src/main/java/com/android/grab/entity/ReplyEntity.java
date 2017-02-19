package com.android.grab.entity;

public class ReplyEntity {
	private int _id;
	private String reply;
	private int type;
	public static final int TYPE_NO_GRAB_RED_MONEY = 0;
	public static final int TYPE_GRAB_RED_MONEY = 1;

	public ReplyEntity(int _id, String reply, int type) {
		this._id = _id;
		if (reply != null) {
			this.reply = reply.trim();
		}
		this.type = type;
	}

	public ReplyEntity(String reply, int type) {
		this.reply = reply;
		this.type = type;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		if (reply != null) {
			this.reply = reply.trim();
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ReplyEntity [_id=" + _id + ", reply=" + reply + ", type=" + type + "]";
	}

}
