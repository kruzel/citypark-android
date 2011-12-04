package com.citypark;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.citypark.utility.PayTask;
import com.citypark.utility.PaymentListener;
import com.citypark.utility.StopPaymentTask;

public class PayActivity extends Activity implements PaymentListener {
	
	//TODO add initialization from preferences file to cover case app is restarting
	
	private TextView txtProgressMsg = null;
	private ProgressBar progBarPayment = null;
	private PayTask payTask = null;
	private StopPaymentTask stopPayTask = null;
	
	@Override
	public void onCreate(final Bundle savedState) {
		super.onCreate(savedState);
		
		setContentView(R.layout.pay);
		
		txtProgressMsg = (TextView) findViewById(R.id.textViewPaymentMessage);
		progBarPayment = (ProgressBar) findViewById(R.id.progressBarPayment);
	}
	
	public void OnPay(View view) {
		
		if(view.isSelected()){
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
		
         if(success)
        	 txtProgressMsg.setText(R.string.payment_succeeded);
         else
        	 txtProgressMsg.setText(R.string.payment_failes);
         
         progBarPayment.setVisibility(View.INVISIBLE);
         
	}
	
}
