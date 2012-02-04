/**
 * 
 */
package com.citypark.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.citypark.CityParkApp;
import com.citypark.ParkingMap;
import com.citypark.R;

/**
 * Service providing live navigation using GPS and notification updates
 * reflecting current location on route.
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
 * @version Nov 4, 2010
 */
public class LocationService extends Service implements LocationListener {
	/** Local binder. **/
    private final IBinder mBinder = new LocalBinder();
    /** Notification manager. **/
	//private NotificationManager mNM;
	/** Location manager. **/
	private LocationManager mLocationManager;
	/** Custom application reference. **/
	private CityParkApp app;
	/** Notification for notifying. **/
	private Notification notification;
	/** Intent for callbacks from notifier. **/
	private PendingIntent contentIntent;
	/** Receiver for navigation updates. **/
	private ParkingHandler mLocationReceiver;
	
	private int status = LocationProvider.OUT_OF_SERVICE;
	private Boolean enabled = true;
	
	 /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	@Override
    public void onCreate() {
        //mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        /* Get location manager. */
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        app = (CityParkApp) getApplication();
        mLocationReceiver = new ParkingHandler(app);
        int icon = R.drawable.logo;
        CharSequence tickerText = "";
        long when = System.currentTimeMillis();

        notification = new Notification(icon, tickerText, when);
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        Intent notificationIntent = new Intent(this, ParkingMap.class);
        notificationIntent.putExtra(getString(R.string.jump_intent), true);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, this); //min interval 15 sec
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    	shutdown();
    }
    
    /**
     * Listen for location updates and pass an update intent back to the
     * routemap, then update the status bar notification with current
     * instruction.
     */
    
    @Override
	public void onLocationChanged(Location location) {   	
    	if(status!=LocationProvider.AVAILABLE || !enabled)
    		return;
    	
    	mLocationReceiver.run(app,location);
	}
    
    private void shutdown() {
    	mLocationManager.removeUpdates(this);
    	//mNM.cancelAll();
    }

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String provider) {
		enabled = false;
		
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String provider) {
		enabled = true;
		
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		this.status =  status;
	}

}
