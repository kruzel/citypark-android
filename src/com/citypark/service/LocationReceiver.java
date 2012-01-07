/**
 * 
 */
package com.citypark.service;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.Time;

import com.citypark.CityParkApp;
import com.citypark.PaymentActivity;
import com.citypark.R;
import com.citypark.RouteMap;
import com.citypark.utility.ParkingSessionPersist;
import com.citypark.utility.route.PGeoPoint;

/**
 * @author TQJ764
 *
 */
public class LocationReceiver extends BroadcastReceiver {
	//Get application reference
	private CityParkApp app;
	private PGeoPoint last = null;
	private Time lastTime = null;
	private ParkingSessionPersist parking_manager;
	/** preferences file **/
	protected SharedPreferences mPrefs = null;
	/** Preferences mEditor. **/
	private SharedPreferences.Editor editor = null;
	/** street parking release task **/
	ReportParkingReleaseTask reportParkingReleaseTask;
	
	public LocationReceiver(CityParkApp app) {
		super();		
		this.app = app;
		parking_manager = new ParkingSessionPersist(app.getApplicationContext());
	}
	
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		PGeoPoint current = (PGeoPoint) intent.getExtras().get(context.getString(R.string.point));
		Time curTime = new Time();
		curTime.setToNow();
		
		//update citypark server on location
		if(app != null && app.getSessionId() != null) {
			ReportLocationTask locTask = null;
			long timediff = 0; //millis
			int distDiff = 0; //meters
			if(last !=null && lastTime!=null) {
				distDiff = last.distanceTo(current);
				timediff = curTime.toMillis(true) - lastTime.toMillis(true);
			}
				
			if ((last == null) || (distDiff > 20.0) || (timediff > 30000)) { // position update report
					locTask = new ReportLocationTask(context, app.getSessionId(), current.getLatitudeE6()/1E6, current.getLongitudeE6()/1E6);
					locTask.execute();
					
					last = current;
					lastTime = curTime;
			} 
			
			//TODO if not parking and speed is < 10 for 5 min, ask user if he is parking
			
			//if parking and started driving, close session, and free parking in parking_manager (app in background)
			//TODO this code have a bug
			if (parking_manager.isParking() && last!=null && lastTime!=null && timediff>0) {
				float speed = distDiff / 1000f / timediff / 3600000f; //kmph
				if (speed > 20) { 
					unpark(context);
				}
			}
		}
	}
	
	public void unpark(Context context) {
		Intent intent = new Intent(context,RouteMap.class);
		intent.putExtra(context.getString(R.string.unpark), true);
		context.startActivity(intent);
		
	}

}
