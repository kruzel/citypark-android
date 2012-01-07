package com.citypark.adapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.citypark.R;
import com.citypark.dto.*;

public class GarageDataAdapter extends ArrayAdapter<GarageData> {

	private ArrayList<GarageData> items;

	public GarageDataAdapter(Context context, int textViewResourceId, ArrayList<GarageData> items) {
		super(context, textViewResourceId, items);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate((R.layout.garage_item), null);
		}
		GarageData garageData = items.get(position);
		if (garageData != null) {
			TextView tt = (TextView) v.findViewById(R.id.toptext);
			TextView bt = (TextView) v.findViewById(R.id.bottomtext);
			if (tt != null) {
				tt.setText("Name: "+garageData.getName());                            }
			if(bt != null){
				bt.setText("First hour price: "+ garageData.getFirstHourPrice());
			}

			if(garageData.getImage1()!=null&& !"null".equalsIgnoreCase(garageData.getImage1())&&!"".equals(garageData.getImage1())){
				try {
					URI uri = new URI(
						    "http", 
						    "api.cityparkmobile.com", 
						    garageData.getImage1(),
						    null);
					String request = uri.toASCIIString();
				//	Drawable image = ImageOperations(getContext(),"http://api.cityparkmobile.com"+URLEncoder.encode(garageData.getImage1(),"UTF-8"),"image.jpg");
					Drawable image = ImageOperations(getContext(),request,"image.jpg");
					ImageView imgView = new ImageView(getContext());
					imgView = (ImageView)v.findViewById(R.id.icon);
					imgView.setImageDrawable(image);
				} catch (URISyntaxException e) {
					Log.e("Garage data adapter error on garage parking id="+garageData.getParkingId(),e.getMessage());
					e.printStackTrace();
				}
			}else{
				ImageView imag = (ImageView)v.findViewById(R.drawable.icon);
				ImageView imgView = new ImageView(getContext());
				imgView = (ImageView)v.findViewById(R.id.icon);
				imgView=imag;
			}

		}
		return v;
	}

	private Drawable ImageOperations(Context ctx, String url, String saveFilename) {
		try {
			InputStream is = (InputStream) this.fetch(url);
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

	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

}
