package com.citypark.view.overlay;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import com.citypark.CityParkApp;
import com.citypark.constants.CityParkConsts;
import com.citypark.parser.CityParkStreetParkingParser;
import com.citypark.utility.ParkingSessionPersist;

public class LiveStreetParkingMarkers {
	private final Context context;
	/** Application reference. **/
	protected static CityParkApp app;
	
	/** street segments overlay **/
	protected List<PathOverlay> mSegmentsOverlays = null;
	protected List<PathOverlay> mOldSegmentsOverlays = new ArrayList<PathOverlay>();
	
	/** OSM MapView reference **/
	protected MapView mOsmv;
	/** Radius to return markers within. **/
	protected static final int RADIUS = 500;

	public LiveStreetParkingMarkers(MapView osmv, final Context ctxt, CityParkApp app) {
		this.mOsmv = osmv;
		context = ctxt.getApplicationContext();	
		this.app = app;
	}
	
	public void clearSegments(final List<PathOverlay> segmentsOverlays){
		if(segmentsOverlays != null){
			for ( PathOverlay overlay : segmentsOverlays) {
				mOsmv.getOverlays().remove(overlay);
			}
		}
	}
	
	public void setSegments(final List<PathOverlay> newSegmentsOverlays){
		if(newSegmentsOverlays != null) {
			for ( PathOverlay overlay : newSegmentsOverlays) {
				mOsmv.getOverlays().add(overlay);
			}
			//mOsmv.postInvalidate();
		}
	}
	
	/**
	 * Get street segments from the api.
	 * 
	 * @param p Center point
	 * @param distance radius to collect markers within.
	 * @return an arraylist of street segments corresponding to streets in range.
	 */

	public List<PathOverlay> getSegments(final GeoPoint p,
			final int distance, final Context mAct) {
		
		final List<PathOverlay> segments = new ArrayList<PathOverlay>();
		
		//use CityPark to find street segments
		String cpSessionId = app.getSessionId();
		PathOverlay overlay;
		int color = Color.TRANSPARENT;
		if (cpSessionId != null) {
			final CityParkStreetParkingParser parser = new CityParkStreetParkingParser(mAct,cpSessionId,p.getLatitudeE6(),p.getLongitudeE6(),distance);
			
			// Parse XML to street segments
			// and add each street segment as a separate overlay with color according to street segment wait time
			for (StreetSegment streetSegment : parser.parse()) {
				if(streetSegment.getSearch_time() == -1 )
					color = Color.TRANSPARENT;
				else if(streetSegment.getSearch_time() < CityParkConsts.FAST_PARKING_LIMIT) //sec
					color = Color.GREEN;
				else if(streetSegment.getSearch_time() < CityParkConsts.MEDIUM_PARKING_LIMIT) //sec
					color = Color.YELLOW;
				else 
					color = Color.RED;
				
				overlay = new PathOverlay(color, context);
				
				GeoPoint point1 = new GeoPoint(streetSegment.getStart_latitude(),streetSegment.getStart_longitude());
				overlay.addPoint(point1);
				GeoPoint point2 = new GeoPoint(streetSegment.getEnd_latitude(),streetSegment.getEnd_longitude());
				overlay.addPoint(point2);
				overlay.setEnabled(true);
				overlay.getPaint().setStrokeWidth(10.0f);
				overlay.getPaint().setAlpha(100);
				
				segments.add(overlay);
			}
		}
		
		return segments;
	}
	
	/**
	 * Update street segments markers around given point.
	 * @param p the Geopoint to gather segments markers around.
	 */

	public void refresh(final GeoPoint p) {
		Thread update = new Thread() {
			private static final int MSG = 0;
			@Override
			public void run() {
				mSegmentsOverlays = getSegments(p, RADIUS, context);
				LiveStreetParkingMarkers.this.messageHandler.sendEmptyMessage(MSG);
			}
		};
		update.start();
	}
	
	/**
	 * Handler for street segments thread.
	 * Remove the existing segments overlay if they exist and
	 * replace them with the new one from the thread.
	 */
	
	private final Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			setSegments(mSegmentsOverlays);
			clearSegments(mOldSegmentsOverlays);
			
			mOldSegmentsOverlays.clear();
			mOldSegmentsOverlays.addAll(mSegmentsOverlays);
		}
	};
	
}
