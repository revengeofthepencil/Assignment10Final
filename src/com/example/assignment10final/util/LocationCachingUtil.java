package com.example.assignment10final.util;

import java.util.Date;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationCachingUtil  {

	// 15 mintues
	private static long MILLISECONDS_TO_CACHE = 900000L;
	private static LocationCachingUtil locationCachingUtil;
	private Date lastChecked;
	Location location;
	LocationManager locationManager;
	
	private LocationCachingUtil() {}
	
	public Location getLocation() {
		if (lastChecked != null && location != null) {
			long diff = System.currentTimeMillis() - lastChecked.getTime();
			Log.i(CloudConstants.LOG_KEY, 
					"diff = " + diff);

			if (diff < MILLISECONDS_TO_CACHE) {
				Log.i(CloudConstants.LOG_KEY, 
						"no need to check again, we've already got a location");
				return location;
			}
		}
		
		// if we make it here, we either have no cached location or the cache has expired
		return null;
	}
	
	public void setLocation(Location location) {
		this.lastChecked = new Date();
		this.location = location;
	}
	
	public static LocationCachingUtil getInstance() {
		if (locationCachingUtil == null) {
			locationCachingUtil = new LocationCachingUtil();
		}
		
		return locationCachingUtil;
	}
	
}
