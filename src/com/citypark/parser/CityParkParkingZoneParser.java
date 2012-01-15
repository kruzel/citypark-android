/**
 * 
 */
package com.citypark.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import android.content.Context;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.citypark.R;
import com.citypark.constants.GarageAvailability;
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
public class CityParkParkingZoneParser extends XMLParser {
	static final String XMLNS = "http://citypark.co.il/ws/";
	
	/**
	 * @param feedUrl
	 */
	public CityParkParkingZoneParser(final Context context, final String sessionId, final double latitude, final double longitude) {
		
		try {
			feedUrl = new URL(context.getString(R.string.citypark_api) + "getParkingAreaZone" + "?sessionId=" + sessionId + "&latitude="+ latitude + "&longitude=" + longitude );
			//feedUrl = new URL(context.getString(R.string.citypark_garages_api) + "?sessionId=" + sessionId + "&latitude="+ "32.0717" + "&longitude=" + "34.7792");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class LocationData{
		String city;
		String street;
		String parkingZone;
		double latitude;
		double longitude;
		
		public LocationData() {
			super();
		}
		public LocationData(String city, String street, String parkingZone,
				double latitude, double longitude) {
			super();
			this.city = city;
			this.street = street;
			this.parkingZone = parkingZone;
			this.latitude = latitude;
			this.longitude = longitude;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getParkingZone() {
			return parkingZone;
		}
		public void setParkingZone(String parkingZone) {
			this.parkingZone = parkingZone;
		}
		public double getLatitude() {
			return latitude;
		}
		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}
		public double getLongitude() {
			return longitude;
		}
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}
		public PGeoPoint getPGeoPoint(){
			return new PGeoPoint(latitude,longitude);
		}

	}

	public LocationData parse() {
			final LocationData p = new LocationData();

			final RootElement root = new RootElement(XMLNS,"LocationData");
			//final Element node = root.getChild(XMLNS,"City");
			// Listen for start of tag, get attributes and set them
			// on current marker.
			//Please note that the order should stay longitude and after latitude as they appear in the XML!!!!
			root.getChild(XMLNS,"City").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setCity(body);
				}
			});
			
			root.getChild(XMLNS,"ParkingZone").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setParkingZone(body);
				}
			});
			
			root.getChild(XMLNS,"Longitude").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {				
					p.setLongitude(Double.parseDouble(body));
				}
			});
			
			root.getChild(XMLNS,"Latitude").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setLatitude(Double.parseDouble(body));
				}
			});
			
			try {
				Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root
						.getContentHandler());
			} catch (IOException e) {
				Log.e(e.toString(), "CityParkGaragesParser - " + feedUrl);
				Toast.makeText(mContext, mContext.getString(R.string.io_error_msg),Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (SAXException e) {
				Log.e(e.getMessage(), "CityParkGaragesParser - " + feedUrl);
				Toast.makeText(mContext, mContext.getString(R.string.response_error_msg),Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}catch(Exception ex){
				Log.e( "CityParkGaragesParser - " + feedUrl, ex.getMessage());
			}
			return p;
	}

}
