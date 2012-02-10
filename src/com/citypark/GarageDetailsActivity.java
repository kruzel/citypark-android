package com.citypark;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.citypark.api.parser.GarageDetailes;
import com.citypark.api.task.GarageDetailsFetchTask;
import com.citypark.api.task.GarageDetailsListener;
import com.citypark.api.task.LoginTask;
import com.citypark.api.task.ReportGarageStatusTask;
import com.citypark.constants.CityParkConsts;
import com.citypark.constants.GarageAvailability;

public class GarageDetailsActivity extends Activity implements
		GarageDetailsListener {

	private GarageDetailsFetchTask task;
	Thread t;
	
	ReportGarageStatusTask reportTask;

	/** Dialog display. **/
	protected Dialog dialog;

	GarageDetailes currentGarageDetails;

	private int garageId;
	private TextView garageName;
	private TextView garageAddress;
	private TextView garagePaymentMthod;
	private ImageView criple;
	private ImageView garage;
	private ImageView nis;
	private ImageView noLimit;
	private ImageView roof;
	private ImageView underground;
	private ImageView withLock;
	private ImageView garageImage;

	private TextView fisrtHourMidWeek;
	private TextView firstHourWeekend;
	private TextView extraQuaterMidWeek;
	private TextView extraQuaterWeekend;
	private TextView allDayMidWeek;
	private TextView allDayWeekend;

	private TextView couponText;
	
	private ToggleButton tgBtnReportFree;
	private ToggleButton tgBtnReportbusy;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO proper rotation handling while avoiding re-fetch of all overlays
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.garage_detailes);

		init();
	}

	@Override
	protected void onResume() {
		init();

		task = new GarageDetailsFetchTask(GarageDetailsActivity.this, this,
				LoginTask.getSessionId(), garageId);
		task.execute((Void[]) null);
		super.onResume();
	}

	public void showDirections(View view) {
		Intent intent = new Intent(GarageDetailsActivity.this,
				CityParkRouteActivity.class);

		intent.putExtra(CityParkConsts.LATITUDE,
				currentGarageDetails.getLatitude());
		intent.putExtra(CityParkConsts.LONGITUDE,
				currentGarageDetails.getLongitude());
		startActivity(intent);
	}
	
	public void onFreePressed(View view) {
		if(tgBtnReportFree.isChecked()) {
			tgBtnReportFree.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_green_selected));
			tgBtnReportbusy.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_red_unselected));
		
			//run report status task
			if(reportTask!=null)
				reportTask.cancel(true);
			
			reportTask = new ReportGarageStatusTask(this, LoginTask.getSessionId(), garageId, GarageAvailability.FREE);
			reportTask.execute(null);
		} 
	}
	
	public void onBusyPressed(View view) {
		if(tgBtnReportbusy.isChecked()) {
			tgBtnReportbusy.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_red_selected));
			tgBtnReportFree.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_green_unselected));
			
			//run report status task
			if(reportTask!=null)
				reportTask.cancel(true);
			
			reportTask = new ReportGarageStatusTask(this, LoginTask.getSessionId(), garageId, GarageAvailability.BUSY);
			reportTask.execute(null);
		}
	}

	private void init() {
		if (!LoginTask.isLoggedIn())
			return;

		showDialog(R.id.loading_info);

		garageId = getIntent().getIntExtra(CityParkConsts.GARAGE_ID, 0);
		if (garageId == 0) {
			// TODO show message
			// TODO try to get location
			return;
		}

		garageName = (TextView) findViewById(R.id.textViewName);
		garageAddress = (TextView) findViewById(R.id.textViewAddress);
		garagePaymentMthod = (TextView) findViewById(R.id.textViewPaymentMethod);
		// images
		criple = (ImageView) findViewById(R.id.imageViewCriple);
		garage = (ImageView) findViewById(R.id.imageViewGarage);
		nis = (ImageView) findViewById(R.id.imageViewNis);
		noLimit = (ImageView) findViewById(R.id.imageViewRoof);
		roof = (ImageView) findViewById(R.id.imageViewRoof);
		underground = (ImageView) findViewById(R.id.imageViewUnderground);
		withLock = (ImageView) findViewById(R.id.imageViewWithlock);
		garageImage = (ImageView) findViewById(R.id.imageViewImage);
		// prices table
		fisrtHourMidWeek = (TextView) findViewById(R.id.textViewFirstHourMidWeek);
		firstHourWeekend = (TextView) findViewById(R.id.textViewFirstHourWeekend);
		extraQuaterMidWeek = (TextView) findViewById(R.id.textViewExtraQuaterMidWeek);
		extraQuaterWeekend = (TextView) findViewById(R.id.textViewExtraQuaterWeekend);
		allDayMidWeek = (TextView) findViewById(R.id.textViewAllDayMidWeek);
		allDayWeekend = (TextView) findViewById(R.id.textViewAllDayWeekend);
		// coupon
		couponText = (TextView) findViewById(R.id.textViewCouponText);
		
		tgBtnReportFree = (ToggleButton) findViewById(R.id.button_report_garage_free);
		tgBtnReportbusy = (ToggleButton) findViewById(R.id.button_report_garage_busy);

	}

	@Override
	public void GarageDetailsFetchComplete(GarageDetailes garageDetails) {
		if (garageDetails == null) {
			Toast.makeText(this, getString(R.string.io_error_msg),
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		if (garageDetails.getName() != null
				&& garageDetails.getName().length() > 0) {
			garageName.setText(garageDetails.getName());
			String address = "";
			if (garageDetails.getStreetName() != null) {
				address += garageDetails.getStreetName();
				if (garageDetails.getHouseNumber() != 0)
					address += " " + garageDetails.getHouseNumber();
			}
			if (garageDetails.getCity() != null)
				address += " " + garageDetails.getCity();

			garageAddress.setText(address);
		}
		// TODO garagePaymentMthod
		// garagePaymentMthod.setText("On Exit, Visa, Cash");
		// images
		if (garageDetails.getCriple())
			criple.setImageDrawable(getResources().getDrawable(
					R.drawable.criple));
		else
			criple.setImageDrawable(getResources().getDrawable(
					R.drawable.criple_g));

		if (garageDetails.getGarage())
			garage.setImageDrawable(getResources().getDrawable(
					R.drawable.garage));
		else
			garage.setImageDrawable(getResources().getDrawable(
					R.drawable.garage_g));

		if (garageDetails.getNoLimit())
			noLimit.setImageDrawable(getResources().getDrawable(
					R.drawable.nolimit));
		else
			noLimit.setImageDrawable(getResources().getDrawable(
					R.drawable.nolimit_g));

		if (garageDetails.getRoof())
			roof.setImageDrawable(getResources().getDrawable(R.drawable.roof));
		else
			roof.setImageDrawable(getResources().getDrawable(R.drawable.roof_g));

		if (garageDetails.getUnderground())
			underground.setImageDrawable(getResources().getDrawable(
					R.drawable.underground));
		else
			underground.setImageDrawable(getResources().getDrawable(
					R.drawable.underground_g));

		if (garageDetails.getWithLock())
			withLock.setImageDrawable(getResources().getDrawable(
					R.drawable.withlock));
		else
			withLock.setImageDrawable(getResources().getDrawable(
					R.drawable.withlock_g));

		if (garageDetails.getImageURL() != null
				&& !"null".equalsIgnoreCase(garageDetails.getImageURL())
				&& !"".equals(garageDetails.getImageURL())) {
			try {
				URI uri = new URI("http", "api.cityparkmobile.com",
						garageDetails.getImageURL(), null);
				String request = uri.toASCIIString();
				// Drawable image =
				// ImageOperations(getContext(),"http://api.cityparkmobile.com"+URLEncoder.encode(garageData.getImage1(),"UTF-8"),"image.jpg");
				Drawable image = ImageOperations(this, request, "image.jpg");
				if (image == null) {
					ImageView imag = (ImageView) findViewById(R.id.imageViewImage);
					imag.setImageDrawable(getResources().getDrawable(
							R.drawable.icon_citypark));
					garageImage = imag;
				} else {
					garageImage.setImageDrawable(image);
				}
			} catch (Exception e) {
				Log.e("Garage data adapter error on garage parking id="
						+ garageDetails.getId(), e.getMessage());
				e.printStackTrace();
				ImageView imag = (ImageView) findViewById(R.drawable.icon);
				garageImage = imag;
			}
		} else {
			/*
			 * ImageView imag = (ImageView)findViewById(R.id.imageViewImage);
			 * imag
			 * .setImageDrawable(getResources().getDrawable(R.drawable.icon_citypark
			 * )); garageImage=imag;
			 */
			ImageView imag = (ImageView) findViewById(R.drawable.icon);
			garageImage = imag;
		}

		// prices table
		if (garageDetails.getFirstHourPrice() == 0)
			fisrtHourMidWeek.setText(getString(R.string.parking_free));
		if (garageDetails.getFirstHourPrice() > 0)
			fisrtHourMidWeek.setText(Double.toString(garageDetails
					.getFirstHourPrice()));

		if (garageDetails.getFirstHourPrice() == 0
				&& garageDetails.getExtraQuarterPrice() == 0)
			extraQuaterMidWeek.setText(getString(R.string.parking_free));
		else if (garageDetails.getExtraQuarterPrice() > 0)
			extraQuaterMidWeek.setText(Double.toString(garageDetails
					.getExtraQuarterPrice()));

		if (garageDetails.getAllDayPrice() == 0)
			allDayMidWeek.setText(getString(R.string.parking_free));
		if (garageDetails.getAllDayPrice() > 0)
			allDayMidWeek.setText(Double.toString(garageDetails
					.getAllDayPrice()));

		// TODO get values from server
		firstHourWeekend.setText("-");
		extraQuaterWeekend.setText("-");
		allDayWeekend.setText("-");

		// coupon
		if (garageDetails.getCouponText() != null)
			couponText.setText(garageDetails.getCouponText());

		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
		
		if(garageDetails.getAvailability()==GarageAvailability.FREE) {
			tgBtnReportFree.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_green_selected));
			tgBtnReportbusy.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_red_unselected));
		} else if(garageDetails.getAvailability()==GarageAvailability.BUSY) {
			tgBtnReportFree.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_green_unselected));
			tgBtnReportbusy.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_red_selected));
		} 
		//else info unavailable
			
		currentGarageDetails = garageDetails;
	}

	private Drawable ImageOperations(Context ctx, String url,
			String saveFilename) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			// Toast.makeText(this,
			// getString(R.string.io_error_msg),Toast.LENGTH_LONG).show();
			return null;
		}
	}

	public Object fetch(String address) throws MalformedURLException,
			IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

	protected Dialog onCreateDialog(int id) {
		ProgressDialog pDialog;
		switch (id) {
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
