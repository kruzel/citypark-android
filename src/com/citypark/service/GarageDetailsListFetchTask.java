package com.citypark.service;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.citypark.constants.CityParkConsts;
import com.citypark.dto.GarageData;
import com.citypark.parser.CityParkGarageDetailsListParser;
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
