package com.citypark;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Register extends Activity {

	private EditText txtEmail = null;
	private EditText txtPassword  = null;
	private EditText txtFirstName  = null;
	private EditText txtLastName  = null;
	private EditText txtLicensePlate  = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
     // Restore preferences
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE);
        
        txtEmail = (EditText) findViewById(R.id.id_email);
        txtPassword = (EditText) findViewById(R.id.id_password); 
        txtFirstName = (EditText) findViewById(R.id.id_firstname); 
        txtLastName = (EditText) findViewById(R.id.id_lastname);
        txtLicensePlate = (EditText) findViewById(R.id.id_licenseplate);
        
        txtEmail.setText(mPrefs.getString("email", null));
        txtPassword.setText(mPrefs.getString("password", null));
        txtFirstName.setText(mPrefs.getString("first_name", null));
        txtLastName.setText(mPrefs.getString("last_name", null));
        txtLicensePlate.setText(mPrefs.getString("license_plate", null));
    } 
   
    public void onRegister(View view) {
    	Register.this.startActivity(new Intent(Register.this, LiveRouteMap.class));
    	
    	SharedPreferences mPrefs = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("email", txtEmail.getText().toString());
        editor.putString("password", txtPassword.getText().toString());
        editor.putString("first_name", txtFirstName.getText().toString());
        editor.putString("last_name", txtLastName.getText().toString());
        editor.putString("license_plate", txtLicensePlate.getText().toString());

        // Commit the edits!
        if(!editor.commit())
        	Log.e("onRegister", "registration failed");
        finish();
        
    }
}  