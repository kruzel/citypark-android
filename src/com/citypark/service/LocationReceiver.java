/**
 * 
 */
package com.citypark.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.Time;
import android.widget.Toast;

import com.citypark.CityParkApp;
import com.citypark.LiveRouteMap;
import com.citypark.R;
import com.citypark.api.task.LoginTask;
import com.citypark.api.task.ReportLocationTask;
import com.citypark.utility.ParkingSessionPersist;
import com.citypark.utility.route.PGeoPoint;

/**
 * @author TQJ764
 *
 */
public class LocationReceiver extends BroadcastReceiver {
	//Get application reference
	private PGeoPoint last = null;
	private Time lastTime = null;
	private Time lastUnparkAckRequest = null;
	private ParkingSessionPersist parking_manager;
	/** preferences file **/
	protected SharedPreferences mPrefs = null;
	
	public LocationReceiver(CityParkApp app) {
		super();		
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
		if(LoginTask.isLoggedIn()) {
			ReportLocationTask locTask = null;
			long timediff = 0; //millis
			int distDiff = 0; //meters
			if(last !=null && lastTime!=null) {
				distDiff = last.distanceTo(current);
				timediff = curTime.toMillis(true) - lastTime.toMillis(true);
			}
				
			if ((last == null) || (distDiff > 20.0) || (timediff > 30000)) { // position update report
					locTask = new ReportLocationTask(context, LoginTask.getSessionId(), current.getLatitudeE6()/1E6, current.getLongitudeE6()/1E6);
					locTask.execute();
					
					last = current;
					lastTime = curTime;
			} 
			
			//TODO if not parking and speed is < 10 for 5 min, ask user if he is parking
			
			//TODO if parking and getting close the car report new API - reportEarlyParkingRelease (mark as yellow on map)
			
			//if parking and started driving, close session, and free parking in parking_manager (app in background)
			//TODO we need to ask not more then once in an hour.
			//		add soft sound to get attention
			//		dismiss automatically without changing to unpark (timer of 10 sec)
			if (parking_manager.isParking() && last!=null && lastTime!=null && timediff>0) {
				float speed = distDiff / 1000f / timediff * 3600000f; //kmph
				if (speed > 10) { 
					if(lastUnparkAckRequest==null || (curTime.toMillis(true) - lastUnparkAckRequest.toMillis(true))/1000 > 3600) { 
						unpark(context);
						lastUnparkAckRequest.setToNow();
					}
				}
			}
		}
	}
	
	public void unpark(Context context) {
		Intent intent = new Intent(context,LiveRouteMap.class);
		intent.putExtra(context.getString(R.string.unpark), true);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}

}
