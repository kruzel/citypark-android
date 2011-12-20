package com.citypark.constants;

public enum GarageAvailability {
	FREE, BUSY, UNKNOWN;

	public static GarageAvailability getByValue(int currentFree) {
		switch (currentFree) {
		case -1:
			return UNKNOWN;
		case 0:
			return BUSY;
		default:
			return FREE;

		}
	}
}
