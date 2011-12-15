package com.citypark;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.citypark.service.PaymentListener;
import com.citypark.service.StartPaymentTask;
import com.citypark.service.StopPaymentTask;
import com.citypark.service.TimeLimitAlertListener;
import com.citypark.utility.ParkingSessionPersist;

public class PaymentActivity extends Activity implements PaymentListener {
	
	private ToggleButton tgBtnPay = null;
	private ToggleButton tgBtnRemind = null;
	private TextView txtProgressMsg = null;
	private ProgressBar progBarPayment = null;
	private StartPaymentTask payTask = null;
	private StopPaymentTask stopPayTask = null;
	private TimePicker timePicker = null;
	
	/** ParkingSessionPersist manager. */
	ParkingSessionPersist parking_manager = null;
	
	@Override
	public void onCreate(final Bundle savedState) {
		super.onCreate(savedState);
		
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
		
	}
	
	public void OnPay(View view) {
		
		if(parking_manager.isPaymentActive()){
			txtProgressMsg.setVisibility(View.VISIBLE);
			txtProgressMsg.setText(R.string.payment_progress);
			progBarPayment.setVisibility(View.VISIBLE);
			
			stopPayTask = new StopPaymentTask(this, this, parking_manager.getCPSessionId(), "Pango", parking_manager.getLocation().getLatitudeE6()/1E6, parking_manager.getLocation().getLongitudeE6()/1E6, "UNVERIFIED");
			stopPayTask.execute();
			
		} else {
			txtProgressMsg.setVisibility(View.VISIBLE);
			txtProgressMsg.setText(R.string.payment_progress);
			progBarPayment.setVisibility(View.VISIBLE);
			
			//TODO add inheritance classes for the different payment methods (Pango, Celopark,..)
			//operationStatus values:ACKNOWLEDGED,FAILED,UNVERIFIED\
			//TODO add payment verification logic and update operationStatus accordingly
			payTask = new StartPaymentTask(this, this, parking_manager.getCPSessionId(), "Pango", parking_manager.getLocation().getLatitudeE6()/1E6, parking_manager.getLocation().getLongitudeE6()/1E6, "UNVERIFIED");
			payTask.execute();
		}
	}
	
	public void OnRemind(View view) {
		if(!parking_manager.isReminderActive()){
			//start reminder service
			Intent intent = new Intent(PaymentActivity.this, TimeLimitAlertListener.class);
            PendingIntent sender = PendingIntent.getBroadcast(PaymentActivity.this,
                    0, intent, 0);
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR,timePicker.getCurrentHour());
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

	@Override
	public void PaymentComplete(Boolean success) {
		
		progBarPayment.setVisibility(View.INVISIBLE);
		
		if(parking_manager.isPaymentActive()) {
			 if(success){
				 parking_manager.setPaymentEnd();
				 txtProgressMsg.setText(R.string.payment_succeeded);
			 }
	         else
	        	 txtProgressMsg.setText(R.string.payment_failes);
			 
		} else {
			 if(success) {
				 parking_manager.setPaymentStart();
				 txtProgressMsg.setText(R.string.payment_succeeded);
			 }
	         else
	        	 txtProgressMsg.setText(R.string.payment_failes);
		}
         
	}
	
}
