/**
 * 
 */
package com.citypark.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import android.content.Context;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import com.citypark.R;
import com.citypark.utility.route.PGeoPoint;

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
public class CityParkRegisterParser extends XMLParser {
	static final String XMLNS = "http://citypark.co.il/ws/";
	
	/**
	 * @param feedUrl
	 */
	public CityParkRegisterParser(final Context context, final String email, final String password, final String firstName, final String familyName, final String phoneNumber, final String licensesPlate, final String paymentService) {
		super(context.getString(R.string.citypark_register_api) + "?email=" + email + "&password=" + password + "&firstName=" + firstName + "&familyName=" + familyName + "&phoneNumber=" + phoneNumber + "&licensesPlate=" + licensesPlate + "&paymentService=" + paymentService);
	}

	public String parse() {
		final Reponse res = new Reponse();
		final RootElement root = new RootElement(XMLNS,"string");
		//final Element node = root.getChild(XMLNS,"String");
		// Listen for start of tag, get attributes and set them
		// on current marker.
		//Please note that the order should stay as they appear in the XML!!!!
		root.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {	
				res.response = body;
			}
		});
		
		try {
			Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root
					.getContentHandler());
		} catch (IOException e) {
			Log.e(e.getMessage(), "CityParkRegisterParser - " + feedUrl);
		} catch (SAXException e) {
			Log.e(e.getMessage(), "CityParkRegisterParser - " + feedUrl);
		}
		return res.response;
	}
	
	private class Reponse {
		public String response;
	}

}
