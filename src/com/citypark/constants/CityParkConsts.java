/**
 * 
 */
package com.citypark.constants;

import android.view.Menu;

public final class CityParkConsts {
		
	/** Osmdroid consts. **/
	public static final int MENU_MY_LOCATION = Menu.FIRST;
    public static final int MENU_MAP_MODE = MENU_MY_LOCATION + 1;
    public static final int MENU_SAMPLES = MENU_MAP_MODE + 1;
    public static final int MENU_ABOUT = MENU_SAMPLES + 1;

    public static final int NOT_SET = Integer.MIN_VALUE;
	
	/** Pi/180 for converting degrees - radians. **/
	public static final double PI_180 = Math.PI / 180;
	/** Radius of the earth for degrees - miles calculations. **/
	public static final double EARTH_RADIUS = 3960.0;
	
	/** parking consts **/
	public static final double MEDIUM_PARKING_LIMIT = 33; //% probability
	public static final double FAST_PARKING_LIMIT = 67; //% probability
	
	/** Router Consts. **/
	/** Google. **/
	public static final String G = "Google";
	/** MapQuest. **/
	public static final String MQ = "MapQuest";
	/** CycleStreets. **/
	public static final String CS = "CycleStreets";
	
	/** registration **/
	public static final String USER_ALREADY_EXISTS = "USER ALREADY EXISTS";
	
	/** Initial zoom level. */
	public static final int ZOOM_THRESHOLD = 16;
	
	public static final Long OVERLAY_UPDATE_INTERVAL = 1000L;
	
	public static final String GARAGE_ID = "garageId";
	/** Radius to return markers within. **/
	public static final int RADIUS = 500;
	public static final String LATITUDE = "LATITUDE";
	public static final String LONGITUDE = "LONGITUDE";
	public static final int MILLION = 1000000;

	private CityParkConsts() { }
}
