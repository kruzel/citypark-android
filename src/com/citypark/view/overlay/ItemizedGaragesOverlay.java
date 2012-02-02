/**
 * 
 */
package com.citypark.view.overlay;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import com.citypark.GarageDetailsActivity;
import com.citypark.constants.CityParkConsts;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * @author TQJ764
 *
 */
public class ItemizedGaragesOverlay extends ItemizedOverlay<OverlayItem> {
	private static final int FONT_SIZE = 20;
    private static final int TITLE_MARGIN = 3;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context context;
	
	public ItemizedGaragesOverlay(Context context,List<OverlayItem> pList, Drawable pDefaultMarker) {
		super(boundCenterBottom(pDefaultMarker));
		this.context = context;
		mOverlays.addAll(pList);
		populate();
	}
	
	@Override
	public void draw(android.graphics.Canvas canvas, MapView mapView,
            boolean shadow) {
		
		super.draw(canvas, mapView, shadow);
		
		// go through all OverlayItems and draw title for each of them
        for (OverlayItem item:mOverlays)
        {
        	int markerHeight = 0;
        	Drawable dr = item.getMarker(0);
    		if(dr!=null) {
        		Bitmap markerBitmap = ((BitmapDrawable) boundCenterBottom(dr)).getBitmap();
        		markerHeight = markerBitmap.getHeight();
    		}
        		
        	if(item.getTitle()!= null && item.getTitle().length() > 0 ) {
	            /* Converts latitude & longitude of this overlay item to coordinates on screen.
	             * As we have called boundCenterBottom() in constructor, so these coordinates
	             * will be of the bottom center position of the displayed marker.
	             */	
	            GeoPoint point = item.getPoint();
	            Point markerBottomCenterCoords = new Point();
	            mapView.getProjection().toPixels(point, markerBottomCenterCoords);
	
	            /* Find the width and height of the title*/
	            TextPaint paintText = new TextPaint();
	            Paint paintRect = new Paint();
	
	            paintText.setTextAlign(Paint.Align.CENTER);
	            paintText.setTextSize(FONT_SIZE);
	            paintText.setFakeBoldText(true);
	            paintText.setARGB(255, 0, 0, 0);
	          
	            canvas.drawText(item.getTitle(), markerBottomCenterCoords.x, markerBottomCenterCoords.y - markerHeight/2  ,
	                     paintText);
        		
        	}
        }
     
	}

	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	public void addAllOverlays( List<OverlayItem> ovrleayItems) {
		mOverlays.addAll(ovrleayItems);
		populate();
	}

	@Override
	public boolean onSnapToItem(int arg0, int arg1, Point arg2, MapView arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		if(mOverlays==null)
			return 0;
		else
			return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		int garageId = Integer.parseInt(mOverlays.get(index).getSnippet());
		if(garageId!=0) {
			Intent intent = new Intent(context,GarageDetailsActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra(CityParkConsts.GARAGE_ID, garageId);
			context.startActivity(intent);
		}
		
		return super.onTap(index);
	}

	
}
