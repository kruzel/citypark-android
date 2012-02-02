/**
 * 
 */
package com.citypark;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

/**
 * @author ofer
 *
 */
public abstract class PaymentSmsActivity extends PaymentActivity {
    final protected String SENT = "SMS_SENT";
    final protected String DELIVERED = "SMS_DELIVERED";

    //---sends an SMS message to another device---
	protected void sendSMS(String phoneNumber, String message)
    {        
		//start SMS receiver
		registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
            	//---get the SMS message passed in---
    	        Bundle bundle = arg1.getExtras();        
    	        SmsMessage[] msgs = null;
    	        String str = "";            
    	        if (bundle != null)
    	        {
    	            //---retrieve the SMS message received---
    	            Object[] pdus = (Object[]) bundle.get("pdus");
    	            msgs = new SmsMessage[pdus.length];            
    	            for (int i=0; i<msgs.length; i++){
    	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);         
    	                str = msgs[i].getMessageBody().toString();
    	                if (parseResponse(str)) 
    	                	PaymentComplete(true);
    	                return;
    	            }
    	        }                        
    	        
    	        //if didn't find good sms ack, it failed
    	        PaymentComplete(false);
            }
        }, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
		
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
 
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
 
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        break;
                    default:
                    	PaymentComplete(false);
                        break;
                }
            }
        }, new IntentFilter(SENT));
 
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED:                    
                        PaymentComplete(false);
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        
 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);        
    }

	protected abstract Boolean parseResponse(String msg);
}
