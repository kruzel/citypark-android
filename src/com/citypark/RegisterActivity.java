package com.citypark;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.citypark.api.task.LoginTask;
import com.citypark.api.task.RegisterationListener;
import com.citypark.api.task.RegistrationTask;
import com.citypark.constants.CityParkConsts;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class RegisterActivity extends Activity implements RegisterationListener {
	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
	          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
	          "\\@" +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	          "(" +
	          "\\." +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	          ")+"
	      );


	public static final String TAG = "FACEBOOK";
	private Facebook facebook = new Facebook("253072914778231");
	private AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
	private String fbId, fbName, fbEmail;

	private RegistrationTask regTask;
	
	private SharedPreferences mPrefs = null;
	private SharedPreferences.Editor mEditor = null;
    
	private EditText txtEmail = null;
	private EditText txtPassword  = null;
	private EditText txtFirstName  = null;
	private EditText txtLastName  = null;
	private EditText txtLicensePlate  = null;
	private String strPaymentMethod = "None";
	private Button btnPaymentMethod = null;
	private EditText txtPhoneNumber = null;
	private Button btnRegister = null;
	
	/** Dialog display. **/
	protected Dialog dialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
		// TODO proper rotation handling while avoiding re-fetch of all overlays
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
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
        btnRegister = (Button) findViewById(R.id.id_register);
        
        txtEmail.setText(mPrefs.getString("email", null));
        txtPassword.setText(mPrefs.getString("password", null));
        txtFirstName.setText(mPrefs.getString("first_name", null));
        txtLastName.setText(mPrefs.getString("last_name", null));
        txtLicensePlate.setText(mPrefs.getString("license_plate", null));
        txtPhoneNumber.setText(mPrefs.getString("phone_number", null));
        
        //get phone number, depends on operator support, may return null
        TelephonyManager tMgr =(TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        txtPhoneNumber.setText(mPrefs.getString("phone_number", tMgr.getLine1Number()));
        
        strPaymentMethod = mPrefs.getString(getString(R.string.payment_method),"None");
        btnPaymentMethod.setText(strPaymentMethod);
    } 
    
    public void onFBConnect(View view) {
    	showDialog(R.id.awaiting_register);
    	/*
         * Get existing access_token if any
         */
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }
        
        /*
         * Only call authorize if the access_token has expired.
         */
        if(!facebook.isSessionValid()) {

            facebook.authorize(this, new String[] { "email"}, new DialogListener() {
            	
            	/*public void postOnWall(String msg) {
            		Log.d("Tests", "Testing graph API wall post");
            		try {
            			String response = facebook.request("me");
            			Bundle parameters = new Bundle();
            			parameters.putString("message", msg);
            			parameters.putString("description", "test test test");
            			response = facebook.request("me/feed", parameters, "POST");
            			Log.d("Tests", "got response: " + response);
            			if (response == null || response.equals("")
            					|| response.equals("false")) {
            				Log.v("Error", "Blank response");
            			}
            		} catch (Exception e) {
            			e.printStackTrace();
            		}
            	}*/
            	
                @Override
                public void onComplete(Bundle values) {
                	Log.d(TAG, "LoginONComplete");
                    mEditor.putString("access_token", facebook.getAccessToken());
                    mEditor.putLong("access_expires", facebook.getAccessExpires());
                    mEditor.commit();
                    
                   
                    mAsyncRunner.request("me", new RequestListener() {

						@Override
						public void onComplete(String response, Object state) {
							try {
                                Log.d(TAG, "IDRequestONComplete");
                                Log.d(TAG, "Response: " + response.toString());
                                JSONObject json = Util.parseJson(response);
                                fbId = json.getString("id");
                                fbName = json.getString("name");
                                fbEmail = json.getString("email");
                               // postOnWall("ran");
                                
                                


	                        } catch (JSONException e) {
	                                Log.d(TAG, "JSONException: " + e.getMessage());
		                    } catch (FacebookError e) {
		                            Log.d(TAG, "FacebookError: " + e.getMessage());
	                        }
							
							//showDialog(R.id.awaiting_register);
					    	if(strPaymentMethod.contains("Payment Method"))
					    		strPaymentMethod = "None";
					    		
					        mEditor.putString("email", fbEmail);
					        mEditor.putString("password", fbId);
					        mEditor.putString("first_name", fbName);
					        mEditor.putString("last_name", "");
					        mEditor.putString("license_plate", txtLicensePlate.getText().toString());
					        mEditor.putString("phone_number",txtPhoneNumber.getText().toString());
					        mEditor.putString(getString(R.string.payment_method), strPaymentMethod);
					        
					        //spawn registration task
					        //we finish only after receiving response from the server
					        if(regTask!=null)
					        	regTask.cancel(false);
					        regTask = new RegistrationTask(RegisterActivity.this, RegisterActivity.this, fbEmail, 
					        		fbId, "", 
					        		"", txtPhoneNumber.getText().toString(), txtLicensePlate.getText().toString(), strPaymentMethod);
					        regTask.execute((Void[])null);
					        
									
						}
						
								
						@Override
						public void onIOException(IOException e, Object state) {
							Log.d(TAG, "onIOException: " + e.getMessage());
						}

						@Override
						public void onFileNotFoundException(
								FileNotFoundException e, Object state) {
							Log.d(TAG, "onFileNotFoundException: " + e.getMessage());
						}

						@Override
						public void onMalformedURLException(
								MalformedURLException e, Object state) {	
							Log.d(TAG, "onMalformedURLException: " + e.getMessage());
						}

						@Override
						public void onFacebookError(FacebookError e,
								Object state) {
							Log.d(TAG, "onFacebookError: " + e.getMessage());
						}
                    	
                    });

                }
    
                @Override
                public void onFacebookError(FacebookError e) 
                {
                	Log.d(TAG, "FacebookError: " + e.getMessage());
                }
    
                @Override
                public void onError(DialogError e) 
                {
                	Log.d(TAG, "Error: " + e.getMessage());
                }
    
                @Override
                public void onCancel() 
                {
                	Log.d(TAG, "OnCancel");
                }
            });
            
            
            
            // if facebookClient.authorize(...) was
			// successful, this runs // this also runs
			// after successful post // after posting,
			// "post_id" is added to the values bundle
			// // I use that to differentiate between a
			// call from // faceBook.authorize(...) and
			// a call from a successful post // is there
			// a better way of doing this? if
					//if (!values.containsKey("post_id")) {
            
            //--------------------------
            
            	/*if (facebook.isSessionValid()) {	
            		Bundle parameters = new Bundle();	
            		parameters.putString("message", "Check it out!");
            		try {				String response = facebook.request("me/feed", parameters,"POST");
            		System.out.println(response);
            		} catch (IOException e) {	
            			e.printStackTrace();	
            			}		
            	}*/
            
            //--------------------
            //Facebook post
						try {
						// if (facebook.isSessionValid()){
							Bundle parameters = new Bundle();
							parameters.putString("message",
									"CityPark - I've Just Parked My Car!");
							facebook.dialog(this, "stream.publish",
									parameters, new Facebook.DialogListener() {										
										@Override
										public void onFacebookError(FacebookError arg0) {}										
										@Override
										public void onError(DialogError arg0) {}										
										@Override
										public void onComplete(Bundle arg0) {}										
										@Override
										public void onCancel() {}
									});
							//}
							} catch (Exception e) { 
							Log.d(TAG,e.getMessage());
						}
        }
    }

	

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
   
    public void onRegister(View view) {    	
    	//remove trailing and leading spaces
    	txtEmail.setText(txtEmail.getText().toString().trim());
    	txtPassword.setText(txtPassword.getText().toString().trim());
    	txtFirstName.setText(txtFirstName.getText().toString().trim());
    	txtLastName.setText(txtLastName.getText().toString().trim());
    	
    	if(txtEmail.getText().length() == 0) {
    		txtEmail.setError(getString(R.string.registration_missing_fields));
    		return;
    	}
    	
    	try {
    		if(!EMAIL_ADDRESS_PATTERN.matcher(txtEmail.getText().toString()).matches()) {
    			txtEmail.setError(getString(R.string.email_invalid));
    			return;
    		}
        }
        catch( NullPointerException exception ) {
            return;
        }

    	
    	if(txtPassword.getText().length() == 0) {
    		txtPassword.setError(getString(R.string.registration_missing_fields));
    		return;
    	}
    	
    	btnRegister.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_pressed));
    	
    	showDialog(R.id.awaiting_register);
    	if(strPaymentMethod.contains("Payment Method"))
    		strPaymentMethod = "None";
    		
        mEditor.putString("email", txtEmail.getText().toString());
        mEditor.putString("password", txtPassword.getText().toString());
        mEditor.putString("first_name", txtFirstName.getText().toString());
        mEditor.putString("last_name", txtLastName.getText().toString());
        mEditor.putString("license_plate", txtLicensePlate.getText().toString());
        mEditor.putString("phone_number",txtPhoneNumber.getText().toString());
        mEditor.putString(getString(R.string.payment_method), strPaymentMethod);
        
        //spawn registration task
        //we finish only after receiving response from the server
        if(regTask!=null)
        	regTask.cancel(false);
        regTask = new RegistrationTask(this, this, txtEmail.getText().toString(), 
        		txtPassword.getText().toString(), txtFirstName.getText().toString(), 
        		txtLastName.getText().toString(), txtPhoneNumber.getText().toString(), txtLicensePlate.getText().toString(), strPaymentMethod);
        regTask.execute((Void[])null);
        
        btnRegister.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_normal));
    }
    
    public void OnPaymenMethodClick(View view) {
    	showDialog(R.id.select_payment_provider);
    }

	@Override
	public void RegistrationComplete(final String successCode) {
		if(dialog!=null && dialog.isShowing())
			dialog.dismiss();
		
		if(successCode==null) {
			Log.e("onRegister", "registration failed due to server error");
        	Toast.makeText(this, R.string.registration_failed, Toast.LENGTH_LONG).show();
        	return;
		}
		
		if(CityParkConsts.USER_ALREADY_EXISTS.equalsIgnoreCase(successCode)){
			Log.e(CityParkConsts.USER_ALREADY_EXISTS,"User already exists in the system!");
			
			//TODO handle existing user - re-registration
		}
		
		if(mEditor.commit()){
			LoginTask.reload();
			this.startActivity(new Intent(this, CityParkRouteActivity.class));
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
	
    protected Dialog onCreateDialog(int id) {
    	AlertDialog.Builder builder;
    	ProgressDialog pDialog;
        switch(id) {
        case R.id.select_payment_provider:
        	final CharSequence[] items = {"None"}; //, "Pango"}; , "CelOpark"};
        	builder = new AlertDialog.Builder(this);
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
        case R.id.awaiting_register:
			pDialog = new ProgressDialog(this);
			pDialog.setCancelable(true);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage(getText(R.string.register));
			pDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					dialog.dismiss();
				}
			});
			dialog = pDialog;
			break;
		default:
            dialog = null;
        }
        return dialog;
    }
}  