package com.citypark.api.task;


import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.os.AsyncTask;

import com.citypark.view.overlay.LiveGarageMarkers;
import com.citypark.view.overlay.LiveStreetLinesMarkers;
import com.citypark.view.overlay.LiveStreetReleasesMarkers;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class AllOverlayFetchTask extends AsyncTask<GeoPoint, Void, Void> {
	private OverlayListener listener;
	private LiveStreetLinesMarkers lineMarkers;
	private LiveStreetReleasesMarkers releasesMarkers;
	private LiveGarageMarkers garageMarkers;

	private Boolean garagesRes;
	private Boolean releasesRes;
	private Boolean linesRes;
	
	LineFetchTask lineTask;
	ReleasesFetchTask relTask;
	GaragesFetchTask garageTask;

	public AllOverlayFetchTask(final MapView osmv, Context context, OverlayListener listener, LiveGarageMarkers garageMarkers, LiveStreetReleasesMarkers releasesMarkers, LiveStreetLinesMarkers linesMarkers) {
		super();
		this.listener = listener;
		this.lineMarkers = linesMarkers;
		this.releasesMarkers = releasesMarkers;
		this.garageMarkers = garageMarkers;
		
		lineTask = new LineFetchTask();
		relTask = new ReleasesFetchTask();
		garageTask = new GaragesFetchTask();
	}

	@Override
	protected void onCancelled() {
		lineTask.cancel(true);
		relTask.cancel(true);
		garageTask.cancel(true);
		
		super.onCancelled();
	}

	@Override
	protected Void doInBackground(GeoPoint... p) {
		linesRes = false;
		releasesRes = false;
		garagesRes = false;
		
		lineTask.execute(p[0]);
		relTask.execute(p[0]);
		garageTask.execute(p[0]);
		
		try {
			synchronized (lineTask) {
				lineTask.get();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			
		}
		
		try {
			synchronized (relTask) {
				relTask.get();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			
		}
		
		try {
			synchronized (garageTask) {
				garageTask.get();				
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if(!isCancelled())
			listener.overlayFetchComplete(garagesRes,releasesRes,linesRes);
		super.onPostExecute(result);
	}
	
	private class LineFetchTask extends AsyncTask<GeoPoint, Void, Boolean> {

		@Override
		protected Boolean doInBackground(GeoPoint... p) {
			linesRes = lineMarkers.fetch(p[0]);
			return linesRes;
		}
		
	}
	
	private class ReleasesFetchTask extends AsyncTask<GeoPoint, Void, Boolean> {

		@Override
		protected Boolean doInBackground(GeoPoint... p) {
			releasesRes = releasesMarkers.fetch(p[0]); 
			return releasesRes;
		}
		
	}
	
	private class GaragesFetchTask extends AsyncTask<GeoPoint, Void, Boolean> {

		@Override
		protected Boolean doInBackground(GeoPoint... p) {
			garagesRes = garageMarkers.fetch(p[0]);
			return garagesRes;
		}
		
	}
}
