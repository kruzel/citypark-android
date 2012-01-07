package com.citypark.service;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.citypark.constants.CityParkConsts;
<<<<<<< HEAD
import com.citypark.dto.GarageData;
import com.citypark.parser.CityParkGarageDetailsListParser;
=======
import com.citypark.dto.GaragePoint;
import com.citypark.parser.CityParkGaragesParser;
>>>>>>> f198459639ab7663169e9245ccb8b0d08243ee19

public class GarageDetailsListFetchTask extends AsyncTask<Void, Void, Void> {
	
	private Context context;
	private String sessionId;
	private double lat,lng;
	private List<GarageData> details;
	private GarageDetailsListListener listener;
	

	public GarageDetailsListFetchTask(Context context, GarageDetailsListListener listener, String sessionId,double lat,double lng) {
		super();
		this.context = context;
		this.sessionId = sessionId;
		this.listener = listener;
		this.lat = lat;
		this.lng = lng;
	}

	@Override
	protected Void doInBackground(Void... params) {
		CityParkGarageDetailsListParser parser = new CityParkGarageDetailsListParser(context, sessionId, lat,lng,CityParkConsts.RADIUS);
		details = parser.parse();

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		listener.GarageDetailsFetchComplete(details);
		super.onPostExecute(result);
	}

}
