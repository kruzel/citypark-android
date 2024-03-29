package org.osmdroid.views.overlay;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.util.constants.MapViewConstants;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;

/**
 * Draws a mini-map as an overlay layer. It currently uses its own MapTileProviderBasic or a tile
 * provider supplied to it. Do NOT share a tile provider amongst multiple tile drawing overlays - it
 * will create an under-sized cache.
 *
 * @author Marc Kurtz
 *
 */
public class MinimapOverlay extends TilesOverlay implements MapViewConstants {

	private int mWidth = 100;
	private int mHeight = 100;
	private int mPadding = 10;
	private int mZoomDifference;
	private final Paint mPaint;
	private int mWorldSize_2;

	// The Mercator coordinates of what is on the screen
	final private Rect mViewportRect = new Rect();

	// The Mercator coordinates of the map tile area we are interested in the target zoom level
	final private Rect mTileArea = new Rect();

	// The Canvas coordinates where the minimap should be drawn
	final private Rect mMiniMapCanvasRect = new Rect();

	// Stores the intersection of the minimap and the Canvas clipping area
	final private Rect mIntersectionRect = new Rect();

	/**
	 * Creates a {@link MinimapOverlay} with the supplied tile provider. The {@link Handler} passed
	 * in is typically the same handler being used by the main map.
	 *
	 * @param pContext
	 *            a context
	 * @param tileRequestCompleteHandler
	 *            a handler for the tile request complete notifications
	 * @param pTileProvider
	 *            a tile provider
	 */
	public MinimapOverlay(final Context pContext, final Handler pTileRequestCompleteHandler,
			final MapTileProviderBase pTileProvider, final int pZoomDifference) {
		super(pTileProvider, pContext);
		setZoomDifference(pZoomDifference);

		mTileProvider.setTileRequestCompleteHandler(pTileRequestCompleteHandler);

		mPaint = new Paint();
		mPaint.setColor(Color.GRAY);
		mPaint.setStyle(Style.FILL);
		mPaint.setStrokeWidth(2);
	}

	/**
	 * Creates a {@link MinimapOverlay} with the supplied tile provider. The {@link Handler} passed
	 * in is typically the same handler being used by the main map.
	 *
	 * @param pContext
	 *            a context
	 * @param tileRequestCompleteHandler
	 *            a handler for the tile request complete notifications
	 * @param pTileProvider
	 *            a tile provider
	 */
	public MinimapOverlay(final Context pContext, final Handler pTileRequestCompleteHandler,
			final MapTileProviderBase pTileProvider) {
		this(pContext, pTileRequestCompleteHandler, pTileProvider,
				DEFAULT_ZOOMLEVEL_MINIMAP_DIFFERENCE);
	}

	/**
	 * Creates a {@link MinimapOverlay} that uses its own {@link MapTileProviderBasic}. The
	 * {@link Handler} passed in is typically the same handler being used by the main map.
	 *
	 * @param pContext
	 *            a context
	 * @param tileRequestCompleteHandler
	 *            a handler for tile request complete notifications
	 */
	public MinimapOverlay(final Context pContext, final Handler pTileRequestCompleteHandler) {
		this(pContext, pTileRequestCompleteHandler, new MapTileProviderBasic(pContext));
	}

	public void setTileSource(final ITileSource pTileSource) {
		mTileProvider.setTileSource(pTileSource);
	}

	public int getZoomDifference() {
		return mZoomDifference;
	}

	public void setZoomDifference(final int zoomDifference) {
		mZoomDifference = zoomDifference;
	}

	@Override
	protected void onDraw(final Canvas pC, final MapView pOsmv) {

		// Don't draw if we are animating
		if (pOsmv.isAnimating()) {
			return;
		}

		// Calculate the half-world size
		final Projection projection = pOsmv.getProjection();
		final int zoomLevel = projection.getZoomLevel();
		final int tileZoom = projection.getTileMapZoom();
		mWorldSize_2 = 1 << (zoomLevel + tileZoom - 1);

		// Find what's on the screen
		final BoundingBoxE6 boundingBox = projection.getBoundingBox();
		final Point upperLeft = org.osmdroid.views.util.Mercator
				.projectGeoPoint(boundingBox.getLatNorthE6(), boundingBox.getLonWestE6(), zoomLevel
						+ tileZoom, null);
		final Point lowerRight = org.osmdroid.views.util.Mercator
				.projectGeoPoint(boundingBox.getLatSouthE6(), boundingBox.getLonEastE6(), zoomLevel
						+ tileZoom, null);

		// Save the Mercator coordinates of what is on the screen
		mViewportRect.set(upperLeft.x, upperLeft.y, lowerRight.x, lowerRight.y);

		// Start calculating the tile area with the current viewport
		mTileArea.set(mViewportRect);

		// Get the target zoom level difference.
		int miniMapZoomLevelDifference = getZoomDifference();

		// Make sure the zoom level difference isn't below the minimum zoom level
		if (zoomLevel - getZoomDifference() < mTileProvider.getMinimumZoomLevel()) {
			miniMapZoomLevelDifference += zoomLevel - getZoomDifference()
					- mTileProvider.getMinimumZoomLevel();
		}

		// Shift the screen coordinates into the target zoom level
		mTileArea.set(mTileArea.left >> miniMapZoomLevelDifference,
				mTileArea.top >> miniMapZoomLevelDifference,
				mTileArea.right >> miniMapZoomLevelDifference,
				mTileArea.bottom >> miniMapZoomLevelDifference);

		// Limit the area we are interested in for tiles to be the MAP_WIDTH by MAP_HEIGHT and
		// centered on the center of the screen
		mTileArea.set(mTileArea.centerX() - (getWidth() / 2), mTileArea.centerY()
				- (getHeight() / 2), mTileArea.centerX() + (getWidth() / 2), mTileArea.centerY()
				+ (getHeight() / 2));

		// Get the area where we will draw the minimap in screen coordinates
		mMiniMapCanvasRect.set(mViewportRect.right - getPadding() - getWidth(),
				mViewportRect.bottom - getPadding() - getHeight(), mViewportRect.right
						- getPadding(), mViewportRect.bottom - getPadding());
		mMiniMapCanvasRect.offset(-mWorldSize_2, -mWorldSize_2);

		// Draw a solid background where the minimap will be drawn with a 2 pixel inset
		pC.drawRect(mMiniMapCanvasRect.left - 2, mMiniMapCanvasRect.top - 2,
				mMiniMapCanvasRect.right + 2, mMiniMapCanvasRect.bottom + 2, mPaint);

		super.drawTiles(pC, projection.getZoomLevel() - miniMapZoomLevelDifference,
				projection.getTileSizePixels(), mTileArea);
	}

	@Override
	protected void onTileReadyToDraw(final Canvas c, final Drawable currentMapTile,
			final Rect tileRect) {

		// Get the offsets for where to draw the tiles relative to where the minimap is located
		final int xOffset = (tileRect.left - mTileArea.left) + (mMiniMapCanvasRect.left);
		final int yOffset = (tileRect.top - mTileArea.top) + (mMiniMapCanvasRect.top);

		// Set the drawable's location
		currentMapTile.setBounds(xOffset, yOffset, xOffset + tileRect.width(),
				yOffset + tileRect.height());

		// Save the current clipping bounds
		final Rect oldClip = c.getClipBounds();

		// Check to see if the drawing area intersects with the minimap area
		if (mIntersectionRect.setIntersect(oldClip, mMiniMapCanvasRect)) {
			// If so, then clip that area
			c.clipRect(mIntersectionRect);

			// Draw the tile, which will be appropriately clipped
			currentMapTile.draw(c);

			// Restore the original clipping bounds
			c.clipRect(oldClip);
		}
	}

	@Override
	protected void onDrawFinished(final Canvas pC, final MapView pOsmv) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(final MotionEvent pEvent, final MapView pMapView) {
		// Consume event so layers underneath don't receive
		if (mMiniMapCanvasRect.contains((int) pEvent.getX() + mViewportRect.left - mWorldSize_2,
				(int) pEvent.getY() + mViewportRect.top - mWorldSize_2)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean onDoubleTapUp(final MotionEvent pEvent, final MapView pMapView) {
		// Consume event so layers underneath don't receive
		if (mMiniMapCanvasRect.contains((int) pEvent.getX() + mViewportRect.left - mWorldSize_2,
				(int) pEvent.getY() + mViewportRect.top - mWorldSize_2)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean onLongPress(final MotionEvent pEvent, final MapView pMapView) {
		// Consume event so layers underneath don't receive
		if (mMiniMapCanvasRect.contains((int) pEvent.getX() + mViewportRect.left - mWorldSize_2,
				(int) pEvent.getY() + mViewportRect.top - mWorldSize_2)) {
			return true;
		}

		return false;
	}

	/**
	 * Sets the width of the mini-map in pixels
	 *
	 * @param width
	 *            the width to set in pixels
	 */
	public void setWidth(final int width) {
		mWidth = width;
	}

	/**
	 * Gets the width of the mini-map in pixels
	 *
	 * @return the width in pixels
	 */
	public int getWidth() {
		return mWidth;
	}

	/**
	 * Sets the height of the mini-map in pixels
	 *
	 * @param height
	 *            the height to set in pixels
	 */
	public void setHeight(final int height) {
		mHeight = height;
	}

	/**
	 * Gets the height of the mini-map in pixels
	 *
	 * @return the height in pixels
	 */
	public int getHeight() {
		return mHeight;
	}

	/**
	 * Sets the number of pixels from the lower-right corner to offset the mini-map
	 *
	 * @param padding
	 *            the padding to set in pixels
	 */
	public void setPadding(final int padding) {
		mPadding = padding;
	}

	/**
	 * Gets the number of pixels from the lower-right corner to offset the mini-map
	 *
	 * @return the padding in pixels
	 */
	public int getPadding() {
		return mPadding;
	}
}
