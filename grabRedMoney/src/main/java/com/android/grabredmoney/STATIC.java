package com.android.grabredmoney;

public class STATIC {

	public static final String IS_RUNNING_CORE_SERVECE = "isRunningCoreService";
	public static final String IS_FIRST_START_APPLICATION = "isFirstApplication";
	public static final String IS_ACTIVITE_APP = "is_activite_app";

	public static final String GRAB_RED_MONEY_MODE = "grabRedMoneyMode";
	public static final String GRAB_RED_MONEY_YES_REPLY = "grabRedMoneyYesReply";
	public static final String GRAB_RED_MONEY_NO_REPLY = "grabRedMoneyNoReply";

	public static final String IS_OPEN_FIND_RED_MONEY_SOUND = "isOpenFindRedMOneySound";
	public static final String IS_OPEN_YES_GRAB_RED_MONEY_SOUND = "isOpenYesGrabRedMoneySound";
	public static final String IS_OPEN_NO_GRAB_RED_MONEY_SOUND = "isOpenNoGrabRedMoneySound";

	public static final String ACTIVITE_TIME = "activite_time";// 存放激活时间
	public static final String SHOW_DATA_AT_ACTIVITE_AD_ACTIVITY = "show_data";
	public static final String SHOW_AD_TIME_RECENTLY = "show_ad_time_recently";// 最近显示广告的时间

	public static final String HAND_SLOW_RED_GRAB_FINISH = "红包派完了";
	public static final String SWITCH_TO_PRESS_SPEAK = "切换到按住说话";
	public static final String RED_MONEY_OVER_24_HOUR = "红包已超过24小时";
	public static final String SEND = "发送";

	public static final int REPLY_TYPE_DEFAULT = 0;// 回复类型为默认
	public static final int REPLY_TYPE_NO_GRAB_RED_MONEY = -1;// 没有抢到红包
	public static final int REPLY_TYPE_GRAB_RED_MONEY = 1;// 抢到红包

	public static final long ACTIVITE_TIME_LONG_ONE = 977600000000000000l;// 激活以及管多长时间
																	// 90×24×60×60×1000
	public static final long ACTIVITE_TIME_LONG_IN = 240000;// 在多长时间的范围内可以激活
															// 2.5*60*1000
															// 两分半钟内可以激活
}
