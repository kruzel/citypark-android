package com.citypark.api.task;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.citypark.view.overlay.LiveGarageMarkers;
import com.citypark.view.overlay.LiveStreetLinesMarkers;
import com.citypark.view.overlay.LiveStreetReleasesMarkers;

public class AllOverlayFetchTask {
	
	private OverlayListener listener;
	private LiveStreetLinesMarkers lineMarkers;
	private LiveStreetReleasesMarkers releasesMarkers;
	private LiveGarageMarkers garageMarkers;
	
	private Boolean res = false;

	public AllOverlayFetchTask(final MapView osmv, Context context, OverlayListener listener, LiveGarageMarkers garageMarkers) {
		super();
		this.listener = listener;
		
		lineMarkers = new LiveStreetLinesMarkers(osmv, context);
		releasesMarkers = new LiveStreetReleasesMarkers(osmv, context);
		this.garageMarkers = garageMarkers;
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
			
				res = lineMarkers.fetch(p);
				if(res==true) 
					res = releasesMarkers.fetch(p);
				if(res==true) 
					res = garageMarkers.fetch(p);
				
				AllOverlayFetchTask.this.messageHandler.sendEmptyMessage(MSG);
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
			if(res) {
				lineMarkers.updateMap();
				releasesMarkers.updateMap();
				garageMarkers.updateMap();
			}
				
			listener.overlayFetchComplete(res);
		}
	};

}
