/**
 * 
 */
package com.citypark.api.parser;

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
import com.citypark.dto.GarageData;
import com.citypark.utility.route.PGeoPoint;

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
public class CityParkGaragesByIdParser extends XMLParser {
	static final String XMLNS = "http://citypark.co.il/ws/";
	
	/**
	 * @param feedUrl
	 */
	public CityParkGaragesByIdParser(final Context context, final String sessionId, final int parkingId) {
		super(context.getString(R.string.citypark_api) + "fetchGarageParkingById" + "?sessionId=" + sessionId + "&parkingId=" + parkingId+"&language="+context.getString(R.string.language));		
	}

	public GarageDetailes parse() {
			final GarageDetailes p = new GarageDetailes();
			final RootElement root = new RootElement(XMLNS,"Parking");
			// Listen for start of tag, get attributes and set them
			// on current marker.
			//Please note that the order should stay longitude and after latitude as they appear in the XML!!!!
			root.getChild(XMLNS,"ParkingId").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setId(Integer.parseInt(body));
				}
			});
			root.getChild(XMLNS,"Name").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setName(body);
				}
			});
			root.getChild(XMLNS,"City").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setCity(body);
				}
			});
			root.getChild(XMLNS,"StreetName").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setStreetName(body);
				}
			});
			root.getChild(XMLNS,"HouseNumber").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setHouseNumber(Integer.parseInt(body));
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
			root.getChild(XMLNS,"Image").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					if(body!=null)
						p.setImageURL(body);
				}
			});
			root.getChild(XMLNS,"Jenion").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					if(body!=null)
						p.setGarage((body.equals("1")) ? true : false );
				}
			});
			root.getChild(XMLNS,"Criple").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					if(body!=null)
						p.setCriple((body.equals("1")) ? true : false );
				}
			});
			root.getChild(XMLNS,"Nolimit").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					if(body!=null)
						p.setNoLimit((body.equals("1")) ? true : false );
				}
			});
			root.getChild(XMLNS,"Withlock").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					if(body!=null)
						p.setWithLock((body.equals("1")) ? true : false );
				}
			});
			root.getChild(XMLNS,"Underground").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					if(body!=null)
						p.setUnderground((body.equals("1")) ? true : false );
				}
			});
			root.getChild(XMLNS,"Roof").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					if(body!=null)
						p.setRoof((body.equals("1")) ? true : false );
				}
			});
			root.getChild(XMLNS,"Toshav").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {
					if(body!=null)
						p.setToshav((body.equals("1")) ? true : false );
				}
			});
			root.getChild(XMLNS,"Coupon_text").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					p.setCouponText(body);
				}
			});		
			root.getChild(XMLNS,"Current_Pnuyot").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					try{
						int currentFree = Integer.parseInt(body);
						p.setAvailability(GarageAvailability.getByValue(currentFree));
					}catch(Exception ex){
						p.setAvailability(GarageAvailability.UNKNOWN);
					}					
				}
			});

			root.getChild(XMLNS,"AllDayPrice").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {
					try  
					{  
						if(body.length()==0)
							p.setAllDayPrice(-1);
						else
							p.setAllDayPrice(Integer.parseInt(body));  
				    }  
				    catch( NumberFormatException e )  
				    {  
				    	p.setAllDayPrice(-1);
				    } 
				}
			});
			root.getChild(XMLNS,"ExtraQuarterPrice").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {
					try  
					{  
						if(body.length()==0)
							p.setExtraQuarterPrice(-1);
						else
							p.setExtraQuarterPrice(Integer.parseInt(body));  
				    }  
				    catch( NumberFormatException e )  
				    {  
				    	p.setExtraQuarterPrice(-1);
				    } 
				}
			});
			root.getChild(XMLNS,"FirstHourPrice").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {
					try  
					{  
						if(body.length()==0)
							p.setFirstHourPrice(-1);
						else
							p.setFirstHourPrice(Integer.parseInt(body));  
				    }  
				    catch( NumberFormatException e )  
				    {  
				    	p.setFirstHourPrice(-1);
				    } 
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
			return p;
	}

}
