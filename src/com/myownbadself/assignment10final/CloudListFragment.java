package com.myownbadself.assignment10final;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.assignment10final.R;
import com.myownbadself.assignment10final.model.CloudSighting;
import com.myownbadself.assignment10final.model.CloudSightingCollection;
import com.myownbadself.assignment10final.util.CloudConstants;
import com.myownbadself.assignment10final.util.PictureUtils;

public class CloudListFragment extends ListFragment {
	List<CloudSighting> cloudSightings;
	final Set<Integer> checkedPositions = new HashSet<Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		cloudSightings = CloudSightingCollection.getInstance(
				getActivity()).getCloudSightings();
		Collections.sort(cloudSightings);
		
		ArrayAdapter<CloudSighting> adapter = new CloudSightingAdapter(
				cloudSightings);
		setListAdapter(adapter);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cloud_list,
				container, false);
		ListView listView = (ListView)view.findViewById(android.R.id.list);
		registerForContextMenu(listView);
		initDeleteButton(view);
		initMapButton(view);
		return view;
	}


	private void initMapButton(View view) {
		Button mapButton = (Button) view.findViewById(R.id.button_show_map);
		mapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), 
						CloudMapActivity.class);
				startActivity(intent);
			}
		});
	}

	private void initDeleteButton(final View view) {
		Button deleteButton = (Button) view.findViewById(R.id.button_delete_selected);
		final CloudSightingAdapter adapter = 
				(CloudSightingAdapter) getListAdapter();
		
		
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				
				if (!checkedPositions.isEmpty()) {
					// TODO: it's sort of awkward to maintain a separate list,
					// but I'm running into concurrent modification issues
					// otherwise
					List<CloudSighting> sightingsToDrop = 
							new ArrayList<CloudSighting>();
				    Iterator<Integer> checkedPositionIterator = 
				    		checkedPositions.iterator();
	                while (checkedPositionIterator.hasNext()) {
	                	int position = checkedPositionIterator.next();
						
						if (cloudSightings.size() > position) {
							sightingsToDrop.add(cloudSightings.get(position));
						}
						checkedPositionIterator.remove();
	                }
					
	                if (sightingsToDrop.size() > 0) {
						for (CloudSighting dropSighting : sightingsToDrop) {
							// delete any images for this cloud sighting from disk
							PictureUtils.deleteImageForCloudSighting(
									dropSighting, getActivity());
							// drop the cloud sighting from the collection
							CloudSightingCollection.deleteCloudSighting(
									dropSighting);
						}
	                }

	                // save the state of the sighting list back to the file
	                CloudSightingCollection.getInstance(getActivity()).saveCloudSightings();
					adapter.notifyDataSetChanged();
				}

			}
		});
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
			
			
			final CloudSighting cloudSighting = getItem(position);
			TextView sightingInfo = (TextView) convertView
					.findViewById(R.id.textview_cloud_sighting_loc);

			sightingInfo.setText(cloudSighting.
					getConditionInfo().getLocation());
			
			TextView sightingDate = (TextView) convertView
					.findViewById(R.id.textview_cloud_sighting_date);
			sightingDate.setText(CloudConstants.FORMATTER.format(
					cloudSighting.getDate()));

			// get the image for this cloud or use the default
			initSightingImage(convertView, cloudSighting);
			
			View cloudItemContainer = convertView.findViewById(
					R.id.layout_cloud_item_container);
			
			
			// we're putting the OnClickListener here instead of at the list
			// level to prevent the app from moving to the detail view when a
			// user clicks the checkbox. Yeah, there's probably an easier way
			cloudItemContainer.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), CloudDetailActivity.class);
					intent.putExtra(CloudConstants.EXTRA_CLOUD_SIGHTING_ID, 
							cloudSighting.getId());
					startActivity(intent);
					
				}
			});

			return convertView;
		}
		
	}
	

	private void initSightingImage(View view, CloudSighting cloudSighting) {
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview_cloud_list_picture);


		
		// use the default image if nothing is set for the current sighting or
		// if the image fails to load from disk
		if (cloudSighting.getCloudImage() == null
				|| PictureUtils.populateImageViewFromFile(getActivity(), 
						imageView, 
						cloudSighting.getCloudImage(),
						CloudConstants.MAX_THUMBNAIL_WIDTH,
						CloudConstants.MAX_THUMBNAIL_HEIGHT) == false) {
			imageView.setImageResource(R.drawable.cloud_default);
			
		}

	}
}
