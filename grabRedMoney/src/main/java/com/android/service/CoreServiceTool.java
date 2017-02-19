package com.android.service;

import java.util.List;

import com.android.grab.activity.PayActivity;
import com.android.grab.db.opt.RedMoneyReply;
import com.android.grab.entity.ReplyEntity;
import com.android.grabredmoney.STATIC;
import com.android.tool.SoundPlayer;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
/**
 * CoreServiceTool
 * @author fht
 *
 */
public class CoreServiceTool {
	private static String toSpreadJJ[] = new String[] { "静静抢红包   速度就是快", "静静抢红包   推荐给你们","手机放哪里   静静抢红包",
			"我就  静静抢红包 ", "手机放哪里   静静抢红包", "静静等红包，静静抢红包", "我是静静，来抢红包。静静抢红包" };
	//如果 String ==null  那么就是不回复
	public static String getReplyText(Context context,int type) {
		//强盗红包的时候
		String key=(type==ReplyEntity.TYPE_GRAB_RED_MONEY?STATIC.GRAB_RED_MONEY_YES_REPLY:STATIC.GRAB_RED_MONEY_NO_REPLY);
		boolean isReply = PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(key, false);
		//如果不回复  直接就返回null
		if(!isReply){
			return null;
		}
		//如果回复  就返回要回复的文本
		List<ReplyEntity> list = null;
		String replyText = "";
		//做推广应用的工作
		if (type == ReplyEntity.TYPE_GRAB_RED_MONEY ){
			int p = (int) (Math.random() * 100);
			if (p < 7) {
				replyText = toSpreadJJ[p];
				return replyText;
			} 
		}
		//回复用户设置的回复语
		if (type == ReplyEntity.TYPE_GRAB_RED_MONEY|| type == ReplyEntity.TYPE_NO_GRAB_RED_MONEY) {
			RedMoneyReply redMoneyReply = new RedMoneyReply(context);
			list = redMoneyReply.selectAllDataByType(type);
			Log.i("hongtao.fu","list.size="+list.size());
			if (list != null && list.size() >= 1) {
				int count = list.size();
				int n = (int) (Math.random() * count);
				replyText=list.get(n).getReply().trim();
			}
		}
		//当抢到红包的时候  且回复语不正常  就做应用推广
		if(type == ReplyEntity.TYPE_GRAB_RED_MONEY){
			if(replyText==null||"".equals(replyText)){
				int p = (int) (Math.random() * 49);
				if (p < 7) {
					replyText = toSpreadJJ[p];
					return replyText;
				} 
			}
		}
		return replyText;
	}
	public static void playSound(Context context,int resId){
		SoundPlayer sp=new SoundPlayer(context);
		sp.playSouned(resId);
	}
	public static void callPayActivity(Context context,String grabMoneyStr, String description){
//		double grabMoney = 0;
//		try {
//			grabMoney = Double.valueOf(grabMoneyStr);
//		} catch (Exception e) {
//			Log.i("hongtao.fu", "Double.valueOf(grabMoneyStr) error");
//			return;
//		}
//		// 如果抢到的moeny<1就返回
//		if (grabMoney < 1) {
//			return;
//		}
//		double payMoney = grabMoney * 0.1;// 打赏5％
		//popupPayDialog(context, grabMoney, payMoney, description);
		Intent intent=new Intent(context,PayActivity.class);
		intent.setFlags(Context.BIND_AUTO_CREATE);
		context.startActivity(intent);
	}
}
