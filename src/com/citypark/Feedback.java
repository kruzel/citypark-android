/**
 * 
 */
package com.citypark;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.citypark.api.task.LoginTask;
import com.citypark.utility.MyHttpClient;
import com.citypark.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * Activity for sending feedback on a route to CycleStreets.net.
 * 
 * This file is part of BikeRoute.
 * 
 * Copyright (C) 2011  Jonathan Gray
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * @author jono@nanosheep.net
 * @version Nov 11, 2010
 */
public class Feedback extends Activity {
	private CityParkApp app;
	private TextView nameField;
	private TextView commentField;
	private Button submit;
	private SubmitHandler submitHandler;

	@Override
	public final void onCreate(final Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	app = ((CityParkApp) getApplication());
	requestWindowFeature(Window.FEATURE_RIGHT_ICON);
	setContentView(R.layout.feedback);
	setFeatureDrawableResource(Window.FEATURE_RIGHT_ICON, R.drawable.logo);
	
	nameField = (TextView) findViewById(R.id.name_input);
	commentField = (TextView) findViewById(R.id.comment_input);
	submit = (Button) findViewById(R.id.submit_button);
	
	//Handle rotations
	final Object[] data = (Object[]) getLastNonConfigurationInstance();
	if (data != null) {
		nameField.setText((CharSequence) data[0]);
		commentField.setText((CharSequence) data[1]);
	}
	
	if(nameField.getText().length()==0) {
		SharedPreferences mPrefs = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE);
		nameField.setText(mPrefs.getString("first_name", null) +" " + mPrefs.getString("last_name", null));
	}
	
	//Input validation
	final Validate watcher = new Validate();
	nameField.addTextChangedListener(watcher);
	commentField.addTextChangedListener(watcher);
	
	//Form submit handler
	submitHandler = new SubmitHandler();
	submit.setOnClickListener(submitHandler);
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		Object[] objs = new Object[3];
		objs[0] = nameField.getText();
		objs[1] = commentField.getText();
	    return objs;
	}
	
	@Override
	public Dialog onCreateDialog(final int id) {
		AlertDialog.Builder builder;
		ProgressDialog pDialog;
		Dialog dialog;
		switch(id) {
		case R.id.send:
			pDialog = new ProgressDialog(this);
			pDialog.setCancelable(true);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage(getText(R.string.send_msg));
			pDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(final DialogInterface arg0) {
					removeDialog(R.id.send);
				}
			});
			pDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(final DialogInterface arg0) {
						submitHandler.cancel(true);
				}
				
			});
			dialog = pDialog;
			break;
		case R.id.thanks:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(getText(R.string.thanks_message)).setCancelable(
					true).setPositiveButton(getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog,
								final int id) {
							dialog.dismiss();
							finish();
						}
					});
			dialog = builder.create();
			break;
		case R.id.feedback_fail:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(getText(R.string.feedback_fail_message)).setCancelable(
					true).setPositiveButton(getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialog,
								final int id) {
							dialog.dismiss();
						}
					});
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
	
	private class SubmitHandler extends AsyncTask<Void, Void, Integer> implements OnClickListener {
		private static final int OK = 1;

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View arg0) {
			this.execute();
		}
		
		@Override
		protected void onPreExecute() {
			Feedback.this.showDialog(R.id.send);
		}

		/**
		 * Fire a thread to submit feedback.
		 * 
		 * @param itineraryId
		 * @param name
		 * @param email
		 * @param comment
		 */
		private int doSubmit(final String sessionId, final String name,
				final String comment) {
			int result = OK;
			
			try {
				String[] recipients = new String[]{"support@citypark.co.il", "",};
				Intent target = new Intent(Intent.ACTION_SEND);
				target.putExtra(Intent.EXTRA_EMAIL, recipients );
				target.putExtra(Intent.EXTRA_SUBJECT, "CityPark Android feedback");
				target.putExtra(Intent.EXTRA_TEXT, "My name is " + name + ", sessionId: " + sessionId + ", and my feedback: " + comment);
				target.setType("text/plain");
				Intent intent = Intent.createChooser(target,"Select email app to use");
				startActivity(intent);
			} catch (Exception e) {
				result = -1;
			}
			return result;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Integer doInBackground(Void... arg0) {
			return doSubmit(LoginTask.getSessionId(), nameField.getText().toString(), 
					commentField.getText().toString());
		}
		
		@Override
        protected void onPostExecute(final Integer msg) {
			Feedback.this.dismissDialog(R.id.send);
			if (msg == OK) {
				Feedback.this.showDialog(R.id.thanks);
			} else {
				Feedback.this.showDialog(R.id.feedback_fail);
			}
        }
        
        @Override
        protected void onCancelled() {
        }
		
	}
	
	private class Validate implements TextWatcher {
		Pattern p;
		
		public Validate() {
			p = Pattern.compile("((([ \\t]*[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+[ \\t]*)|(\\\"([ \\t]*([\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F\\x21\\x23-\\x5B\\x5D-\\x7E]|(\\\\[\\x01-\\x09\\x0B\\x0C\\x0E-\\x7F])))*[ \\t]*\\\"))+)?[ \\t]*<(([ \\t]*([a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+(\\.[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+)*)[ \\t]*)|(\\\"([ \\t]*([\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F\\x21\\x23-\\x5B\\x5D-\\x7E]|(\\\\[\\x01-\\x09\\x0B\\x0C\\x0E-\\x7F])))*[ \\t]*\\\"))@([ \\t]*([a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+(\\.[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+)*)[ \\t]*|\\[([ \\t]*[\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F\\x21-\\x5A\\x5E-\\x7E]|(\\\\[\\x01-\\x09\\x0B\\x0C\\x0E-\\x7F])+)*[ \\t]*\\])>|(([ \\t]*([a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+(\\.[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+)*)[ \\t]*)|(\\\"([ \\t]*([\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F\\x21\\x23-\\x5B\\x5D-\\x7E]|(\\\\[\\x01-\\x09\\x0B\\x0C\\x0E-\\x7F])))*[ \\t]*\\\"))@([ \\t]*([a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+(\\.[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~]+)*)[ \\t]*|\\[([ \\t]*[\\x01-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F\\x21-\\x5A\\x5E-\\x7E]|(\\\\[\\x01-\\x09\\x0B\\x0C\\x0E-\\x7F])+)*[ \\t]*\\])");
		}
		
		/* (non-Javadoc)
		 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
		 */
		@Override
		public void afterTextChanged(Editable arg0) {
			if ((commentField.getText().length() != 0)) {
				submit.setEnabled(true);
			} else {
				submit.setEnabled(false);
			}
		}

		/**
		 * @param text
		 * @return
		 */
		private boolean isValid(CharSequence text) {
			return p.matcher(text).matches();
		}

		/* (non-Javadoc)
		 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
		 */
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
		 */
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
