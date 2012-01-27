package com.citypark.adapter;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.citypark.R;
import com.citypark.dto.GarageData;

public class GarageDataAdapter extends ArrayAdapter<GarageData> {

	private ArrayList<GarageData> items;
	Context context;

	public GarageDataAdapter(Context context, int textViewResourceId, ArrayList<GarageData> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.context = context;
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
				tt.setText(context.getString(R.string.garage_name)+": "+garageData.getName());                            
			}
			if(bt != null){
				if(garageData.getFirstHourPrice()==0)
					bt.setText(context.getString(R.string.first_hour)+": "+ context.getString(R.string.parking_free));
				else if(garageData.getFirstHourPrice()>0)
					bt.setText(context.getString(R.string.first_hour)+": "+ garageData.getFirstHourPrice());
				else
					bt.setText("");
			}

			ImageView imgView = (ImageView)v.findViewById(R.id.icon);
			
			if(garageData.getImageDrawable()!=null){
				//use image from server	
				imgView.setImageDrawable(garageData.getImageDrawable());	
			}else{
				imgView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icon));
			}

		}
		return v;
	}

}
