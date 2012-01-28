package com.citypark.api.task;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.citypark.view.overlay.LiveGarageMarkers;
import com.citypark.view.overlay.LiveStreetLinesMarkers;
import com.citypark.view.overlay.LiveStreetReleasesMarkers;

public class ReleasesOverlayFetchTask {
	Thread update;
	
	private OverlayListener listener;
	private LiveStreetReleasesMarkers releasesMarkers;
	private LiveGarageMarkers garageMarkers;
	private Boolean res = false;

	public ReleasesOverlayFetchTask(final MapView osmv, Context context, OverlayListener listener, LiveGarageMarkers garageMarkers, LiveStreetReleasesMarkers releasesMarkers) {
		super();
		this.listener = listener;
		
		this.releasesMarkers = releasesMarkers;
		this.garageMarkers = garageMarkers;
	}

	public void refresh(final GeoPoint p) {
		if(update!=null)
			update.stop();
		
		update = new Thread() {
			private static final int MSG = 0;
			@Override
			public void run() {
			
				res = releasesMarkers.fetch(p);
			
				ReleasesOverlayFetchTask.this.messageHandler.sendEmptyMessage(MSG);
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
			listener.overlayFetchComplete(false,res,false);
		}
	};

}
