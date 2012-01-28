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
	private Thread update;
	
	private OverlayListener listener;
	private LiveStreetLinesMarkers lineMarkers;
	private LiveStreetReleasesMarkers releasesMarkers;
	private LiveGarageMarkers garageMarkers;

	private Boolean garagesRes;
	private Boolean releasesRes;
	private Boolean linesRes;

	public AllOverlayFetchTask(final MapView osmv, Context context, OverlayListener listener, LiveGarageMarkers garageMarkers, LiveStreetReleasesMarkers releasesMarkers, LiveStreetLinesMarkers linesMarkers) {
		super();
		this.listener = listener;
		
		this.lineMarkers = linesMarkers;
		this.releasesMarkers = releasesMarkers;
		this.garageMarkers = garageMarkers;
	}
	
	/**
	 * Update markers around given point.
	 * @param p the Geopoint to gather markers around.
	 */

	public void refresh(final GeoPoint p) {
		if(update!=null)
			update.stop();
		
		update = new Thread() {
			private static final int MSG = 0;
			@Override
			public void run() {
				linesRes = lineMarkers.fetch(p);
				releasesRes = releasesMarkers.fetch(p);
				garagesRes = garageMarkers.fetch(p);
				
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
			listener.overlayFetchComplete(garagesRes,releasesRes,linesRes);
		}
	};

}
