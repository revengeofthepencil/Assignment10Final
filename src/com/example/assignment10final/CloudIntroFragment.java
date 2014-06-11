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
	
	// test coords for Seattle
	private static final double[] COORDS_TEST = 
			new double[]{47.605876, -122.321718};
	
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
			textView.setText("Ran at " + new Date());
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
