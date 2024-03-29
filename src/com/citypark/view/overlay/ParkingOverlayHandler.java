package com.citypark.view.overlay;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;

import com.citypark.R;
import com.citypark.api.parser.CityParkGaragesParser;
import com.citypark.constants.CityParkConsts;
import com.citypark.dto.GaragePoint;
import com.citypark.utility.Convert;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


public final class ParkingOverlayHandler {
		
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
			 List<GaragePoint> garageList = parser.parse();
			 if(garageList==null)
				 return null;	

			// Parse XML to overlayitems (cycle stands)
			for (GaragePoint garagePoint : garageList) {
				//TODO move images selection logic to server - for multiple owners support
				//TODO add images caching
				if (garagePoint.getOwner().contains("ahuzot")) {
					switch (garagePoint.getAvailability()) {
					case UNKNOWN:
						markerIcon = mAct.getResources().getDrawable(R.drawable.ahuzat_hof_grey);
						break;
					case BUSY:
						markerIcon = mAct.getResources().getDrawable(R.drawable.ahuzat_hof_red);
						break;
					default:
						markerIcon = mAct.getResources().getDrawable(R.drawable.ahuzat_hof_green);
						break;
					}
				} else {
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
				}
					
				OverlayItem marker;
				if (garagePoint.getPrice()==0)
					marker = new OverlayItem(garagePoint.getGeoPoint(), mAct.getString(R.string.parking_free), garagePoint.getIdString());
				else if (garagePoint.getPrice()>0)
					marker = new OverlayItem(garagePoint.getGeoPoint(), Integer.toString((int)garagePoint.getPrice()), garagePoint.getIdString()); //mAct.getResources().getString(R.string.currency) +
				else
					marker = new OverlayItem(garagePoint.getGeoPoint(),"", garagePoint.getIdString());
				marker.setMarker(markerIcon);
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
