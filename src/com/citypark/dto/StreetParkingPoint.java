package com.citypark.dto;

import com.citypark.utility.route.PGeoPoint;

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
	public PGeoPoint getPGeoPoint(){
		return new PGeoPoint(latitude,longitude);
	}

}