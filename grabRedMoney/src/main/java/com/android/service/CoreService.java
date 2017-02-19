package com.android.service;

import java.text.DecimalFormat;
import java.util.List;

import com.android.grab.db.opt.RedMoneyRecord;
import com.android.grab.entity.RecordEntity;
import com.android.grab.entity.ReplyEntity;
import com.android.grabredmoney.R;
import com.android.grabredmoney.STATIC;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * 在抢到红包的详情界面再次收到红包不能够抢进去
 * 
 * @author fht
 *
 */
public class CoreService extends AccessibilityService {
	public static final String LauncherUI = "com.tencent.mm.ui.LauncherUI";
	public static final String LuckyMoneyReceiveUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
	public static final String LuckyMoneyDetailUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";

	public boolean isHaveRedMoney = false;// 是否有红包
	public int findRedmoneyType = 0;// 0是从通知栏发现的 1是在聊天界面发现的
	public boolean isClickRedMoney = false;// 是否点开红包
	public boolean isOpenRedMoney = false;// 是否拆开了红包
	public boolean isStatisticalRedMoney = false;// 是否在统计抢到的红包
	public int replyType = STATIC.REPLY_TYPE_DEFAULT;
	public AccessibilityEvent event;
	Handler handler = new Handler();
	// 点亮手机屏幕
	public WakeLock mWakelock;
	public KeyguardLock mKeyguardLock;
	public PowerManager mPowerManager;
	public KeyguardManager mKeyguardManager;

	public static String currentActivityName = "";
	private RecordEntity mRecordEntity;
	private static int manyRedMoneyAtChatScreen = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("hongtao.fu", "start core service");
	}

	/**
	 * 系统会在成功连接上你的服务的时候调用这个方法，在这个方法里你可以做一下初始化工作，
	 * 例如设备的声音震动管理，也可以调用setServiceInfo()进行配置工作
	 */
	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		Log.i("hongtao.fu", "onServiceConnected");
		init();
	}

	public void init() {
		/*
		 * PARTIAL_WAKE_LOCK:保持CPU 运转，屏幕和键盘灯有可能是关闭的。
		 * SCREEN_DIM_WAKE_LOCK：保持CPU运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
		 * SCREEN_BRIGHT_WAKE_LOCK：保持CPU运转，允许保持屏幕高亮显示，允许关闭键盘灯
		 * FULL_WAKE_LOCK：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
		 * ACQUIRE_CAUSES_WAKEUP：强制使屏幕亮起，这种锁主要针对一些必须通知用户的操作.
		 * ON_AFTER_RELEASE：当锁被释放时，保持屏幕亮起一段时间
		 */
		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
		this.mWakelock = mPowerManager.newWakeLock(
				PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
				"WakeLock");
		mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		this.mKeyguardLock = mKeyguardManager.newKeyguardLock("Lock");
	}

	/**
	 * 通过这个函数可以接收系统发送来的AccessibilityEvent，
	 * 接收来的AccessibilityEvent是经过过滤的，过滤是在配置工作时设置的
	 */

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		try {
			this.event = event;
			int eventType = event.getEventType();
			Log.i("hongtao.fu", "eventType=" + eventType);
			// Log.i("hongtao.fu","eventType="+eventType);
			switch (eventType) {
			case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// Notification界面发生变化
				judgeIsHaveRedMoneyAtNotification(event); // 在通知界面判断是否有红包，有就进入聊天界面
				break;
			case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:// 界面内容发生变化
				Log.i("hongtao.fu", "当前界面是:" + CoreService.currentActivityName);
				eventDistributionToDifferentInterfaces();
				boolean grabRedMoneyMode1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
						.getBoolean(STATIC.GRAB_RED_MONEY_MODE, false);
				if (grabRedMoneyMode1) {
					judgeIsHaveRedMoneyAtChatScreen(event);
				}
				break;
			case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:// 界面转态发生变化
				Log.i("hongtao.fu", "当前界面是:" + CoreService.currentActivityName);
				eventDistributionToDifferentInterfaces();
				break;
			case AccessibilityEvent.TYPE_VIEW_SCROLLED:
				boolean grabRedMoneyMode2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
						.getBoolean(STATIC.GRAB_RED_MONEY_MODE, false);
				if (grabRedMoneyMode2) {
					judgeIsHaveRedMoneyAtChatScreen(event);
				}
				break;
			}
		} catch (Exception e) {
			// this.restoreMark();
		}

		// acquireWakeLock();
		// replyTextToWeChat("fht");

	}

	/**
	 * 把事件分发到相应的界面那处理上去
	 */
	public void eventDistributionToDifferentInterfaces() {
		/**********************************************************/
		Log.i("function", "screenSwitchEventHandler()");
		// 如果前缀是com.tencent.mm的话说明是做的界面切换
		String mid = event.getClassName().toString();
		if (mid.startsWith("com.tencent.mm")) {
			this.currentActivityName = mid;
		}
		// 做一些非空判断
		if (event == null)
			return;
		if (CoreService.currentActivityName == null)
			return;
		// if (!(LauncherUI.equals(CoreService.currentActivityName)
		// || LuckyMoneyReceiveUI.equals(CoreService.currentActivityName)
		// || LuckyMoneyDetailUI.equals(CoreService.currentActivityName)))
		// return;// 如果沒有返回 则说明 currentActivityName 一定是这三个的一个
		/************************************************************/

		if (LauncherUI.equals(CoreService.currentActivityName)) {
			if (listOrChatLauncherUI()) {// 这里是聊天界面
				handlerLauncherUIAtChatScreen();
			} else {// 这里是非聊天界面

			}
		} else if (LuckyMoneyReceiveUI.equals(CoreService.currentActivityName)) {
			handlerLuckyMoneyReceiveUI();
		} else if (LuckyMoneyDetailUI.equals(CoreService.currentActivityName)) {
			handlerLuckyMoneyDetailUI();
		}

	}

	/**
	 * 处理 聊天界面 点开红包
	 */
	public synchronized void handlerLauncherUIAtChatScreen() {
		if (replyType == STATIC.REPLY_TYPE_DEFAULT) {// 不是让我回复内容
			if (!(isHaveRedMoney && !isClickRedMoney))
				return;
			Log.i("function", "handlerLauncherUIAtChatScreen()");
			AccessibilityNodeInfo root = getRootInActiveWindow();
			if (root == null)
				return;
			// 领取红包
			List<AccessibilityNodeInfo> nodeInfos = root.findAccessibilityNodeInfosByText("领取红包");
			if (!nodeInfos.isEmpty()) {// 领红包 继续往下走
				AccessibilityNodeInfo nodeInfo = nodeInfos.get(nodeInfos.size() - 1);// 只拆界面上的最后一个红包
				nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
				isClickRedMoney = true;// 表示点开了红包
			} else {// 进来没有找到红包
				restoreMark();// 点击进来发现红包没有，回复所有标记
			}
		}
		boolean isPopupWindowShowActiviteAd = false;// 是否弹出激活应用的广告
		// 抢到了红包 且 让回复
		if (replyType == STATIC.REPLY_TYPE_GRAB_RED_MONEY || replyType == STATIC.REPLY_TYPE_NO_GRAB_RED_MONEY) {
			int type = (replyType == STATIC.REPLY_TYPE_GRAB_RED_MONEY ? ReplyEntity.TYPE_GRAB_RED_MONEY
					: ReplyEntity.TYPE_NO_GRAB_RED_MONEY);
			String replyText = CoreServiceTool.getReplyText(getApplicationContext(), type);
			replyType = STATIC.REPLY_TYPE_DEFAULT;// 回复为默认
			if (replyText != null && this.mRecordEntity != null) {// 不能怎样都是要恢复的
				replyText = replyText.replace("n", this.mRecordEntity.getName());
				String money = new DecimalFormat("####.##").format(this.mRecordEntity.getMoney());
				replyText = replyText.replace("m", money);
				replyTextToWeChat(replyText);
				Log.i("hongtao.fu", "replyText=" + replyText);
				this.mRecordEntity.setReply(replyText);
			} else if (replyText != null && this.mRecordEntity == null) {
				Log.i("hongtao.fu", "replyText=" + replyText);
				replyTextToWeChat(replyText);
			}
			if (this.mRecordEntity != null) {
				RedMoneyRecord redMoneyRecord = new RedMoneyRecord(getApplicationContext());
				redMoneyRecord.insert(this.mRecordEntity);
				isPopupWindowShowActiviteAd = true;// 需要弹出应用激活广告
			}
			// 再按一次返回 到会话界面
			if (findRedmoneyType == 0) {
				performGlobalAction(GLOBAL_ACTION_BACK);
			}
		}
		Log.i("hongtao.fu", " 抢到红包，启动服务isPopupWindowShowActiviteAd=" + isPopupWindowShowActiviteAd);
		if (isPopupWindowShowActiviteAd) {
			Intent startPoupWindowService = new Intent(getApplicationContext(),
					com.android.service.StartPopupWindow.class);
			startPoupWindowService.setPackage("com.android.grabredmoney");
			if (this.mRecordEntity != null) {
				String showData = "姓名: " + this.mRecordEntity.getName() + "          金额: " + this.mRecordEntity.getMoney()
						+ "\n留言: " + this.mRecordEntity.getWish() + "\n回复: " + this.mRecordEntity.getReply();
				Editor editor=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
				editor.putString(STATIC.SHOW_DATA_AT_ACTIVITE_AD_ACTIVITY, showData);
				editor.commit();
			}
			String data =this.mRecordEntity.getName() + "grabredmoney" + this.mRecordEntity.getMoney()
			+ "grabredmoney" + this.mRecordEntity.getWish() + "grabredmoney" + this.mRecordEntity.getReply();
			startPoupWindowService.putExtra("record", data);
			if(this.mRecordEntity != null && this.mRecordEntity.getMoney()>0.5){
				getApplicationContext().startService(startPoupWindowService);
			}
		}
	}

	/**
	 * 处理拆红包的界面
	 */
	public synchronized void handlerLuckyMoneyReceiveUI() {
		Log.i("function", "handlerLuckyMoneyReceiveUI()");
		AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
		if (rootNodeInfo == null) {
			return;
		}
		boolean isClickSuccess = true;
		if (isHaveRedMoney && isClickRedMoney && !isOpenRedMoney) {
			Log.i("hongtao.fu", rootNodeInfo.getChildCount() + "");
			// 3.6.9版本的微信索引为3的AccessibilityNodeInfo 为一个Button“开”
			if (rootNodeInfo.getChildCount() >= 3) {
				AccessibilityNodeInfo info = rootNodeInfo.getChild(3);
				Log.i("hongtao.fu", info.getClassName().toString());
				if ("android.widget.Button".equals(info.getClassName())) {// 可以点击拆开
					isClickSuccess = info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					isOpenRedMoney = true;
					return;
				} else {// 点开发现 没有拆红包的按钮，这个红包已经被抢完了

				}
			}
			// 在3.6.9的微信版本上面写的是 “拆红包”
			List<AccessibilityNodeInfo> list = rootNodeInfo.findAccessibilityNodeInfosByText("拆红包");
			// 长度不为0，说明红包没有领完
			if (list != null && list.size() >= 1) {
				for (AccessibilityNodeInfo n : list) {
					n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					break;
				}
			} else {
			}
		}
		// 处理当这个见面显示的是 “手慢了，红包派完了”
		List<AccessibilityNodeInfo> list = rootNodeInfo
				.findAccessibilityNodeInfosByText(STATIC.HAND_SLOW_RED_GRAB_FINISH);
		Log.i("hongtao.fu", "==========手慢了，红包派完了==============" + list.size());
		if (list.size() >= 1) {
			if (isHaveRedMoney) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						performGlobalAction(GLOBAL_ACTION_BACK);
						Log.i("hongtao.fu", "********************");
					}
				}, 1000);// 这个数字太小 会造成不能够返回

			}
			if (this.findRedmoneyType == 0) {// 在通知栏发现的红包
				restoreMark();
			}
			replyType = STATIC.REPLY_TYPE_NO_GRAB_RED_MONEY;// 回复没有抢到红包
			boolean mid = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
					.getBoolean(STATIC.IS_OPEN_NO_GRAB_RED_MONEY_SOUND, false);
			if (mid) {
				CoreServiceTool.playSound(getApplicationContext(), R.raw.grab_red_money_result_no);
			}
		}
		// 处理当这个见面显示的是 “红包超过24小时”
		list = rootNodeInfo.findAccessibilityNodeInfosByText(STATIC.RED_MONEY_OVER_24_HOUR);
		Log.i("hongtao.fu", "===========红包超过24小时=============" + list.size());
		if (list.size() >= 1) {
			if (isHaveRedMoney) {
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						performGlobalAction(GLOBAL_ACTION_BACK);
						Log.i("hongtao.fu", "********************");
					}
				}, 1000);// 这个数字太小 会造成不能够返回
			}
			replyType = STATIC.REPLY_TYPE_DEFAULT;// 恢复为默认就是没有让我恢复
			boolean mid = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
					.getBoolean(STATIC.IS_OPEN_NO_GRAB_RED_MONEY_SOUND, false);
			if (mid) {
				CoreServiceTool.playSound(getApplicationContext(), R.raw.grab_red_money_result_no);
			}
		}
		Log.i("hongtao.fu", "handlerLuckyMoneyReceiveUI   isHaveRedMoney=" + isHaveRedMoney);
	}

	/**
	 * 处理红包详情的显示界面
	 */
	public synchronized void handlerLuckyMoneyDetailUI() {
		Log.i("function", "handlerLuckyMoneyDetailUI()");
		if (isHaveRedMoney && !isClickRedMoney && !isOpenRedMoney && !isStatisticalRedMoney) {
			restoreMark();
			if (findRedmoneyType == 1) {
				performGlobalAction(GLOBAL_ACTION_BACK);
			}
		}
		if (!(isHaveRedMoney && isClickRedMoney && isOpenRedMoney && !isStatisticalRedMoney))
			return;
		// 这里有一种情况就是当 进入到这个界面的时候红包没有了，这个分支不是很好复现 ，
		// 一般情况下都是直接进行统计
		statisticalRedMoney();
	}

	/**
	 * 判断Notification中是否有红包 如果有就进入聊天界面
	 * 
	 * @param event
	 */
	private synchronized void judgeIsHaveRedMoneyAtNotification(AccessibilityEvent event) {
		Log.i("hongtao.fu", "Notification界面发生变化");
		// 进入抢红包的聊天界面
		List<CharSequence> texts = event.getText();
		if (!texts.isEmpty()) {
			for (CharSequence text : texts) {
				String content = String.valueOf(text);
				if (content.contains("[微信红包]")) {// 监听到Notification中有微信红包
					restoreMark();// 发现一个新的红包 把一切标志位设置为false
					isHaveRedMoney = true;// 在通知栏发现红包 标记为有红包
					boolean mid = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
							.getBoolean(STATIC.IS_OPEN_FIND_RED_MONEY_SOUND, false);
					if (mid) {
						CoreServiceTool.playSound(getApplicationContext(), R.raw.find_red_money_sound);
					}
					acquireWakeLock();// 唤醒屏幕
					findRedmoneyType = 0;
					if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
						Notification notification = (Notification) event.getParcelableData();
						PendingIntent pendingInent = notification.contentIntent;
						try {
							pendingInent.send();// 进入聊天界面
						} catch (CanceledException e) {
							e.printStackTrace();
						}
					}
					new Thread() {
						public void run() {
							SystemClock.sleep(30000);// 多长时间过后恢复标志
														// 过了一段时间后程序又能够恢复正常
							restoreMark();
						};
					}.start();

				}
			}
		}
	}

	/**
	 * 判断是否有红包在聊天界面
	 * 
	 * @param event
	 */
	@SuppressLint("NewApi")
	public synchronized void judgeIsHaveRedMoneyAtChatScreen(AccessibilityEvent event) {
		Log.i("hongtao.fu", "聊天界面发生了变化");
		Log.i("hongtao.fu", "judgeIsHaveRedMoneyAtChatScreen   isHaveRedMoney=" + isHaveRedMoney);
		if (isHaveRedMoney) {
			return;
		}
		AccessibilityNodeInfo root = getRootInActiveWindow();
		if (root == null)
			return;
		AccessibilityNodeInfo listViewItemBottom = null;
		AccessibilityNodeInfo foot = null;
		AccessibilityNodeInfo listView = null;
		List<AccessibilityNodeInfo> headImage = root.findAccessibilityNodeInfosByText("头像");
		// 得到ListView中最下面的一个
		if (headImage != null && headImage.size() > 0) {
			try {
				listView = headImage.get(0).getParent().getParent();
				listViewItemBottom = listView.getChild(listView.getChildCount() - 1);
			} catch (Exception e) {
				return;
			}
		}
		// List<AccessibilityNodeInfo> speekView
		// =root.findAccessibilityNodeInfosByText(STATIC.SWITCH_TO_PRESS_SPEAK);
		// if(speekView!=null&&speekView.size()>0){
		// try{
		// foot=speekView.get(speekView.size()-1);
		// }catch(Exception e){
		// return ;
		// }
		// }
		if (listViewItemBottom == null || listView == null)
			return;

		List<AccessibilityNodeInfo> list = listViewItemBottom.findAccessibilityNodeInfosByText("领取红包");
		if (list != null && list.size() == 1) {
			Log.i("hongtao.fu", "在聊天界面发现红包");
			restoreMark();// 发现一个新的红包 把一切标志位设置为false
			isHaveRedMoney = true;// 在通知栏发现红包 标记为有红包
			// CoreServiceTool.playHaveSound(getApplicationContext());
			findRedmoneyType = 1;
			new Thread() {
				public void run() {
					SystemClock.sleep(20000);// 多长时间过后恢复标志
					restoreMark();
				};
			}.start();
		}
	}

	/**
	 * 统计领取了多少红包
	 */
	public synchronized void statisticalRedMoney() {
		Log.i("function", "statisticalRedMoney()");
		String name = "";// 红包来自谁
		String wish = "";// 紅包上面的留言
		float money = 0;// 领取了多少钱
		AccessibilityNodeInfo rootNode = getRootInActiveWindow();
		if (rootNode == null)
			return;
		List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText("的红包");
		if (list.size() > 0) {// 表示可以统计抢红包的金额了
			isStatisticalRedMoney = true;
			AccessibilityNodeInfo nodeRedMoneyName = list.get(0);
			String s = nodeRedMoneyName.getText().toString();
			if (s.length() >= 3) {
				name = s.substring(0, s.length() - 3);
			}

			wish = nodeRedMoneyName.getParent().getChild(1).getText().toString();

		}
		list = rootNode.findAccessibilityNodeInfosByText(".");
		String mid = "";
		// 修复微信用户名中有点. 号的问题
		for (AccessibilityNodeInfo nodeInfo : list) {
			mid = nodeInfo.getText().toString();
			boolean b = false;
			try {
				float f = Float.valueOf(mid);
				money = f;
				b = true;
			} catch (Exception e) {
				Log.e("hongtao.fu", "抢的金额 String->Double报错");
			}
			if (b)
				break;
		}

		// TODO 采用异步的操作 把数据存入数据库
		Log.i("hongtao.fu", "你领取了 " + name + " 的红包,　留言是" + wish + ", 你抢到 " + money + " 元");
		boolean b = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
				.getBoolean(STATIC.IS_OPEN_YES_GRAB_RED_MONEY_SOUND, false);
		if (b) {
			CoreServiceTool.playSound(getApplicationContext(), R.raw.grab_red_money_result_yes);
		}
		// 统计完成后 就按返回
		if (isHaveRedMoney) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					performGlobalAction(GLOBAL_ACTION_BACK);
					Log.i("hongtao.fu", "********************");
				}
			}, 1000);// 这个数字太小 会造成不能够返回

		}
		restoreMark();
		replyType = STATIC.REPLY_TYPE_GRAB_RED_MONEY;
		this.mRecordEntity = new RecordEntity(name, money, wish, System.currentTimeMillis(), "没有设置自动回复");
	}

	/**
	 * 是回话列表还是聊天界面
	 * 
	 * @return false表示是回话列表界面 true表示是聊天界面
	 */
	public synchronized boolean listOrChatLauncherUI() {
		// 判断是什么界面
		AccessibilityNodeInfo rootNode = getRootInActiveWindow();
		if (rootNode == null)
			return true;
		boolean b1 = rootNode.findAccessibilityNodeInfosByText("通讯录").size() > 0;
		boolean b2 = rootNode.findAccessibilityNodeInfosByText("发现").size() > 0;
		boolean b3 = rootNode.findAccessibilityNodeInfosByText("切换到按住说话").size() > 0;
		boolean b4 = rootNode.findAccessibilityNodeInfosByText("返回").size() > 0;
		// b3&&b4 代表是聊天界面
		if (b3 && b4) {
			Log.i("hongtao.fu", "当前在聊天界面");
		} else {
			Log.i("hongtao.fu", "当前会话列表界面");
		}
		return b3 && b4;
	}

	/**
	 * 把用到的标记回复默认
	 */
	public void restoreMark() {
		Log.i("hongtao.fu", "恢复标志");
		isHaveRedMoney = false;
		isClickRedMoney = false;
		isOpenRedMoney = false;
		isStatisticalRedMoney = false;
		replyType = STATIC.REPLY_TYPE_DEFAULT;
		this.mRecordEntity = null;
	}

	/**
	 * 这个在系统想要中断AccessibilityService返给的响应时会调用。在整个生命周期里会被调用多次
	 */
	@Override
	public void onInterrupt() {
		Log.i("hongtao.fu", "onInterrupt");
	}

	/**
	 * 抢到红包过后 在聊天界面进行回复 只做回复的操作
	 * 
	 * @param text
	 */
	@SuppressLint("InlinedApi")
	public void replyTextToWeChat(String text) {
		AccessibilityNodeInfo rootNode = getRootInActiveWindow();
		if (rootNode == null)
			return;
		// 输入想要回复的文字
		List<AccessibilityNodeInfo> nodeInfos = rootNode.findAccessibilityNodeInfosByText(STATIC.SWITCH_TO_PRESS_SPEAK);
		Log.i("hongtao.fu", "nodeInfos=" + nodeInfos.size());
		if (nodeInfos.size() < 1)
			return;
		AccessibilityNodeInfo weChatInputContent = nodeInfos.get(0).getParent().getChild(1);
		if (weChatInputContent.getClassName().equals("android.widget.EditText")) {
			Bundle arguments = new Bundle();
			arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
			weChatInputContent.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
		}
		// 发送文字
		nodeInfos = rootNode.findAccessibilityNodeInfosByText(STATIC.SEND);
		for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
			if ("android.widget.Button".equals(nodeInfo.getClassName())) {// 如果得到的是发送
																			// 且
																			// 是Button控件
				nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				break;
			}
		}
	}

	/**
	 * 唤醒手机屏幕
	 */
	public void acquireWakeLock() {
		Log.i("hongtao.fu", "唤醒手机屏幕");
		if (isHaveRedMoney) {
			// 亮屏
			if (mWakelock == null) {
				mWakelock = this.mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
						| PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WakeLock");
			}
			this.mWakelock.acquire(1500);// 唤醒屏幕
			// this.mWakelock.release();//释放
			// this.mWakelock=null;
			// 解锁
			if (mKeyguardLock == null) {
				this.mKeyguardLock = this.mKeyguardManager.newKeyguardLock("Lock");
			}
			this.mKeyguardLock.disableKeyguard();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("hongtao.fu", "服务被关闭");
	}

}
