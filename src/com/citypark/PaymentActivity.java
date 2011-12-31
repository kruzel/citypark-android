package com.citypark;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.citypark.constants.CityParkConsts;
import com.citypark.parser.CityParkParkingZoneParser;
import com.citypark.parser.CityParkParkingZoneParser.LocationData;
import com.citypark.service.StartPaymentTask;
import com.citypark.service.StopPaymentTask;
import com.citypark.service.TimeLimitAlertListener;
import com.citypark.utility.ParkingSessionPersist;

public abstract class PaymentActivity extends Activity {
	
	/** payment parameters **/
	protected EditText myLicensePlate;
	protected EditText parkingCity;
	protected EditText parkingZone;
	/** controls **/
	private ToggleButton tgBtnPay = null;
	private ToggleButton tgBtnRemind = null;
	private ProgressBar progBarPayment = null;
	private StartPaymentTask payTask = null;
	private StopPaymentTask stopPayTask = null;
	private TimePicker timePicker = null;
	/** Application reference. **/
	private CityParkApp app;
	/** ParkingSessionPersist manager. */
	protected ParkingSessionPersist parking_manager = null;
	/** preferences file **/
	protected SharedPreferences mPrefs;
	
	/** operation result code **/
	private int resultCode = -1;
	
	@Override
	public void onCreate(final Bundle savedState) {
		super.onCreate(savedState);
		
		app = (CityParkApp) getApplication();
		setContentView(R.layout.pay);
		
		// Initialize parking manager
		parking_manager = new ParkingSessionPersist(this);
		
		tgBtnPay = (ToggleButton) findViewById(R.id.toggleButtonPay);
		tgBtnRemind = (ToggleButton) findViewById(R.id.toggleButtonRemind);
		progBarPayment = (ProgressBar) findViewById(R.id.progressBarPay);
		timePicker = (TimePicker) findViewById(R.id.timePickerEnd);
		myLicensePlate = (EditText) findViewById(R.id.editTextLicnsePlate);
		parkingCity = (EditText) findViewById(R.id.editTextCity);
		parkingZone = (EditText) findViewById(R.id.editTextParkingZone);

		//set license plate 
		mPrefs = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE);
   	 	myLicensePlate.setText(mPrefs.getString("license_plate", null));
				
   	 	//initiate city fetching via GeoLocator 
		Thread tcity = new Thread() {
			@Override
			public void run() {
				//Initialise geocoder
				final Geocoder geocoder = new Geocoder(PaymentActivity.this);
				/* Get current lat & lng if available. */
				final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
				try {
				Location self = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				Location selfNet= lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				
				//Only use GPS if more recent fix 
				if (self != null) {
					self =  (selfNet == null) || (selfNet.getTime() < self.getTime() + 600000) ? self : selfNet;
				}
				
				/* Autofill starting location by reverse geocoding current
				 * lat & lng
				 */
				
				if (self != null) {
					try {
						if(app!=null && app.getSessionId()!=null) {
							CityParkParkingZoneParser zoneParser = new CityParkParkingZoneParser(PaymentActivity.this,app.getSessionId(),self.getLatitude(),self.getLongitude());
							LocationData ld = zoneParser.parse();
							parkingZone.setText(ld.getParkingZone());
							parkingCity.setText(ld.getCity());
						}
					} catch (Exception e) {
						Log.e(e.getMessage(), "FindPlace - location: " + self);
					}
				}
				} catch (IllegalArgumentException e) {
					Log.e("Location Service", "No Location provider.");
				}
			}
		};
		tcity.run();
   	 	
		progBarPayment.setVisibility(View.INVISIBLE);
		timePicker.setIs24HourView(true);
		tgBtnPay.setChecked(parking_manager.isPaymentActive());
		tgBtnRemind.setChecked(parking_manager.isReminderActive());
	}
	
	public void OnPay(View view) {   	
		if(parking_manager.isPaymentActive()){
			Toast.makeText(this, R.string.payment_progress , Toast.LENGTH_SHORT).show();
			progBarPayment.setVisibility(View.VISIBLE);
		} else {
			Toast.makeText(this, R.string.payment_progress , Toast.LENGTH_SHORT).show();
			progBarPayment.setVisibility(View.VISIBLE);
		}
	}
	
	public void OnRemind(View view) {
		if(!parking_manager.isReminderActive()){
			//start reminder service
			Intent intent = new Intent(this, TimeLimitAlertListener.class);
            PendingIntent sender = PendingIntent.getBroadcast(this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE,timePicker.getCurrentMinute());
                                  
            // Schedule the alarm!
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            
        	Time now = new Time();
    		now.setToNow();
    		parking_manager.setReminder(now);
            
		} else {

			//stop reminder service
			Intent intent = new Intent(PaymentActivity.this, TimeLimitAlertListener.class);
            PendingIntent sender = PendingIntent.getBroadcast(PaymentActivity.this,
                    0, intent, 0);

            // And cancel the alarm.
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            am.cancel(sender);
            
            parking_manager.stopReminder();

		}
	}

	public void PaymentComplete(Boolean success) {
		
		progBarPayment.setVisibility(View.INVISIBLE);
		
		if(parking_manager.isPaymentActive()) {
			 if(success){
				 resultCode = CityParkConsts.STOP_PAYMENT_SUCCEEDED;
				 parking_manager.setPaymentEnd();
				 Toast.makeText(this, R.string.payment_succeeded , Toast.LENGTH_LONG).show();
				 stopPayTask = new StopPaymentTask(this, app.getSessionId(), getPaymentMethod(), parking_manager.getLocation().getLatitudeE6()/1E6, 
						 parking_manager.getLocation().getLongitudeE6()/1E6, "ACKNOWLEDGED");
				 stopPayTask.execute();
			 }
	         else {
	        	 resultCode = CityParkConsts.STOP_PAYMENT_FAILED;
	        	 Toast.makeText(this, R.string.payment_failes , Toast.LENGTH_LONG).show();
	        	 
	        	 stopPayTask = new StopPaymentTask(this, app.getSessionId(), getPaymentMethod(), parking_manager.getLocation().getLatitudeE6()/1E6, 
						 parking_manager.getLocation().getLongitudeE6()/1E6, "FAILED");
				 stopPayTask.execute();
	         }
		} else {
			 if(success) {
				 resultCode = CityParkConsts.START_PAYMENT_SUCCEEDED;
				 parking_manager.setPaymentStart();
				 Toast.makeText(this, R.string.payment_succeeded , Toast.LENGTH_LONG).show();
				 
				//TODO operationStatus values:ACKNOWLEDGED,FAILED,UNVERIFIED\
				payTask = new StartPaymentTask(this, app.getSessionId(), getPaymentMethod(), parking_manager.getLocation().getLatitudeE6()/1E6, 
						parking_manager.getLocation().getLongitudeE6()/1E6, "ACKNOWLEDGED");
				payTask.execute();
			 }
	         else {
	        	 resultCode = CityParkConsts.START_PAYMENT_FAILED;
	        	 Toast.makeText(this, R.string.payment_failes , Toast.LENGTH_LONG).show();
	        	 
	        	 payTask = new StartPaymentTask(this, app.getSessionId(), getPaymentMethod(), parking_manager.getLocation().getLatitudeE6()/1E6, 
							parking_manager.getLocation().getLongitudeE6()/1E6, "FAILED");
					payTask.execute();
	         }
		}
	}

	@Override
	public void onBackPressed() {
		setResult(resultCode);
		finish();
		
		super.onBackPressed();
	}	
	
	public abstract String getPaymentMethod();
}
