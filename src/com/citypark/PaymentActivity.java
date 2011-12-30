package com.citypark;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.citypark.constants.CityParkConsts;
import com.citypark.service.StartPaymentTask;
import com.citypark.service.StopPaymentTask;
import com.citypark.service.TimeLimitAlertListener;
import com.citypark.utility.ParkingSessionPersist;

public abstract class PaymentActivity extends Activity {
	
	private ToggleButton tgBtnPay = null;
	private ToggleButton tgBtnRemind = null;
	private TextView txtProgressMsg = null;
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
	protected String myPhoneNumber;
	protected String myLicensePlate;
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
		txtProgressMsg = (TextView) findViewById(R.id.textViewPaymentMessage);
		progBarPayment = (ProgressBar) findViewById(R.id.progressBarPayment);
		timePicker = (TimePicker) findViewById(R.id.timePickerEnd);
				
		timePicker.setIs24HourView(true);
		
		tgBtnPay.setChecked(parking_manager.isPaymentActive());
		tgBtnRemind.setChecked(parking_manager.isReminderActive());
		
		mPrefs = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE);
   	 	myLicensePlate = mPrefs.getString("license_plate", null);
   	 	myPhoneNumber = mPrefs.getString("phone_number", null);
	}
	
	public void OnPay(View view) {   	
		if(parking_manager.isPaymentActive()){
			txtProgressMsg.setVisibility(View.VISIBLE);
			txtProgressMsg.setText(R.string.payment_progress);
			progBarPayment.setVisibility(View.VISIBLE);
		} else {
			txtProgressMsg.setVisibility(View.VISIBLE);
			txtProgressMsg.setText(R.string.payment_progress);
			progBarPayment.setVisibility(View.VISIBLE);
		}
	}
	
	public void OnRemind(View view) {
		if(!parking_manager.isReminderActive()){
			//start reminder service
			Intent intent = new Intent(PaymentActivity.this, TimeLimitAlertListener.class);
            PendingIntent sender = PendingIntent.getBroadcast(PaymentActivity.this,
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
				 txtProgressMsg.setText(R.string.payment_succeeded);
				 
				 stopPayTask = new StopPaymentTask(this, app.getSessionId(), "Pango", parking_manager.getLocation().getLatitudeE6()/1E6, parking_manager.getLocation().getLongitudeE6()/1E6, "UNVERIFIED");
				 stopPayTask.execute();
			 }
	         else {
	        	 resultCode = CityParkConsts.STOP_PAYMENT_FAILED;
	        	 txtProgressMsg.setText(R.string.payment_failes);
	         }
		} else {
			 if(success) {
				 resultCode = CityParkConsts.START_PAYMENT_SUCCEEDED;
				 parking_manager.setPaymentStart();
				 txtProgressMsg.setText(R.string.payment_succeeded);
				 
				//TODO operationStatus values:ACKNOWLEDGED,FAILED,UNVERIFIED\
				payTask = new StartPaymentTask(this, app.getSessionId(), "Pango", parking_manager.getLocation().getLatitudeE6()/1E6, 
						parking_manager.getLocation().getLongitudeE6()/1E6, "UNVERIFIED");
				payTask.execute();
			 }
	         else {
	        	 resultCode = CityParkConsts.START_PAYMENT_FAILED;
	        	 txtProgressMsg.setText(R.string.payment_failes);
	         }
		}
	}

	@Override
	public void onBackPressed() {
		setResult(resultCode);
		finish();
		
		super.onBackPressed();
	}	
}
