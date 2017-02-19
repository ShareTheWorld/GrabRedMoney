package com.android.grab.adapter;

import java.text.DecimalFormat;
import java.util.Calendar;

import com.android.grab.db.opt.RedMoneyRecord;
import com.android.grab.entity.RecordEntity;
import com.android.grabredmoney.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RedMoneyRecordAdapter extends ArrayAdapter<RecordEntity> {

	private Context mContext;
	private LruCache<Integer, Bitmap> mMemoryCache;

	public RedMoneyRecordAdapter(Context context, int resource) {
		super(context, resource);
		mContext = context;

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory;
		mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(Integer key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		View view = convertView;
		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.red_money_record_card, parent, false);
			viewHolder = new ViewHolder();
			view.setTag(viewHolder);
			viewHolder.whoRedMoney = (TextView) view
					.findViewById(R.id.who_red_money);
			viewHolder.unreadMark = (ImageView) view
					.findViewById(R.id.unread_mark);
			viewHolder.redMoneyBackgroundImage = (LinearLayout) view
					.findViewById(R.id.red_money_background_image);
			viewHolder.leaveMessage = (TextView) view
					.findViewById(R.id.leave_message);
			viewHolder.replyMessage = (TextView) view
					.findViewById(R.id.reply_message);
			viewHolder.grabRedMoneyTime = (TextView) view
					.findViewById(R.id.grab_red_money_time);
			viewHolder.money = (TextView) view.findViewById(R.id.money);
			viewHolder.rankOrder = (TextView) view
					.findViewById(R.id.rank_order);
		} else {// 这里在做一个listViewItem的回收处理
			viewHolder = (ViewHolder) view.getTag();
			int p = Integer.valueOf(viewHolder.rankOrder.getText().toString());
			if (p < getCount()) {
				final String read_id = String.valueOf(getItem(p).get_id());
				final int read_status = getItem(p).getRead();
				if (read_status == RecordEntity.UNREAD) {
					// 在listView中设置为已读
					getItem(p).setRead(RecordEntity.READ);
					// 在数据库中设置为已读
					new Thread() {
						public void run() {
							RedMoneyRecord record = new RedMoneyRecord(
									getContext());
							record.setReadStatus(read_id, RecordEntity.READ);
						};
					}.start();
				}
			}
		}
		// 设置领取去的红包的名字
		viewHolder.whoRedMoney.setText(getItem(position).getName());

		// 设置显示是否已读的标记
		if (getItem(position).getRead() == RecordEntity.UNREAD) {
			viewHolder.unreadMark.setImageResource(R.drawable.unread_mark);
		} else {
			viewHolder.unreadMark.setImageResource(R.drawable.read_mark);
		}

		// 设置领取的红包的留言 （祝词）
		viewHolder.leaveMessage.setText(getItem(position).getWish());
		// 设置领取额的红包的回复
		viewHolder.replyMessage.setText(String.valueOf(getItem(position)
				.getReply()));
		// 设置背景图片
		// setBackgroundImageView(viewHolder, position);
		// 设置领取红包的时间
		viewHolder.grabRedMoneyTime.setText(getStrTime(getItem(position)
				.getTime()));
		// 设置抢到的金额
		Log.i("hongtao.fu", "money=" + getItem(position).getMoney() + "  ");
		DecimalFormat decimalFormat = new DecimalFormat("####.##");
		viewHolder.money.setText(decimalFormat.format(getItem(position)
				.getMoney()));
		// 设置上_id
		viewHolder.rankOrder.setText(String.valueOf(position));
		return view;
	}

	private static class ViewHolder {
		TextView whoRedMoney;
		ImageView unreadMark;
		LinearLayout redMoneyBackgroundImage;
		TextView leaveMessage;
		TextView replyMessage;
		TextView grabRedMoneyTime;
		TextView money;
		TextView rankOrder;

	}

	/**
	 * 设置图片
	 * 
	 * @param viewHolder
	 * @param position
	 */
	private void setBackgroundImageView(ViewHolder viewHolder, int position) {
	}

	/**
	 * 传入一个 long得到一个时间字符串
	 * 
	 * @param l
	 * @return
	 */
	public String getStrTime(long l) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(l);
		String strTime = calendar.get(Calendar.YEAR) + "-"
				+ (calendar.get(Calendar.MONTH) + 1) + "-"
				+ calendar.get(Calendar.DAY_OF_MONTH) + " "
				+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE) + ":"
				+ calendar.get(Calendar.SECOND);
		return strTime;
	}
}