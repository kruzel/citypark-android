package com.citypark.service;


import com.citypark.parser.CityParkStartPaymentParser;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Time;

public class StopPaymentTask extends AsyncTask<Void, Void, Boolean> {

	private PaymentListener payLitener = null;
	private Context context;
	private String sessionId;
	private String paymentProviderName;
	private double latitude;
	private double longitude;
	private String operationStatus;	
	
	public StopPaymentTask(PaymentListener payLitener, Context context,
			String sessionId, String paymentProviderName, double latitude,
			double longitude, String operationStatus) {
		super();
		this.payLitener = payLitener;
		this.context = context;
		this.sessionId = sessionId;
		this.paymentProviderName = paymentProviderName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.operationStatus = operationStatus;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		//TODO stop payment request (SMS,..)
		
		Time timeStart = new Time();
		Time timeNow = new Time();
		timeStart.setToNow();
		timeNow.setToNow();
		
        while ((timeNow.toMillis(false) - timeStart.toMillis(false)) < 10000) {
        	try {
				Thread.currentThread();
				Thread.sleep(1000);
				//TODO check payment confirmation
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	timeNow.setToNow();
        }
        
      //update citypark through API on success or failure
        CityParkStartPaymentParser parser = new CityParkStartPaymentParser(context, sessionId, paymentProviderName, latitude, longitude, operationStatus);
        
		return true;
	}
	
	protected void onPostExecute(Boolean success) {
		
		if(payLitener!=null)
			payLitener.PaymentComplete(success);
		
		//TODO error handling
		
     }

}
