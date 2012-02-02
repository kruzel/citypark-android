package com.citypark.service;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MapCenterHandler implements LocationListener {
	
	private MapController mapController;
	
	public MapCenterHandler(MapController controller) {
		mapController = controller;
	}

	@Override
	public void onLocationChanged(Location location) {		
		int lat = (int) (location.getLatitude() * 1E6);
		int lng = (int) (location.getLongitude() * 1E6);
		GeoPoint point = new GeoPoint(lat, lng);
		mapController.animateTo(point); //	mapController.setCenter(point);
	}

	@Override
	public void onProviderDisabled(String provider) {	
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
