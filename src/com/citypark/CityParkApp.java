/**
 * 
 */
package com.citypark;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import com.citypark.constants.CityParkConsts;
import com.citypark.service.LocationService;
import com.citypark.service.LoginListener;
import com.citypark.service.LoginTask;
import com.citypark.utility.AddressDatabase;
import com.citypark.utility.LocationReceiver;
import com.citypark.utility.RouteDatabase;
import com.citypark.utility.route.Route;
import com.citypark.utility.route.Segment;

/**
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
 * @author jono@nanosheep.net
 * @version Jul 2, 2010
 */
@ReportsCrashes(formKey = "dGF2OV9Cd3VsOWtLMjRnWG9FRG5mZEE6MQ",
	mode = ReportingInteractionMode.NOTIFICATION,
    resNotifTickerText = R.string.crash_notif_ticker_text,
    resNotifTitle = R.string.crash_notif_title,
    resNotifText = R.string.crash_notif_text,
    resDialogText = R.string.crash_dialog_text,
    resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
    resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
    resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
    resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
    )
public class CityParkApp extends Application implements LoginListener {
	/** Route object. **/
	private Route route;
	/** The current segment. **/
	private Segment segment;
	/** Previous addresses db. **/
	private AddressDatabase addressDB;
	/** Favourite routes db. **/
	private RouteDatabase routeDB;
	/** preferences file **/
	protected SharedPreferences mPrefs;
	/** user info**/
	private String strEmail = null;
	private String strPassword = null;
	/** session info **/ 
	private String mSessionId = null;
	private int zoom = CityParkConsts.ZOOM;
	
	private boolean isLoginInProgress = false;
	
	/** Navigation service. **/
	private LocationService mBoundService;
	
		/** Receiver for navigation updates. **/
	private LocationReceiver mBroadcastReceiver = new LocationReceiver(this);
	
	/** Connection to location service. **/
	private ServiceConnection mConnection = new ServiceConnection() {
	    @Override
		public void onServiceConnected(ComponentName className, IBinder service) {
	        mBoundService = ((LocationService.LocalBinder)service).getService();
	    }

	    @Override
		public void onServiceDisconnected(ComponentName className) {
	        mBoundService = null;
	    }
	};
	/** Are we bound to location service? **/
	private boolean mIsBound;
	
	//TODO move login to here

	public CityParkApp () {
		super();
	}

	@Override
	public void onCreate() {
		Thread t = new Thread() {
			@Override
			public void run () {
				addressDB = new AddressDatabase(CityParkApp.this);
				setRouteDB(new RouteDatabase(CityParkApp.this));
			}
		};
		t.start();
		ACRA.init(this);
		
		getSessionId();
		
	}
	
	@Override
	public void onTerminate() {
		
		doUnbindService();
	    unregisterReceiver(mBroadcastReceiver);
	    
		super.onTerminate();
	}
	
	public String getEmail() {
		return strEmail;
	}

	public String getPassword() {
		return strPassword;
	}
	
	public String getSessionId() {
		
		//TODO prevent init twice
		if(mSessionId == null && !isLoginInProgress) {       	
			mPrefs = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE);
        	strEmail = mPrefs.getString("email", null);
            strPassword = mPrefs.getString("password", null);
            
        	if((strEmail == null) || (strEmail.length() == 0) || (strPassword == null) ||(strPassword.length() == 0) ) {
        		super.onCreate();
	        }
	        else {
	        	isLoginInProgress = true;
	        	LoginTask loginTask = new LoginTask(strEmail, strPassword, this,this);
	          	loginTask.execute((Void[])null);
	        }
		}
		
		return mSessionId;
	}

	public void setSessionId(String mSessionId) {
		this.mSessionId = mSessionId;
	}
	
	@Override
	public void loginComplete(String sessionId) {
		// if login ok, initialize LiveRouteMap
		if(sessionId == null) {
			Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
		} else {
			setSessionId(sessionId);
			registerReceiver(mBroadcastReceiver, new IntentFilter(getString(R.string.navigation_intent)));
			doBindService();
			
			super.onCreate();
		}
		isLoginInProgress = false;
	}
	
	/**
	 * Bind to navigation service.
	 */
	
	private void doBindService() {
		if (!mIsBound) {
			bindService(new Intent(CityParkApp.this, LocationService.class), mConnection, Context.BIND_AUTO_CREATE);
			mIsBound = true;
		}
	}

	/**
	 * Unbind from navigation service.
	 */
	
	private void doUnbindService() {
	    if (mIsBound) {
	        // Detach our existing connection.
	        unbindService(mConnection);
	        mIsBound = false;
	    }
	}
		
	public ServiceConnection getConnection() {
		return mConnection;
	}

	public boolean isBound() {
		return mIsBound;
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}
	
	/**
	 * @param route the route to set
	 */
	public void setRoute(final Route route) {
		this.route = route;
		segment = (route != null) && !route.getSegments().isEmpty() ? route.getSegments().get(0) : null;
	}

	/**
	 * @return the route
	 */
	public Route getRoute() {
		return route;
	}

	/**
	 * @param segment the segment to set.
	 */
	public void setSegment(final Segment segment) {
		this.segment = segment;
	}

	/**
	 * @return the current segment
	 */
	public Segment getSegment() {
		return segment;
	}

	/**
	 * @return the db
	 */
	public AddressDatabase getDb() {
		return addressDB;
	}

	/**
	 * @param routeDB the routeDB to set
	 */
	public void setRouteDB(RouteDatabase routeDB) {
		this.routeDB = routeDB;
	}

	/**
	 * @return the routeDB
	 */
	public RouteDatabase getRouteDB() {
		return routeDB;
	}

}
