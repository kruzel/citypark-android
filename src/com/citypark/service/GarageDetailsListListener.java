package com.citypark.service;

import java.util.List;

import com.citypark.parser.CityParkGaragesParser.GaragePoint;

public interface GarageDetailsListListener {

	/**
	 * Called when a Garage detail fetch complete.
	 * @param successCode
	 */
	public void GarageDetailsFetchComplete(final List<GaragePoint> gpList);
	
}
