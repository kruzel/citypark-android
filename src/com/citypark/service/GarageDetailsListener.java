package com.citypark.service;

import com.citypark.parser.GarageDetailes;

public interface GarageDetailsListener {

	/**
	 * Called when a Garage detail fetch complete.
	 * @param successCode
	 */
	public void GarageDetailsFetchComplete(final GarageDetailes garageDetails);
	
}
