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
public class ItemizedReleasesOverlay extends ItemizedIconOverlay<OverlayItem> {
    private int markerHeight;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	public ItemizedReleasesOverlay(List<OverlayItem> pList, Drawable pDefaultMarker,
			OnItemGestureListener mOnItemGestureListener, ResourceProxy pResourceProxy) {
		super(pList, pDefaultMarker, mOnItemGestureListener, pResourceProxy);
		
		markerHeight = ((BitmapDrawable) pDefaultMarker).getBitmap().getHeight();
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
