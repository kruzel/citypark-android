package com.citypark;

import android.view.View;

public class PaymentCelOParkActivity extends PaymentSmsActivity {

	@Override
	public void OnPay(View view) {
		// TODO Auto-generated method stub
		super.OnPay(view);
		
		String msg;
		String pangoSmsNumber = "0000";
		
		if(parking_manager.isPaymentActive()){
			//TODO הפסק
			msg = myLicensePlate.getText().toString() + " הפסק";
		} else {
			//TODO התחל רחובות אזור 1 רכב 7107864
			 msg = null;
			 if(myLicensePlate.length()>0)
					 msg += myLicensePlate.getText().toString() + " רכב ";
			 if (parkingZone.length() > 0)
				 msg += parkingZone.getText().toString() + " אזור ";
			 msg = parkingCity.getText().toString() + "התחל ";
		}
		
		//sendSMS(pangoSmsNumber, msg);
		PaymentComplete(true);
	}
	
	@Override
	protected Boolean parseResponse(String msg) {
		return (msg.contains(parkingCity.getText().toString()));
		//TODO set time limit according to response
	}

	@Override
	public String getPaymentMethod() {
		return "CelOpark";
	}

}
