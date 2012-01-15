package com.citypark.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.citypark.R;
import com.citypark.parser.CityParkLoginParser;

public class LoginTask extends AsyncTask<Void, Void, String> {
	//set once on app init
	private static String strEmail = null; 
	private static String strPassword = null;
	private static Context mAct = null;
	
	//global app variables
	private static String sessionId = null;
	private static Boolean isLoginInProgress = false;
	
	//per login listener
	private LoginListener loginListener;
	
	protected LoginTask(LoginListener loginListener) {
		this.loginListener = loginListener;
	}
	
	@Override
	protected String doInBackground(Void... arg) {
		
		CityParkLoginParser loginParser = new CityParkLoginParser(mAct, strEmail, strPassword);
		
		String sessionId = loginParser.parse();

		return sessionId;
	}
	
	protected void onPostExecute(String sessionId) {
		loginComplete(sessionId);
	}
	
	public static Boolean init(Context context) {
		LoginTask.mAct = context;
		
		SharedPreferences mPrefs = context.getSharedPreferences(context.getString(R.string.prefs_name), Context.MODE_PRIVATE);
		LoginTask.strEmail = mPrefs.getString("email", null);
		LoginTask.strPassword = mPrefs.getString("password", null);
		
		return true;
	}
	
	public static Boolean reload() {		
		SharedPreferences mPrefs = LoginTask.mAct.getSharedPreferences(LoginTask.mAct.getString(R.string.prefs_name), Context.MODE_PRIVATE);
		LoginTask.strEmail = mPrefs.getString("email", null);
		LoginTask.strPassword = mPrefs.getString("password", null);
		
		return true;
	}
	
	public static void login(LoginListener loginListener) {
		if(getSessionId() == null && !isLoginInProgress) {       	            
        	if(!isRegistered()) {
        		//user is not registered, register now (on the listener)
        		isLoginInProgress = false;
        		sessionId = null;
        		if(loginListener!=null)
        			loginListener.loginFailed();
	        }
	        else {
	        	isLoginInProgress = true;
	        	sessionId = null;
	        	LoginTask loginTask = new LoginTask(loginListener);
	          	loginTask.execute((Void[])null);
	        }
		}
	
		return;
	}
	
	private void loginComplete(String sessionId) {
		if(sessionId==null)
			if(loginListener!=null)
				loginListener.loginFailed();
		else {
			LoginTask.setSessionId(sessionId);
			if(loginListener!=null)
				loginListener.loginComplete(sessionId);
		}
		isLoginInProgress = false;
	}

	public static Boolean isLoggedIn() {
		return (getSessionId() != null);
	}
	
	public static String getSessionId() {
		return sessionId;
	}

	public static void setSessionId(String sessionId) {
		LoginTask.sessionId = sessionId;
	}
	
	public static Boolean isRegistered() {
		if((strEmail == null) || (strEmail.length() == 0) || (strPassword == null) ||(strPassword.length() == 0) ) 
			return false;
		else
			return true;
		
	}
}
