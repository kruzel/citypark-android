/**
 * 
 */
package com.citypark.utility.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.EditText;
import android.widget.TextView;

import com.citypark.CityParkApp;

import com.citypark.R;

/**
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
 * @version Jan 21, 2011
 */
public class DialogFactory {
	
	/**
	 * Get some common dialogs by id.
	 * @param id
	 * @param context
	 * @return
	 */
	
	public static Dialog getDialog(final int id, final Context context) {
		Dialog d = null;
		switch(id) {
		case R.id.network_error:
			d = getAlert(context, R.string.planfail_network_msg);
			break;
		case R.id.plan_fail:
			d = getAlert(context, R.string.planfail_msg);
			break;
		case R.id.ioerror:
			d = getAlert(context, R.string.io_error_msg);
			break;
		case R.id.argerror:
			d = getAlert(context, R.string.arg_error_msg);
			break;
		case R.id.reserror:
			d = getAlert(context, R.string.result_error_msg);
			break;
		case R.id.geocodeerror:
			d = getAlert(context, R.string.geocodeerror);
			break;
		case R.id.geocodeconnecterror:
			d = getAlert(context, R.string.geocodeconnecterror);
			break;
		}
		
		return d;
	}
	
	/**
	 * Get a generic alert with the message specified by R.string id.
	 * @param context
	 * @param msg
	 * @return
	 */
	
	public static AlertDialog getAlert(Context context, final int msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getText(msg)).setCancelable(
				true).setPositiveButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int id) {
					}
				});
		return builder.create();
	}
	
	/**
	 * Return an about dialog with clickable links.
	 * @param context
	 * @return
	 */
	public static AlertDialog getAboutDialog(Context context) {
		final TextView message = new TextView(context);
		
		final Spanned s = 
		               Html.fromHtml(context.getString(R.string.about_message));
		message.setText(s);
		message.setMovementMethod(LinkMovementMethod.getInstance());

		return new AlertDialog.Builder(context)
		   .setCancelable(true)
		   .setIcon(android.R.drawable.ic_dialog_info)
		   .setPositiveButton(R.string.ok, null)
		   .setView(message)
		   .create();
	}
	
}
