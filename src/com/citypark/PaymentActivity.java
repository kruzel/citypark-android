package com.citypark;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.citypark.utility.Parking;
import com.citypark.utility.PayTask;
import com.citypark.utility.PaymentListener;
import com.citypark.utility.StopPaymentTask;

public class PaymentActivity extends Activity implements PaymentListener {
	
	private ToggleButton tgBtnPay = null;
	private ToggleButton tgBtnRemind = null;
	private TextView txtProgressMsg = null;
	private ProgressBar progBarPayment = null;
	private PayTask payTask = null;
	private StopPaymentTask stopPayTask = null;
	
	/** Parking manager. */
	Parking parking_manager = null;
	
	@Override
	public void onCreate(final Bundle savedState) {
		super.onCreate(savedState);
		
		setContentView(R.layout.pay);
		
		// Initialize parking manager
		parking_manager = new Parking(this);
		
		tgBtnPay = (ToggleButton) findViewById(R.id.toggleButtonPay);
		txtProgressMsg = (TextView) findViewById(R.id.textViewPaymentMessage);
		progBarPayment = (ProgressBar) findViewById(R.id.progressBarPayment);
		
		boolean isPaymentActive = parking_manager.isPaymentActive();
		
		tgBtnPay.setChecked(isPaymentActive);
		
	}
	
	public void OnPay(View view) {
		
		if(parking_manager.isPaymentActive()){
			txtProgressMsg.setVisibility(View.VISIBLE);
			txtProgressMsg.setText(R.string.payment_progress);
			progBarPayment.setVisibility(View.VISIBLE);
			
			stopPayTask = new StopPaymentTask(this);
			stopPayTask.execute();
			
		} else {
			txtProgressMsg.setVisibility(View.VISIBLE);
			txtProgressMsg.setText(R.string.payment_progress);
			progBarPayment.setVisibility(View.VISIBLE);
			
			//TODO add inheritance classes for the different payment methods (Pango, Celopark,..)
			payTask = new PayTask(this);
			payTask.execute();
		}
	}
	
	public void OnRemind(View view) {
		if(view.isSelected()){
			
			//TODO stop reminder service
			
		} else {
			//TODO start reminder service
			
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
