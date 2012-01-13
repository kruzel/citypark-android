package com.citypark.service;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

import com.citypark.PaymentActivity;
import com.citypark.PaymentCelOParkActivity;
import com.citypark.PaymentPangoActivity;
import com.citypark.R;

public class TimeLimitAlertListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, R.string.time_limit_reached, Toast.LENGTH_SHORT)
				.show();

		String payMethod = intent.getStringExtra(context.getString(R.string.payment_method));

		// send off alarm sound
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			// alert is null, using backup
			alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) { // I can't see this ever being null (as always
									// have a default notification) but just in
									// case
				// alert backup is null, using 2nd backup
				alert = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}

		MediaPlayer mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(context, alert);

			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// open the right activity according to the payment method
		Intent newIntent;
		if(payMethod!=null) {
			if (payMethod.contains("Pango")) {
				newIntent = new Intent(context, PaymentPangoActivity.class);
			} else if (payMethod.contains("CelOpark")) {
				newIntent = new Intent(context, PaymentCelOParkActivity.class);
			} else {
				newIntent = new Intent(context, PaymentActivity.class);
			}
			
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			context.startActivity(intent);
		}
	}
}
