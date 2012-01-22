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
import com.citypark.api.parser.CityParkStreetLinesParser;
import com.citypark.api.task.LoginTask;
import com.citypark.constants.CityParkConsts;
import com.citypark.utility.ParkingSessionManager;

public class LiveStreetLinesMarkers {
	private final Context context;
	
	/** street segments overlay **/
	protected List<PathOverlay> mSegmentsOverlays = null;
	protected List<PathOverlay> mOldSegmentsOverlays = new ArrayList<PathOverlay>();
	
	/** OSM MapView reference **/
	protected MapView mOsmv;
	/** Radius to return markers within. **/
	protected static final int RADIUS = 500;

	public LiveStreetLinesMarkers(MapView osmv, final Context ctxt) {
		this.mOsmv = osmv;
		context = ctxt.getApplicationContext();	
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
		String cpSessionId = LoginTask.getSessionId();
		PathOverlay overlay;
		int color = Color.TRANSPARENT;
		if (cpSessionId != null) {
			final CityParkStreetLinesParser parser = new CityParkStreetLinesParser(mAct,cpSessionId,p.getLatitudeE6(),p.getLongitudeE6(),distance);
			List<StreetSegment> linesList = parser.parse();
			if (linesList == null)
				return null;
			
			// Parse XML to street segments
			// and add each street segment as a separate overlay with color according to street segment wait time
			for (StreetSegment streetSegment : linesList) {
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

	public Boolean fetch(final GeoPoint p) {
		mSegmentsOverlays = getSegments(p, RADIUS, context);
		if(mSegmentsOverlays!=null)
			return true;
		
		return false;
	}
	
	public void updateMap() {
		if (mSegmentsOverlays != null){
			setSegments(mSegmentsOverlays);
			clearSegments(mOldSegmentsOverlays);
			
			mOldSegmentsOverlays.clear();
			mOldSegmentsOverlays.addAll(mSegmentsOverlays);
		}
	}
	
	public void clearFromMap() {
		clearSegments(mSegmentsOverlays);
	}
}
