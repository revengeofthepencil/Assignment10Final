package com.example.assignment10final;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.assignment10final.model.CloudSighting;
import com.example.assignment10final.model.CloudSightingCollection;
import com.example.assignment10final.util.CloudConstants;
import com.example.assignment10final.util.PictureUtils;

public class CloudListFragment extends ListFragment {
	List<CloudSighting> cloudSightings;
	final Set<Integer> checkedPositions = new HashSet<Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		cloudSightings = CloudSightingCollection.getInstance(
				getActivity()).getCloudSightings();
		
		ArrayAdapter<CloudSighting> adapter = new CloudSightingAdapter(
				cloudSightings);
		setListAdapter(adapter);
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.cloud_list, menu);
	}

	
	
	@Override
	public void onResume() {
		super.onResume();
		((CloudSightingAdapter) getListAdapter()).notifyDataSetChanged();
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
			case R.id.menu_item_new_cloud:
				Intent intent = new Intent(getActivity(), CloudDetailActivity.class);
				intent.putExtra(CloudConstants.EXTRA_CLOUD_SIGHTING_ID, -1);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private class CloudSightingAdapter extends ArrayAdapter<CloudSighting> {

		public CloudSightingAdapter(List<CloudSighting> sightings) {
			super(getActivity(), 0, sightings);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.list_item_cloud_sighting, null);
			}


			CheckBox checkCloud = (CheckBox) convertView.findViewById(
					R.id.check_cloud_sighting);
			
			// see if this checkbox should be checked
			if (!checkedPositions.isEmpty() && 
					checkedPositions.contains(position)) {
				checkCloud.setChecked(true);
			} else {
				checkCloud.setChecked(false);
			}
			
			checkCloud.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
					if (isChecked == true) {
						checkedPositions.add(position);
					} else {
						checkedPositions.remove(position);
					}
					
				}
			});
			
			
			CloudSighting cloudSighting = getItem(position);
			TextView sightingInfo = (TextView) convertView
					.findViewById(R.id.textview_cloud_sighting_info);

			sightingInfo.setText(cloudSighting.toString());
			
			// we're putting the OnClickListener here instead of at the list
			// level to prevent the app from moving to the detail view when a
			// user clicks the checkbox. Yeah, there's probably an easier way
			sightingInfo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i(CloudConstants.LOG_KEY, "sightingInfo.setOnClickListener");
					Intent intent = new Intent(getActivity(), CloudDetailActivity.class);
					intent.putExtra(CloudConstants.EXTRA_CLOUD_SIGHTING_ID, position);
					startActivity(intent);
					
				}
			});


			Drawable img = null;
			
			if (cloudSighting.getCloudImage() != null) {
				String path = getActivity().getFileStreamPath(cloudSighting.getCloudImage())
						.getAbsolutePath();
				if (path != null) {
					
					BitmapDrawable bmd = PictureUtils.getScaledDrawable(getActivity(), 
							path);
					if (bmd != null) {
						img = bmd;
					}
				}
				
			} 
			
			// if no image was set or we couldn't get the Bitmap image, 
			//	use the default
			if (img == null) {
				img = getActivity().getResources()
						.getDrawable(R.drawable.cloud_default);
			}
			

			img.setBounds(0, 0, 60, 60);
			sightingInfo.setCompoundDrawables(img, null, null, null);

			return convertView;
		}
		
	}
}
