/**
 * 
 */
package com.citypark;

import org.acra.ErrorReporter;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.citypark.api.task.LoginTask;
import com.citypark.service.LocationService;

@ReportsCrashes(formKey = "dEdVNHJqQjJUem5UOHZjal9YNjItc0E6MQ",
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
//TODO how to add specific value reports
//ErrorReporter.getInstance().putCustomData("myVariable", myVariable);

//TODO add common data structures and common images caching for all activities

public class CityParkApp extends Application {
	
	/** preferences file **/
	protected SharedPreferences mPrefs;
	
	/** Navigation service. **/
	private LocationService mBoundService;
		
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

	public CityParkApp () {
		super();
	}

	@Override
	public void onCreate() {
		//ACRA.init(this);
		LoginTask.init(this);		
		
		SharedPreferences mPrefs = getSharedPreferences(getString(R.string.prefs_name), Context.MODE_PRIVATE);
		String email = mPrefs.getString("email", "no email");
		String name = mPrefs.getString("first_name", " ") + " " + mPrefs.getString("last_name", " ");
		
		ErrorReporter.getInstance().putCustomData("email", email);
		ErrorReporter.getInstance().putCustomData("name", name);
		
		super.onCreate();
	}
	
	@Override
	public void onTerminate() {

		super.onTerminate();
	}
	
	public void finishAllAppObjecs(){
		doUnbindService();
		LoginTask.setSessionId(null);
	}
	
	/**
	 * Bind to Location service.
	 * called by RouteMap after login or with valid session id
	 */
	
	public void doBindService() {
		if (!mIsBound) {
			bindService(new Intent(CityParkApp.this, LocationService.class), mConnection, Context.BIND_AUTO_CREATE);
			mIsBound = true;
		}
	}

	/**
	 * Unbind from location service.
	 */
	
	public void doUnbindService() {
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

}
