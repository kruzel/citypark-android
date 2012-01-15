/**
 * 
 */
package com.citypark.parser;

import java.io.IOException;
import org.xml.sax.SAXException;
import android.content.Context;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

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
 * 
 * @author jono@nanosheep.net
 * @version Jun 26, 2010
 */
public class CityParkStartPaymentParser extends XMLParser {
	static final String XMLNS = "http://citypark.co.il/ws/";
	
	/**
	 * 
	 * @param operationStatus values:ACKNOWLEDGED,FAILED,UNVERIFIED
	 */
	public CityParkStartPaymentParser(final Context context, final String sessionId, final String paymentProviderName, final double latitude, final double longitude, final String operationStatus) { 
		super(context.getString(R.string.citypark_api) + "reportStartPayment" + "?sessionId=" + sessionId + "&paymentProviderName=" + paymentProviderName + "&latitude=" + Double.toString(latitude) + "&longitude=" + Double.toString(longitude) + "&operationStatus=" + operationStatus, context);
	}

	public String parse() {
		final Result res = new Result();
		final RootElement root = new RootElement(XMLNS,"boolean");
		// Listen for start of tag, get attributes and set them
		// on current marker.
		//Please note that the order should stay as they appear in the XML!!!!
		root.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {	
				res.result = body;
			}
		});
		
		try {
			Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root
					.getContentHandler());
		} catch (IOException e) {
			Log.e(e.getMessage(), "CityParkStartPaymentParser - " + feedUrl);
			Toast.makeText(mContext, mContext.getString(R.string.io_error_msg),Toast.LENGTH_LONG).show();
		} catch (SAXException e) {
			Log.e(e.getMessage(), "CityParkStartPaymentParser - " + feedUrl);
			Toast.makeText(mContext, mContext.getString(R.string.response_error_msg),Toast.LENGTH_LONG).show();
		}
		return res.result;
	}
	
	private class Result {
		public String result;
	}

}
