package com.citypark.api.task;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.citypark.view.overlay.LiveGarageMarkers;
import com.citypark.view.overlay.LiveStreetLinesMarkers;
import com.citypark.view.overlay.LiveStreetReleasesMarkers;

public class AllOverlayFetchTask extends AsyncTask<GeoPoint, Void, Void> {
	private OverlayListener listener;
	private LiveStreetLinesMarkers lineMarkers;
	private LiveStreetReleasesMarkers releasesMarkers;
	private LiveGarageMarkers garageMarkers;

	private Boolean garagesRes;
	private Boolean releasesRes;
	private Boolean linesRes;

	public AllOverlayFetchTask(final MapView osmv, Context context, OverlayListener listener, LiveGarageMarkers garageMarkers, LiveStreetReleasesMarkers releasesMarkers, LiveStreetLinesMarkers linesMarkers) {
		super();
		this.listener = listener;
		this.lineMarkers = linesMarkers;
		this.releasesMarkers = releasesMarkers;
		this.garageMarkers = garageMarkers;
	}

	@Override
	protected Void doInBackground(GeoPoint... p) {
		linesRes = false;
		releasesRes = false;
		garagesRes = false;
		
		if(!isCancelled())
			linesRes = lineMarkers.fetch(p[0]);
		if(!isCancelled())
			releasesRes = releasesMarkers.fetch(p[0]);
		if(!isCancelled())
			garagesRes = garageMarkers.fetch(p[0]);
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if(!isCancelled())
			listener.overlayFetchComplete(garagesRes,releasesRes,linesRes);
		super.onPostExecute(result);
	}
}
