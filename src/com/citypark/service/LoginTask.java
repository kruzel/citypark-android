package com.citypark.service;

import android.content.Context;
import android.os.AsyncTask;
import com.citypark.parser.CityParkLoginParser;

public class LoginTask extends AsyncTask<Void, Void, String> {
	private String email; 
	private String password;
	private Context mAct;
	private LoginListener loginListener;
	
	public LoginTask(String email, String password, Context mAct, LoginListener loginListener) {
		this.email = email;
		this.password = password;
		this.mAct = mAct;
		this.loginListener = loginListener;
	}
	
	@Override
	protected String doInBackground(Void... arg) {
		
		CityParkLoginParser loginParser = new CityParkLoginParser(mAct, email, password);
		
		String sessionId = loginParser.parse();

		return sessionId;
	}
	
	protected void onPostExecute(String sessionId) {
		loginListener.loginComplete(sessionId);
	}
}
