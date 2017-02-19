package com.android.grabredmoney;

import java.text.DecimalFormat;
import java.util.Calendar;

import com.android.grab.activity.GrabRedMoneyPreferenceActivity;
import com.android.grab.activity.HelpActivity;
import com.android.grab.activity.RedMoneyRecordActivity;
import com.android.grab.activity.RedMoneyReplyActivity;
import com.android.grab.db.opt.RedMoneyRecord;
import com.android.grab.db.opt.RedMoneyReply;
import com.android.grab.entity.ReplyEntity;
import com.android.service.StartPopupWindow;
import com.android.tool.PayUtil;
import com.android.tool.SharedPreferencesGet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	private TextView sumMoneyTV;
	private TextView monthMoneyTV;
	private TextView weekMoneyTV;
	private TextView dayMoneyTV;
	private boolean isPopStartDialog = true;
	private TextView grabing;
	private ToggleButton grabRedMoneyModeSwitch;
	private TextView grabRedMoneyModeDescription;

	private ToggleButton grabRedMoneyYesReplySwitch;
	private TextView grabRedMoneyYesReplyDescription;

	private ToggleButton grabRedMoneyNoReplySwitch;
	private TextView grabRedMoneyNoReplyDescription;
	private SharedPreferences sp;
	private Animation roateAnim;

	private int mWhereFromStart = 0;
	private boolean isAddAdToLayout = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setDefaultValue();
		sumMoneyTV = (TextView) findViewById(R.id.sum_money);
		monthMoneyTV = (TextView) findViewById(R.id.month_money);
		weekMoneyTV = (TextView) findViewById(R.id.week_money);
		dayMoneyTV = (TextView) findViewById(R.id.day_money);
		setMoney();// 设置每个时间段的收益
		// **************抢红包的旋转动画********************begin
		grabing = (TextView) findViewById(R.id.grabing);
		roateAnim = AnimationUtils.loadAnimation(this, R.anim.grabing_anim);
		LinearInterpolator lin = new LinearInterpolator();
		roateAnim.setInterpolator(lin);
		// grabing.setAnimation(roateAnim);
		grabing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActiviteDialog();
			}
		});
		// **************抢红包的旋转动画********************end
		initSetting();
		isAddAdToLayout = false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		setMoney();// 设置每个时间段的收益
		Log.i("hongtao.fu", "onStart()");
		boolean isStartServiceDialog = startServiceDialog();
		if (!isStartServiceDialog) {
			long activiteTime = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
					.getLong(STATIC.ACTIVITE_TIME, 0);
			long currentTime = System.currentTimeMillis();
			if ((currentTime - activiteTime) > STATIC.ACTIVITE_TIME_LONG_ONE) {// &&
																				// isPopStartDialog
																				// ==
																				// false
				Log.i("hongtao.fu", "**********启动激活对话框***************");
				startActiviteDialog();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferencesGet.setShowAdTimeRecently(getApplicationContext(), System.currentTimeMillis());
		initSetting();
		try {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			mWhereFromStart = sp.getInt(StartPopupWindow.WE_CHAT_GRAB_RED_MONEY_START_KEY, 0);
			Editor editor = sp.edit();
			editor.putInt(StartPopupWindow.WE_CHAT_GRAB_RED_MONEY_START_KEY, 0);
			editor.commit();
			// *******************广告条广告*******************begin
			if (mWhereFromStart != StartPopupWindow.WE_CHAT_GRAB_RED_MONEY_START_VALUE) {

			}
			Log.i("hongtao.fu",
					"从哪里启动的    " + mWhereFromStart + "    " + StartPopupWindow.WE_CHAT_GRAB_RED_MONEY_START_VALUE);
			if (mWhereFromStart == StartPopupWindow.WE_CHAT_GRAB_RED_MONEY_START_VALUE) {

			}
		} catch (Exception e) {

		}
		mWhereFromStart = 0;
		PayUtil payUtil=new PayUtil();
		payUtil.init(this);
		payUtil.installPayPlug();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.red_money_reply_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_setting:
			Intent intent = new Intent(this, GrabRedMoneyPreferenceActivity.class);
			this.startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	public void open(View view) {
		try {
			Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startDialogAd(View view) {
		Log.i("hongtao.fu", "显示插屏");
	}

	public void startInsertAd(View view) {
		Log.i("hongtao.fu", "显示积分墙");
		// ***********方式一打开积分墙****************
		// ***********方式二打开积分墙****************
		// OffersManager.getInstance(this).showOffersWallDialog(this);//对话框的方式显示
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 启动红包记录Activity
	 * 
	 * @param view
	 */
	public void startRedMoneyRecordActivity(View view) {
		Intent intent = new Intent(this, RedMoneyRecordActivity.class);
		intent.setPackage(getPackageName());
		this.startActivity(intent);
	}

	/**
	 * 启动红包回复设置Activity
	 * 
	 * @param view
	 */
	public void startRedMoneyReplyActivity(View view) {
		Intent intent = new Intent(this, RedMoneyReplyActivity.class);
		intent.setPackage(getPackageName());
		this.startActivity(intent);
	}

	/**
	 * 设置上收益
	 */
	public void setMoney() {
		new Thread() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Calendar c = Calendar.getInstance();
						int year = c.get(Calendar.YEAR);
						int month = c.get(Calendar.MONTH);
						int day_month = c.get(Calendar.DAY_OF_MONTH);
						int day_week = c.get(Calendar.DAY_OF_WEEK);
						c.set(year, month, day_month, 0, 0, 100);
						long dayBeginTime = c.getTimeInMillis();// 得到今天的开始时间
						Log.i("hongtao.fu", "===" + c.get(Calendar.YEAR) + "   " + c.get(Calendar.MONTH) + "   "
								+ c.get(Calendar.DAY_OF_MONTH));
						c.set(year, month, day_month, 0, 0, 0);
						c.add(Calendar.DAY_OF_WEEK, -6);
						long weekBeginTime = c.getTimeInMillis();// 得到本周的开始时间
						Log.i("hongtao.fu", "===" + c.get(Calendar.YEAR) + "   " + c.get(Calendar.MONTH) + "   "
								+ c.get(Calendar.DAY_OF_MONTH));
						c.set(year, month, day_month, 0, 0, 0);
						c.add(Calendar.DAY_OF_MONTH, -30);
						long monthBeginTime = c.getTimeInMillis();// 得到本月的开始时间
						Log.i("hongtao.fu", "===" + c.get(Calendar.YEAR) + "   " + c.get(Calendar.MONTH) + "   "
								+ c.get(Calendar.DAY_OF_MONTH));
						RedMoneyRecord record = new RedMoneyRecord(getApplicationContext());
						float dayMoney = record.getMoneyInTime(dayBeginTime);// 得到今天的收益
						float weekMoney = record.getMoneyInTime(weekBeginTime);// 的到本周的收益
						float monthMoney = record.getMoneyInTime(monthBeginTime);// 得到本月的收益
						float sumMoney = record.getMoneyInTime(0);// 得到总共的收益
						DecimalFormat decimalFormat = new DecimalFormat("####.##");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
						final String sDayMoney = decimalFormat.format(dayMoney);
						final String sWeekMoney = decimalFormat.format(weekMoney);
						final String sMonthMoney = decimalFormat.format(monthMoney);
						final String sSumMoney = decimalFormat.format(sumMoney);

						dayMoneyTV.setText(sDayMoney);
						weekMoneyTV.setText(sWeekMoney);
						monthMoneyTV.setText(sMonthMoney);
						sumMoneyTV.setText(sSumMoney);

					}
				});
				
			}
		}.start();
	}

	public void initSetting() {
		sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		// *************抢红包模式**********************begin
		boolean b = sp.getBoolean(STATIC.GRAB_RED_MONEY_MODE, false);
		grabRedMoneyModeSwitch = (ToggleButton) this.findViewById(R.id.grab_red_money_mode_switch);
		grabRedMoneyModeDescription = (TextView) findViewById(R.id.grab_red_money_mode_description);
		if (b) {
			grabRedMoneyModeDescription.setText("无论你在那里，静静都会为你第一时间点击红包");
			grabRedMoneyModeSwitch.setChecked(true);
		} else {
			grabRedMoneyModeDescription.setText("如果你在聊天界面,静静将不会为你抢红包");
			grabRedMoneyModeSwitch.setChecked(false);
		}
		grabRedMoneyModeSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					grabRedMoneyModeDescription.setText("无论你在那里，静静都会为你第一时间点击红包");
				} else {
					grabRedMoneyModeDescription.setText("如果你在聊天界面,静静将不会为你抢红包");
				}
				Editor editor = sp.edit();
				editor.putBoolean(STATIC.GRAB_RED_MONEY_MODE, isChecked);
				editor.commit();
			}
		});
		// *************抢红包模式**********************end

		// *************抢到红包**********************begin
		boolean g1 = sp.getBoolean(STATIC.GRAB_RED_MONEY_YES_REPLY, true);
		grabRedMoneyYesReplySwitch = (ToggleButton) this.findViewById(R.id.grab_red_money_yes_reply_switch);
		grabRedMoneyYesReplyDescription = (TextView) findViewById(R.id.grab_red_money_yes_reply_description);
		if (g1) {
			grabRedMoneyYesReplyDescription.setText("抢到了红包，静静将会为你自动回复");
			grabRedMoneyYesReplySwitch.setChecked(true);
		} else {
			grabRedMoneyYesReplyDescription.setText("抢到了红包，静静将不会为你自动回复");
			grabRedMoneyYesReplySwitch.setChecked(false);
		}
		grabRedMoneyYesReplySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					grabRedMoneyYesReplyDescription.setText("抢到了红包，静静将会为你自动回复");
				} else {
					grabRedMoneyYesReplyDescription.setText("抢到了红包，静静将不会为你自动回复");
				}
				Editor editor = sp.edit();
				editor.putBoolean(STATIC.GRAB_RED_MONEY_YES_REPLY, isChecked);
				editor.commit();
			}
		});
		// *************抢到红包**********************end

		// ************没抢到红包**********************begin
		boolean g2 = sp.getBoolean(STATIC.GRAB_RED_MONEY_NO_REPLY, false);
		grabRedMoneyNoReplySwitch = (ToggleButton) this.findViewById(R.id.grab_red_money_no_reply_switch);
		grabRedMoneyNoReplyDescription = (TextView) findViewById(R.id.grab_red_money_no_reply_description);
		if (g2) {
			grabRedMoneyNoReplyDescription.setText("没抢到了红包，静静将会为你自动回复");
			grabRedMoneyNoReplySwitch.setChecked(true);
		} else {
			grabRedMoneyNoReplyDescription.setText("没抢到了红包，静静将不会为你自动回复");
			grabRedMoneyNoReplySwitch.setChecked(false);
		}
		grabRedMoneyNoReplySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					grabRedMoneyNoReplyDescription.setText("没抢到了红包，静静将会为你自动回复");
				} else {
					grabRedMoneyNoReplyDescription.setText("没抢到了红包，静静将不会为你自动回复");
				}
				Editor editor = sp.edit();
				editor.putBoolean(STATIC.GRAB_RED_MONEY_NO_REPLY, isChecked);
				editor.commit();
			}
		});
		// *************没抢到红包**********************end
	}

	/**
	 * 启动服务的对话框
	 */
	public boolean startServiceDialog() {
		boolean isStartServiceDialog = false;
		String enabledGrabRedMoneyServices = Settings.Secure.getString(getApplicationContext().getContentResolver(),
				Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
		boolean isRunningCoreService = true;
		if (enabledGrabRedMoneyServices == null || "".equals(enabledGrabRedMoneyServices)) {
			isRunningCoreService = false;
		}
		if (!isRunningCoreService && isPopStartDialog) {
			float scale = getResources().getDisplayMetrics().density;
			int size = (int) (50 * scale + 0.5f);// dp 转 px
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grab_logo);
			bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
			AlertDialog.Builder builder = new Builder(this);
			builder.setIcon(new BitmapDrawable(bitmap));
			builder.setTitle("开启服务");
			builder.setMessage("现在就去开启抢红包服务");
			builder.setPositiveButton("去开启", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			builder.setNegativeButton("等一下", null);
			AlertDialog dialog = builder.create();
			dialog.show();
			isStartServiceDialog = true;// 启动了dialog
		} else {
		}
		isPopStartDialog = false;

		if (isRunningCoreService) {
			grabing.setText("等红\n包中");
			grabing.setAnimation(roateAnim);
			grabing.setOnClickListener(new TextView.OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(getApplicationContext(), "静静在等红包", Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			grabing.setText("启动\n服务");
			grabing.clearAnimation();
			grabing.setOnClickListener(new TextView.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return isStartServiceDialog;
	}

	/**
	 * 启动激活的对话框
	 */
	public void startActiviteDialog() {

	}

	/**
	 * 弹出帮助说明
	 * 
	 * @param view
	 */
	public void popHelp(View view) {
		Intent intent = new Intent(this, HelpActivity.class);
		intent.setPackage(getPackageName());
		startActivity(intent);
	}

	public static boolean isClickDownload = false;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
	
	/**
	 * 设置静静抢红包的默认值
	 */
	public void setDefaultValue() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (sp.getBoolean(STATIC.IS_FIRST_START_APPLICATION, true)) {
			Editor editor = sp.edit();
			editor.putBoolean(STATIC.GRAB_RED_MONEY_MODE, true);
			editor.putBoolean(STATIC.GRAB_RED_MONEY_YES_REPLY, true);
			editor.putBoolean(STATIC.GRAB_RED_MONEY_NO_REPLY, false);
			editor.putBoolean(STATIC.IS_FIRST_START_APPLICATION, false);
			editor.putBoolean(STATIC.IS_OPEN_FIND_RED_MONEY_SOUND, true);
			editor.putBoolean(STATIC.IS_OPEN_YES_GRAB_RED_MONEY_SOUND, true);
			editor.putBoolean(STATIC.IS_OPEN_NO_GRAB_RED_MONEY_SOUND, false);
			editor.commit();
			RedMoneyReply redMoneyReply = new RedMoneyReply(getApplicationContext());
			redMoneyReply.insert(new ReplyEntity("谢谢老板,我抢到了m元,@n", ReplyEntity.TYPE_GRAB_RED_MONEY));
			redMoneyReply.insert(new ReplyEntity("就是喜欢 静静抢红包", ReplyEntity.TYPE_GRAB_RED_MONEY)); 
			redMoneyReply.insert(new ReplyEntity("静静抢红包 速度就是块", ReplyEntity.TYPE_GRAB_RED_MONEY)); 
			redMoneyReply.insert(new ReplyEntity("你们的速度太快了", ReplyEntity.TYPE_NO_GRAB_RED_MONEY));
		}
		// 主要是为了那些已经激活了的人 begin
		boolean isActiviteApp = sp.getBoolean(STATIC.IS_ACTIVITE_APP, false);
		if (isActiviteApp) {
			long activiteTime = System.currentTimeMillis();
			Editor editor = sp.edit();
			editor.putLong(STATIC.ACTIVITE_TIME, activiteTime);
			editor.putBoolean(STATIC.IS_ACTIVITE_APP, false);
			editor.commit();
		}
		// 主要是为了那些已经激活了的人 end

	}

}
