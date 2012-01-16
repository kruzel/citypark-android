package com.citypark.api.task;

import java.util.List;

import com.citypark.dto.GarageData;


public interface GarageDetailsListListener {

	/**
	 * Called when a Garage detail fetch complete.
	 * @param successCode
	 */
	public void GarageDetailsFetchComplete(final List<GarageData> gpList);
	
}
