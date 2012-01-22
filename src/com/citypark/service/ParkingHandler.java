/**
 * 
 */
package com.citypark.service;

import org.osmdroid.util.GeoPoint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
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
	private PGeoPoint lastSpeedPos = null;
	private Time lastSpeedTime = null;
	private int lastDistFromCar = 0;
	private Boolean approachedCar = false;
	private Boolean speedhecked = false;
	private Time lastLocationReport = new Time();
	
	private ParkingSessionManager parking_manager;
	/** preferences file **/
	protected SharedPreferences mPrefs = null;
	
	public ParkingHandler(CityParkApp app) {
		super();		
		parking_manager = new ParkingSessionManager(app.getApplicationContext());
		lastLocationReport.setToNow();
	}
	
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	public void run(Context context, Location location) {			
		
		PGeoPoint curPos = new PGeoPoint(location);
		
		Time curTime = new Time();
		curTime.setToNow();
		GeoPoint carPos = parking_manager.getGeoPoint();
		int curDistFromCar = curPos.distanceTo(carPos);
		//Toast.makeText(context, "curDistFromCar="+curDistFromCar, Toast.LENGTH_SHORT).show();
		
		//initialize values
		if(lastPos==null || lastTime==null) {
			lastPos = curPos;
			lastTime = curTime;
			lastSpeedPos = curPos;
			lastSpeedTime = curTime;
		}
		
			ReportLocationTask locTask = null;
			long timediff = 0; //millis
			int distDiff = 0; //meters
			float speed = 0; //kmph

			distDiff = lastPos.distanceTo(curPos);
			timediff = curTime.toMillis(true) - lastTime.toMillis(true);
			
			//update citypark server on location
			if ((distDiff > 20.0) || (curTime.toMillis(true)-lastLocationReport.toMillis(true) > 30000)) { 
				if(LoginTask.isLoggedIn()) { 
					locTask = new ReportLocationTask(context, LoginTask.getSessionId(), curPos.getLatitudeE6()/1E6, curPos.getLongitudeE6()/1E6);
					locTask.execute();
					lastLocationReport.setToNow();
				}
			} 	
			
			if(parking_manager.isParking() && location.hasAccuracy() && location.getAccuracy()<20){ 
				//if parking and started driving, close session, and free parking in parking_manager (app in background)
				if(location.hasAccuracy() && location.getAccuracy()<15 && location.hasSpeed()) //prefer to work with sensors derived speed value
					speed = location.getSpeed();
				if(curPos.distanceTo(lastSpeedPos)>40 || (curTime.toMillis(true)-lastSpeedTime.toMillis(true))>5000) { //otherwise calculate 
					speed = distDiff / 1000f / timediff * 3600000f; //kmph
					lastSpeedPos = curPos;
					lastSpeedTime = curTime;
				}	
				
				if (speed > 15 && !speedhecked) { 
						Toast.makeText(context, "identified unpark via speed", Toast.LENGTH_SHORT).show();
						
						unpark(context);
						
						//don't check speed criteria if already did
						speedhecked=true;
				}
					
				//1. detect user getting far away from the parking car
				if(curDistFromCar>30 && approachedCar==false) 
					lastDistFromCar = curDistFromCar; 
				
				//2. detect user approaching parking car
				if(curDistFromCar<40 && (lastDistFromCar-curDistFromCar)>20 && approachedCar==false) { //approaching car, by foot
					approachedCar = true;
					
					Toast.makeText(context, "approaching car...", Toast.LENGTH_SHORT).show();
					
					//TODO if parking approaching the car report new API - reportPotentialParkingRelease (mark as yellow on map)
				}
				
				//3. detect user started driving car after approached it
				if(approachedCar==true && curDistFromCar>30) {
						Toast.makeText(context, "identified unpark via car approach alg", Toast.LENGTH_SHORT).show();
						
						unpark(context);
						approachedCar=false; //restart state machine
				}
			} 			
			//TODO else, if not parking and speed is < 10 for 5 min, ask user if he is parking - consider false positive

			lastPos = curPos;
			lastTime = curTime;
	}
	
	public void unpark(Context context) {
		approachedCar = false;
		speedhecked=false;
		lastDistFromCar = 0;
		Toast.makeText(context, "unpark called", Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent(context,LiveRouteMap.class);
		intent.putExtra(context.getString(R.string.unpark), true);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}

}
