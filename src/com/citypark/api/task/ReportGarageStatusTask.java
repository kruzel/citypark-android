package com.citypark.api.task;

import android.content.Context;
import android.os.AsyncTask;

import com.citypark.api.parser.CityParkReportGarageStatusParser;
import com.citypark.constants.GarageAvailability;

public class ReportGarageStatusTask extends AsyncTask<Void, Void, Void> {
	
	private Context context;
	private String sessionId;
	private int parkingId;
	private final GarageAvailability status;

	public ReportGarageStatusTask(Context context, String sessionId,
			int parkingId, GarageAvailability status) {
		super();
		this.context = context;
		this.sessionId = sessionId;
		this.parkingId = parkingId;
		this.status = status;
	}

	@Override
	protected Void doInBackground(Void... params) {
		CityParkReportGarageStatusParser parser = new CityParkReportGarageStatusParser(context, sessionId, parkingId, status);
		parser.parse();

		return null;
	}

}
