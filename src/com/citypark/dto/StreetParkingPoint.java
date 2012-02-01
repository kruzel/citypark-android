package com.citypark.dto;

import com.google.android.maps.GeoPoint;

public class StreetParkingPoint{
	double latitude;
	double longitude;
	
	public StreetParkingPoint() {
		// TODO Auto-generated constructor stub
	}

	public StreetParkingPoint(StreetParkingPoint p) {
		this.latitude = p.latitude;
		this.longitude = p.longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public GeoPoint getGeoPoint(){
		return new GeoPoint((int)(latitude*1E6),(int)(longitude*1E6));
	}

}