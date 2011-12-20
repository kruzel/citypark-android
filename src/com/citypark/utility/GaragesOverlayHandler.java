package com.citypark.utility;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;

import com.citypark.R;
import com.citypark.constants.CityParkConsts;
import com.citypark.parser.CityParkGaragesParser;
import com.citypark.parser.CityParkGaragesParser.GaragePoint;

/**
 * Utility class for querying garages api based on gis data.
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
 * @author jono@nanosheep.net
 * @version Jun 21, 2010
 */

public final class GaragesOverlayHandler {
		
	/**
	 * Find the nearest cycle stand to the point given, or
	 * return null if there's not one in a mile.
	 * @param point GeoPoint to search near
	 * @return a GeoPoint representing the cycle stand position or null.
	 */
	
	public static GeoPoint getNearest(final GeoPoint point, final Context mAct, final String cpSessionId) {
		GeoPoint closest = null;
		double best = 9999999;
		double dist;
		
		for (OverlayItem o : getMarkers(point, 1, mAct, cpSessionId)) {
			dist = point.distanceTo(o.mGeoPoint);
		
			if (best > dist) {
				best = dist;
				closest = o.mGeoPoint;
			}	
		}
		return closest;
	}
	
	public static GeoPoint getNearest(final Address address, final Context mAct, final String cpSessionId) {
		return getNearest(new GeoPoint(Convert.asMicroDegrees(address.getLatitude()),
				Convert.asMicroDegrees(address.getLongitude())), mAct, cpSessionId);
	}

	/**
	 * Get markers from the api.
	 * 
	 * @param p Center point
	 * @param distance radius to collect markers within.
	 * @return an arraylist of OverlayItems corresponding to markers in range.
	 */

	public static List<OverlayItem> getMarkers(final GeoPoint p,
			final int distance, final Context mAct, final String cpSessionId) {
		
		final List<OverlayItem> markers = new ArrayList<OverlayItem>();
		
		//use CityPark to find garages
		if (cpSessionId != null) {
			Drawable markerIcon;
			final CityParkGaragesParser parser = new CityParkGaragesParser(mAct,cpSessionId,p.getLatitudeE6(),p.getLongitudeE6(),distance);
			
			//final HotspotPlace hotspot = new HotspotPlace(0, 10);	

			// Parse XML to overlayitems (cycle stands)
			for (GaragePoint garagePoint : parser.parse()) {
				switch (garagePoint.getAvailability()) {
				case UNKNOWN:
					markerIcon = mAct.getResources().getDrawable(R.drawable.ic_marker_garage);
					break;
				case BUSY:
					markerIcon = mAct.getResources().getDrawable(R.drawable.ic_marker_garage_red);
					break;
				default:
					markerIcon = mAct.getResources().getDrawable(R.drawable.ic_marker_garage_green);
					break;
				}
				
				OverlayItem marker = new OverlayItem(Integer.toString((int)garagePoint.getPrice()), garagePoint.getName(), garagePoint.getPGeoPoint());
				marker.setMarker(markerIcon);
				marker.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
				markers.add(marker);
			}
		}
		
		return markers;
	}

	/**
	 * Generate an array of points representing a MBR for the circle described
	 * by the radius given.
	 * 
	 * @param p point to use as center
	 * @param distance radius to bound within.
	 * @return an array of 4 geopoints representing an mbr drawn clockwise from the ne corner.
	 */
	private static List<GeoPoint> getBounds(final GeoPoint p, final double distance) {
		final List<GeoPoint> points = new ArrayList<GeoPoint>(4);
		final int degrees = Convert.asMicroDegrees(((distance / CityParkConsts.EARTH_RADIUS) 
				* (1/CityParkConsts.PI_180)));
		final double latRadius = CityParkConsts.EARTH_RADIUS * Math.cos(degrees * CityParkConsts.PI_180);
		final int degreesLng = Convert.asMicroDegrees( (distance / latRadius) * (1/CityParkConsts.PI_180));

		final int maxLng = degreesLng + p.getLongitudeE6();
		final int maxLat = degrees + p.getLatitudeE6();

		final int minLng = p.getLongitudeE6() - degreesLng;
		final int minLat = p.getLatitudeE6() - degrees;

		points.add(new GeoPoint(maxLat, maxLng));
		points.add(new GeoPoint(maxLat, minLng));
		points.add(new GeoPoint(minLat, minLng));
		points.add(new GeoPoint(minLat, maxLng));

		return points;
	}
	
	/**
	 * Get an OSM bounding box string of an array of GeoPoints representing
	 * a bounding box drawn clockwise from the northeast.
	 * @param points List of geopoints
	 * @return a string in OSM xapi bounding box form.
	 */
	
	private static String getOSMBounds(final List<GeoPoint> points) {
		final StringBuffer sBuf = new StringBuffer("%5Bbbox=");
		sBuf.append(Convert.asDegrees(points.get(2).getLongitudeE6()));
		sBuf.append(',');
		sBuf.append(Convert.asDegrees(points.get(2).getLatitudeE6()));
		sBuf.append(',');
		sBuf.append(Convert.asDegrees(points.get(0).getLongitudeE6()));
		sBuf.append(',');
		sBuf.append(Convert.asDegrees(points.get(0).getLatitudeE6()));
		sBuf.append("%5D");

		return sBuf.toString();
	}

}
