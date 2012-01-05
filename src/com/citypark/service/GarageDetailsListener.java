package com.citypark.service;

import com.citypark.parser.CityParkGaragesByIdParser.GarageDetailes;

public interface GarageDetailsListener {

	/**
	 * Called when a Garage detail fetch complete.
	 * @param successCode
	 */
	public void GarageDetailsFetchComplete(final GarageDetailes garageDetails);
	
}
