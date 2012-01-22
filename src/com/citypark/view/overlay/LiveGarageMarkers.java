package com.citypark.view.overlay;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.citypark.CityParkApp;
import com.citypark.GarageDetailsActivity;
import com.citypark.R;
import com.citypark.api.task.LoginTask;
import com.citypark.constants.CityParkConsts;

/**
 * This file is part of CityPark.
 * 
 * Copyright (C) 2011  Ofer Kruzel
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
 * @author okruzel@gmail.com
 * @version Dec 21, 2011
 */

public class LiveGarageMarkers implements OnItemGestureListener<OverlayItem> {
	/** Reference to map view to draw markers over. **/
	private final MapView mv;
	/** Markers list for use by thread. **/
	private List<OverlayItem> markers;
	private final Context context;
	/** List of overlay items. **/
	private final List<OverlayItem> mOverlays;
	/** Itemized Overlay. **/
	private static ItemizedGaragesOverlay iOverlay;

	public LiveGarageMarkers(final MapView mOsmv, final Context ctxt) {
		mv = mOsmv;
		context = ctxt.getApplicationContext();
		mOverlays = new ArrayList<OverlayItem>(1);
		//iOverlay = new ItemizedParkingOverlay(mOverlays,ctxt.getResources().getDrawable(R.drawable.ic_marker_garage), this, mv.getResourceProxy());
	}

	/**
	 * Update markers around given point.
	 * @param p the Geopoint to gather markers around.
	 */

	public Boolean fetch(final GeoPoint p) {
		markers = ParkingOverlayHandler.getMarkers(p, CityParkConsts.RADIUS, context,LoginTask.getSessionId());
		if(markers != null)
			return true;
		
		return false;
	}
	
	public void updateMap() {
		if(markers != null) {
			clearFromMap();
			mOverlays.clear();
			mOverlays.addAll(markers);
			iOverlay = new ItemizedGaragesOverlay(mOverlays,context.getResources().getDrawable(R.drawable.ic_marker_garage), LiveGarageMarkers.this, mv.getResourceProxy());
			iOverlay.addAllOverlays(mOverlays);
			mv.getOverlays().add(iOverlay);
		}
	}
	
	public void clearFromMap() {
		if (mv.getOverlays().contains(iOverlay)) {
			mv.getOverlays().remove(iOverlay);
		}
	}

	/* (non-Javadoc)
	 * @see org.andnav.osm.views.overlay.OpenStreetMapViewItemizedOverlay.OnItemGestureListener#onItemLongPress(int, java.lang.Object)
	 */
	@Override
	public boolean onItemLongPress(int index, OverlayItem item) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.andnav.osm.views.overlay.OpenStreetMapViewItemizedOverlay.OnItemGestureListener#onItemSingleTapUp(int, java.lang.Object)
	 */
	@Override
	public boolean onItemSingleTapUp(int index,
			OverlayItem item) {
		
		int garageId = Integer.parseInt(item.mDescription);
		Intent intent = new Intent(context,GarageDetailsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		intent.putExtra(CityParkConsts.GARAGE_ID, garageId);
		context.startActivity(intent);
		
		return false;
	}

}
