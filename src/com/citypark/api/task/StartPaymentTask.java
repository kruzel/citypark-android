package com.citypark.api.task;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.widget.Toast;

import com.citypark.api.parser.CityParkStartPaymentParser;

public class StartPaymentTask extends AsyncTask<Void, Void, Boolean> {
	private Context context;
	private String sessionId;
	private String paymentProviderName;
	private double latitude;
	private double longitude;
	private String operationStatus;	

	public StartPaymentTask(Context context,
			String sessionId, String paymentProviderName, double latitude,
			double longitude, String operationStatus) {
		super();
		this.context = context;
		this.sessionId = sessionId;
		this.paymentProviderName = paymentProviderName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.operationStatus = operationStatus;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
        //update citypark through API on success or failure
        CityParkStartPaymentParser parser = new CityParkStartPaymentParser(context, sessionId, paymentProviderName, latitude, longitude, operationStatus);
        parser.parse();
        
		return true;
	}

}
