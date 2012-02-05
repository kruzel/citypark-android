package com.citypark;

import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.citypark.api.parser.CityParkParkingZoneParser;
import com.citypark.api.parser.CityParkParkingZoneParser.LocationData;
import com.citypark.api.task.LoginTask;
import com.citypark.api.task.StartPaymentTask;
import com.citypark.api.task.StopPaymentTask;
import com.citypark.service.TimeLimitAlertListener;
import com.citypark.utility.ParkingSessionManager;

public class PaymentActivity extends Activity {
	
	/** payment parameters **/
	protected EditText myLicensePlate;
	protected EditText parkingCity;
	protected EditText parkingZone;
	/** controls **/
	private ToggleButton tgBtnPay = null;
	private ToggleButton tgBtnRemind = null;
	//TODO replace with a progress dialog
	private StartPaymentTask payTask = null;
	private StopPaymentTask stopPayTask = null;
	private TimePicker timePicker = null;
	/** ParkingSessionPersist manager. */
	protected ParkingSessionManager parking_manager = null;
	/** preferences file **/
	protected SharedPreferences mPrefs;
	private PendingIntent sender;	
	private MediaPlayer mMediaPlayer;
	private Boolean resultCode = false;
	
	@Override
	public void onCreate(final Bundle savedState) {
		super.onCreate(savedState);
		
		// TODO proper rotation handling while avoiding re-fetch of all overlays
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.pay);
		
		// Initialize parking manager
		parking_manager = new ParkingSessionManager(this);
		
		tgBtnRemind = (ToggleButton) findViewById(R.id.toggleButtonRemind);
		timePicker = (TimePicker) findViewById(R.id.timePickerEnd);
		
		tgBtnPay = (ToggleButton) findViewById(R.id.toggleButtonPay);
		myLicensePlate = (EditText) findViewById(R.id.editTextLicnsePlate);
		parkingCity = (EditText) findViewById(R.id.editTextCity);
		parkingZone = (EditText) findViewById(R.id.editTextParkingZone);
		
		timePicker.setIs24HourView(true);
		tgBtnRemind.setChecked(parking_manager.isReminderActive());

		//enable payment only if payment provider is defined
		if(!getPaymentMethod().equals("None")) {

			//set license plate 
			mPrefs = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE);
	   	 	myLicensePlate.setText(mPrefs.getString("license_plate", null));
	   	 	
			tgBtnPay.setChecked(parking_manager.isPaymentActive());
					
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
							if(LoginTask.isLoggedIn()) {
								CityParkParkingZoneParser zoneParser = new CityParkParkingZoneParser(PaymentActivity.this,LoginTask.getSessionId(),self.getLatitude(),self.getLongitude());
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
		} else { 
			//hide all payment views
			tgBtnPay.setVisibility(View.INVISIBLE);
			myLicensePlate.setVisibility(View.INVISIBLE);
			parkingCity.setVisibility(View.INVISIBLE);
			parkingZone.setVisibility(View.INVISIBLE);
			findViewById(R.id.textView1).setVisibility(View.INVISIBLE);
			findViewById(R.id.textView2).setVisibility(View.INVISIBLE);
			findViewById(R.id.textView3).setVisibility(View.INVISIBLE);
			findViewById(R.id.linearLayout1).setVisibility(View.INVISIBLE);
			findViewById(R.id.linearLayout2).setVisibility(View.INVISIBLE);
			findViewById(R.id.linearLayout3).setVisibility(View.INVISIBLE);
		}
   	 			
		if (getIntent().getBooleanExtra(getString(R.string.reminder_intent), false)) 
			alarmRing();
	}
	
	@Override
	protected void onResume() {
		if (getIntent().getBooleanExtra(getString(R.string.reminder_intent), false)) 
			alarmRing();
		
		super.onResume();
	}

	@Override
	public void finish() {
		Intent intent = new Intent(this,CityParkRouteActivity.class);
		intent.putExtra("PaymentActivityResult", resultCode);
		startActivity(intent);
		super.finish();
	}

	public void OnPay(View view) {   	
		if(parking_manager.isPaymentActive()){
			Toast.makeText(this, R.string.payment_progress , Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, R.string.payment_progress , Toast.LENGTH_SHORT).show();
		}
	}
	
	public void OnRemind(View view) {
		        
		if(parking_manager.isReminderActive()){
			// And cancel the alarm.
			tgBtnRemind.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_normal)); 
			tgBtnRemind.setText(getResources().getString(R.string.reminder_button));
			parking_manager.stopReminder();
			
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            am.cancel(sender);
            if(mMediaPlayer!=null){
            	mMediaPlayer.stop();
            	mMediaPlayer=null;
            }
            
        	//we can report that we are not parking anymore (no reminder and no payment active
            resultCode = true;
		} else {
			//start reminder service
			tgBtnRemind.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_pressed)); 
			tgBtnRemind.setText(getResources().getString(R.string.reminder_button_unset));
			
			Intent intent = new Intent(this, TimeLimitAlertListener.class);
			intent.putExtra(getString(R.string.payment_method), getPaymentMethod());
	        sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	        
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
            
		} 
	}

	public void PaymentComplete(Boolean success) {
				
		if(parking_manager.isPaymentActive()) {
			 if(success){
				 //stop payment succeeded
				 resultCode = true;
				 parking_manager.setPaymentEnd();
				 Toast.makeText(this, R.string.payment_succeeded , Toast.LENGTH_LONG).show();
				 
				 if(stopPayTask!=null)
					 stopPayTask.cancel(true);
				 stopPayTask = new StopPaymentTask(this, LoginTask.getSessionId(), getPaymentMethod(), parking_manager.getCarPos().getLatitudeE6()/1E6, 
						 parking_manager.getCarPos().getLongitudeE6()/1E6, "ACKNOWLEDGED");
				 stopPayTask.execute();
			 }
	         else {
	        	//stop payment failed
	        	 resultCode = false;
	        	 Toast.makeText(this, R.string.payment_failes , Toast.LENGTH_LONG).show();
	        	 
	        	 if(payTask!=null)
					 payTask.cancel(true);
	        	 stopPayTask = new StopPaymentTask(this, LoginTask.getSessionId(), getPaymentMethod(), parking_manager.getCarPos().getLatitudeE6()/1E6, 
						 parking_manager.getCarPos().getLongitudeE6()/1E6, "FAILED");
				 stopPayTask.execute();
	         }
		} else {
			 if(success) {
				//start payment succeeded
				 resultCode = false;
				 parking_manager.setPaymentStart();
				 Toast.makeText(this, R.string.payment_succeeded , Toast.LENGTH_LONG).show();
				 
				//TODO operationStatus values:ACKNOWLEDGED,FAILED,UNVERIFIED\
				 if(payTask!=null)
					 payTask.cancel(true);
				payTask = new StartPaymentTask(this, LoginTask.getSessionId(), getPaymentMethod(), parking_manager.getCarPos().getLatitudeE6()/1E6, 
						parking_manager.getCarPos().getLongitudeE6()/1E6, "ACKNOWLEDGED");
				payTask.execute();
			 }
	         else {
	        	//start payment failed
	        	 resultCode = false;
	        	 Toast.makeText(this, R.string.payment_failes , Toast.LENGTH_LONG).show();
	        	 
	        	 if(payTask!=null)
					 payTask.cancel(true);
	        	 payTask = new StartPaymentTask(this, LoginTask.getSessionId(), getPaymentMethod(), parking_manager.getCarPos().getLatitudeE6()/1E6, 
							parking_manager.getCarPos().getLongitudeE6()/1E6, "FAILED");
					payTask.execute();
	         }
		}
	}
	
	public String getPaymentMethod(){
		return "None";
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getBooleanExtra(getString(R.string.reminder_intent), false)) 
			alarmRing();
		
		super.onNewIntent(intent);
	}
	
	private void alarmRing() {
		if(parking_manager.isReminderActive()) {
			// send off alarm sound
			Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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
		
		parking_manager.stopReminder();
		tgBtnRemind.setChecked(false);
		tgBtnRemind.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_normal)); 
	}
	
}
