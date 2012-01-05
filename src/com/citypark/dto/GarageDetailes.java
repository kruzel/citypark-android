package com.citypark.dto;

import com.citypark.constants.GarageAvailability;
import com.citypark.utility.route.PGeoPoint;

public class GarageDetailes{
	String name;
	double latitude;
	double longitude;
	double firstHourPrice;
	double extraQuarterPrice;
	double allDayPrice;
	GarageAvailability availability;
	
	public GarageDetailes() {
		// TODO Auto-generated constructor stub
	}

	public GarageDetailes(GarageDetailes p) {
		super();
		this.name = p.name;
		this.latitude = p.latitude;
		this.longitude = p.longitude;
		this.firstHourPrice = p.firstHourPrice;
		this.extraQuarterPrice = p.extraQuarterPrice;
		this.allDayPrice = p.allDayPrice;
		this.availability = p.availability;
	}

	public double getFirstHourPrice() {
		return firstHourPrice;
	}
	
	public void setFirstHourPrice(double firstHourPrice) {
		this.firstHourPrice = firstHourPrice;
	}

	public double getExtraQuarterPrice() {
		return extraQuarterPrice;
	}

	public void setExtraQuarterPrice(double extraQuarterPrice) {
		this.extraQuarterPrice = extraQuarterPrice;
	}

	public double getAllDayPrice() {
		return allDayPrice;
	}

	public void setAllDayPrice(double allDayPrice) {
		this.allDayPrice = allDayPrice;
	}

	public GarageAvailability getAvailability() {
		return availability;
	}
	public void setAvailability(GarageAvailability availability) {
		this.availability = availability;
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
	public void setName(String name) {
		this.name = name;
	}
	public String getName(){
		return name;
	}
}