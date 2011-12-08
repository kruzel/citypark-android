package com.citypark.service;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.citypark.R;
import com.citypark.parser.CityParkLoginParser;
import com.citypark.utility.ParkingSessionPersist;

public class LoginTask extends AsyncTask<Void, Void, String> {
	private String email; 
	private String password;
	private Context mAct;
	private ParkingSessionPersist parking_manager;
	
	public LoginTask(String email, String password, Context mAct, ParkingSessionPersist parking_manager) {
		this.email = email;
		this.password = password;
		this.mAct = mAct;
		this.parking_manager = parking_manager;
	}
	
	@Override
	protected String doInBackground(Void... arg) {
		final String query = mAct.getString(R.string.citypark_login_api) + "?username="+ email + "&password=" + password;
		CityParkLoginParser loginParser = new CityParkLoginParser(query);
		
		String sessionId = loginParser.parse();

		return sessionId;
	}
	
	protected void onPostExecute(String sessionId) {
		if(sessionId == null) {
			Toast.makeText(mAct, mAct.getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
		} else {
			parking_manager.setCPSessionId(sessionId);
		}
	}
}
