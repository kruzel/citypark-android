package com.citypark.view.overlay;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class ItemizedParkingOverlay extends ItemizedOverlay<OverlayItem> {
	
	

	public ItemizedParkingOverlay(Drawable pDefaultMarker,
			ResourceProxy pResourceProxy) {
		super(pDefaultMarker, pResourceProxy);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onSnapToItem(int arg0, int arg1, Point arg2, MapView arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void draw(Canvas arg0, MapView arg1, boolean arg2) {
		// TODO Auto-generated method stub
		super.draw(arg0, arg1, arg2);
		
	}



}
