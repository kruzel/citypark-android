package com.citypark.service;

import com.citypark.parser.CityParkRegisterParser;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;

public class RegistrationTask extends AsyncTask<Void, Void, String> {
	
	private Context mAct;
	private RegisterationListener registrationListener;
	private String email; 
	private String password;
	private String firstName;
	private String familyName;
	private String phoneNumber;
	private String licensePlate;
	private String paymentMethod;

	public RegistrationTask(Context mAct,
			RegisterationListener registrationListener, String email,
			String password, String firstName, String familyName, String phoneNumber,
			String licensePlate, String paymentMethod) {
		super();
		this.mAct = mAct;
		this.registrationListener = registrationListener;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.familyName = familyName;
		this.phoneNumber = phoneNumber;
		this.licensePlate = licensePlate;
		this.paymentMethod = paymentMethod;
	}

	@Override
	protected String doInBackground(Void... params) {
		CityParkRegisterParser parser = new CityParkRegisterParser(mAct, email, password, firstName, familyName, phoneNumber, licensePlate, paymentMethod);
		
		String res = parser.parse();

		return res;
	}

	@Override
	protected void onCancelled() {
		// TODO consider aborting the api call
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		registrationListener.RegistrationComplete(result);
	}
	
}
