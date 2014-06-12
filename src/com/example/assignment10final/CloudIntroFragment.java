package com.example.assignment10final;

import java.util.Date;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.assignment10final.model.ConditionInfo;
import com.example.assignment10final.util.WundergroundReader;

public class CloudIntroFragment extends Fragment {
	
	// test coords 
	private static final double[] COORDS_TEST = 
			new double[]{37.764207, -122.469143};
	
	// NOLA: 29.917758,-90.113994
	// Seattle: 47.605876, -122.321718
	// San Francisco: 37.764207, -122.469143
	
	private ConditionInfo conditionInfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_cloud_intro, container, false);
		//new WundergroundReader().fetchConditions(COORDS_TEST);
		new FetchConditionsTask().execute();
		initText(view);
		return view;
	}
	
	private void setUpAdapter() {
		if (getActivity() == null || conditionInfo == null) {
			return;
		}
		
		TextView textView = (TextView) getActivity().findViewById(
				R.id.textview_intro);

		if (textView != null) {
			StringBuilder conditionSB = new StringBuilder();
			
			if (conditionInfo.getLocation() != null) {
				conditionSB.append("Location: " 
						+ conditionInfo.getLocation() + "\n");
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
				conditionSB.append("Humidity: " 
						+ conditionInfo.getHumidity() + "\n");
			}
			
			if (conditionInfo.getWind() != null) {
				conditionSB.append("Wind: " 
						+ conditionInfo.getWind() + "\n");
			}

			textView.setText(conditionSB.toString());
		}
	}
	private void initText(View view) {
		TextView textView = (TextView) view.findViewById(R.id.textview_intro);
		Date now = new Date();
		textView.setText("it is now " + now);
	}
	

	private class FetchConditionsTask extends AsyncTask<Void, Void, 
		ConditionInfo> {

		@Override
		protected ConditionInfo doInBackground(Void... params) {
			return new WundergroundReader().fetchConditions(COORDS_TEST);
		}

		@Override
		protected void onPostExecute(ConditionInfo result) {
			conditionInfo = result;
			setUpAdapter(); // put data into gallery
		}
		
		
		
		
	}
}
