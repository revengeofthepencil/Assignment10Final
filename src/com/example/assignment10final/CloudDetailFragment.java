package com.example.assignment10final;

import java.io.File;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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

import com.example.assignment10final.model.CloudSighting;
import com.example.assignment10final.model.CloudSightingCollection;
import com.example.assignment10final.model.ConditionInfo;
import com.example.assignment10final.util.CloudConstants;
import com.example.assignment10final.util.LocationCachingUtil;
import com.example.assignment10final.util.PictureUtils;
import com.example.assignment10final.util.WundergroundReader;

public class CloudDetailFragment extends Fragment {

	// NOLA: 29.917758,-90.113994
	// Seattle: 47.605876, -122.321718
	// San Francisco: 37.764207, -122.469143

	private CloudSighting cloudSighting;
	private ConditionInfo conditionInfo;
	private int id;
	private View view;
	public static final int REQUEST_PHOTO = 1;


	public static CloudDetailFragment newInstance(int id) {
		Bundle args = new Bundle();
		
		args.putInt(CloudConstants.EXTRA_CLOUD_SIGHTING_ID, id);
		CloudDetailFragment dFrag = new CloudDetailFragment();
		dFrag.setArguments(args);
		return dFrag;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		id = getActivity().getIntent().getIntExtra(
				CloudConstants.EXTRA_CLOUD_SIGHTING_ID, 0);

		if(id < 0) {
			cloudSighting = new CloudSighting(new Date());
			Log.i(CloudConstants.LOG_KEY, "in onCreate with id = " + id + "new alien");
		} else {
			cloudSighting = CloudSightingCollection.getInstance(getActivity())
					.getCloudSightings().get(id);
			if (cloudSighting.getConditionInfo() != null) {
				conditionInfo = cloudSighting.getConditionInfo();
			}
			
			Log.i(CloudConstants.LOG_KEY, "in onCreate with id = " + id + ", alien location = "
					 + cloudSighting.getDate());
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
				|| populateImageViewFromFile(imageView, 
						cloudSighting.getCloudImage()) == false) {
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
		if (id < 0 
				&& cloudSighting.getConditionInfo() != null 
				&& cloudSighting.getConditionInfo().getLocation().length() > 0) {
			Log.i(CloudConstants.LOG_KEY, "adding new sighting for location "
					+ cloudSighting.getConditionInfo().getLocation());

			CloudSightingCollection.addCloudSighting(cloudSighting);

			// hold on to the id for another pause
			id = CloudSightingCollection.getCount() - 1;
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
			
			
			// TODO: this is too damn slow - we need a way to enter coords outside of this screen
			if (this.location == null) {
				this.location = new Location("");
				this.location.setLatitude(47.605876);
				this.location.setLongitude(-122.321718);				
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
	
	private boolean populateImageViewFromFile(ImageView imageView, String photoPath) {
		
		BitmapDrawable bmDrawable = null;
		
		// bail out if we don't have the photo
		if (getActivity().getFileStreamPath(photoPath) == null) {
			return false;
		}
		
		String path = getActivity().getFileStreamPath(photoPath)
				.getAbsolutePath();
		if (path == null) {
			return false;
		}
		
		bmDrawable = PictureUtils.getScaledDrawable(getActivity(), path);
		if (bmDrawable == null) {
			return false;
		}

		imageView.setImageDrawable(bmDrawable);			
		return true;
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
		if (sighting.getCloudImage() != null) {
			
			File existingImage = getActivity().getFileStreamPath(
					sighting.getCloudImage());
			if (existingImage != null && existingImage.exists()) {
				existingImage.delete();
			}
		}
		
		// now that we've dumped the old image, set the new one
		sighting.setCloudImage(imagePath);
	}

}
