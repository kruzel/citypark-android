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
	
	public static int getInt(GarageAvailability avail) {
		switch (avail) {
		case UNKNOWN:
			return -1;
		case BUSY:
			return 0;
		case FREE:
			return 1;
		default:
			return -1;

		}
	}
}
