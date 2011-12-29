package com.citypark.utility;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
	
	Calendar paymentSmsTime = Calendar.getInstance();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
		smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}

		// show first message
//		Toast toast = Toast.makeText(context,
//		"Received SMS: " + smsMessage[0].getMessageBody(), Toast.LENGTH_LONG);
//		toast.show();
		
		//TODO iterate through returnedSMSs and check for payment acknowledge received after paymentSmsTime
		//TODO should be implemented for Pango and CelOpark in derived classes
	}

}

