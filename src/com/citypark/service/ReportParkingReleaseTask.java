package com.citypark.service;

import android.content.Context;
import android.os.AsyncTask;

import com.citypark.parser.CityParkReportParkingReleaseParser;

public class ReportParkingReleaseTask extends AsyncTask<Void, Void, Void> {
	
	private Context context;
	private String sessionId;
	private double latitude;
	private double longitude;

	public ReportParkingReleaseTask(Context context, String sessionId,
			double latitude, double longitude) {
		super();
		this.context = context;
		this.sessionId = sessionId;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	protected Void doInBackground(Void... params) {
		CityParkReportParkingReleaseParser parser = new CityParkReportParkingReleaseParser(context, sessionId, latitude, longitude);
		parser.parse();

		return null;
	}

}
