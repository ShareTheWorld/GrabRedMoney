package com.android.tool;

import com.android.grabredmoney.R;
import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {
	private Context context;

	public SoundPlayer(Context context) {
		this.context = context;
	}

	public void playSouned(int id) {
		MediaPlayer mp = MediaPlayer.create(context,id);
		if (mp.isPlaying()) {
			mp.stop();
		}
		mp.start();
	}
}
