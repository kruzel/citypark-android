package com.citypark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.citypark.api.task.AllOverlayFetchTask;
import com.citypark.api.task.LoginListener;
import com.citypark.api.task.LoginTask;
import com.citypark.api.task.OverlayListener;
import com.citypark.api.task.ReleasesOverlayFetchTask;
import com.citypark.api.task.ReportParkingReleaseTask;
import com.citypark.api.task.ReportParkingTask;
import com.citypark.constants.CityParkConsts;
import com.citypark.utility.ParkingSessionManager;
import com.citypark.utility.dialog.DialogFactory;
import com.citypark.view.overlay.ItemizedIconOverlay;
import com.citypark.view.overlay.LiveGarageMarkers;
import com.citypark.view.overlay.LiveStreetLinesMarkers;
import com.citypark.view.overlay.LiveStreetReleasesMarkers;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

/**
 * A class for displaying a route map with overlays for directions and nearby
 * bicycle garages.
 * 
 * This file is part of BikeRoute.
 * 
 * Copyright (C) 2011 Jonathan Gray
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * @author jono@nanosheep.net
 * 
 */

public class ParkingMap extends CityParkMapActivity implements LoginListener,
		OnTouchListener, OverlayListener {

	/** markers overlay handler task. */
	private AllOverlayFetchTask overlayTask;
	private ReleasesOverlayFetchTask releasesOverlayTask;
	private LiveGarageMarkers garageMarkers;
	private LiveStreetReleasesMarkers releasesMarkers;
	private LiveStreetLinesMarkers linesMarkers;

	private Boolean firstOverlayLoading = true;

	/** Parked car location overlay **/
	protected ItemizedIconOverlay parkedCarOverlay;
	protected List<OverlayItem> parkedCarOverlayItems;

	/** Location manager. **/
	protected LocationManager mLocationManager;

	/* Constants. */
	protected boolean isSearching = false;

	/** ParkingSessionPersist manager. */
	protected ParkingSessionManager parking_manager;

	/** Dialog display. **/
	protected Dialog dialog;

	/** Application reference. **/
	protected CityParkApp app;

	/** Units for directions. **/
	protected String unit;
	/** payment method **/
	protected String payMethod;

	/** Preferences manager. **/
	protected SharedPreferences mSettings;
	/** Wakelock. **/
	private PowerManager.WakeLock wl;

	private Boolean finishOnPark = false;

	private ReportParkingReleaseTask reportParkingReleaseTask;
	private ReportParkingTask reportParkingTask;

	// map parking releases overlays update handler
	Time lastMapUpdateTime = new Time();

	// map all overlays update handler
	private GeoPoint lastAllOverlaysUpdateCenter = null;
	private Time lastAllOverlaysUpdateTime = new Time();
	private int lastZoomLevel = 0;

	private MediaPlayer mMediaPlayer;

	// map overlays handler
	private Handler mHandler = new Handler();
	// map overlays thread
	private Runnable mUpdateOverlaysTask = new Runnable() {
		public void run() {
			if (parking_manager.isParking())
				return;
			
			float[] results = new float[3];
			if(lastAllOverlaysUpdateCenter!=null)
				Location.distanceBetween(lastAllOverlaysUpdateCenter.getLatitudeE6()/1E6, lastAllOverlaysUpdateCenter.getLongitudeE6()/1E6, mOsmv.getMapCenter().getLatitudeE6()/1E6, mOsmv.getMapCenter().getLongitudeE6()/1E6, results);

			if ((lastAllOverlaysUpdateCenter != null && results[0] > 250)
					|| (mOsmv.getZoomLevel() > lastZoomLevel)
					&& mOsmv.getZoomLevel() == 15) {
				showAllParkings(false);
			} else {
				Time curTime = new Time();
				curTime.setToNow();
				if (curTime.toMillis(true) - lastMapUpdateTime.toMillis(true) > CityParkConsts.OVERLAY_UPDATE_INTERVAL * 15) {
					// refresh only releases points
					if (mOsmv.getZoomLevel() >= 15
							&& lastAllOverlaysUpdateCenter != null) {
						releasesOverlayTask.cancel(true);
						releasesOverlayTask = new ReleasesOverlayFetchTask(mOsmv, ParkingMap.this, ParkingMap.this,
								garageMarkers, releasesMarkers);
						releasesOverlayTask
								.execute(lastAllOverlaysUpdateCenter);
						lastMapUpdateTime.setToNow();
					}
				}
			}

			mHandler.postDelayed(this, CityParkConsts.OVERLAY_UPDATE_INTERVAL);
		}
	};

	private Runnable mUnparkDialogTimer = new Runnable() {
		public void run() {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			// don't assume, avoid fault positive
			// unpark();

			if (finishOnPark) {
				setResult(1);
				finish();
			}
		}
	};

	private Runnable mParkDialogTimer = new Runnable() {
		public void run() {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			// don't assume, avoid fault positive
			// park();

			if (finishOnPark) {
				setResult(1);
				finish();
			}
		}
	};

	@Override
	public void onCreate(final Bundle savedState) {
		super.onCreate(savedState);

		// TODO proper rotation handling while avoiding re-fetch of all overlays
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Get application reference
		app = (CityParkApp) getApplication();
		
		/* Get Preferences. */
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		
		payMethod = mPrefs.getString(getString(R.string.payment_method), null);

		// Get wake lock
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Navigating");

		// Initialize map, view & controller
		setContentView(R.layout.main);
		this.mOsmv = (MapView) findViewById(R.id.mapview);

		this.mLocationOverlay = new MyLocationOverlay(
				this.getApplicationContext(), this.mOsmv);
		this.mLocationOverlay.enableCompass();
		
		this.mOsmv.displayZoomControls(false);
		this.mOsmv.getOverlays().add(this.mLocationOverlay);

		mOsmv.getController().setZoom(
				mPrefs.getInt(getString(R.string.prefs_zoomlevel), 16));
		
		int x = mPrefs.getInt(getString(R.string.prefs_scrollx), 0);
		int y = mPrefs.getInt(getString(R.string.prefs_scrolly), 0);
		if(x!=0 && y!=0)
			mOsmv.scrollTo(x,y);
		
		mOsmv.setBuiltInZoomControls(true);
		
		/* Get location manager. */
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationHandler(mOsmv.getController()));

		// Directions overlay
		final View overlay = findViewById(R.id.directions_overlay);
		overlay.setVisibility(View.INVISIBLE);


		// Initialize parking manager
		parking_manager = new ParkingSessionManager(this);

		// init live marker updaters
		garageMarkers = new LiveGarageMarkers(mOsmv, this);
		linesMarkers = new LiveStreetLinesMarkers(mOsmv, this);
		releasesMarkers = new LiveStreetReleasesMarkers(mOsmv, this);

		overlayTask = new AllOverlayFetchTask(mOsmv, this, this, garageMarkers,
				releasesMarkers, linesMarkers);
		releasesOverlayTask = new ReleasesOverlayFetchTask(mOsmv, this, this,
				garageMarkers, releasesMarkers);

		parkedCarOverlayItems = new ArrayList<OverlayItem>(1);

		if (parking_manager.isParking()) {
			setCarLocationFlag(parking_manager.getGeoPoint());
		}
	}

	@Override
	protected void onDestroy() {
		if (!parking_manager.isParking())
			app.finishAllAppObjecs();

		mHandler.removeCallbacks(mUpdateOverlaysTask);

		super.onDestroy();
	}

	/**
	 * Handle jump intents from directions view.
	 */

	@Override
	public void onNewIntent(final Intent intent) {
		if (intent.getBooleanExtra(getString(R.string.unpark), false)) {
			playNotification();
			showDialog(R.id.unpark);
		}
		// check if got back from PaymentActivity
		if (intent.getBooleanExtra("PaymentActivityResult", false)) {
			if (!parking_manager.isPaymentActive()
					&& !parking_manager.isReminderActive()) {
				unparkCompletion();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		/* Units preferences. */
		unit = mSettings.getString("unitsPref", "km");
		this.mLocationOverlay.enableMyLocation();
		this.mLocationOverlay.enableCompass();

		if (mSettings.getBoolean("keepAwake", false)) {
			wl.acquire();
		}

		lastAllOverlaysUpdateCenter = mOsmv.getMapCenter();
		lastZoomLevel = mOsmv.getZoomLevel();

		mOsmv.setOnTouchListener(this);

		payMethod = mPrefs.getString(getString(R.string.payment_method), null);

		// check if awaken by location receiver
		if (getIntent().getBooleanExtra(getString(R.string.unpark), false)) {
			playNotification();
			showDialog(R.id.unpark);
		}
		// check if got back from PaymentActivity
		if (getIntent().getBooleanExtra("PaymentActivityResult", false)) {
			if (!parking_manager.isPaymentActive()
					&& !parking_manager.isReminderActive()) {
				unparkCompletion();
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		this.mLocationOverlay.disableMyLocation();
		this.mLocationOverlay.disableCompass();
		if (wl.isHeld()) {
			wl.release();
		}
		
		mPrefs.edit().putInt(getString(R.string.prefs_zoomlevel), 16);
		mPrefs.edit().commit();
	}

	@Override
	protected void onStart() {
		// RouteMap.this.mLocationOverlay.followLocation(true);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		float[] results = new float[3];
		if(lastAllOverlaysUpdateCenter!=null) {
			Location.distanceBetween(lastAllOverlaysUpdateCenter.getLatitudeE6()/1E6, lastAllOverlaysUpdateCenter.getLongitudeE6()/1E6, mOsmv.getMapCenter().getLatitudeE6()/1E6, mOsmv.getMapCenter().getLongitudeE6()/1E6, results);
		}
		
		if (LoginTask.isLoggedIn()) {
			if (!parking_manager.isParking()) {
				if (lastAllOverlaysUpdateCenter == null
						|| (lastAllOverlaysUpdateCenter != null &&  results[0] > 250)) {
					showAllParkings(true);
					mHandler.removeCallbacks(mUpdateOverlaysTask);
					mHandler.postDelayed(mUpdateOverlaysTask,
							CityParkConsts.OVERLAY_UPDATE_INTERVAL);

				}
			}
		} else {
			if (LoginTask.isRegistered()) {
				LoginTask.login(this);
				showDialog(R.id.awaiting_login);
			} else
				this.startActivity(new Intent(this, RegisterActivity.class));
		}

		super.onStart();
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	@Override
	public void loginComplete(String sessionId) {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

		showAllParkings(true);

		app.doBindService();
	}

	@Override
	public void loginFailed() {
		if (dialog.isShowing())
			dialog.dismiss();

		Toast.makeText(this, getString(R.string.login_failed),
				Toast.LENGTH_LONG).show();

		// user is not registered, register now
		this.startActivity(new Intent(this, RegisterActivity.class));
	}

	/**
	 * Creates dialogs for loading, on errors, alerts. Available dialogs:
	 * Planning progress, planning error, unpark.
	 * 
	 * @return the appropriate Dialog object
	 */

	@Override
	public Dialog onCreateDialog(final int id) {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
		AlertDialog.Builder builder;
		ProgressDialog pDialog;
		switch (id) {
		case R.id.unpark:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.unpark_ack))
					.setCancelable(false)
					.setPositiveButton(getString(R.string.yes),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int id) {
									unpark();
									dialog.dismiss();
								}
							})
					.setNegativeButton(getString(R.string.no),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int id) {
									dialog.cancel();
								}
							});
			dialog = builder.create();
			mHandler.postDelayed(mUnparkDialogTimer, 30000);
			break;
		case R.id.park:
			playNotification();
			builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.park_ack))
					.setCancelable(false)
					.setPositiveButton(getString(R.string.yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									park();
									dialog.cancel();
									if (finishOnPark) {
										setResult(1);
										finish();
									}
								}
							})
					.setNegativeButton(getString(R.string.no),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									if (finishOnPark) {
										app.finishAllAppObjecs(); // terminate
																	// app
										setResult(1);
										finish();
									}
								}
							});
			dialog = builder.create();
			mHandler.postDelayed(mParkDialogTimer, 30000);
			break;
		case R.id.awaiting_fix:
			pDialog = new ProgressDialog(this);
			pDialog.setCancelable(true);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage(getText(R.string.fix_msg));
			pDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					ParkingMap.this.removeDialog(R.id.awaiting_fix);
				}
			});
			dialog = pDialog;
			break;
		case R.id.awaiting_login:
			pDialog = new ProgressDialog(this);
			pDialog.setCancelable(true);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage(getText(R.string.awaiting_login));
			pDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					ParkingMap.this.removeDialog(R.id.awaiting_login);
				}
			});
			dialog = pDialog;
			break;
		case R.id.loading_info:
			pDialog = new ProgressDialog(this);
			pDialog.setCancelable(true);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage(getText(R.string.load_msg));
			pDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					ParkingMap.this.removeDialog(R.id.loading_info);
				}
			});
			dialog = pDialog;
			break;
		case R.id.about:
			dialog = DialogFactory.getAboutDialog(this);
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	/**
	 * Create the options menu.
	 * 
	 * @return true if menu created.
	 */

	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return true;
	}

	/**
	 * Prepare the menu. Set parking related menus to reflect parked or unparked
	 * state, set directions menu & turnbyturn visible only if a route has been
	 * planned.
	 * 
	 * @return a boolean indicating super's state.
	 */

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		final MenuItem park = menu.findItem(R.id.park);
		final MenuItem unPark = menu.findItem(R.id.unpark);
		final MenuItem steps = menu.findItem(R.id.directions);
		final MenuItem garageList = menu.findItem(R.id.garagelist);
		final MenuItem turnByTurn = menu.findItem(R.id.turnbyturn);
		final MenuItem map = menu.findItem(R.id.map);
		final MenuItem elev = menu.findItem(R.id.elevation);
		final MenuItem share = menu.findItem(R.id.sharing);
		final MenuItem csShare = menu.findItem(R.id.share);
		final MenuItem save = menu.findItem(R.id.save);
		final MenuItem navigate = menu.findItem(R.id.navigate);
		final MenuItem stopService = menu.findItem(R.id.stop_nav);

		// TODO need to resolve Geocoding stability issue (web service
		// availability)
		navigate.setVisible(false);

		if (parking_manager.isParking() || parking_manager.isPaymentActive()
				|| parking_manager.isReminderActive()) {
			park.setVisible(false);
			unPark.setVisible(true);
		} else {
			park.setVisible(true);
			unPark.setVisible(false);
		}
		garageList.setVisible(true);// Ran
		csShare.setVisible(true);
		stopService.setVisible(true);

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Handle option selection.
	 * 
	 * @return true if option selected.
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.share:
			Intent target = new Intent(Intent.ACTION_SEND);
			target.putExtra(Intent.EXTRA_SUBJECT, "CityPark");
			target.putExtra(Intent.EXTRA_TEXT,
					getString(R.string.share_citypark) + " "
							+ getString(R.string.citypark_website));
			target.setType("text/plain");
			intent = Intent.createChooser(target,
					getString(R.string.share_chooser_title));
			startActivity(intent);
			break;
		case R.id.feedback:
			intent = new Intent(this, Feedback.class);
			startActivity(intent);
			break;
		case R.id.prefs:
			intent = new Intent(this, Preferences.class);
			startActivityForResult(intent, R.id.trace);
			break;
		case R.id.unpark:
			unpark();
			break;
		case R.id.center:
			Location self = mLocationOverlay.getLastFix();

			if (self == null) {
				self = mLocationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			if (self == null) {
				self = mLocationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			if (self != null) {
				GeoPoint p = new GeoPoint((int)(self.getLatitude()*1E6),(int)(self.getLongitude()*1E6));
				mOsmv.getController().setCenter(p);
			}
			// showAllParkings(false);
			break;
		case R.id.showparking:
			Toast.makeText(this, "Getting garages from OpenStreetMap..",
					Toast.LENGTH_LONG).show();
			showAllParkings(true);
			return true;
		case R.id.garagelist:
			intent = new Intent(this, GarageListActivity.class);
			GeoPoint gp = mOsmv.getMapCenter();
			intent.putExtra(CityParkConsts.LATITUDE,
					(double) gp.getLatitudeE6());
			intent.putExtra(CityParkConsts.LONGITUDE,
					(double) gp.getLongitudeE6());
			startActivity(intent);
			break;
		case R.id.park:
			park();
			if (dialog.isShowing())
				dialog.dismiss();

			if (payMethod.contains("Pango")) {
				intent = new Intent(this, PaymentPangoActivity.class);
				startActivity(intent);
			} else if (payMethod.contains("CelOpark")) {
				intent = new Intent(this, PaymentCelOParkActivity.class);
				startActivity(intent);
			} else {
				intent = new Intent(this, PaymentActivity.class);
				startActivity(intent);
				// Toast.makeText(this, "Payment provider no set !",
				// Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.map:
			break;
		case R.id.about:
			showDialog(R.id.about);
			break;
		case R.id.save:
			showDialog(R.id.save);
			break;
		case R.id.stop_nav:
			stopNavigation();
			checkParkAndFinish(true,1);
			break;
		default:
			return false;

		}
		return true;
	}

	public void showAllParkings(Boolean showProgDialog) {
		if (!LoginTask.isLoggedIn())
			return;

		if (mOsmv.getZoomLevel() < 15)
			return;

		if (parking_manager.isParking())
			return;

		if (showProgDialog) {
			firstOverlayLoading = true;
			if (dialog == null || (dialog != null && !dialog.isShowing()))
				showDialog(R.id.loading_info);
		}
		
		overlayTask.cancel(true);
		overlayTask = new AllOverlayFetchTask(mOsmv, this, this, garageMarkers,
				releasesMarkers, linesMarkers);
		overlayTask.execute(mOsmv.getMapCenter());

		lastAllOverlaysUpdateTime.setToNow();
		lastAllOverlaysUpdateCenter = mOsmv.getMapCenter();
		lastZoomLevel = mOsmv.getZoomLevel();
	}

	public void stopNavigation() {
	
	}

	public void checkParkAndFinish(final Boolean finish, final int result) {
		// open dialog and ask user if he really un-parked
		if (parking_manager.isParking() || parking_manager.isPaymentActive()
				|| parking_manager.isReminderActive()) {
			// keep location tracking on for unpark detection
			setResult(1);
			finish();
		} else {
			// check with user if really parked
			finishOnPark = finish;
			showDialog(R.id.park);
		}
	}

	public void park() {
		showDialog(R.id.awaiting_fix);
		ParkingMap.this.mLocationOverlay.runOnFirstFix(new Runnable() {
			@Override
			public void run() {
				Location self = mLocationOverlay.getLastFix();

				if (self == null) {
					self = mLocationManager
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				}
				if (self == null) {
					self = mLocationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				}
				if (self != null) {
					parking_manager.park(new GeoPoint((int)(self.getLatitude()*1E6), (int)(self
							.getLongitude()*1E6)));
					setCarLocationFlag(parking_manager.getGeoPoint());

					if (LoginTask.isLoggedIn()) {
						reportParkingTask = new ReportParkingTask(
								ParkingMap.this, LoginTask.getSessionId(), self
										.getLatitude(), self.getLongitude());
						reportParkingTask.execute((Void[]) null);
					}
				}
			}
		});

		stopNavigation();
	}

	public void unpark() {
		if (parking_manager.isPaymentActive()
				|| parking_manager.isReminderActive()) {
			Intent intent;

			if (payMethod.contains("Pango")) {
				intent = new Intent(this, PaymentPangoActivity.class);
				startActivityForResult(intent, R.id.payment);
			} else if (payMethod.contains("CelOpark")) {
				intent = new Intent(this, PaymentCelOParkActivity.class);
				startActivityForResult(intent, R.id.payment);
			} else {
				unparkCompletion();
				intent = new Intent(this, PaymentActivity.class);
				startActivityForResult(intent, R.id.payment);
				Toast.makeText(this,
						getString(R.string.payment_method_not_defined),
						Toast.LENGTH_LONG).show();
			}
		} else
			unparkCompletion();
	}

	public void unparkCompletion() {
		clearCarLocationFlag();

		if (LoginTask.isLoggedIn()) {
			showDialog(R.id.awaiting_fix);
			ParkingMap.this.mLocationOverlay.runOnFirstFix(new Runnable() {
				@Override
				public void run() {
					if (ParkingMap.this.dialog.isShowing())
						ParkingMap.this.dialog.dismiss();

					if (parking_manager.isParking()) {
						reportParkingReleaseTask = new ReportParkingReleaseTask(
								ParkingMap.this,
								LoginTask.getSessionId(),
								parking_manager.getGeoPoint().getLatitudeE6() / 1E6,
								parking_manager.getGeoPoint().getLongitudeE6() / 1E6);
						reportParkingReleaseTask.execute();
						parking_manager.unPark();
						// firstOverlayLoading = true;
						LoginTask.setSessionId(null);
						LoginTask.login(ParkingMap.this); // renew session
					}
				}
			});
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO this code is called only once - MapView bug?

		return false;
	}

	@Override
	public void overlayFetchComplete(final Boolean garagesRes,
			final Boolean releasesRes, final Boolean linesRes) {

		if (LoginTask.isLoggedIn()) {
			if (!parking_manager.isParking()) {
				if (linesRes)
					linesMarkers.updateMap();
				if (releasesRes)
					releasesMarkers.updateMap();
				if (garagesRes)
					garageMarkers.updateMap();

				mHandler.removeCallbacks(mUpdateOverlaysTask);
				mHandler.postDelayed(mUpdateOverlaysTask,
						CityParkConsts.OVERLAY_UPDATE_INTERVAL);
			}

			if (firstOverlayLoading) {
				firstOverlayLoading = false;

				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
			}
		}
	}

	private void playNotification() {
		// send off alarm sound
		Uri alert = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if (alert == null) {
			// alert is null, using backup
			alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			if (alert == null) { // I can't see this ever being null (as always
									// have a default notification) but just in
									// case
				// alert backup is null, using 2nd backup
				alert = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_ALARM);
			}
		}

		mMediaPlayer = new MediaPlayer();
		try {
			parking_manager.stopReminder();
			mMediaPlayer.setDataSource(this, alert);

			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void setCarLocationFlag(GeoPoint p) {
		// stop overlays updates
		mHandler.removeCallbacks(mUpdateOverlaysTask);

		garageMarkers.clearFromMap();
		releasesMarkers.clearFromMap();
		linesMarkers.clearFromMap();

		// clear car location flag if exist
		if (mOsmv.getOverlays().contains(parkedCarOverlay)) {
			mOsmv.getOverlays().remove(parkedCarOverlay);
		}
		parkedCarOverlayItems.clear();

		OverlayItem parkedCarFlag = new OverlayItem(p, "", "");
		parkedCarOverlayItems.add(parkedCarFlag);
		parkedCarOverlay = new ItemizedIconOverlay(this,
						getResources().getDrawable(
						R.drawable.flag));
		parkedCarOverlay.addItem(parkedCarFlag);
		mOsmv.getOverlays().add(parkedCarOverlay);
	}

	protected void clearCarLocationFlag() {

		if (mOsmv.getOverlays().contains(parkedCarOverlay)) {
			mOsmv.getOverlays().remove(parkedCarOverlay);
		}
		parkedCarOverlayItems.clear();
		parkedCarOverlay = null;
	}
}
