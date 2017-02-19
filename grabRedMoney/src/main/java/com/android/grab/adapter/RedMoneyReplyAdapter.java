package com.android.grab.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.grab.db.opt.RedMoneyReply;
import com.android.grab.entity.ReplyEntity;
import com.android.grabredmoney.R;

public class RedMoneyReplyAdapter extends ExpandableListItemAdapter<ReplyEntity> {

	private Context mContext;
	private LruCache<Integer, Bitmap> mMemoryCache;

	/**
	 * Creates a new ExpandableListItemAdapter with the specified list, or an
	 * empty list if items == null.
	 */
	public RedMoneyReplyAdapter(Context context, List<ReplyEntity> items) {
		super(context, R.layout.red_money_reply_card, R.id.activity_expandablelistitem_card_title,
				R.id.activity_expandablelistitem_card_content, items);
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
	public View getTitleView(int position, View convertView, ViewGroup parent) {
		ViewGroup viewGroup = (ViewGroup) convertView;
		if (viewGroup == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			viewGroup = (ViewGroup) inflater.inflate(R.layout.red_money_reply_title, null);
		}
		TextView replyTitle = (TextView) viewGroup.findViewById(R.id.reply_title);
		// replyTitle.setOnLongClickListener(new
		// DeleteOnLongClickListener(position));
		String strTitle = getItem(position).getReply();
		replyTitle.setText(strTitle);
		RadioButton grabRedMoneyIsOk = (RadioButton) viewGroup.findViewById(R.id.grab_red_money_is_ok);
		RadioButton grabRedMoneyIsKo = (RadioButton) viewGroup.findViewById(R.id.grab_red_money_is_ko);
		if (getItem(position).getType() == ReplyEntity.TYPE_GRAB_RED_MONEY) {
			grabRedMoneyIsOk.setChecked(true);
			grabRedMoneyIsKo.setChecked(false);
		} else {
			grabRedMoneyIsOk.setChecked(false);
			grabRedMoneyIsKo.setChecked(true);
		}
		grabRedMoneyIsOk.setOnCheckedChangeListener(new RadionButtonOnCheckedChangeListener(position));
		View delete=viewGroup.findViewById(R.id.red_money_reply_delete);
		delete.setOnClickListener(new DeleteOnClickListener(position));
		return viewGroup;
	}

	@Override
	public View getContentView(int position, View convertView, ViewGroup parent) {
		ViewGroup viewGroup = (ViewGroup) convertView;
		if (viewGroup == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			viewGroup = (ViewGroup) inflater.inflate(R.layout.red_money_reply_content, null);
		}
		EditText replyContent = (EditText) viewGroup.findViewById(R.id.reply_content);
		String strReply = getItem(position).getReply();
		replyContent.setText(strReply);
		replyContent.setOnFocusChangeListener(new ReplyOnFocusChangeListener(position));
		TextView hidePosition = (TextView) viewGroup.findViewById(R.id.hide_position);
		hidePosition.setText(String.valueOf(position));
		viewGroup.setBackgroundColor(0x84817382);
		return viewGroup;
	}

	@Override
	public void onCollapsing(View contentParent, View titleParent) {
		TextView hidePosition = (TextView) contentParent.findViewById(R.id.hide_position);
		int position = Integer.valueOf(hidePosition.getText().toString());
		EditText replyContent = (EditText) contentParent.findViewById(R.id.reply_content);
		String strReplyContent = replyContent.getText().toString();
		RedMoneyReply redMoneyReply = new RedMoneyReply(mContext);
		RadioButton grabRedMoneyIsOk = (RadioButton) titleParent.findViewById(R.id.grab_red_money_is_ok);
		int type = grabRedMoneyIsOk.isChecked() ? ReplyEntity.TYPE_GRAB_RED_MONEY : ReplyEntity.TYPE_NO_GRAB_RED_MONEY;
		if(getCount()>position){
			redMoneyReply.update(new ReplyEntity(getItem(position).get_id(), strReplyContent, type));
			getItem(position).setReply(strReplyContent);
			getItem(position).setType(type);
		}

		TextView replyTitle = (TextView) titleParent.findViewById(R.id.reply_title);
		replyTitle.setText(strReplyContent);

		View grabRedMoneyResult = titleParent.findViewById(R.id.grab_red_money_result);
		grabRedMoneyResult.setVisibility(View.GONE);
	}

	@Override
	public void onExpanding(View contentParent, View titleParent) {
		TextView replyTitle = (TextView) titleParent.findViewById(R.id.reply_title);
		replyTitle.setText("n=名字,m=金额");
		final View grabRedMoneyResult = titleParent.findViewById(R.id.grab_red_money_result);

		grabRedMoneyResult.setVisibility(View.VISIBLE);

	}

	/**
	 * 当回复的类型改变过后 修改该数据库
	 * 
	 * @author fht
	 *
	 */
	private class RadionButtonOnCheckedChangeListener implements OnCheckedChangeListener {
		private int position;

		public RadionButtonOnCheckedChangeListener(int position) {
			this.position = position;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			int type = isChecked ? ReplyEntity.TYPE_GRAB_RED_MONEY : ReplyEntity.TYPE_NO_GRAB_RED_MONEY;
			RedMoneyReply redMoneyReply = new RedMoneyReply(mContext);
			if(getCount()>position){
				redMoneyReply.updateType(new ReplyEntity(getItem(position).get_id(), null, type));
				getItem(position).setType(type);
			}
		}
	}

	/**
	 * 当回复的文字改变过后 更新数据库 通过监听失去焦点来保存数据
	 * 
	 * @author fht
	 *
	 */
	private class ReplyOnFocusChangeListener implements OnFocusChangeListener {
		private int position;

		public ReplyOnFocusChangeListener(int position) {
			this.position = position;
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			Log.i("hongtao.fu", "********");
			if (!hasFocus) {
				RedMoneyReply redMoneyReply = new RedMoneyReply(mContext);
				EditText replyContent = (EditText) v.findViewById(R.id.reply_content);
				String strContentReply = replyContent.getText().toString();
				if(getCount()>position){
					redMoneyReply.updateReply(new ReplyEntity(getItem(position).get_id(), strContentReply, 0));
					getItem(position).setReply(strContentReply);
				}
			}
		}
	}

	/**
	 * 长按 删除操作
	 */
	private class DeleteOnClickListener implements OnClickListener {
		private int position;

		public DeleteOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if(getCount()<=1) {
				Toast.makeText(mContext, "请保留一条用作回复", Toast.LENGTH_SHORT).show();
				return ;
			}
			Log.i("hongtao.fu","remove="+position);
			RedMoneyReply redMoneyReply=new RedMoneyReply(mContext);
			redMoneyReply.delete(getItem(position).get_id());
			Log.i("hongtao.fu",getItem(position).toString());
			remove(getItem(position));
		}

		

	}

}