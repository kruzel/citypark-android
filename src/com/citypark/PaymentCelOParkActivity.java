package com.citypark;

import android.view.View;

public class PaymentCelOParkActivity extends PaymentSmsActivity {

	@Override
	public void OnPay(View view) {
		// TODO Auto-generated method stub
		super.OnPay(view);
		
		String msg;
		String pangoSmsNumber = "4500";
		
		if(parking_manager.isPaymentActive()){
			//TODO send stop payment SMS
			msg = "stop payment message " + myLicensePlate;
		} else {
			//TODO send start payment SMS
			 msg = "start payment message " + myLicensePlate;
		}
		
		sendSMS(pangoSmsNumber, msg);
	}

	@Override
	public String getPaymentMethod() {
		return "CelOpark";
	}

	@Override
	protected Boolean parseResponse(String msg) {
		// TODO Auto-generated method stub
		return null;
	}

}
