package com.citypark;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.citypark.adapter.GarageDataAdapter;
import com.citypark.api.task.GarageDetailsListFetchTask;
import com.citypark.api.task.GarageDetailsListListener;
import com.citypark.api.task.LoginTask;
import com.citypark.constants.CityParkConsts;
import com.citypark.dto.GarageData;

public class GarageListActivity extends ListActivity implements GarageDetailsListListener {

	private ProgressDialog m_ProgressDialog = null;
	private ArrayList<GarageData> m_garage = null;
	private GarageDataAdapter m_adapter;
	private GarageDetailsListFetchTask task;
	private double lat,lng;

	/** Dialog display. **/
	protected Dialog dialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		showDialog(R.id.loading_info);
		setContentView(R.layout.garage_item_list_view);
		m_garage = new ArrayList<GarageData>();
		this.m_adapter = new GarageDataAdapter(this, R.layout.garage_item,
				m_garage);
		setListAdapter(this.m_adapter);
		
		lat = getIntent().getDoubleExtra(CityParkConsts.LATITUDE,0.0d);
		lng = getIntent().getDoubleExtra(CityParkConsts.LONGITUDE,0.0d);
		if(lat==0||lng==0) {			
			return;
		}
		
		if(LoginTask.isLoggedIn()) {
			task = new GarageDetailsListFetchTask(this, this, LoginTask.getSessionId(), lat, lng);
			task.execute();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		try
		{ 
			super.onListItemClick(l, v, position, id);
			GarageData garageData = (GarageData)l.getItemAtPosition(position);
			Intent intent = new Intent(this,GarageDetailsActivity.class);
			//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			intent.putExtra(CityParkConsts.GARAGE_ID, garageData.getParkingId());
			this.startActivity(intent);
		}
		catch(Exception ex)
		{
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void GarageDetailsFetchComplete(List<GarageData> gdList) {
		if(gdList!=null){
			m_garage.clear();
			m_garage.addAll(gdList);
			m_adapter.notifyDataSetChanged();
		}
		else
			Toast.makeText(this, getString(R.string.io_error_msg), Toast.LENGTH_LONG).show();
		
		if(dialog!=null && dialog.isShowing())
			dialog.dismiss();
	}
	
    protected Dialog onCreateDialog(int id) {
    	ProgressDialog pDialog;
        switch(id) {
        case R.id.loading_info:
			pDialog = new ProgressDialog(this);
			pDialog.setCancelable(true);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDialog.setMessage(getText(R.string.load_msg));
			pDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					dialog.dismiss();
				}
			});
			dialog = pDialog;
			break;
		default:
            dialog = null;
        }
        return dialog;
    }
}