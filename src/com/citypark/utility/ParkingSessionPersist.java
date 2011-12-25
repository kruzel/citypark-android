package com.citypark.utility;

import org.osmdroid.util.GeoPoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;

import com.citypark.constants.CityParkConsts;

/**
 * A class to handle parking a car.
 * 
 * Stores and retrieves parking location in sharedpreferences.
 * 
 * This file is part of BikeRoute.
 * 
 * Copyright (C) 2011  Jonathan Gray
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * @author jono@nanosheep.net
 * 
 */

public class ParkingSessionPersist {
		
	/** Shared preferences object. **/
	private final SharedPreferences settings;
	/** Preferences mEditor. **/
	private final SharedPreferences.Editor editor;

	public ParkingSessionPersist(final Context context) {
		settings = context.getSharedPreferences(CityParkConsts.PREFS_NAME, 0);
		editor = settings.edit();
	}

	public final void park(final int latitude, final int longitude) {
		park(new GeoPoint(latitude, longitude));
	}

	/**
	 * 'Park' at the location given. Set values for lng & lat in
	 * sharedpreferences.
	 * 
	 * @param p Location to park at.
	 */

	public final void park(final GeoPoint p) {
		editor.putInt(CityParkConsts.LAT, p.getLatitudeE6());
		editor.putInt(CityParkConsts.LNG, p.getLongitudeE6());
		editor.commit();
	}

	/**
	 * Unpark from the current parking location.
	 */

	public final void unPark() {
		editor.remove(CityParkConsts.LAT);
		editor.remove(CityParkConsts.LNG);
		editor.commit();
	}

	/**
	 * Check if parking is set.
	 * 
	 * @return a boolean indicating if values are set for lat & lng
	 */

	public final boolean isParked() {
		return settings.contains(CityParkConsts.LAT) && settings.contains(CityParkConsts.LNG);
	}

	/**
	 * Get the location of the current parking spot.
	 * 
	 * @return a Geopoint for the location. Sets lat & lng to -1 if values not
	 *         found.
	 */

	public final GeoPoint getLocation() {
		final int lat = settings.getInt(CityParkConsts.LAT, -1);
		final int lng = settings.getInt(CityParkConsts.LNG, -1);
		return new GeoPoint(lat, lng);
	}
	
	/**
	 * set payment start time.
	 */
	public final void setPaymentStart() {
		Time now = new Time();
		now.setToNow();
		editor.putString(CityParkConsts.PAYMENT_START_TIME, now.toString());
		editor.commit();
	}

	/**
	 * unset payment.
	 */

	public final void setPaymentEnd() {
		editor.remove(CityParkConsts.PAYMENT_START_TIME);
		editor.commit();
	}

	/**
	 * Check if parking is active.
	 * 
	 * @return a boolean indicating if values are set payment
	 */

	public final boolean isPaymentActive() {
		return settings.contains(CityParkConsts.PAYMENT_START_TIME);
	}
	
	/**
	 * set payment start time.
	 */
	public final void setReminder(Time time) {
		editor.putString(CityParkConsts.ALARM_TIME, time.toString());
		editor.commit();
	}

	/**
	 * unset payment.
	 */

	public final void stopReminder() {
		editor.remove(CityParkConsts.ALARM_TIME);
		editor.commit();
	}

	/**
	 * Check if parking is active.
	 * 
	 * @return a boolean indicating if values are set payment
	 */

	public final boolean isReminderActive() {
		return settings.contains(CityParkConsts.ALARM_TIME);
	}
}
