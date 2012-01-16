package com.citypark.api.task;

import com.citypark.api.parser.GarageDetailes;

public interface GarageDetailsListener {

	/**
	 * Called when a Garage detail fetch complete.
	 * @param successCode
	 */
	public void GarageDetailsFetchComplete(final GarageDetailes garageDetails);
	
}
