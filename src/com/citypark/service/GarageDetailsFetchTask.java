package com.citypark.service;

import com.citypark.parser.CityParkGaragesByIdParser;
import com.citypark.parser.CityParkReportLocationParser;
import com.citypark.parser.GarageDetailes;

import android.content.Context;
import android.os.AsyncTask;

public class GarageDetailsFetchTask extends AsyncTask<Void, Void, Void> {
	
	private Context context;
	private String sessionId;
	private int parkingId;
	GarageDetailes details;
	GarageDetailsListener listener;

	public GarageDetailsFetchTask(Context context, GarageDetailsListener listener, String sessionId, int parkingId) {
		super();
		this.context = context;
		this.sessionId = sessionId;
		this.parkingId = parkingId;
		this.listener = listener;
	}

	@Override
	protected Void doInBackground(Void... params) {
		CityParkGaragesByIdParser parser = new CityParkGaragesByIdParser(context, sessionId, parkingId);
		details = parser.parse();

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		listener.GarageDetailsFetchComplete(details);
		super.onPostExecute(result);
	}

}
