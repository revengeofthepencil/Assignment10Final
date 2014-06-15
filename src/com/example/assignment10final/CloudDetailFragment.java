package com.example.assignment10final;

import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.assignment10final.model.CloudSighting;
import com.example.assignment10final.model.CloudSightingCollection;
import com.example.assignment10final.model.ConditionInfo;
import com.example.assignment10final.util.CloudConstants;
import com.example.assignment10final.util.LocationCachingUtil;
import com.example.assignment10final.util.WundergroundReader;

public class CloudDetailFragment extends Fragment {

	// NOLA: 29.917758,-90.113994
	// Seattle: 47.605876, -122.321718
	// San Francisco: 37.764207, -122.469143

	private CloudSighting cloudSighting;
	private ConditionInfo conditionInfo;
	private int id;

	
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
			Log.i(CloudConstants.LOG_KEY, "in onCreate with id = " + id + ", alien location = "
					 + cloudSighting.getDate());
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_cloud_detail, container,
				false);
		new FetchConditionsTask(getActivity()).execute();
		initDateView(view);
		return view;
	}


	private void populateConditions() {
		if (getActivity() == null || conditionInfo == null) {
			return;
		}

		TextView textView = (TextView) getActivity().findViewById(
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
		}
	}

	private void initDateView(View view) {
		TextView textView = (TextView) view.findViewById(
				R.id.textview_sighting_date);
		textView.setText(CloudConstants.FORMATTER
				.format(cloudSighting.getDate()));
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
}
