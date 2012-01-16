package com.citypark.api.task;


import com.citypark.api.parser.CityParkStartPaymentParser;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Time;

public class StopPaymentTask extends AsyncTask<Void, Void, Boolean> {
	private Context context;
	private String sessionId;
	private String paymentProviderName;
	private double latitude;
	private double longitude;
	private String operationStatus;	
	
	public StopPaymentTask(Context context,
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
