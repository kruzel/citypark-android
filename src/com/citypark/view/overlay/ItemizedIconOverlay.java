/**
 * 
 */
package com.citypark.view.overlay;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * @author TQJ764
 *
 */
public class ItemizedIconOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context context;
	
	public ItemizedIconOverlay(Context context, Drawable pDefaultMarker) {
		super(boundCenterBottom(pDefaultMarker));
		this.context = context;
		populate();
	}

	public void addItem(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	public void addAllItems( List<OverlayItem> ovrleayItems) {
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
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// go through all OverlayItems and draw title for each of them
        for (OverlayItem item:mOverlays)
        {
        	Drawable dr = item.getMarker(0);
    		if(dr!=null) 
    			boundCenterBottom(dr);
        }
		super.draw(canvas, mapView, shadow);
	}
	
	

}
