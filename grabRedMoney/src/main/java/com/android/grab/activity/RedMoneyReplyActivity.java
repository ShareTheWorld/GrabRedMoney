package com.android.grab.activity;

import java.util.List;

import com.android.grab.adapter.RedMoneyReplyAdapter;
import com.android.grab.db.opt.RedMoneyReply;
import com.android.grab.entity.ReplyEntity;
import com.android.grabredmoney.R;
import com.haarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class RedMoneyReplyActivity extends BaseActivity {

	private RedMoneyReplyAdapter mRedMoneyReplyAdapter;
	private ListView mListView;
	private List<ReplyEntity> mListReplys;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.red_money_reply_activity);
		mListView = (ListView) this.findViewById(R.id.red_money_reply_listview);

		mRedMoneyReplyAdapter = new RedMoneyReplyAdapter(this, getItems());
		AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(mRedMoneyReplyAdapter);
		alphaInAnimationAdapter.setAbsListView(mListView);
		mListView.setAdapter(alphaInAnimationAdapter);
	}

	public List<ReplyEntity> getItems() {
		RedMoneyReply redMoneyReply = new RedMoneyReply(getApplicationContext());
		List<ReplyEntity> replys = redMoneyReply.selectAllData();
		this.mListReplys = replys;
		return replys;
	}

	public void replyAdd(View view) {
		RedMoneyReply redMoneyReply = new RedMoneyReply(getApplicationContext());
		ReplyEntity replyEntity = new ReplyEntity("", ReplyEntity.TYPE_GRAB_RED_MONEY);
		long l = redMoneyReply.insert(replyEntity);
		if (l < 0) {
			Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
		Log.i("hongtao.fu", "long=" + l);
		replyEntity.set_id((int) l);
		mRedMoneyReplyAdapter.add(replyEntity);
		mRedMoneyReplyAdapter.notifyDataSetChanged();
		mListView.smoothScrollToPosition(mRedMoneyReplyAdapter.getCount());
	}

	@Override
	public void onBackPressed() {
		// EditText是根据失去焦点进行保存的，这里是让最后编辑的EditText失去焦点 把数据保存
		View v = mListView.findFocus();
		v.setFocusable(false);
		super.onBackPressed();
	}

}
