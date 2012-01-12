package com.citypark;

import java.io.FileOutputStream;
import java.util.List;
import java.util.ListIterator;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.acra.ErrorReporter;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.PathOverlay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.citypark.constants.CityParkConsts;
import com.citypark.service.LoginListener;
import com.citypark.service.LoginTask;
import com.citypark.service.ReportParkingReleaseTask;
import com.citypark.service.ReportParkingTask;
import com.citypark.service.RoutePlannerTask;
import com.citypark.utility.CarAlert;
import com.citypark.utility.Convert;
import com.citypark.utility.ParkingSessionPersist;
import com.citypark.utility.TurnByTurnGestureListener;
import com.citypark.utility.dialog.DialogFactory;
import com.citypark.utility.route.PGeoPoint;
import com.citypark.utility.route.Route;
import com.citypark.utility.route.Segment;
import com.citypark.view.overlay.LiveGarageMarkers;
import com.citypark.view.overlay.LiveStreetLinesMarkers;
import com.citypark.view.overlay.LiveStreetReleasesMarkers;
import com.citypark.view.overlay.RouteOverlay;

/**
 * A class for displaying a route map with overlays for directions and
 * nearby bicycle garages.
 * 
 * This file is part of BikeRoute.
 * 
 * Copyright (C) 2011  Jonathan Gray
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * @author jono@nanosheep.net
 * 
 */

public class RouteMap extends OpenStreetMapActivity implements LoginListener, OnTouchListener {

	/** ParkingOverlayHandler markers overlay. */
	private LiveGarageMarkers garages;
	/** Street segments lines overlay **/
	private LiveStreetLinesMarkers streetSegments;
	/** Street parking releases overlay **/
	private LiveStreetReleasesMarkers streetParkingReleases;
	/** Route overlay. **/
	protected PathOverlay routeOverlay;
	/** Travelled route overlay. **/
	protected PathOverlay travelledRouteOverlay;
	/** Location manager. **/
	protected LocationManager mLocationManager;
	
	/* Constants. */
	protected boolean isSearching = false;

	/** ParkingSessionPersist manager. */
	protected ParkingSessionPersist parking_manager;

	/** Bike alert manager. **/
	private CarAlert carAlert;
	
	/** Dialog display. **/
	protected Dialog dialog;
	
	/** Application reference. **/
	protected CityParkApp app;
	
	/** Onscreen directions shown. **/
	protected boolean directionsVisible;
	
	/** Gesture detection for the onscreen directions. **/
    private GestureDetector gestureDetector;
	private OnTouchListener gestureListener;
	
	/** Units for directions. **/
	protected String unit;
	/** payment method **/
	protected String payMethod;
	
	/** Preferences manager. **/
	protected SharedPreferences mSettings;
	/** Wakelock. **/
	private PowerManager.WakeLock wl;
	
	Boolean finishOnPark = false;
	
	/** user info**/
	private String strEmail = null;
	private String strPassword = null;
	//login info
	Boolean isLoginInProgress = false;
	
	ReportParkingReleaseTask reportParkingReleaseTask;
	ReportParkingTask reportParkingTask;
	
	//map overlays update handler
	GeoPoint LastMapCenter = null;
	Time lastMapUpdateTime = new Time();
	
	private Handler mHandler = new Handler();
	private Boolean mFirstTimeOverlayUpdaye = true; //work around to MapView OnTouch event received only once
	
	private Runnable mUpdateOverlaysTask = new Runnable() {
		   public void run() {
			   if(LastMapCenter!=null && LastMapCenter.distanceTo(mOsmv.getMapCenter()) > 100)
			   		showAllParkings();
			   else {
				   Time curTime = new Time();
				   curTime.setToNow();
				   if(curTime.toMillis(true) - lastMapUpdateTime.toMillis(true) > CityParkConsts.OVERLAY_UPDATE_INTERVAL * 3) //refresh ony releases points
					   streetParkingReleases.refresh(mOsmv.getMapCenter());
			   }
			   
			   lastMapUpdateTime.setToNow();
			   LastMapCenter = mOsmv.getMapCenter();
			   mHandler.postDelayed(this, CityParkConsts.OVERLAY_UPDATE_INTERVAL);
		   }
		};

	@Override
	public void onCreate(final Bundle savedState) {
		super.onCreate(savedState);
		
		showDialog(R.id.awaiting_fix);
		
		/* Get Preferences. */
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		/* Get location manager. */
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		payMethod = mPrefs.getString("payment_method", null);
		
		//Set OSD invisible
		directionsVisible = false;
		
		//Get wakelock
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Navigating");
		
		// Initialize map, view & controller
		setContentView(R.layout.main);
		this.mOsmv = (MapView) findViewById(R.id.mapview);
		//this.mOsmv.setResourceProxy(mResourceProxy);
        mOsmv.setTileSource(TileSourceFactory.MAPNIK);
        this.mLocationOverlay = new MyLocationOverlay(this.getApplicationContext(), this.mOsmv, mResourceProxy);
        this.mLocationOverlay.enableCompass();
        this.mOsmv.setBuiltInZoomControls(true);
        this.mOsmv.setMultiTouchControls(true);
        this.mOsmv.getOverlays().add(this.mLocationOverlay);
        this.mOsmv.getOverlays().add(new OSDOverlay(this));
        
        /* Route paths. **/
		routeOverlay = new RouteOverlay(Color.BLUE,this);
		travelledRouteOverlay = new RouteOverlay(Color.GREEN,this);
		mOsmv.getOverlays().add(routeOverlay);
		mOsmv.getOverlays().add(travelledRouteOverlay);
        

        mOsmv.getController().setZoom(mPrefs.getInt(getString(R.string.prefs_zoomlevel), 16));
        mOsmv.scrollTo(mPrefs.getInt(getString(R.string.prefs_scrollx), 0), 
        		mPrefs.getInt(getString(R.string.prefs_scrolly), 0));
		mOsmv.setBuiltInZoomControls(true);
		
		//Directions overlay
		final View overlay = findViewById(R.id.directions_overlay);
		overlay.setVisibility(View.INVISIBLE);
		
		//Get application reference
		app = (CityParkApp)getApplication();
		
		mOsmv.getController().setZoom(app.getZoom());

		// Initialize parking manager
		parking_manager = new ParkingSessionPersist(this);

		// Initialize car alert manager
		carAlert = new CarAlert(this);
		
		//init live marker updaters
		garages = new LiveGarageMarkers(mOsmv, this, app);
		streetSegments = new LiveStreetLinesMarkers(mOsmv, this, app);
		streetParkingReleases = new LiveStreetReleasesMarkers(mOsmv, this, app);
				
		//Handle rotations
		final Object[] data = (Object[]) getLastNonConfigurationInstance();
		if ((data != null) && ((Boolean) data[0])) {
			mOsmv.getController().setZoom(mPrefs.getInt(getString(R.string.prefs_zoomlevel), 16));
			showStep();
			//TODO open in last location
		}
		
		if(app.getSessionId()==null) {
			//center on my location
			RouteMap.this.mLocationOverlay.followLocation(true);
		}
				
		if (getIntent().getIntExtra(RoutePlannerTask.PLAN_TYPE, RoutePlannerTask.ADDRESS_PLAN) == RoutePlannerTask.BIKE_PLAN) {
			//TODO make it work again
			carAlert.setCarAlert(parking_manager.getLocation());
		}
	}

	/**
	 * Handle jump intents from directionsview.
	 */
	
	@Override
	public void onNewIntent(final Intent intent) {
		if (intent.getBooleanExtra(getString(R.string.jump_intent), false) && app.getRoute() != null) {
			showStep();
			traverse(app.getSegment().startPoint());
			mOsmv.getController().setCenter(app.getSegment().startPoint());
		}
		if (intent.getIntExtra(RoutePlannerTask.PLAN_TYPE, RoutePlannerTask.ADDRESS_PLAN) == RoutePlannerTask.BIKE_PLAN) {
			carAlert.setCarAlert(parking_manager.getLocation());
		}
		if (intent.getBooleanExtra(getString(R.string.unpark), false)) {
			showDialog(R.id.unpark);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		/* Units preferences. */
		unit = mSettings.getString("unitsPref", "km");
		this.mLocationOverlay.enableMyLocation();
        this.mLocationOverlay.disableFollowLocation();
        this.mLocationOverlay.enableCompass();
        mOsmv.setTileSource(TileSourceFactory.getTileSource(mSettings.getString("tilePref", "Mapnik")));
        
	      //center on my location
	    //RouteMap.this.mLocationOverlay.followLocation(true);
        
        if(app.getRoute() != null) {
        	ErrorReporter.getInstance().putCustomData("Route", app.getRoute().getName());
        	ErrorReporter.getInstance().putCustomData("Router", app.getRoute().getRouter());
        	ErrorReporter.getInstance().putCustomData("Route Length", app.getRoute().getSegments().toString());
        	traverse(app.getSegment().startPoint());
    		mOsmv.getController().setCenter(app.getSegment().startPoint());
    		if(mSettings.getBoolean("keepAwake", false)) {
    			wl.acquire();
    		}
        }
        
        if(app.getSessionId() != null) 
        	loginComplete(app.getSessionId());
        else
        	login();
        
	    mHandler.removeCallbacks(mUpdateOverlaysTask);
	    mHandler.postDelayed(mUpdateOverlaysTask, CityParkConsts.OVERLAY_UPDATE_INTERVAL);
	      
        mOsmv.setOnTouchListener(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		this.mLocationOverlay.disableMyLocation();
		this.mLocationOverlay.disableCompass();
		if (wl.isHeld()) {
			wl.release();
		}
		app.setZoom(mOsmv.getZoomLevel());
		
		mHandler.removeCallbacks(mUpdateOverlaysTask);

	}
	
	public void login() {
		if(app.getSessionId() == null && !isLoginInProgress) {       	
        	strEmail = mPrefs.getString("email", null);
            strPassword = mPrefs.getString("password", null);
            
        	if((strEmail == null) || (strEmail.length() == 0) || (strPassword == null) ||(strPassword.length() == 0) ) {
        		//user is not registered, register now
        		this.startActivity(new Intent(this, RegisterActivity.class));
	        }
	        else {
	        	isLoginInProgress = true;
	        	LoginTask loginTask = new LoginTask(strEmail, strPassword, this,this);
	          	loginTask.execute((Void[])null);
	        }
		}
		else 
			showAllParkings();
		return;
	}
	
	@Override
	public void loginComplete(String sessionId) {
		app.setSessionId(sessionId);
		showAllParkings();
		isLoginInProgress = false;
		app.doBindService();
		
		if(dialog.isShowing())
			dialog.dismiss();
	}
	
	/**
	 * Draw the route to the map.
	 */
	@Deprecated
	protected void viewRoute() {
		if (routeOverlay == null) {
			routeOverlay = new RouteOverlay(Color.BLUE,this);
			travelledRouteOverlay = new RouteOverlay(Color.GREEN,this);
			for(PGeoPoint pt : app.getRoute().getPoints()) {
				routeOverlay.addPoint(pt);
			}
			mOsmv.getOverlays().add(routeOverlay);
			mOsmv.getOverlays().add(travelledRouteOverlay);
		}
		mOsmv.invalidate();
		
		lastMapUpdateTime.setToNow();
	}
	
	/**
	 * Creates dialogs for loading, on errors, alerts.
	 * Available dialogs:
	 * Planning progress, planning error, unpark.
	 * @return the appropriate Dialog object
	 */
	
	@Override
	public Dialog onCreateDialog(final int id) {
		AlertDialog.Builder builder;
		ProgressDialog pDialog;
		switch(id) {
		case R.id.unpark:
			builder = new AlertDialog.Builder(this);
			builder.setMessage("Unpark?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int id) {
									unpark();			
									dialog.dismiss();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										final DialogInterface dialog,
										final int id) {
									dialog.cancel();
								}
							});
			dialog = builder.create();
			break;
		case R.id.park:
			builder = new AlertDialog.Builder(this);
			builder.setMessage("Have you parked?")
			       .setCancelable(false)
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   park();
			        	   dialog.cancel();
			        	   if(finishOnPark) {
			        		   setResult(1);
			       				finish();
			        	   }
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			                if(finishOnPark) {
			        		   setResult(1);
			       				finish();
			        	   }
			           }
			       });
			dialog = builder.create();
			break;
		case R.id.awaiting_fix:
			pDialog = new ProgressDialog(this);
			pDialog.setCancelable(true);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage(getText(R.string.fix_msg));
			pDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					RouteMap.this.removeDialog(R.id.awaiting_fix);
				}
			});
			dialog = pDialog;
			break;
		case R.id.about:
			dialog = DialogFactory.getAboutDialog(this);
			break;
		case R.id.save:
			dialog = DialogFactory.getSaveDialog(this, app.getRoute());
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	/**
	 * Create the options menu.
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
	 * state, set directions menu & turnbyturn visible only if a route has been planned.
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
		if (parking_manager.isParking() || parking_manager.isPaymentActive() || parking_manager.isReminderActive()) {
			park.setVisible(false);
			unPark.setVisible(true);
		} else {
			park.setVisible(true);
			unPark.setVisible(false);
		}
		garageList.setVisible(true);//Ran
		if (app.getRoute() != null) {
			save.setVisible(true);
			//steps.setVisible(true);
			//elev.setVisible(true);
			//share.setVisible(true);
//			if (directionsVisible) {
//				turnByTurn.setVisible(false);
//				map.setVisible(true);
//			} else {
//				turnByTurn.setVisible(true);
//				map.setVisible(false);
//			}
			
//			if (app.getRoute().getRouter().equals(CityParkConsts.CS)) {
//				//csShare.setVisible(true);
//				menu.setGroupVisible(R.id.cyclestreets, true);
//			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Handle option selection.
	 * @return true if option selected.
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.share:
			Intent target = new Intent(Intent.ACTION_SEND);
			target.putExtra(Intent.EXTRA_TEXT, getString(R.string.cs_jump) + app.getRoute().getItineraryId());
			target.setType("text/plain");
			intent = Intent.createChooser(target, getString(R.string.share_chooser_title));
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
			RouteMap.this.mLocationOverlay.followLocation(true);
			showAllParkings();
			break;
		case R.id.showparking:
			Toast.makeText(this, "Getting garages from OpenStreetMap..",
					Toast.LENGTH_LONG).show();
			showAllParkings();
			return true;
		case R.id.garagelist:
			intent = new Intent(this, GarageListActivity.class);
			GeoPoint gp = mOsmv.getMapCenter();
			intent.putExtra(CityParkConsts.LATITUDE, (double)gp.getLatitudeE6());
			intent.putExtra(CityParkConsts.LONGITUDE, (double)gp.getLongitudeE6());
			startActivity(intent);
			break;
		case R.id.export:
			String xml = app.getRoute().toXml();
			export(xml, R.string.filename);
			break;
		case R.id.export_gpx:
			String gpx = app.getRoute().toGPX();
			export(gpx, R.string.filename_gpx);
			break;
		case R.id.park:
			if(app.getSessionId()!=null) {
				showDialog(R.id.awaiting_fix);
				RouteMap.this.mLocationOverlay.runOnFirstFix(new Runnable() {
					@Override
					public void run() {
						if (RouteMap.this.dialog.isShowing()) {
								RouteMap.this.dismissDialog(R.id.awaiting_fix);
								Location self = mLocationOverlay.getLastFix();
								
								if (self == null) {
									self = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
								}
								if (self == null) {
									self = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
								}
								if (self != null) {
									parking_manager.park(new PGeoPoint(self.getLatitude(), self.getLongitude()));
									reportParkingTask = new ReportParkingTask(RouteMap.this, app.getSessionId(), self.getLatitude(), self.getLongitude());
									reportParkingTask.execute(null);
								}
						}
					}
				});
			}
			
			stopNavigation();
			
			if(payMethod.contains("Pango")) {
				intent = new Intent(this, PaymentPangoActivity.class);
				startActivity(intent);
			} else if(payMethod.contains("CelOpark")) {
				intent = new Intent(this, PaymentCelOParkActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(this, "Undefined payment provider...",
						Toast.LENGTH_LONG).show();
			}
			
			break;
		case R.id.directions:
			intent = new Intent(this, DirectionsView.class);
			startActivityForResult(intent, R.id.trace);
			break;
		case R.id.turnbyturn:
			showStep();
			break;
		case R.id.map:
			hideStep();
			break;
		case R.id.navigate:
			intent = new Intent(this, Navigate.class);
			startActivityForResult(intent, R.id.trace);
			break;
		case R.id.elevation:
			XYMultipleSeriesDataset elevation = app.getRoute().getElevations();
			final XYMultipleSeriesRenderer renderer = app.getRoute().getChartRenderer();
			if (!"km".equals(unit)) {
				elevation = Convert.asImperial(elevation);
				renderer.setYTitle("ft");
			    renderer.setXTitle("m");
			}
			intent = ChartFactory.getLineChartIntent(this, elevation, renderer);
			startActivityForResult(intent, R.id.trace);
			break;
		case R.id.about:
			showDialog(R.id.about);
			break;
		case R.id.save:
			showDialog(R.id.save);
			break;
		default:
			return false;

		}
		return true;
	}
	
	/**
	 * Helper for sharing xml'd route files.
	 * @param content
	 * @param fileNameId
	 */
	
	private void export(final String content, int fileNameId) {
		Intent t;
		FileOutputStream fos;
		try {
			fos = openFileOutput(getString(fileNameId), Context.MODE_WORLD_READABLE);
			fos.write(content.getBytes());
			fos.close();
		} catch (Exception e) {
			Log.e("File export", e.getMessage());
		}
		t = new Intent(Intent.ACTION_SEND);
		t.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getFileStreamPath(getString(fileNameId))));
		t.setType("text/xml");
		startActivity(Intent.createChooser(t, getString(R.string.share_chooser_title)));
	}
	
	/**
	 * Retain any route data if the screen is rotated.
	 */
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		Object[] objs = new Object[1];
		objs[0] = directionsVisible;
	    return objs;
	}
	
	/**
	 * Traverse the route up to the point given, overlay a different coloured route
	 * up to there.
	 * @param point
	 */
	
	protected void traverse(final PGeoPoint point) {
		ErrorReporter.getInstance().putCustomData("CrashPoint", point.toString());
		travelledRouteOverlay.clearPath();
		routeOverlay.clearPath();
		int index = app.getRoute().getPoints().indexOf(point);
		int i = 0;
		for (PGeoPoint lPoint : app.getRoute().getPoints()) {
			if (i <= index) {
				travelledRouteOverlay.addPoint(lPoint);
			} 
			if (i >= index){
				routeOverlay.addPoint(lPoint);
			}
			i++;
		}
		mOsmv.invalidate();
	}
	
	/**
	 * Go to the next step of the directions and show it.
	 */
	
	public void nextStep() {
		final int index = app.getRoute().getSegments().indexOf(app.getSegment());
		final ListIterator<Segment> it = app.getRoute().getSegments().listIterator(index + 1);
		if (it.hasNext()) {
			app.setSegment(it.next());
			traverse(app.getSegment().startPoint());
			mOsmv.getController().setCenter(app.getSegment().startPoint());
		}	else {
			traverse(app.getRoute().getEndPoint());
			mOsmv.getController().setCenter(app.getRoute().getEndPoint());
		}
		showStep();
	}
	
	/**
	 * Go to the previous step of the directions and show it.
	 */
	
	public void lastStep() {
		final int index = app.getRoute().getSegments().indexOf(app.getSegment());
		final ListIterator<Segment> it = app.getRoute().getSegments().listIterator(index);
		if (it.hasPrevious()) {
			app.setSegment(it.previous());
		}
		showStep();
		traverse(app.getSegment().startPoint());
		mOsmv.getController().setCenter(app.getSegment().startPoint());
 	}
	
	/**
	 * Hide the onscreen directions.
	 */
	
	public void hideStep() {
		final View overlay = findViewById(R.id.directions_overlay);
		overlay.setVisibility(View.INVISIBLE);
		mOsmv.setClickable(true);
		directionsVisible = false;
		this.mOsmv.setBuiltInZoomControls(true);
        this.mOsmv.setMultiTouchControls(true);
	}
	
	/**
	 * Show the currently selected step of directions onscreen, focus
	 * the map at the start of that section.
	 */
	
	public void showStep() {
		directionsVisible = true;
		this.mOsmv.setBuiltInZoomControls(false);
        this.mOsmv.setMultiTouchControls(false);
        //mOsmv.getController().setZoom(ZOOM);
		
		final View overlay = findViewById(R.id.directions_overlay);
		this.mOsmv.setClickable(false);
		
		//Setup buttons
		final Button back = (Button) overlay.findViewById(R.id.back_button);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lastStep();
			}
			
		});
		
		final Button next = (Button) overlay.findViewById(R.id.next_button);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nextStep();
			}
			
		});
		
		overlay.setOnTouchListener(gestureListener);
		
		final TextView turn = (TextView) overlay.findViewById(R.id.turn);
		final TextView distance = (TextView) overlay.findViewById(R.id.distance);
		final TextView num = (TextView) overlay.findViewById(R.id.step_no);
		turn.setText(Html.fromHtml(app.getSegment().getInstruction()), TextView.BufferType.SPANNABLE);
		
		final String distanceString = "km".equals(unit) ? Convert.asMeterString(app.getSegment().getLength()) + " ("
				+ Convert.asKilometerString(app.getSegment().getDistance()) + ")" : 
					Convert.asFeetString(app.getSegment().getLength()) + " ("
					+ Convert.asMilesString(app.getSegment().getDistance()) + ")";
		
		distance.setText(distanceString);
		num.setText(app.getRoute().getSegments().indexOf(app.getSegment()) 
				+ 1 + "/" + app.getRoute().getSegments().size());
		overlay.setVisibility(View.VISIBLE);
	}

	/** Overlay to handle swipe events when showing directions.
	 * 
	 */
	
	private class OSDOverlay extends Overlay {

		/**
		 * @param ctx
		 */
		public OSDOverlay(final Context ctx) {
			super(ctx);
			//Detect swipes (left & right, taps.)
	        gestureDetector = new GestureDetector(RouteMap.this, new TurnByTurnGestureListener(RouteMap.this));
	        gestureListener = new View.OnTouchListener() {
	            @Override
				public boolean onTouch(final View v, final MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
	                }
	        };
		}
		
		/**
		 * If the onscreen display is enabled, capture motion events to control it.
		 */
		
		@Override
		public boolean onTouchEvent(final MotionEvent event, final MapView mv) {
			if (RouteMap.this.directionsVisible) {
				RouteMap.this.gestureDetector.onTouchEvent(event);
				return true;
			} else {
		        return false;
			}
		}


		/* (non-Javadoc)
		 * @see org.osmdroid.views.overlay.Overlay#draw(android.graphics.Canvas, org.osmdroid.views.MapView, boolean)
		 */
		@Override
		protected void draw(Canvas arg0, MapView arg1, boolean arg2) {
			// TODO Auto-generated method stub
			
		}
	}

	public void showAllParkings(GeoPoint p) {
		if(app.getSessionId() != null) {
			streetSegments.refresh(p);
			garages.refresh(p);
			streetParkingReleases.refresh(p);
		}
	}
	
	public void showAllParkings() {
		Route route = app.getRoute();
		
		if(route == null) {
			showAllParkings(mOsmv.getMapCenter());
		} else {
			//show parking around destination
			int index = app.getRoute().getSegments().size()-1;
			Segment seg = null;
			if(index>=0)
				seg = app.getRoute().getSegments().get(index);
			if(seg!=null) {
				List<PGeoPoint> pList = seg.getPoints();
				GeoPoint p = pList.get(pList.size()-1);
				showAllParkings(p);
			}
		}
	}
	
	public void stopNavigation() {
		//nothing to do in this class, only in LiveRouteMap class where nvigation is done
		travelledRouteOverlay.clearPath();
		routeOverlay.clearPath();
		mOsmv.invalidate();
		app.setRoute(null);
	}
	
	/**
   	 * Finish cascade passer.
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == R.id.payment) {
        	if (resultCode == CityParkConsts.STOP_PAYMENT_SUCCEEDED) {
        		unparkCompletion();
        	}
        }
    }
    
	public void checkParkAndFinish(final Boolean finish, final int result) {	
		//open dialog and ask user if he really un-parked
		if(parking_manager.isParking() || parking_manager.isPaymentActive()) {
			setResult(1);
			finish();
		} else {
			finishOnPark = finish;
			showDialog(R.id.park);
		}
	}
	
	public void park() {
		showDialog(R.id.awaiting_fix);
		
		Location self = mLocationOverlay.getLastFix();
		
		if (self == null) {
			self = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if (self == null) {
			self = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (self != null) {
			parking_manager.park(new PGeoPoint(self.getLatitude(), self.getLongitude()));
		}
		dismissDialog(R.id.awaiting_fix);
	}
	
	public void unpark() {
		if (parking_manager.isPaymentActive() || parking_manager.isReminderActive()) {
			Intent intent;
			
			if(payMethod.contains("Pango")) {
				intent = new Intent(this, PaymentPangoActivity.class);
				startActivityForResult(intent, R.id.payment);
			} else if(payMethod.contains("CelOpark")) {
				intent = new Intent(this, PaymentCelOParkActivity.class);
				startActivityForResult(intent, R.id.payment);
			} else {
				Toast.makeText(this, "Undefined payment provider...",
						Toast.LENGTH_LONG).show();
			}
		} else
			unparkCompletion();
	}
	
	public void unparkCompletion() {
		if(app.getSessionId()!=null){
			showDialog(R.id.awaiting_fix);
			RouteMap.this.mLocationOverlay.runOnFirstFix(new Runnable() {
				@Override
				public void run() {
					if (RouteMap.this.dialog.isShowing()) {
							RouteMap.this.dismissDialog(R.id.awaiting_fix);
							Location self = mLocationOverlay.getLastFix();
							
							if (self == null) {
								self = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							}
							if (self == null) {
								self = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
							}
							if (self != null) {
								reportParkingReleaseTask = new ReportParkingReleaseTask(RouteMap.this,app.getSessionId(),self.getLatitude(), self.getLongitude());
								reportParkingReleaseTask.execute();
							}
					}
				}
			});
		}
			
		parking_manager.unPark();
		
		//renew session
		login();
		
		RouteMap.this.hideStep();
		carAlert.unsetAlert();
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//TODO this code is called only once - MapView bug?
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			showAllParkings();
       }
		return false;
	}
	
}
