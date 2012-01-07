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
import com.citypark.dto.GarageData;
import com.citypark.utility.route.PGeoPoint;
/**
 * This class fetches all garage details in a given radius.
 * @author Ran Brandes
 *
 */
public class CityParkGarageDetailsListParser extends XMLParser {
	static final String XMLNS = "http://citypark.co.il/ws/";
	
	/**
	 * @param feedUrl
	 */
	public CityParkGarageDetailsListParser(final Context context, final String sessionId, final double latitude, final double longitude, final int distance) {
		
		try {
			feedUrl = new URL(context.getString(R.string.citypark_api) + "findAllGarageParkingDataByLatitudeLongitude" + "?sessionId=" + sessionId + "&latitude="+ latitude/1E6 + "&longitude=" + longitude/1E6 + "&distance=" + distance);
		} catch (MalformedURLException e) {
			Log.e("CityParkGarageDetailsListParser error",e.getMessage());
		}
	}

	

	public List<GarageData> parse() {
			final RootElement root = new RootElement(XMLNS,"ArrayOfParking");
			final List<GarageData> gdList = new ArrayList<GarageData>();
			final Element node = root.getChild(XMLNS,"Parking");
			final GarageData gd = new GarageData();
			node.getChild(XMLNS,"ParkingId").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {				
					gd.setParkingId(Integer.parseInt(body));
				}
			});
			
			node.getChild(XMLNS,"Name").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					gd.setName(body);
				}
			});
			
			node.getChild(XMLNS,"Longitude").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {				
					gd.setLongitude(Double.parseDouble(body));
				}
			});
			
			node.getChild(XMLNS,"Latitude").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {	
					gd.setLatitude(Double.parseDouble(body));
				}
			});
			
			node.getChild(XMLNS,"Image").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {						
						gd.setImage1(body);
				}
			});
			
			node.getChild(XMLNS,"FirstHourPrice").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {
					
					try  
					{  
						gd.setFirstHourPrice(Integer.parseInt(body));  
				    }  
				    catch( NumberFormatException e )  
				    {  
				    	gd.setFirstHourPrice(0);
				    } 
						
					gdList.add(new GarageData(gd));
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
			return gdList;
	}

}
