package com.android.grab.activity;

import com.android.grabredmoney.R;
import com.android.grabredmoney.STATIC;
import com.android.tool.SoundPlayer;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;

public class GrabRedMoneyPreferenceActivity extends PreferenceActivity {
	private SwitchPreference grabRedMoneyMode;
	private SwitchPreference isReplyYesGrabRedMoney;
	private SwitchPreference isReplyNoGrabRedMoney;
	private SwitchPreference isOpenFindRedMoneySound;
	private SwitchPreference isOpenYesGrabRedMoneySound;
	private SwitchPreference isOpenNoGrabRedMoneySound;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.setContentView(R.xml.grab_red_money_preference_activity);
		addPreferencesFromResource(R.xml.grab_red_money_preference_activity);

		grabRedMoneyMode = (SwitchPreference) this.findPreference(STATIC.GRAB_RED_MONEY_MODE);
		isReplyYesGrabRedMoney = (SwitchPreference) this.findPreference(STATIC.GRAB_RED_MONEY_YES_REPLY);
		isReplyNoGrabRedMoney = (SwitchPreference) this.findPreference(STATIC.GRAB_RED_MONEY_NO_REPLY);
		isOpenFindRedMoneySound = (SwitchPreference) this.findPreference(STATIC.IS_OPEN_FIND_RED_MONEY_SOUND);
		isOpenYesGrabRedMoneySound = (SwitchPreference) this.findPreference(STATIC.IS_OPEN_YES_GRAB_RED_MONEY_SOUND);
		isOpenNoGrabRedMoneySound = (SwitchPreference) this.findPreference(STATIC.IS_OPEN_NO_GRAB_RED_MONEY_SOUND);
		isOpenFindRedMoneySound.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (newValue instanceof Boolean) {
					boolean b = (Boolean) newValue;
					if (b) {
						SoundPlayer sp = new SoundPlayer(getApplicationContext());
						sp.playSouned(R.raw.find_red_money_sound);
					}
				}
				return true;
			}
		});
		isOpenYesGrabRedMoneySound.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (newValue instanceof Boolean) {
					boolean b = (Boolean) newValue;
					if (b) {
						SoundPlayer sp = new SoundPlayer(getApplicationContext());
						sp.playSouned(R.raw.grab_red_money_result_yes);
					}
				}
				return true;
			}
		});
		isOpenNoGrabRedMoneySound.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (newValue instanceof Boolean) {
					boolean b = (Boolean) newValue;
					if (b) {
						SoundPlayer sp = new SoundPlayer(getApplicationContext());
						sp.playSouned(R.raw.grab_red_money_result_no);
					}
				}
				return true;
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.grab_red_money_prefercence_activity_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_help:
			Intent intent=new Intent(this, HelpActivity.class);
			this.startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

}
