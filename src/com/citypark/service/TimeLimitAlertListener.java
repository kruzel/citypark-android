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
			
			newIntent.putExtra(context.getString(R.string.reminder_intent), true);
			
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			context.startActivity(newIntent);
		}
	}
}
