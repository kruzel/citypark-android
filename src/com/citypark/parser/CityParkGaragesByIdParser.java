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
public class CityParkGaragesByIdParser extends XMLParser {
	static final String XMLNS = "http://citypark.co.il/ws/";
	
	/**
	 * @param feedUrl
	 */
	public CityParkGaragesByIdParser(final Context context, final String sessionId, final int parkingId) {
		
		try {
			feedUrl = new URL(context.getString(R.string.citypark_api) + "fetchGarageParkingById" + "?sessionId=" + sessionId + "&parkingId=" + parkingId);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//TODO:make it nicer
	public class GarageDetailes{	
		private int id;	//<ParkingId>int</ParkingId>
		private String name; //<Name>string</Name>
		private String city; //<City>string</City>
		private String streetName; //<StreetName>string</StreetName>
		private int houseNumber; //<HouseNumber>int</HouseNumber>
		private double longitude; //<Longitude>string</Longitude>
		private double latitude; //<Latitude>string</Latitude>
		//<Numberofparks>string</Numberofparks>
		//<Tel>string</Tel>
		//<Fax>string</Fax>
		//<Email>string</Email>
		//<Onlymail>string</Onlymail>
		//<Comment>string</Comment>
		//<Payment>string</Payment>
		//<Image>string</Image>
		//<Image2>string</Image2>
		//<StartDate>string</StartDate>
		//<EndDate>string</EndDate>
		//<Parkingtype>string</Parkingtype>
		//<Contactname>string</Contactname>
		//<Hourly>string</Hourly>
		//<Daily>string</Daily>
		//<Weekly>string</Weekly>
		//<Monthly>string</Monthly>
		//<Yearly>string</Yearly>
		//<Price>string</Price>
		//<Pricefortime>string</Pricefortime>
		//<Cartype>string</Cartype>
		//<Manuytype>string</Manuytype>
		//<Payments>string</Payments>
		//<Cc>string</Cc>
		//<Cash>string</Cash>
		//<Cheak>string</Cheak>
		//<Paypal>string</Paypal>
		//<Lelohagbalatheshbon>string</Lelohagbalatheshbon>
		//<Majsom>string</Majsom>
		//<Tatkarkait>string</Tatkarkait>
		private Boolean garage = false; //<Jenion>string</Jenion>
		private Boolean criple = false; //<Criple>string</Criple>
		private Boolean noLimit = false; //<Nolimit>string</Nolimit>
		//<Henion>string</Henion>
		private Boolean withLock = false; //<Withlock>string</Withlock>
		private Boolean underground = false; //<Underground>string</Underground>
		private Boolean roof = false; //<Roof>string</Roof>
		//<Vip>string</Vip>
		private Boolean toshav = false; //<Toshav>string</Toshav>
		//<Coupon>string</Coupon>
		private String couponText = "N/A";//<Coupon_text>string</Coupon_text>
		private GarageAvailability availability; //<Current_Pnuyot>string</Current_Pnuyot>
		//<SiteID>string</SiteID>
		//<Heniontype>string</Heniontype>
		private double allDayPrice; //<AllDayPrice>string</AllDayPrice>
		private double extraQuarterPrice; //<ExtraQuarterPrice>string</ExtraQuarterPrice>
		private double firstHourPrice; //<FirstHourPrice>string</FirstHourPrice>
		
		public GarageDetailes() {
			super();
		}

		public GarageDetailes(GarageDetailes g) {
			super();
			this.id = g.id;
			this.name = g.name;
			this.city = g.city;
			this.streetName = g.streetName;
			this.houseNumber = g.houseNumber;
			this.longitude = g.longitude;
			this.latitude = g.latitude;
			this.garage = g.garage;
			this.criple = g.criple;
			this.noLimit = g.noLimit;
			this.withLock = g.withLock;
			this.underground = g .underground;
			this.roof = g.roof;
			this.toshav = g.toshav;
			this.couponText = g.couponText;
			this.availability = g.availability;
			this.allDayPrice = g.allDayPrice;
			this.extraQuarterPrice = g.extraQuarterPrice;
			this.firstHourPrice = g.firstHourPrice;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getStreetName() {
			return streetName;
		}

		public void setStreetName(String streetName) {
			this.streetName = streetName;
		}

		public int getHouseNumber() {
			return houseNumber;
		}

		public void setHouseNumber(int houseNumber) {
			this.houseNumber = houseNumber;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public Boolean getGarage() {
			return garage;
		}

		public void setGarage(Boolean garage) {
			this.garage = garage;
		}

		public Boolean getCriple() {
			return criple;
		}

		public void setCriple(Boolean criple) {
			this.criple = criple;
		}

		public Boolean getNoLimit() {
			return noLimit;
		}

		public void setNoLimit(Boolean noLimit) {
			this.noLimit = noLimit;
		}

		public Boolean getWithLock() {
			return withLock;
		}

		public void setWithLock(Boolean withLock) {
			this.withLock = withLock;
		}

		public Boolean getUnderground() {
			return underground;
		}

		public void setUnderground(Boolean underground) {
			this.underground = underground;
		}

		public Boolean getRoof() {
			return roof;
		}

		public void setRoof(Boolean roof) {
			this.roof = roof;
		}

		public Boolean getToshav() {
			return toshav;
		}

		public void setToshav(Boolean toshav) {
			this.toshav = toshav;
		}

		public String getCouponText() {
			return couponText;
		}

		public void setCouponText(String couponText) {
			this.couponText = couponText;
		}

		public GarageAvailability getAvailability() {
			return availability;
		}

		public void setAvailability(GarageAvailability availability) {
			this.availability = availability;
		}

		public double getAllDayPrice() {
			return allDayPrice;
		}

		public void setAllDayPrice(double allDayPrice) {
			this.allDayPrice = allDayPrice;
		}

		public double getExtraQuarterPrice() {
			return extraQuarterPrice;
		}

		public void setExtraQuarterPrice(double extraQuarterPrice) {
			this.extraQuarterPrice = extraQuarterPrice;
		}

		public double getFirstHourPrice() {
			return firstHourPrice;
		}

		public void setFirstHourPrice(double firstHourPrice) {
			this.firstHourPrice = firstHourPrice;
		}		
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
						p.setAllDayPrice(Double.parseDouble(body));  
				    }  
				    catch( NumberFormatException e )  
				    {  
				    	p.setAllDayPrice(0);
				    } 
				}
			});
			root.getChild(XMLNS,"ExtraQuarterPrice").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {
					
					try  
					{  
						p.setExtraQuarterPrice(Double.parseDouble(body));  
				    }  
				    catch( NumberFormatException e )  
				    {  
				    	p.setExtraQuarterPrice(0);
				    } 
				}
			});
			root.getChild(XMLNS,"FirstHourPrice").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {
					try  
					{  
						p.setFirstHourPrice(Double.parseDouble(body));  
				    }  
				    catch( NumberFormatException e )  
				    {  
				    	p.setFirstHourPrice(0);
				    } 
				}
			});
			
			try {
				Xml.parse(this.getInputStream(), Xml.Encoding.UTF_8, root
						.getContentHandler());
			} catch (IOException e) {
				Log.e(e.toString(), "CityParkGaragesParser - " + feedUrl);
				e.printStackTrace();
			} catch (SAXException e) {
				Log.e(e.getMessage(), "CityParkGaragesParser - " + feedUrl);
				e.printStackTrace();
			}catch(Exception ex){
				Log.e( "CityParkGaragesParser - " + feedUrl, ex.getMessage());
			}
			return p;
	}

}
