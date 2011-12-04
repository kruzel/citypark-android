package com.citypark.utility;

import android.os.AsyncTask;
import android.text.format.Time;

public class PayTask extends AsyncTask<Void, Void, Boolean> {
	
	PaymentListener payLitener = null;
	
	public PayTask(PaymentListener payLitener){
		this.payLitener = payLitener;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
				
		//TODO start payment request (SMS,..)
		//TODO update citypark through API
		
		Time timeStart = new Time();
		Time timeNow = new Time();
		timeStart.setToNow();
		timeNow.setToNow();
		
        while ((timeNow.toMillis(false) - timeStart.toMillis(false)) < 10000) {
        	try {
				Thread.currentThread();
				Thread.sleep(1000);
				//TODO check payment confirmation
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	timeNow.setToNow();
        }
        
      //TODO update citypark through API on success or failure
        
		return true;
	}
	
	protected void onPostExecute(Boolean success) {
		
		if(payLitener!=null)
			payLitener.PaymentComplete(success);
		
		//TODO error handling
		
     }


}
