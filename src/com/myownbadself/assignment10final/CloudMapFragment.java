package com.myownbadself.assignment10final;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.assignment10final.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.myownbadself.assignment10final.model.CloudSighting;
import com.myownbadself.assignment10final.model.CloudSightingCollection;
import com.myownbadself.assignment10final.util.CloudConstants;

public class CloudMapFragment extends SupportMapFragment {
	private GoogleMap googleMap;
	private Map<Marker, CloudSighting> markerMap = 
			new HashMap<Marker, CloudSighting>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

	}
	
	@Override
	public void onResume() {
		super.onResume();
		addSightingMarkers();
	}
	
	

	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.cloud_list, menu);
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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = super.onCreateView(inflater, container, savedInstanceState);
		registerForContextMenu(view);

		googleMap = getMap();
		googleMap.setMyLocationEnabled(true);
		googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
            	CloudSighting sighting = markerMap.get(arg0);
        		Intent intent = new Intent(getActivity(), 
        				CloudDetailActivity.class);
        		intent.putExtra(CloudConstants.EXTRA_CLOUD_SIGHTING_ID, 
        				sighting.getId());
        		startActivity(intent);
                return true;
            }

        });       
		zoomToCloudSightings();
		return view;
	}

	private void zoomToCloudSightings() {
		List<CloudSighting> cloudSightings = 
				CloudSightingCollection.getInstance(getActivity())
					.getCloudSightings();

		Builder boundsBuilder = LatLngBounds.builder();
		if (cloudSightings != null && cloudSightings.size() > 0) {
			for (CloudSighting sighting : cloudSightings) {
				LatLng cloudPoint = new LatLng(sighting.getCoords()[0], 
						sighting.getCoords()[1]);
				boundsBuilder.include(cloudPoint);
			}
		}

		final LatLngBounds bounds = boundsBuilder.build();

		// this was crashing since I was trying to zoom to the extent before the
		// map was loaded. Rather than doing a bunch of screen size calculation,
		// just wait until the map is loaded before zooming
		googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
						bounds, 65));
				addSightingMarkers();
			}
		});

	}

	private void addSightingMarkers() {
		List<CloudSighting> cloudSightings = 
				CloudSightingCollection.getInstance(getActivity())
					.getCloudSightings();
				
		if (cloudSightings != null && cloudSightings.size() > 0) {
			for (CloudSighting sighting : cloudSightings) {
				
				// only put each sighting on the map once
				if (markerMap.containsValue(sighting)) {
					continue;
				}
				
				
				MarkerOptions marker = new MarkerOptions().position(new LatLng(
						sighting.getCoords()[0], 
						sighting.getCoords()[1]));
				marker.title(sighting.getConditionInfo().getLocation() + ", "
						+ sighting.getDate());
				
				
				Log.i(CloudConstants.LOG_KEY, "Adding sighting at "
						+ sighting.getConditionInfo().getLocation());
				Marker bfSightingMarker = googleMap.addMarker(marker);
				markerMap.put(bfSightingMarker, sighting);
			}

		} else {
			Log.i(CloudConstants.LOG_KEY,
					"no sightings available");
		}

		Log.i(CloudConstants.LOG_KEY, "marker map length = "
				+ markerMap.size());

	}
}
