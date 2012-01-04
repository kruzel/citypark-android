/**
 * 
 */
package com.citypark.view.overlay;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

/**
 * @author TQJ764
 *
 */
public class ItemizedParkingOverlay extends ItemizedIconOverlay<OverlayItem> {
	private static final int FONT_SIZE = 14;
    private static final int TITLE_MARGIN = 3;
    private int markerHeight;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	public ItemizedParkingOverlay(List<OverlayItem> pList, Drawable pDefaultMarker,
			OnItemGestureListener mOnItemGestureListener, ResourceProxy pResourceProxy) {
		super(pList, pDefaultMarker, mOnItemGestureListener, pResourceProxy);
		
		markerHeight = ((BitmapDrawable) pDefaultMarker).getBitmap().getHeight();
	}
	
	@Override
	public void draw(android.graphics.Canvas canvas, MapView mapView,
            boolean shadow) {
		// TODO Auto-generated method stub
		super.draw(canvas, mapView, shadow);
		
		// go through all OverlayItems and draw title for each of them
        for (OverlayItem item:mOverlays)
        {
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
	
	            Rect rect = new Rect();
	            paintText.setTextSize(FONT_SIZE);
	            paintText.getTextBounds(item.getTitle(), 0, item.getTitle().length(), rect);
	
	            rect.inset(-TITLE_MARGIN, -TITLE_MARGIN);
	            rect.offsetTo(markerBottomCenterCoords.x - rect.width()/2, markerBottomCenterCoords.y - markerHeight - rect.height()); 
	
	            paintText.setTextAlign(Paint.Align.CENTER);
	            paintText.setTextSize(FONT_SIZE);
	            paintText.setARGB(255, 255, 255, 255);
	            paintRect.setARGB(255, 0, 0, 0);
	
	            canvas.drawRoundRect( new RectF(rect), 2, 2, paintRect);
	            canvas.drawText(item.getTitle(), rect.left + rect.width() / 2,
	                    rect.bottom - TITLE_MARGIN , paintText);
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

}
