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
import com.citypark.constants.CityParkConsts;
import com.citypark.utility.ParkingOverlayHandler;

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

public class LiveGarageMarkers implements OnItemGestureListener<OverlayItem> {
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
	private ItemizedParkingOverlay iOverlay;
	/** Application reference. **/
	protected CityParkApp app;

	public LiveGarageMarkers(final MapView mOsmv, final Context ctxt, CityParkApp app) {
		mv = mOsmv;
		context = ctxt.getApplicationContext();
		mOverlays = new ArrayList<OverlayItem>(1);
		iOverlay = new ItemizedParkingOverlay(mOverlays,ctxt.getResources().getDrawable(R.drawable.ic_marker_garage), this, mv.getResourceProxy());
		this.app = app;
	}

	/**
	 * Update markers around given point.
	 * @param p the Geopoint to gather markers around.
	 */

	public void refresh(final GeoPoint p) {
		Thread update = new Thread() {
			private static final int MSG = 0;
			@Override
			public void run() {
				markers = ParkingOverlayHandler.getMarkers(p, RADIUS, context,app.getSessionId());
				LiveGarageMarkers.this.messageHandler.sendEmptyMessage(MSG);
			}
		};
		update.start();
	}
	
	/**
	 * Handler for parking thread.
	 * Remove the existing parking overlay if it exists and
	 * replace it with the new one from the thread.
	 */
	
	private final Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			if (mv.getOverlays().contains(iOverlay)) {
				mv.getOverlays().remove(iOverlay);
			}
			mOverlays.clear();
			mOverlays.addAll(markers);
			iOverlay = new ItemizedParkingOverlay(mOverlays,context.getResources().getDrawable(R.drawable.ic_marker_garage), LiveGarageMarkers.this, mv.getResourceProxy());
			iOverlay.addAllOverlays(mOverlays);
			mv.getOverlays().add(iOverlay);
			mv.postInvalidate();
		}
	};

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
		// TODO Auto-generated method stub
		int garageId = Integer.parseInt(item.mDescription);
		Intent intent = new Intent(context,GarageDetailsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		intent.putExtra(CityParkConsts.GARAGE_ID, garageId);
		context.startActivity(intent);
		
		return false;
	}

}
