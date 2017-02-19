package com.android.grab.activity;

import java.text.DecimalFormat;

import com.android.grab.db.opt.RedMoneyRecord;
import com.android.grabredmoney.R;
import com.android.tool.PayUtil;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class PayActivity extends Activity {
	private TextView mGrabMoneyTV;
	private TextView mTotalGrabMoeny;
	private RadioButton mPayModeRB;
	private TextView mPayMoneyTV;
	private TextView mDescendMoneyTV;
	private TextView mAddMoneyTV;
	private TextView mRefusePayTV;
	private TextView mAllowPayTV;

	private double mGrabTotalMoney=-1;
	private double mGrabMoney=-1;
	private double mPayMoney=0.1;
	private String mPayMoneyDescription;
	private DecimalFormat mDecimalFormat;

	private PayDialogOnClickListener mPayDialogOnClickListener;
	private PayUtil mPayUtil;
	private static Dialog mWaitDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.request_pay_dialog);
		try {
			String record = this.getIntent().getStringExtra("record");
			String item[] = record.split("grabredmoney");
			mGrabMoney = Double.valueOf(item[1]);
			mPayMoney = mGrabMoney * 0.1;
			if (mPayMoney < 0.01) {
				mPayMoney = 0.01;
			}
			mPayMoneyDescription=record;
			RedMoneyRecord rmr=new RedMoneyRecord(getApplicationContext());
			mGrabTotalMoney=rmr.getMoneyInTime(0);
		} catch (Exception e) {
		}

		init();
	}

	public void init() {
		mGrabMoneyTV = (TextView) findViewById(R.id.grab_money);
		mTotalGrabMoeny = (TextView) findViewById(R.id.grab_total_money);
		mPayModeRB = (RadioButton) findViewById(R.id.pay_zfb);
		mPayMoneyTV = (TextView) findViewById(R.id.pay_money);
		mDescendMoneyTV = (TextView) findViewById(R.id.descend_money);
		mAddMoneyTV = (TextView) findViewById(R.id.add_money);
		mRefusePayTV = (TextView) findViewById(R.id.refuse);
		mAllowPayTV = (TextView) findViewById(R.id.allow);
		mDecimalFormat = new DecimalFormat("######.##");// 构造方法的字符格式这里如果小数不足2位,会以0补足.

		DecimalFormat decimalFormat = new DecimalFormat("######");
		mGrabMoneyTV.setText("静静为你抢了" + mDecimalFormat.format(mGrabMoney) + "￥");
		mTotalGrabMoeny.setText(decimalFormat.format(mGrabTotalMoney) + "￥");
		mPayMoneyTV.setText("赏 " + mDecimalFormat.format(mPayMoney) + " 元");
		mPayDialogOnClickListener = new PayDialogOnClickListener();
		mDescendMoneyTV.setOnClickListener(mPayDialogOnClickListener);
		mAddMoneyTV.setOnClickListener(mPayDialogOnClickListener);
		mRefusePayTV.setOnClickListener(mPayDialogOnClickListener);
		mAllowPayTV.setOnClickListener(mPayDialogOnClickListener);

		mPayUtil = new PayUtil();
		mPayUtil.init(this);

	}

	private class PayDialogOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.descend_money:
				mPayMoney = mPayMoney / 1.2;
				if (mPayMoney < 0.05) {
					mPayMoney = mPayMoney * 1.2;
				}
				mPayMoneyTV.setText("赏 " + mDecimalFormat.format(mPayMoney) + " 元");
				break;
			case R.id.add_money:
				mPayMoney = mPayMoney * 1.2;
				mPayMoneyTV.setText("赏 " + mDecimalFormat.format(mPayMoney) + " 元");
				break;
			case R.id.refuse:
				finish();
				break;
			case R.id.allow:
				long oldTime = System.currentTimeMillis();
				boolean payMode = mPayModeRB.isChecked();
				payMode = false;
				// Toast.makeText(PayActivity.this, mPayMoney + " " + payMode +
				// " " + mpayMoneyDescription, 1).show();
				boolean b = mPayUtil.installPayPlug();
				mPayUtil.pay("打赏", mPayMoneyDescription, mPayMoney, payMode);
				Log.i("hongtao.fu", "time=" + (System.currentTimeMillis() - oldTime) + "");
				showDialog("加载中...");
				break;

			default:
				break;
			}

		}

	}

	public static void dismissDialog() {
		if (mWaitDialog != null && mWaitDialog.isShowing()) {
			mWaitDialog.dismiss();
		}
	}

	void showDialog(String message) {
		try {
			mWaitDialog = createLoadingDialog(this, message);
			mWaitDialog.show();
		} catch (Exception e) {
			// 在其他线程调用dialog会报错
		}
	}

	public static Dialog createLoadingDialog(Context context, String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.progress_dialog_layout, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);// 设置加载信息

		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
		
		loadingDialog.setCancelable(false);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
		return loadingDialog;

	}

}
