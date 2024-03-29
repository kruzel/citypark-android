package com.citypark;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

/**
 * Based on osmdroid default map view activity.
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
 * Default map view activity.
 * @author Manuel Stahl
 *
 */
public class CityParkMapActivity extends MapActivity {

        // ===========================================================
        // Fields
        // ===========================================================

        protected SharedPreferences mPrefs;
        protected MapView mOsmv;
        protected MyLocationOverlay mLocationOverlay;
        protected ProgressBar mProgresBar;

        // ===========================================================
        // Constructors
        // ===========================================================
            
        
        /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE);
        
    }

    @Override
		protected void onDestroy() {
	    	System.gc();
    	
			super.onDestroy();
		}

	@Override
    protected void onPause() {
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putInt(getString(R.string.prefs_scrollx), mOsmv.getScrollX());
        edit.putInt(getString(R.string.prefs_scrolly), mOsmv.getScrollY());
        edit.putInt(getString(R.string.prefs_zoomlevel), mOsmv.getZoomLevel());
        //edit.putBoolean(getString(R.string.prefs_showlocation), mLocationOverlay.isMyLocationEnabled());
//        edit.putBoolean(getString(R.string.prefs_followlocation), mLocationOverlay.isFollowLocationEnabled());
        edit.commit();

        System.gc();
        super.onPause();
    }

    @Override
    protected void onResume() {
    	System.gc();
//        if(mPrefs.getBoolean(getString(R.string.prefs_showlocation), true)) {
//                this.mLocationOverlay.enableMyLocation();
//        }
                
//        if(mPrefs.getBoolean(getString(R.string.prefs_followlocation), true)) {
//        	this.mLocationOverlay.enableFollowLocation();
//        } else {
//        	this.mLocationOverlay.disableFollowLocation();
//        }
        super.onResume();
    }


    @Override
    public boolean onTrackballEvent(final MotionEvent event) {
            return this.mOsmv.onTrackballEvent(event);
    }
    
    @Override
	public boolean onTouchEvent(final MotionEvent event) {
//			if (event.getAction() == MotionEvent.ACTION_MOVE) {
//				this.mLocationOverlay.disableFollowLocation();
//			}
	        return super.onTouchEvent(event);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}