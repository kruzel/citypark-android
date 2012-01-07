package com.citypark.service;

import java.util.List;

<<<<<<< HEAD
import com.citypark.dto.GarageData;

=======
import com.citypark.dto.GaragePoint;
>>>>>>> f198459639ab7663169e9245ccb8b0d08243ee19

public interface GarageDetailsListListener {

	/**
	 * Called when a Garage detail fetch complete.
	 * @param successCode
	 */
	public void GarageDetailsFetchComplete(final List<GarageData> gpList);
	
}
