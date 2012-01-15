package com.citypark;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.citypark.constants.CityParkConsts;
import com.citypark.parser.GarageDetailes;
import com.citypark.service.GarageDetailsFetchTask;
import com.citypark.service.GarageDetailsListener;
import com.citypark.service.LoginTask;

public class GarageDetailsActivity extends Activity implements GarageDetailsListener {
	
	private GarageDetailsFetchTask task;
	
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
		
	Thread t;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.garage_detailes);
		
		if (!LoginTask.isLoggedIn())
			return;
		
		garageId = getIntent().getIntExtra(CityParkConsts.GARAGE_ID, 0);
		if(garageId==0) {
			//TODO show message
			//TODO try to get location
			return;
		}
		
		garageName = (TextView)findViewById(R.id.textViewName);
		garageAddress = (TextView)findViewById(R.id.textViewAddress);
		garagePaymentMthod = (TextView)findViewById(R.id.textViewPaymentMethod);
		//images
		criple = (ImageView)findViewById(R.id.imageViewCriple);
		garage = (ImageView)findViewById(R.id.imageViewGarage);
		nis = (ImageView)findViewById(R.id.imageViewNis);
		noLimit = (ImageView)findViewById(R.id.imageViewRoof);
		roof = (ImageView)findViewById(R.id.imageViewRoof);
		underground = (ImageView)findViewById(R.id.imageViewUnderground);
		withLock = (ImageView)findViewById(R.id.imageViewWithlock);
		garageImage = (ImageView)findViewById(R.id.imageViewImage);
		//prices table
		fisrtHourMidWeek = (TextView)findViewById(R.id.textViewFirstHourMidWeek);
		firstHourWeekend = (TextView)findViewById(R.id.textViewFirstHourWeekend);
		extraQuaterMidWeek = (TextView)findViewById(R.id.textViewExtraQuaterMidWeek);
		extraQuaterWeekend = (TextView)findViewById(R.id.textViewExtraQuaterWeekend);
		allDayMidWeek = (TextView)findViewById(R.id.textViewAllDayMidWeek);
		allDayWeekend = (TextView)findViewById(R.id.textViewAllDayWeekend);
		//coupon
		couponText = (TextView)findViewById(R.id.textViewCouponText);
	}

	@Override
	protected void onResume() {
		task = new GarageDetailsFetchTask(GarageDetailsActivity.this, this, LoginTask.getSessionId(), garageId);
		task.execute((Void[])null);
		super.onResume();
	}
	

	@Override
	public void GarageDetailsFetchComplete(GarageDetailes garageDetails) {
		if(garageDetails==null) {
			Toast.makeText(this, getString(R.string.io_error_msg), Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		garageName.setText(garageDetails.getName());
		garageAddress.setText(garageDetails.getStreetName() + " " + garageDetails.getHouseNumber() + ", " + garageDetails.getCity());
		garagePaymentMthod.setText("On Exit, Visa, Cash");
		//images
		if(garageDetails.getCriple())
			criple.setImageDrawable(getResources().getDrawable(R.drawable.criple));
		else
			criple.setImageDrawable(getResources().getDrawable(R.drawable.criple_g));
		
		if(garageDetails.getGarage())
			garage.setImageDrawable(getResources().getDrawable(R.drawable.garage));
		else
			garage.setImageDrawable(getResources().getDrawable(R.drawable.garage_g));
		
		if(garageDetails.getNoLimit())
			noLimit.setImageDrawable(getResources().getDrawable(R.drawable.nolimit));
		else
			noLimit.setImageDrawable(getResources().getDrawable(R.drawable.nolimit_g));
		
		if(garageDetails.getRoof())
			roof.setImageDrawable(getResources().getDrawable(R.drawable.roof));
		else
			roof.setImageDrawable(getResources().getDrawable(R.drawable.roof_g));

		if(garageDetails.getUnderground())
			underground.setImageDrawable(getResources().getDrawable(R.drawable.underground));
		else
			underground.setImageDrawable(getResources().getDrawable(R.drawable.underground_g));

		if(garageDetails.getWithLock())
			withLock.setImageDrawable(getResources().getDrawable(R.drawable.withlock));
		else
			withLock.setImageDrawable(getResources().getDrawable(R.drawable.withlock_g));

		if(garageDetails.getImageURL()!=null&& !"null".equalsIgnoreCase(garageDetails.getImageURL())&&!"".equals(garageDetails.getImageURL())){
			try {
				URI uri = new URI(
					    "http", 
					    "api.cityparkmobile.com", 
					    garageDetails.getImageURL(),
					    null);
				String request = uri.toASCIIString();
			//	Drawable image = ImageOperations(getContext(),"http://api.cityparkmobile.com"+URLEncoder.encode(garageData.getImage1(),"UTF-8"),"image.jpg");
				Drawable image = ImageOperations(this,request,"image.jpg");
				garageImage.setImageDrawable(image);
			} catch (URISyntaxException e) {
				Log.e("Garage data adapter error on garage parking id="+garageDetails.getId(),e.getMessage());
				e.printStackTrace();
			}
		}else{
			ImageView imag = (ImageView)findViewById(R.drawable.icon);
			garageImage=imag;
		}
		
		//prices table
		fisrtHourMidWeek.setText(Double.toString(garageDetails.getFirstHourPrice()));
		firstHourWeekend.setText("0");
		extraQuaterMidWeek.setText(Double.toString(garageDetails.getExtraQuarterPrice()));
		extraQuaterWeekend.setText("0");
		allDayMidWeek.setText(Double.toString(garageDetails.getAllDayPrice()));
		allDayWeekend.setText("0");
	
		//coupon
		couponText.setText(garageDetails.getCouponText());
	}

	private Drawable ImageOperations(Context ctx, String url, String saveFilename) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, getString(R.string.io_error_msg),Toast.LENGTH_LONG).show();
			return null;
		}
	}

	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

}
