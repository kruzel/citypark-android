package com.citypark.view.overlay;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;

import com.citypark.api.parser.CityParkStreetLinesParser;
import com.citypark.api.task.LoginTask;
import com.citypark.constants.CityParkConsts;
import com.citypark.dto.StreetSegment;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class LiveStreetLinesMarkers {
	private final Context context;
	
	/** street segments overlay **/
	protected List<SegmentOverlay> mSegmentsOverlays = null;
	protected List<SegmentOverlay> mOldSegmentsOverlays = new ArrayList<SegmentOverlay>();
	
	/** OSM MapView reference **/
	protected MapView mOsmv;
	/** Radius to return markers within. **/
	protected static final int RADIUS = 500;

	public LiveStreetLinesMarkers(MapView osmv, final Context ctxt) {
		this.mOsmv = osmv;
		context = ctxt.getApplicationContext();	
	}
	
	public void clearSegments(final List<SegmentOverlay> segmentsOverlays){
		if(segmentsOverlays != null){
			for ( SegmentOverlay overlay : segmentsOverlays) {
				mOsmv.getOverlays().remove(overlay);
			}
			segmentsOverlays.clear();
		}
	}
	
	public void setSegments(final List<SegmentOverlay> newSegmentsOverlays){
		if(newSegmentsOverlays != null) {
			for ( SegmentOverlay overlay : newSegmentsOverlays) {
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

	public List<SegmentOverlay> getSegments(final GeoPoint p,
			final int distance, final Context mAct) {
		
		final List<SegmentOverlay> segments = new ArrayList<SegmentOverlay>();
		
		//use CityPark to find street segments
		String cpSessionId = LoginTask.getSessionId();
		SegmentOverlay overlay;
		int color = Color.TRANSPARENT;
		if (cpSessionId != null) {
			final CityParkStreetLinesParser parser = new CityParkStreetLinesParser(mAct,cpSessionId,p.getLatitudeE6(),p.getLongitudeE6(),distance);
			List<StreetSegment> linesList = parser.parse();
			if (linesList == null)
				return null;
			if(linesList.size()==0)
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
				
				overlay = new SegmentOverlay(color);
				
				GeoPoint point1 = new GeoPoint((int)(streetSegment.getStart_latitude()*1E6),(int)(streetSegment.getStart_longitude()*1E6));
				overlay.setStartPoint(point1);
				GeoPoint point2 = new GeoPoint((int)(streetSegment.getEnd_latitude()*1E6),(int)(streetSegment.getEnd_longitude()*1E6));
				overlay.setEndPoint(point2);
				
				overlay.setWidth(10.0f);
				overlay.setAlpha(100);
				
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
	
	public Boolean visible() {
		return !(mSegmentsOverlays==null || mSegmentsOverlays.size() == 0);
	}
}
