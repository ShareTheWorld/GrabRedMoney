package com.android.grab.view;

import com.android.grabredmoney.R;

import android.content.Context;
import android.preference.SwitchPreference;
import android.util.AttributeSet;
import android.widget.ToggleButton;

public class SwitchPreferenceView extends SwitchPreference {
	private ToggleButton toggeButton;
	public SwitchPreferenceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setLayoutResource(R.layout.view_switch_preference);
	}

}
