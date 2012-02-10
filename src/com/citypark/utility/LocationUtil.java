package com.citypark.utility;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.citypark.ParkingMap;

public class LocationUtil {

	public static Location getCurrentLocation(Context ctx) {
		LocationManager mLocationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		
		Location self = null;
		if (self == null) {
			try {
				self = mLocationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			} catch (Exception ex) {
				Log.w(ParkingMap.class.toString(),
						"Could not get GPS location, " + ex.getMessage());

			}
		}
		if (self == null) {
			try {
				self = mLocationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			} catch (Exception ex) {
				Log.w(ParkingMap.class.toString(),
						"Could not get NETWORK location, " + ex.getMessage());
			}
		}
		return self;
	}
}
