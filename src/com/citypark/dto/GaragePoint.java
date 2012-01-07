package com.citypark.dto;

import com.citypark.constants.GarageAvailability;
import com.citypark.utility.route.PGeoPoint;

public class GaragePoint{
	int id;
	double latitude;
	double longitude;
	double price;
	String name;
	GarageAvailability availability;
	
	public GaragePoint() {
		// TODO Auto-generated constructor stub
	}
	
	public int getId() {
		return id;
	}
	
	public String getIdString() {
		return Integer.toString(id);
	}

	public void setId(int id) {
		this.id = id;
	}

	public GarageAvailability getAvailability() {
		return availability;
	}

	public void setAvailability(GarageAvailability availability) {
		this.availability = availability;
	}

	public GaragePoint(GaragePoint p) {
		this.id = p.id;
		this.latitude = p.latitude;
		this.longitude = p.longitude;
		this.price = p.price;
		this.name = p.name;
		this.availability = p.availability;
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
	public void setPrice(double price) {
		this.price = price;
	}
	public double getPrice(){
		return price;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName(){
		return name;
	}
}