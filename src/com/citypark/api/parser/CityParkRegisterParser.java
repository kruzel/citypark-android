/**
 * 
 */
package com.citypark.api.parser;

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
 * This file is part of CityPark.
 * 
 * Copyright (C) 2011  Ofer Kruzel
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
 * A class to display markers on a map and update them from a remote
 * feed.
 * @author okruzel@gmail.com
 * @version Dec 21, 2011
 */
public class CityParkRegisterParser extends XMLParser {
	static final String XMLNS = "http://citypark.co.il/ws/";
	
	/**
	 * @param feedUrl
	 */
	public CityParkRegisterParser(final Context context, final String email, final String password, final String firstName, final String familyName, final String phoneNumber, final String licensesPlate, final String paymentService) {
		super(context.getString(R.string.citypark_api) + "register" + "?email=" + email + "&password=" + password + "&firstName=" + firstName + "&familyName=" + familyName + "&phoneNumber=" + phoneNumber + "&licensesPlate=" + licensesPlate + "&paymentService=" + paymentService);
	}

	public String parse() {
		final Reponse res = new Reponse();
		final RootElement root = new RootElement(XMLNS,"string");
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
			return null;
		} catch (SAXException e) {
			Log.e(e.getMessage(), "CityParkRegisterParser - " + feedUrl);
			return null;
		}
		
		return res.response;
	}
	
	private class Reponse {
		public String response;
	}

}
