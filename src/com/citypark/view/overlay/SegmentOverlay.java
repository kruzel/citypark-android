package com.citypark.view.overlay;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * 
 * @author Viesturs Zarins
 * 
 *         This class draws a path line in given color.
 */
public class SegmentOverlay extends Overlay {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private GeoPoint startPoint;
	private GeoPoint endPoint;

	/**
	 * Paint settings.
	 */
	protected final Paint mPaint = new Paint();

	// ===========================================================
	// Constructors
	// ===========================================================

	public SegmentOverlay(final int color) {
		this.mPaint.setColor(color);
		this.mPaint.setStrokeWidth(2.0f);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setWidth(final float width) {
		this.mPaint.setStrokeWidth(width);
	}
	
	public void setColor(final int color) {
		this.mPaint.setColor(color);
	}

	public void setAlpha(final int a) {
		this.mPaint.setAlpha(a);
	}

	public void setStartPoint(final GeoPoint pt) {
		startPoint = new GeoPoint(pt.getLatitudeE6(), pt.getLongitudeE6());
	}
	
	public void setEndPoint(final GeoPoint pt) {
		endPoint = new GeoPoint(pt.getLatitudeE6(), pt.getLongitudeE6());
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if(startPoint==null || endPoint==null)
			return;

		final Projection pj = mapView.getProjection();

		Point projectedPoint0 = pj.toPixels(startPoint, null);
		Point projectedPoint1 = pj.toPixels(endPoint, null);

//		final Rect lineBounds = new Rect(); // bounding rectangle for the current line segment.
//		lineBounds.set(projectedPoint0.x, projectedPoint0.y, projectedPoint1.x, projectedPoint1.y);
		canvas.drawLine(projectedPoint0.x, projectedPoint0.y, projectedPoint1.x,
					projectedPoint1.y, this.mPaint);

		super.draw(canvas, mapView, shadow);
		
	}

}
