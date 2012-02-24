package com.citypark;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.citypark.constants.CityParkConsts;
import com.citypark.utility.dialog.DialogFactory;
import com.citypark.utility.route.Road;
import com.citypark.utility.route.RoadProvider;
import com.citypark.view.overlay.ItemizedIconOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class CityParkRouteActivity extends ParkingMap {

	private Road mRoad;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onNewIntent(Intent intent) {
		double lat = intent.getDoubleExtra(CityParkConsts.LATITUDE, 0.0d);
		double lng = intent.getDoubleExtra(CityParkConsts.LONGITUDE, 0.0d);

		if (lat != 0 && lng != 0) {
			showRoute(lat, lng);
		}

		super.onNewIntent(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void loginComplete(String sessionId) {
		super.loginComplete(sessionId);
		// showRoute(32.081859, 34.772961);
	}

	public void showRoute(
	/* final double fromLat,final double fromLon, */final double toLat,
			final double toLon) {
		mProgresBar.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				// double fromLat = 32.089859, fromLon = 34.771961, toLat =
				// 32.081859, toLon = 34.772961;
				Location myLocation = getCurrentLocation();
				if(myLocation!=null) {
					String url = RoadProvider.getUrl(myLocation.getLatitude(),
							myLocation.getLongitude(), toLat, toLon);
					InputStream is = getConnection(url);
					mRoad = RoadProvider.getRoute(is);
					try {
						is.close();
					} catch (IOException ioe) {
						Log.e("Got error while closing the connection for route display.",
								ioe.getMessage());
					}
					mHandler.sendEmptyMessage(0);
				} else 
					Toast.makeText(CityParkRouteActivity.this,
							getString(R.string.fix_failed_msg),
							Toast.LENGTH_LONG).show();
			}
		}.start();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try {
				/*
				 * TextView textView = (TextView)
				 * findViewById(R.id.description); textView.setText(mRoad.mName
				 * + " " + mRoad.mDescription);
				 * textView.setVisibility(TextView.VISIBLE);
				 */

				List<Overlay> listOfOverlays = mOsmv.getOverlays();
				listOfOverlays.remove(mapRouteOverlay);
				clearCarLocationFlag();

				mapRouteOverlay = new MapRouteOverlay(mRoad, mOsmv);
				listOfOverlays.add(mapRouteOverlay);

				// draw flag on destination
				GeoPoint gp = new GeoPoint(
						(int) (mRoad.mRoute[mRoad.mRoute.length - 1][1] * CityParkConsts.MILLION),
						(int) (mRoad.mRoute[mRoad.mRoute.length - 1][0] * CityParkConsts.MILLION));

				OverlayItem parkedCarFlag = new OverlayItem(gp, "", "");
				parkedCarOverlayItems.add(parkedCarFlag);
				parkedCarOverlay = new ItemizedIconOverlay(
						CityParkRouteActivity.this, getResources().getDrawable(
								R.drawable.flag));
				parkedCarOverlay.addItem(parkedCarFlag);
				mOsmv.getOverlays().add(parkedCarOverlay);

				mOsmv.invalidate();

				mProgresBar.setVisibility(View.INVISIBLE);

				/*This is customized implementation
				 * RouteDialog.Builder customBuilder = new RouteDialog.Builder(
				 * CityParkRouteActivity.this); customBuilder
				 * .setTitle(mRoad.mName) .setMessage(mRoad.mDescription)
				 * .setPositiveButton(R.string.ok, new
				 * DialogInterface.OnClickListener() { public void
				 * onClick(DialogInterface dialog, int which) {
				 * dialog.dismiss(); } });
				 */
				final/* RouteDialog */AlertDialog rd = DialogFactory
						.getRouteDialog(CityParkRouteActivity.this,
								mRoad.mName, mRoad.mDescription);
				;// customBuilder.create();

				rd.show();
				final Timer t = new Timer();
				t.schedule(new TimerTask() {
					public void run() {
						rd.dismiss(); // when the task active then close the
										// dialog
						t.cancel(); // also just top the timer thread,
									// otherwise, you may receive a crash report
					}
				}, 10000);
			} catch (Exception ex) {
				Log.e(CityParkRouteActivity.class.toString(), ex.getMessage());
			}
		};

	};

	private InputStream getConnection(String url) {
		InputStream is = null;
		try {
			URLConnection conn = new URL(url).openConnection();
			is = conn.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	class MapRouteOverlay extends com.google.android.maps.Overlay {
		Road mRoad;
		ArrayList<GeoPoint> mPoints;

		public MapRouteOverlay(Road road, MapView mv) {
			mRoad = road;
			if (road.mRoute.length > 0) {
				mPoints = new ArrayList<GeoPoint>();
				for (int i = 0; i < road.mRoute.length; i++) {
					mPoints.add(new GeoPoint(
							(int) (road.mRoute[i][1] * CityParkConsts.MILLION),
							(int) (road.mRoute[i][0] * CityParkConsts.MILLION)));
				}
				/*
				 * int moveToLat = (mPoints.get(0).getLatitudeE6() +
				 * (mPoints.get( mPoints.size() - 1).getLatitudeE6() -
				 * mPoints.get(0).getLatitudeE6()) / 2); int moveToLong =
				 * (mPoints.get(0).getLongitudeE6() + (mPoints.get(
				 * mPoints.size() - 1).getLongitudeE6() -
				 * mPoints.get(0).getLongitudeE6()) / 2); GeoPoint moveTo = new
				 * GeoPoint(moveToLat, moveToLong);
				 */

				MapController mapController = mv.getController();
				mapController.animateTo(mPoints.get(0));
				// mapController.setZoom(7);
			}
		}

		@Override
		public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
			super.draw(canvas, mv, shadow);
			drawPath(mv, canvas);
			return true;
		}

		public void drawPath(MapView mv, Canvas canvas) {
			int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
			Paint paint = new Paint();
			Point point;

			paint.setColor(Color.BLUE);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(6);
			for (int i = 0; i < mPoints.size(); i++) {
				point = new Point();
				mv.getProjection().toPixels(mPoints.get(i), point);
				x2 = point.x;
				y2 = point.y;
				if (i > 0) {
					canvas.drawLine(x1, y1, x2, y2, paint);
				}
				x1 = x2;
				y1 = y2;
			}
		}
	}
}
