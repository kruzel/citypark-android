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
public class CityParkStreetParkingParser extends XMLParser {
	static final String XMLNS = "http://citypark.co.il/ws/";
	
	/**
	 * @param feedUrl
	 */
	public CityParkStreetParkingParser(final Context context, final String sessionId, final double latitude, final double longitude, final double distance) {
		
		try {
			//TODO use real lat long
			//feedUrl = new URL(context.getString(R.string.citypark_garages_api) + "?sessionId=" + sessionId + "&latitude="+ latitude + "&longitude=" + longitude + "&distance=" + distance);
			feedUrl = new URL(context.getString(R.string.citypark_street_parking_api) + "?sessionId=" + sessionId + "&latitude="+ "32.0717" + "&longitude=" + "34.7792" + "&distance=" + "2000");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//TODO:make it nicer
	public class StreetSegment{
		double start_latitude;
		double start_longitude;
		double end_latitude;
		double end_longitude;
		Integer id;
		double search_time;
		
		public StreetSegment() {
			// TODO Auto-generated constructor stub
		}public StreetSegment(StreetSegment s) {
			start_latitude = s.start_latitude;
			start_longitude = s.start_longitude;
			end_latitude = s.end_latitude;
			end_longitude = s.end_longitude;
			id = s.id;
			search_time = s.search_time;
		}
		
		public double getSearch_time() {
			return search_time;
		}
		public void setSearch_time(double search_time) {
			this.search_time = search_time;
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		double searchTime;
		
		public double getStart_latitude() {
			return start_latitude;
		}
		public void setStart_latitude(double start_latitude) {
			this.start_latitude = start_latitude;
		}
		public double getStart_longitude() {
			return start_longitude;
		}
		public void setStart_longitude(double start_longitude) {
			this.start_longitude = start_longitude;
		}
		public double getEnd_latitude() {
			return end_latitude;
		}
		public void setEnd_latitude(double end_latitude) {
			this.end_latitude = end_latitude;
		}
		public double getEnd_longitude() {
			return end_longitude;
		}
		public void setEnd_longitude(double end_longitude) {
			this.end_longitude = end_longitude;
		}
		public double getSearchTime() {
			return searchTime;
		}
		public void setSearchTime(double searchTime) {
			this.searchTime = searchTime;
		}
		
	
	}

	public List<StreetSegment> parse() {
			final StreetSegment p = new StreetSegment();

			final RootElement root = new RootElement(XMLNS,"ArrayOfSearchParkingSegment ");
			final List<StreetSegment> marks = new ArrayList<StreetSegment>();
			final Element node = root.getChild(XMLNS,"SearchParkingSegment");
			// Listen for start of tag, get attributes and set them
			// on current marker.
			//Please note that the order should stay longitude and after latitude as they appear in the XML!!!!
			node.getChild(XMLNS,"StartLongitude").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setStart_longitude(Double.parseDouble(body));
				}
			});
			
			node.getChild(XMLNS,"StartLatitude").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {				
					p.setStart_latitude(Double.parseDouble(body));
				}
			});
			
			node.getChild(XMLNS,"EndLongitude").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setEnd_longitude(Double.parseDouble(body));
				}
			});
			
			node.getChild(XMLNS,"EndLatitude").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {				
					p.setEnd_latitude(Double.parseDouble(body));
				}
			});
			
			node.getChild(XMLNS,"Id").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {				
					p.setId(Integer.parseInt(body));
				}
			});
			
			node.getChild(XMLNS,"SearchTime").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setSearchTime(Double.parseDouble(body));
					marks.add(new StreetSegment(p));
				}
			});
			
			try {
				Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root
						.getContentHandler());
			} catch (IOException e) {
				Log.e(e.getMessage(), "CityParkStreetParkingParser - " + feedUrl);
			} catch (SAXException e) {
				Log.e(e.getMessage(), "CityParkStreetParkingParser - " + feedUrl);
			}
			return marks;
	}

}
