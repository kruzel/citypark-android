package com.citypark.dto;

public class AreaParkings {
	private int numOfParking;
	private int numOfFreeParking;
	
	public AreaParkings() {
		super();
		this.numOfParking = 0;
		this.numOfFreeParking = 0;
	}
	
	public AreaParkings(AreaParkings ap) {
		this.numOfParking = ap.numOfParking;
		this.numOfFreeParking = ap.numOfFreeParking;
	}

	public int getNumOfParking() {
		return numOfParking;
	}

	public void setNumOfParking(int numOfParking) {
		this.numOfParking = numOfParking;
	}

	public int getNumOfFreeParking() {
		return numOfFreeParking;
	}

	public void setNumOfFreeParking(int numOfFreeParking) {
		this.numOfFreeParking = numOfFreeParking;
	}
	
	

}
