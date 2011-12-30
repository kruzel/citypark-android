package com.citypark;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.citypark.constants.CityParkConsts;
import com.citypark.service.RegisterationListener;
import com.citypark.service.RegistrationTask;

public class RegisterActivity extends Activity implements RegisterationListener {

	SharedPreferences mPrefs = null;
    SharedPreferences.Editor mEditor = null;
    
	private EditText txtEmail = null;
	private EditText txtPassword  = null;
	private EditText txtFirstName  = null;
	private EditText txtLastName  = null;
	private EditText txtLicensePlate  = null;
	private String strPaymentMethod = "None";
	private Button btnPaymentMethod = null;
	private EditText txtPhoneNumber = null;
	
	static final int DIALOG_PAYMENT_METHOD_ID = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
     // Restore preferences
        mPrefs = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE);
        mEditor = mPrefs.edit();
                
        txtEmail = (EditText) findViewById(R.id.id_email);
        txtPassword = (EditText) findViewById(R.id.id_password); 
        txtFirstName = (EditText) findViewById(R.id.id_firstname); 
        txtLastName = (EditText) findViewById(R.id.id_lastname);
        txtLicensePlate = (EditText) findViewById(R.id.id_licenseplate);
        btnPaymentMethod = (Button) findViewById(R.id.id_payment_method);
        txtPhoneNumber = (EditText) findViewById(R.id.id_phone_number);
        
        txtEmail.setText(mPrefs.getString("email", null));
        txtPassword.setText(mPrefs.getString("password", null));
        txtFirstName.setText(mPrefs.getString("first_name", null));
        txtLastName.setText(mPrefs.getString("last_name", null));
        txtLicensePlate.setText(mPrefs.getString("license_plate", null));
        txtPhoneNumber.setText(mPrefs.getString("phone_number", null));
        
        //get phone number, depends on operator support, may return null
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        txtPhoneNumber.setText(mPrefs.getString("phone_number", tMgr.getLine1Number()));
        
        strPaymentMethod = mPrefs.getString("payment_method", getString(R.string.payment_method));
        btnPaymentMethod.setText(strPaymentMethod);
    } 
   
    public void onRegister(View view) {
    	if(txtEmail.getText().length() == 0 || txtPassword.getText().length() == 0) {
    		Toast.makeText(this, R.string.registration_missing_fields, Toast.LENGTH_LONG).show();
    		return;
    	}
    	
        mEditor.putString("email", txtEmail.getText().toString());
        mEditor.putString("password", txtPassword.getText().toString());
        mEditor.putString("first_name", txtFirstName.getText().toString());
        mEditor.putString("last_name", txtLastName.getText().toString());
        mEditor.putString("license_plate", txtLicensePlate.getText().toString());
        mEditor.putString("phone_number",txtPhoneNumber.getText().toString());
        mEditor.putString("payment_method", strPaymentMethod);
        
        //spawn registration task
        //we finish only after receiving response from the server
        RegistrationTask regTask = new RegistrationTask(this, this, txtEmail.getText().toString(), 
        		txtPassword.getText().toString(), txtFirstName.getText().toString(), 
        		txtLastName.getText().toString(), txtPhoneNumber.getText().toString(), txtLicensePlate.getText().toString(), strPaymentMethod);
        regTask.execute((Void[])null);
    }
    
    public void OnPaymenMethodClick(View view) {
    	showDialog(DIALOG_PAYMENT_METHOD_ID);
    }
    
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch(id) {
        case DIALOG_PAYMENT_METHOD_ID:
        	final CharSequence[] items = {"None", "Pango", "CelOpark"};
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("Select Payment Method");
        	builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
        	    	strPaymentMethod = items[item].toString();
        	    	dialog.dismiss();
        	    	btnPaymentMethod.setText(strPaymentMethod);
        	    }
        	});
        	dialog = builder.create();
            break;
        default:
            dialog = null;
        }
        return dialog;
    }

	@Override
	public void RegistrationComplete(final String successCode) {
		if(CityParkConsts.USER_ALREADY_EXISTS.equalsIgnoreCase(successCode)){
			Log.e(CityParkConsts.USER_ALREADY_EXISTS,"User already exists in the system!");
			
			//TODO get user data from server and store locally
		}
		
		if(mEditor.commit()){
			this.startActivity(new Intent(this, LiveRouteMap.class));
        	finish();
        } else {
        	Log.e("onRegister", "registration failed");
        	Toast.makeText(this, R.string.registration_failed, Toast.LENGTH_LONG).show();
        }	
	}
	
	@Override
	public void onBackPressed() {

	   return;
	}

}  