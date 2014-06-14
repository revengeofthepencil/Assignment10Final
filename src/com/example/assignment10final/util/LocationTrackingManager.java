package com.example.assignment10final.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class LocationTrackingManager {

	public static final String ACTION_LOCATION = "com.example.loctracking.ACTION_LOCATION";

	public static final String TEST_PROVIDER = "TEST_PROVIDER";

	private static LocationTrackingManager locationTrackingManager;

	private Context context;

	private LocationManager locationManager;

	public static void getSingleLocation() {
		String provider = LocationManager.GPS_PROVIDER;
		PendingIntent pendingIntent = locationTrackingManager
				.getLocationPendingIntent(true);

		locationTrackingManager.locationManager
			.requestLocationUpdates(provider, 0, 0, pendingIntent);

	}
	

	private PendingIntent getLocationPendingIntent(Boolean shouldCreate) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		int flags = 0;
		if (!shouldCreate) {
			flags = PendingIntent.FLAG_NO_CREATE;
		}

		return PendingIntent.getBroadcast(context, 0, broadcast, flags);

	}
	
	private LocationTrackingManager(Context context) {
		this.context = context;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

	}

	

	

}
