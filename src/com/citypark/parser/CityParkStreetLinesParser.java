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
import com.citypark.view.overlay.StreetSegment;

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
public class CityParkStreetLinesParser extends XMLParser {
	static final String XMLNS = "http://citypark.co.il/ws/";
	
	/**
	 * @param feedUrl
	 */
	public CityParkStreetLinesParser(final Context context, final String sessionId, final double latitude, final double longitude, final int distance) {
		
		try {
			feedUrl = new URL(context.getString(R.string.citypark_api) + "getStreetParkingPrediction" + "?sessionId=" + sessionId + "&latitude="+ latitude/1E6 + "&longitude=" + longitude/1E6 + "&distance=" + distance);
			//feedUrl = new URL(context.getString(R.string.citypark_street_parking_api) + "?sessionId=" + sessionId + "&latitude="+ "32.0717" + "&longitude=" + "34.7792" + "&distance=" + "2000");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<StreetSegment> parse() {
		// Listen for start of tag, get attributes and set them
		// on current marker.
		//Please note that the order should stay longitude and after latitude as they appear in the XML!!!!	
		final StreetSegment p = new StreetSegment();
		final WaitTime segmentWaitTime = new WaitTime();
		
		final RootElement root = new RootElement(XMLNS,"ArrayOfStreetSegment");
		final List<StreetSegment> marks = new ArrayList<StreetSegment>();
		final Element node = root.getChild(XMLNS,"StreetSegment");
		
		node.getChild(XMLNS,"SWT").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {	
				segmentWaitTime.waitTime = Double.parseDouble(body); 
			}
		});
		
		final Element node1 = node.getChild(XMLNS,"SegmentLine");
		final Element node2 = node1.getChild(XMLNS,"StreetSegmentLine");
		
		node2.getChild(XMLNS,"SegmentUnique").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {				
				p.setId(body);
			}
		});
		
		node2.getChild(XMLNS,"StartLatitude").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {				
				p.setStart_latitude(Double.parseDouble(body));
			}
		});
		
		node2.getChild(XMLNS,"StartLongitude").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {	
				p.setStart_longitude(Double.parseDouble(body));
			}
		});
		
		node2.getChild(XMLNS,"EndLatitude").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {				
				p.setEnd_latitude(Double.parseDouble(body));
			}
		});
		
		node2.getChild(XMLNS,"EndLongitude").setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {	
				p.setEnd_longitude(Double.parseDouble(body));
				p.setSearch_time(segmentWaitTime.waitTime);
				marks.add(new StreetSegment(p));
			}
		});
		
		try {
			Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root
					.getContentHandler());
		} catch (IOException e) {
			Log.e(e.getMessage()+e.toString(), "CityParkStreetLinesParser - " + feedUrl);			
			e.printStackTrace();
		} catch (SAXException e) {
			Log.e(e.getMessage()+e.toString(), "CityParkStreetLinesParser - " + feedUrl);
			e.printStackTrace();
		} catch(Exception e){
			Log.e(e.getMessage(), "CityParkStreetLinesParser - " + feedUrl);
			e.printStackTrace();
		}
		return marks;
	}

	private class WaitTime {
		Double waitTime = -1.0;
	}
}


