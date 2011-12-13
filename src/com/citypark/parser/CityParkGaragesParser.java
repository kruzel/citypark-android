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
public class CityParkGaragesParser extends XMLParser {
	static final String XMLNS = "http://citypark.co.il/ws/";
	
	/**
	 * @param feedUrl
	 */
	public CityParkGaragesParser(final Context context, final String sessionId, final double latitude, final double longitude, final double distance) {
		
		try {
			//TODO use real lat long
			//feedUrl = new URL(context.getString(R.string.citypark_garages_api) + "?sessionId=" + sessionId + "&latitude="+ latitude + "&longitude=" + longitude + "&distance=" + distance);
			feedUrl = new URL(context.getString(R.string.citypark_garages_api) + "?sessionId=" + sessionId + "&latitude="+ "32.0717" + "&longitude=" + "34.7792" + "&distance=" + "1000");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//TODO:make it nicer
	public class GaragePoint{
		double latitude;
		double longitude;
		double price;
		String name;
		
		public GaragePoint() {
			// TODO Auto-generated constructor stub
		}
		public GaragePoint(GaragePoint p) {
			this.latitude = p.latitude;
			this.longitude = p.longitude;
			this.price = p.price;
			this.name = p.name;
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
		public void setPrice(double price) {
			this.price = price;
		}
		public double getPrice(){
			return price;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getName(){
			return name;
		}
	}

	public List<GaragePoint> parse() {
			final GaragePoint p = new GaragePoint();

			final RootElement root = new RootElement(XMLNS,"ArrayOfParking");
			final List<GaragePoint> marks = new ArrayList<GaragePoint>();
			final Element node = root.getChild(XMLNS,"Parking");
			// Listen for start of tag, get attributes and set them
			// on current marker.
			//Please note that the order should stay longitude and after latitude as they appear in the XML!!!!
			node.getChild(XMLNS,"Name").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setName(body);
				}
			});
			
			node.getChild(XMLNS,"Longitude").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {				
					p.setLongitude(Double.parseDouble(body));
				}
			});
			
			node.getChild(XMLNS,"Latitude").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setLatitude(Double.parseDouble(body));
				}
			});
			
			node.getChild(XMLNS,"FirstHourPrice").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {
					
					try  
					{  
						p.setPrice(Double.parseDouble(body));  
				    }  
				    catch( NumberFormatException e )  
				    {  
				    	p.setPrice(0);
				    } 
						
					marks.add(new GaragePoint(p));
				}
			});
			
			try {
				Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root
						.getContentHandler());
			} catch (IOException e) {
				Log.e(e.getMessage(), "CityParkGaragesParser - " + feedUrl);
			} catch (SAXException e) {
				Log.e(e.getMessage(), "CityParkGaragesParser - " + feedUrl);
			}
			return marks;
	}

}
