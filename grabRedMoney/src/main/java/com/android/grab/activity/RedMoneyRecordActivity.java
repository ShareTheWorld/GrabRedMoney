/*
 * Copyright 2013 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.grab.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.grab.adapter.RedMoneyRecordAdapter;
import com.android.grab.db.opt.RedMoneyRecord;
import com.android.grab.entity.RecordEntity;
import com.android.grabredmoney.R;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class RedMoneyRecordActivity extends BaseActivity implements OnDismissCallback {

	private RedMoneyRecordAdapter mRedMoneyRecordAdapter;
	private List<RecordEntity> mRecords;// 数据
	private List<Integer> mNotShowIds = new ArrayList<Integer>();// 用于存放移除
																	// 的数据的_id，这些数据下一次将不会显示

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.red_money_record_activity);

		ListView listView = (ListView) findViewById(R.id.activity_googlecards_listview);

		mRedMoneyRecordAdapter = new RedMoneyRecordAdapter(this, 0);
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				new SwipeDismissAdapter(mRedMoneyRecordAdapter, this));
		swingBottomInAnimationAdapter.setAbsListView(listView);
		listView.setAdapter(swingBottomInAnimationAdapter);
		listView.setOnItemClickListener(new RedMoneyRecordClickListener());
		mRedMoneyRecordAdapter.addAll(getItems());
	}

	private List<RecordEntity> getItems() {
		RedMoneyRecord record = new RedMoneyRecord(getApplicationContext());
		mRecords = record.selectAllData();
		Log.i("hongtao.fu", "records=" + mRecords.size());
		return mRecords;
	}

	@Override
	public void onDismiss(AbsListView listView, int[] reverseSortedPositions) {
		for (int position : reverseSortedPositions) {
			// TODO 删除的时候会调用这个方法
			this.mNotShowIds.add(mRedMoneyRecordAdapter.getItem(position).get_id());
			Log.i("hongtao.fu", "remove=" + position);
			mRedMoneyRecordAdapter.remove(mRedMoneyRecordAdapter.getItem(position));

		}
	}

	/**
	 * ListView的点击事件监听监听
	 * 
	 * @author user
	 *
	 */
	private class RedMoneyRecordClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mRedMoneyRecordAdapter.notifyDataSetChanged();// 处理了当前界面的View重新加载，也把当前界面的数据设置为已读了
		}
	}

	@Override
	public void onBackPressed() {
		if (this.mNotShowIds.size() > 0) {
			float scale = getResources().getDisplayMetrics().density;
			int size = (int) (50 * scale + 0.5f);// dp 转 px
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grab_logo);
			bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
			AlertDialog.Builder builder = new Builder(this);
			builder.setIcon(new BitmapDrawable(bitmap));
			builder.setTitle("移除红包");
			builder.setMessage("是否保存？");
			builder.setPositiveButton("保存", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					RedMoneyRecord record = new RedMoneyRecord(getApplicationContext());
					record.setShowStatusNotShow(mNotShowIds);
					mNotShowIds = null;
					Toast.makeText(getApplicationContext(), "移除的红包将不会再显示", Toast.LENGTH_SHORT).show();
					RedMoneyRecordActivity.super.onBackPressed();
				}
			});
			builder.setNeutralButton("不保存", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(getApplicationContext(), "取消保存", Toast.LENGTH_SHORT).show();
					RedMoneyRecordActivity.super.onBackPressed();
				}
			});
			builder.setNegativeButton("取消", null);
			AlertDialog dialog = builder.create();
			dialog.show();
		} else {
			super.onBackPressed();
		}
	}

}
