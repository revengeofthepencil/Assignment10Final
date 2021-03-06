package com.myownbadself.assignment10final;

import java.util.Date;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assignment10final.R;
import com.myownbadself.assignment10final.model.CloudSighting;
import com.myownbadself.assignment10final.model.CloudSightingCollection;
import com.myownbadself.assignment10final.model.ConditionInfo;
import com.myownbadself.assignment10final.util.CloudConstants;
import com.myownbadself.assignment10final.util.LocationCachingUtil;
import com.myownbadself.assignment10final.util.PictureUtils;
import com.myownbadself.assignment10final.util.WundergroundReader;

public class CloudDetailFragment extends Fragment {

	private CloudSighting cloudSighting;
	private ConditionInfo conditionInfo;
	private String id;
	private View view;
	public static final int REQUEST_PHOTO = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		id = getActivity().getIntent().getStringExtra(
				CloudConstants.EXTRA_CLOUD_SIGHTING_ID);

		if(id != null ) {
			cloudSighting = CloudSightingCollection.getInstance(getActivity())
					.getCloudSightingByID(id);
		} 
		

		if (cloudSighting != null) {
			if (cloudSighting.getConditionInfo() != null) {
				conditionInfo = cloudSighting.getConditionInfo();
			}
			
			Log.i(CloudConstants.LOG_KEY, "in onCreate with id = " + id + ", alien location = "
					 + cloudSighting.getDate());
			getActivity().setTitle(
					cloudSighting.toString());
			
		} else {
			cloudSighting = new CloudSighting(
					UUID.randomUUID().toString(),
					new Date());
			getActivity().setTitle(getString(R.string.new_cloud_sighting));
			Log.i(CloudConstants.LOG_KEY, "in onCreate with id = " + id + "new alien");
			
		}

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_cloud_detail, container,
				false);
		
		if (conditionInfo == null) {
			new FetchConditionsTask(getActivity()).execute();
		}
		
		initDateView();
		populateConditions();
		initSightingImage();
		initChangeImageButton();
		initDescText();
		return view;
	}
	
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		if (requestCode == REQUEST_PHOTO) {
			String imageName = data.getStringExtra(CloudConstants.EXTRA_NEW_IMAGE_NAME);
			
			// if the image name is not null, use it for the sighting
			if (imageName != null) {
				replaceImageForSighting(cloudSighting, imageName);
			}
			
			initSightingImage();
		}
		
	}
	
	
	private void initDescText() {
		EditText descEditText = (EditText)view
				.findViewById(R.id.edittext_sighting_desc);
		if (cloudSighting.getDescription() != null
				&& cloudSighting.getDescription().length() > 0) {
			descEditText.setText(cloudSighting.getDescription());
		}

	}

	public void initChangeImageButton() {
		Button launchCameraButton = (Button) view.findViewById(R.id.button_launch_camera);
		launchCameraButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CameraActivity.class);
				startActivityForResult(intent, REQUEST_PHOTO);

			}
		});

	}

	
	private void initSightingImage() {
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview_cloud_picture);

		// use the default image if nothing is set for the current sighting or
		// if the image fails to load from disk
		if (cloudSighting.getCloudImage() == null
				|| PictureUtils.populateImageViewFromFile(getActivity(), 
						imageView, 
						cloudSighting.getCloudImage(), null, null) == false) {
			imageView.setImageResource(R.drawable.cloud_default);
		}

	}

	private void populateConditions() {
		if (getActivity() == null || conditionInfo == null) {
			return;
		}

		TextView textView = (TextView) view.findViewById(
				R.id.textview_conditions);

		if (textView != null) {
			StringBuilder conditionSB = new StringBuilder();

			if (conditionInfo.getLocation() != null) {
				conditionSB.append("Location: " + conditionInfo.getLocation()
						+ "\n");
			}

			if (conditionInfo.getTemperature() != null) {
				conditionSB.append("Temperature: "
						+ conditionInfo.getTemperature() + "\n");
			}

			if (conditionInfo.getConditions() != null) {
				conditionSB.append("Conditions: "
						+ conditionInfo.getConditions() + "\n");
			}

			if (conditionInfo.getHumidity() != null) {
				conditionSB.append("Humidity: " + conditionInfo.getHumidity()
						+ "\n");
			}

			if (conditionInfo.getWind() != null) {
				conditionSB.append("Wind: " + conditionInfo.getWind() + "\n");
			}

			textView.setText(conditionSB.toString());
			cloudSighting.setConditionInfo(conditionInfo);
		}
	}

	private void initDateView() {
		TextView textView = (TextView) view.findViewById(
				R.id.textview_sighting_date);
		textView.setText(CloudConstants.FORMATTER
				.format(cloudSighting.getDate()));
	}


	@Override
	public void onPause() {
		super.onPause();
		
		EditText descEditText = (EditText)view
				.findViewById(R.id.edittext_sighting_desc);
		
		cloudSighting.setDescription(descEditText.getText().toString());
		
		
		// add to the sighting collection if this is a new sighting and 
		// we have conditions
		if (id == null 
				&& cloudSighting.getConditionInfo() != null 
				&& cloudSighting.getConditionInfo().getLocation().length() > 0) {
			Log.i(CloudConstants.LOG_KEY, "adding new sighting for location "
					+ cloudSighting.getConditionInfo().getLocation());
			CloudSightingCollection.addCloudSighting(cloudSighting);
			// hold on to the id for another pause
			id = cloudSighting.getId();
		}

		CloudSightingCollection.getInstance(getActivity())
			.saveCloudSightings();
		
	}

	private class FetchConditionsTask extends
			AsyncTask<Void, Void, ConditionInfo> 
			implements LocationListener {

		private LocationManager locationManager;
		private Location location;
        private Context context;
        LocationCachingUtil locationTrackingUtil;
		public FetchConditionsTask(Context context) {
			super();
			this.context = context;
		}

        @Override
        protected void onPreExecute() {
        	locationTrackingUtil = LocationCachingUtil.getInstance();
        	Location locationFromUtil = locationTrackingUtil.getLocation();
        	if (locationFromUtil == null) {
    			locationManager = (LocationManager) context.getSystemService(
    					Context.LOCATION_SERVICE);
    			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
    					0, 0, this);
        	} else {
    			Log.i(CloudConstants.LOG_KEY, "No need to wait, the tracking util has a location");
        		location = locationFromUtil;
        	}

        }
        

        @Override
        protected void onCancelled(){
        	if (locationManager != null) {
            	locationManager.removeUpdates(this);
        	}
        }

		
		@Override
		protected ConditionInfo doInBackground(Void... params) {
			// hold up while we wait for updates
			Log.i(CloudConstants.LOG_KEY, "Waiting for coords in doInBackground");
			
			// grab the lastKnown if we don't have a location 
			if (this.location == null) {
				Location lastKnown = locationManager.getLastKnownLocation(
						LocationManager.GPS_PROVIDER);
				this.location = lastKnown;
			}
			
			if (this.location == null) {
				this.location = new Location("");
				this.location.setLatitude(47.615597);
				this.location.setLongitude(-122.321622);
			}
			
			// wait for location updates
			while(this.location == null) {
			}
			
			Log.i(CloudConstants.LOG_KEY, "oh snap! we got lat / long "
					+ this.location.getLatitude() + " / " + this.location.getLongitude());
			return new WundergroundReader().fetchConditions(
					new double[]{this.location.getLatitude(), 
							this.location.getLongitude()}
					);
			
		}

		@Override
		protected void onPostExecute(ConditionInfo result) {
			conditionInfo = result;
			populateConditions(); 
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.i(CloudConstants.LOG_KEY, "Location changed in FetchConditionsTask, " + location.getAccuracy() + " , " + location.getLatitude()+ "," + location.getLongitude());
			this.location = location;
			locationTrackingUtil.setLocation(location);
			locationManager.removeUpdates(this);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}

	}
	


	/**
	 * Set a new image for the CloudSighting after deleting any existing image
	 * from disk - this should keep us from filling up the drive with old
	 * images. It would probably be better to assign each sighting object a
	 * unique ID and use that id as the image name, but I'm running out of time
	 * on this assignment.
	 * 
	 * @param sighting
	 * @param imagePath
	 */
	private void replaceImageForSighting(CloudSighting sighting, String imagePath) {
		PictureUtils.deleteImageForCloudSighting(sighting, getActivity());
		// now that we've dumped the old image, set the new one
		sighting.setCloudImage(imagePath);
	}

	
}


