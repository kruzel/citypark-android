package com.citypark;

import android.view.View;

public class PaymentPangoActivity extends PaymentSmsActivity {

	@Override
	public void OnPay(View view) {
		// TODO Auto-generated method stub
		super.OnPay(view);
		
		String msg;
		String pangoSmsNumber = "4500";
		
		if(parking_manager.isPaymentActive()){
			//TODO Start  city  Rehovot  zone 1 vehicle 4343422
			msg = "Stop payment message " + myLicensePlate;
		} else {
			//TODO Stop vehicle car registration number
			 msg = "Start  city  " + myLicensePlate;
		}
		
		sendSMS(pangoSmsNumber, msg);
	}

	@Override
	public String getPaymentMethod() {
		return "Pango";
	}
}
