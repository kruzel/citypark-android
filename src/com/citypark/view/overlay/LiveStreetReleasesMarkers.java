package com.citypark.view.overlay;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.citypark.R;
import com.citypark.api.parser.CityParkParkingReleasesParser;
import com.citypark.api.task.LoginTask;
import com.citypark.dto.AreaParkings;
import com.citypark.dto.StreetParkingPoint;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
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
 * A class to display markers on a map and update them from a remote
 * feed.
 * @author jono@nanosheep.net
 * @version Jun 21, 2010
 */

public class LiveStreetReleasesMarkers {
	/** Reference to map view to draw markers over. **/
	private final MapView mv;
	/** Markers list for use by thread. **/
	private List<OverlayItem> markers;
	private final Context context;
	/** Radius to return markers within. **/
	protected static final int RADIUS = 500;
	/** List of overlay items. **/
	private final List<OverlayItem> mOverlays;
	/** Itemized Overlay. **/
	private ItemizedIconOverlay iOverlay;
	private ItemizedIconOverlay iPrevOverlay;
	/** summary of fetched parkings **/ 
	AreaParkings areaParking = new AreaParkings();

	public LiveStreetReleasesMarkers(final MapView mOsmv, final Context ctxt) {
		mv = mOsmv;
		context = ctxt.getApplicationContext();
		mOverlays = new ArrayList<OverlayItem>(1);
		//iOverlay = new ItemizedParkingOverlay(mOverlays,context.getResources().getDrawable(R.drawable.ic_marker_garage), null, mv.getResourceProxy());;
	}

	/**
	 * Update markers around given point.
	 * @param p the Geopoint to gather markers around.
	 */

	public Boolean fetch(final GeoPoint p) {
		iPrevOverlay = iOverlay;
		
		markers = getMarkers(p, RADIUS, context,LoginTask.getSessionId());
		if(markers!=null) 
			return true;
		
		return false;
	}
	
	public void updateMap() {
		if(markers!=null) {
			clearFromMap();
			mOverlays.clear();
			mOverlays.addAll(markers);
			iOverlay = new ItemizedIconOverlay(context, context.getResources().getDrawable(R.drawable.ic_marker_garage));
			iOverlay.addAllItems(mOverlays);
			iOverlay.addAllItems(mOverlays);
			mv.getOverlays().add(iOverlay);
		}
	}
	
	public void clearFromMap() {
		if (mv.getOverlays().contains(iPrevOverlay)) {
			mv.getOverlays().remove(iPrevOverlay);
		}
		if (mv.getOverlays().contains(iOverlay)) {
			mv.getOverlays().remove(iOverlay);
		}
	}
	
	public void clearOrphensFromMap() {
		if (mv.getOverlays().contains(iPrevOverlay)) {
			mv.getOverlays().remove(iPrevOverlay);
		}
	}
	
	public AreaParkings getAreaParkings() {
		
		return areaParking;
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
			final CityParkParkingReleasesParser parser = new CityParkParkingReleasesParser(mAct,cpSessionId,p.getLatitudeE6(),p.getLongitudeE6(),distance);
			 List<StreetParkingPoint> releasesList = parser.parse();
			 if(releasesList==null)
				 return null;

			// Parse XML to overlayitems 
			for (StreetParkingPoint parkingPoint : releasesList) {	
				markerIcon = mAct.getResources().getDrawable(R.drawable.green_dot);
				
				OverlayItem marker = new OverlayItem(parkingPoint.getGeoPoint(), null, null);
				marker.setMarker(markerIcon);
				markers.add(marker);
			}
		}
		
		return markers;
	}

	public Boolean haveItems() {
		return (mOverlays!=null && mOverlays.size()>0);
	} 
}
