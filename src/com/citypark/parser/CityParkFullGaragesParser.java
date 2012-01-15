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
import com.citypark.dto.GarageDetailes;

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
public class CityParkFullGaragesParser extends XMLParser {
	static final String XMLNS = "http://citypark.co.il/ws/";
	
	/**
	 * @param feedUrl
	 */
	public CityParkFullGaragesParser(final Context context, final String sessionId, final double latitude, final double longitude, final int distance) {
		super(context.getString(R.string.citypark_api) + "findGarageParkingByLatitudeLongitude" + "?sessionId=" + sessionId + "&latitude="+ latitude/1E6 + "&longitude=" + longitude/1E6 + "&distance=" + distance);		
	}

	public List<GarageDetailes> parse() {
			final GarageDetailes p = new GarageDetailes();

			final RootElement root = new RootElement(XMLNS,"ArrayOfParking");
			final List<GarageDetailes> marks = new ArrayList<GarageDetailes>();
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
			
			node.getChild(XMLNS,"Current_Pnuyot").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					try{
						int currentFree = Integer.parseInt(body);
						p.setAvailability(GarageAvailability.getByValue(currentFree));
					}catch(Exception ex){
						p.setAvailability(GarageAvailability.UNKNOWN);
					}					
				}
			});
			
			node.getChild(XMLNS,"FirstHourPrice").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {
					
					try  
					{  
						p.setFirstHourPrice(Double.parseDouble(body));  
				    }  
				    catch( NumberFormatException e )  
				    {  
				    	p.setFirstHourPrice(0);
				    } 
						
					marks.add(new GarageDetailes(p));
				}
			});
			
			try {
				Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root
						.getContentHandler());
			} catch (IOException e) {
				Log.e(e.toString(), "CityParkGaragesParser - " + feedUrl);
				e.printStackTrace();
				return null;
			} catch (SAXException e) {
				Log.e(e.getMessage(), "CityParkGaragesParser - " + feedUrl);
				e.printStackTrace();
				return null;
			}catch(Exception ex){
				Log.e( "CityParkGaragesParser - " + feedUrl, ex.getMessage());
				return null;
			}
			return marks;
	}

}
