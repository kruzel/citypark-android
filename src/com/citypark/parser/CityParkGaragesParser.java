/**
 * 
 */
package com.citypark.parser;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;
import android.util.Xml;

import com.citypark.utility.route.PGeoPoint;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	static final String XMLNS = "http://tempuri.org/";

	/**
	 * @param feedUrl
	 */
	public CityParkGaragesParser(final String feedUrl) {
		super(feedUrl);
	}

	//TODO:make it nicer
	public class Point{
		double latitude;
		double longitude;
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

	public List<PGeoPoint> parse() {
			final Point p = new Point();

			final RootElement root = new RootElement(XMLNS,"ArrayOfParking");
			final List<PGeoPoint> marks = new ArrayList<PGeoPoint>();
			final Element node = root.getChild(XMLNS,"Parking");
			// Listen for start of tag, get attributes and set them
			// on current marker.
			//Please note that the order should stay longitude and after latitude as they appear in the XML!!!!
			node.getChild(XMLNS,"Longitude").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {				
					p.setLongitude(Double.parseDouble(body));
				}
			});
			
			node.getChild(XMLNS,"Latitude").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setLatitude(Double.parseDouble(body));
					marks.add(p.getPGeoPoint());
				}
			});
			try {
				Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root
						.getContentHandler());
			} catch (IOException e) {
				Log.e(e.getMessage(), "OSMParser - " + feedUrl);
			} catch (SAXException e) {
				Log.e(e.getMessage(), "OSMParser - " + feedUrl);
			}
			return marks;
	}

}
