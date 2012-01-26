/**
 * 
 */
package com.citypark.api.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;
import android.widget.ImageView;
import android.widget.Toast;

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
		super(context.getString(R.string.citypark_api) + "findAllGarageParkingDataByLatitudeLongitude" + "?sessionId=" + sessionId + "&latitude="+ latitude/1E6 + "&longitude=" + longitude/1E6 + "&distance=" + distance);
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
						
						if(gd.getImage1()!=null&& !"null".equalsIgnoreCase(gd.getImage1())&&!"".equals(gd.getImage1())){
							try {
								Drawable image = fetchImage(gd.getImage1());
								gd.setImageDrawable(image);
							} catch (URISyntaxException e) {
								Log.e("Garage data adapter error on garage parking id="+gd.getParkingId(),e.getMessage());
								e.printStackTrace();
							}
						}else{
							gd.setImageDrawable(null);
						}
				}
			});
			
			node.getChild(XMLNS,"FirstHourPrice").setEndTextElementListener(new EndTextElementListener() {
				public void end(String body) {
					try  
					{  
						if(body.length()==0)
							gd.setFirstHourPrice(-1);
						else
							gd.setFirstHourPrice(Integer.parseInt(body));  
				    }  
				    catch( NumberFormatException e )  
				    {  
				    	gd.setFirstHourPrice(-1);
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
				return null;
			} catch (SAXException e) {
				Log.e(e.getMessage(), "CityParkGaragesParser - " + feedUrl);
				e.printStackTrace();
				return null;
			}catch(Exception ex){
				Log.e( "CityParkGaragesParser - " + feedUrl, ex.getMessage());
				return null;
			}
			return gdList;
	}

	private Drawable fetchImage(String fileName) throws URISyntaxException {
		try {
			URI uri = new URI(
				    "http", 
				    "api.cityparkmobile.com", 
				    fileName,
				    null);
			String request = uri.toASCIIString();
			URL url = new URL(request);
			InputStream is = (InputStream) url.getContent();
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
