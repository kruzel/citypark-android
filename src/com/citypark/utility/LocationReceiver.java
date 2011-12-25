/**
 * 
 */
package com.citypark.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.Time;

import com.citypark.CityParkApp;
import com.citypark.R;
import com.citypark.constants.CityParkConsts;
import com.citypark.service.ReportLocationTask;
import com.citypark.utility.route.PGeoPoint;

/**
 * @author TQJ764
 *
 */
public class LocationReceiver extends BroadcastReceiver {
	//Get application reference
	private CityParkApp app;
	private PGeoPoint last = null;
	private Time lastTime = new Time();
	/** preferences file **/
	protected SharedPreferences mPrefs = null;
	/** Preferences mEditor. **/
	private SharedPreferences.Editor editor = null;
	
	public LocationReceiver(CityParkApp app) {
		super();		
		this.app = app;
	}
	
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		PGeoPoint current = (PGeoPoint) intent.getExtras().get(context.getString(R.string.point));
		Time curTime = new Time();
		
		//update citypark server on location
		if(app != null && app.getSessionId() != null) {
			ReportLocationTask locTask = null;
			if (last == null) { // report first position
				locTask = new ReportLocationTask(context, app.getSessionId(), current.getLatitudeE6()/1E6, current.getLongitudeE6()/1E6);
				locTask.execute();
			} else if ((last.distanceTo(current) > 20.0) || (curTime.toMillis(true) - lastTime.toMillis(true) > 30000)) {  //report progress, distance in meters
					locTask = new ReportLocationTask(context, app.getSessionId(), current.getLatitudeE6()/1E6, current.getLongitudeE6()/1E6);
					locTask.execute();
			} 
			
			//if started driving with valid sessionId, close session, and free parking in parking_manager (app in background)
			if (app.getSessionId() != null) {
				float speed = current.distanceTo(last) / 1000 / ((curTime.toMillis(true) - lastTime.toMillis(true)) / 3600000); //kmph
				if (speed > 12) { 
					app.setSessionId(null);
					
					//free parking
					if(mPrefs == null)
						mPrefs = app.getSharedPreferences(app.getString(R.string.prefs_name), app.MODE_PRIVATE);
					if(editor == null)
						editor = mPrefs.edit();
					editor.remove(CityParkConsts.LAT);
					editor.remove(CityParkConsts.LNG);
					editor.commit();
				}
			}
		}
		
		last = current;
		lastTime = curTime;
	}

}
