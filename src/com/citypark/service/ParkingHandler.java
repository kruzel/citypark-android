/**
 * 
 */
package com.citypark.service;

import org.osmdroid.util.GeoPoint;

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
import com.citypark.utility.ParkingSessionManager;
import com.citypark.utility.route.PGeoPoint;

/**
 * @author TQJ764
 *
 */
public class ParkingHandler {
	//Get application reference
	private PGeoPoint lastPos = null;
	private Time lastTime = null;
	private Time lastUnparkAckRequest = null;
	private int lastDistFromCar = 0;
	private Boolean approachedCar = false;
	private ParkingSessionManager parking_manager;
	/** preferences file **/
	protected SharedPreferences mPrefs = null;
	
	public ParkingHandler(CityParkApp app) {
		super();		
		parking_manager = new ParkingSessionManager(app.getApplicationContext());
	}
	
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	public void run(Context context, PGeoPoint curPos) {
		
		Time curTime = new Time();
		curTime.setToNow();
		GeoPoint carPos = parking_manager.getLocation();
		int curDistFromCar = curPos.distanceTo(carPos);
		//Toast.makeText(context, "curDistFromCar="+curDistFromCar, Toast.LENGTH_SHORT).show();
		
		//initialize values
		if(lastPos==null || lastTime==null) {
			lastPos = curPos;
			lastTime = curTime;
		}
		
		//update citypark server on location
		if(LoginTask.isLoggedIn()) {
			ReportLocationTask locTask = null;
			long timediff = 0; //millis
			int distDiff = 0; //meters
			float speed = 0; //kmph

			distDiff = lastPos.distanceTo(curPos);
			timediff = curTime.toMillis(true) - lastTime.toMillis(true);
			if(lastTime != curTime)
				speed = distDiff / 1000f / timediff * 3600000f; //kmph
				
			if ((distDiff > 20.0) || (timediff > 30000)) { // position update report
					locTask = new ReportLocationTask(context, LoginTask.getSessionId(), curPos.getLatitudeE6()/1E6, curPos.getLongitudeE6()/1E6);
					locTask.execute();
			} 
			
			//TODO if not parking and speed is < 10 for 5 min, ask user if he is parking - consider false positive
						
			//handle car approach
			if(parking_manager.isParking() && curDistFromCar<40 && (lastDistFromCar-curDistFromCar)>20) { //approaching car, by foot
				approachedCar = true;
				
				Toast.makeText(context, "approaching car...", Toast.LENGTH_SHORT).show();
				
				//TODO if parking approaching the car report new API - reportPotentialParkingRelease (mark as yellow on map)
			}
			
			if(parking_manager.isParking() && curDistFromCar>30 && approachedCar==false) //update only when we're not close to car and haven't approached yet (sensitivity issues)
				lastDistFromCar = curDistFromCar; 
			
			
			if(parking_manager.isParking() && approachedCar==true && curDistFromCar>30) {
					Toast.makeText(context, "identified unpark via car approach alg", Toast.LENGTH_SHORT).show();
					
					if(lastUnparkAckRequest==null) { 
						unpark(context);
						lastUnparkAckRequest = new Time();
						lastUnparkAckRequest.setToNow();
					} else if ((curTime.toMillis(true) - lastUnparkAckRequest.toMillis(true))/1000 > 3600) { //3600 once an hour
						unpark(context);
						lastUnparkAckRequest.setToNow();
					}
			}
			
			//if parking and started driving, close session, and free parking in parking_manager (app in background)
//			if (parking_manager.isParking() && speed > 15) { 
//					Toast.makeText(context, "identified unpark via speed", Toast.LENGTH_SHORT).show();
//					if(lastUnparkAckRequest==null) { 
//						unpark(context);
//						lastUnparkAckRequest = new Time();
//						lastUnparkAckRequest.setToNow();
//					} else if ((curTime.toMillis(true) - lastUnparkAckRequest.toMillis(true))/1000 > 3600) { //3600 once an hour
//						unpark(context);
//						lastUnparkAckRequest.setToNow();
//					}
//			}
			
			lastPos = curPos;
			lastTime = curTime;
			
		}
	}
	
	public void unpark(Context context) {
		approachedCar = false;
		lastDistFromCar = 0;
		Toast.makeText(context, "unpark called", Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent(context,LiveRouteMap.class);
		intent.putExtra(context.getString(R.string.unpark), true);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}

}
