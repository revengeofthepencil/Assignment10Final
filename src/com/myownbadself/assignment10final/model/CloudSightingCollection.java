package com.myownbadself.assignment10final.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;

import android.content.Context;
import android.media.ThumbnailUtils;
import android.util.Log;

public class CloudSightingCollection {
	private static final String CLOUD_SIGHTING_FILE_NAME = "cloud_sightings.json";
	private static CloudSightingCollection cloudSightingCollection;
	private static List<CloudSighting> cloudSightings = 
			new LinkedList<CloudSighting>();
	private CloudSightingJSONSerializer serializer;

	// track whether or not we loaded dummy data
	private boolean gotDummyData = false;

	private CloudSightingCollection(Context context) {

		serializer = new CloudSightingJSONSerializer(context,
				CLOUD_SIGHTING_FILE_NAME);
		try {
			cloudSightings = serializer.loadCloudSightings();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("IOException", e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSONException", e.getMessage());
		} finally {
			if (cloudSightings == null) {
				cloudSightings = new ArrayList<CloudSighting>();
			}
		}
	}
	
	public List<CloudSighting> getCloudSightings() {
		return cloudSightings;
	}
	
	public CloudSighting getCloudSightingByID(String id) {
		for (CloudSighting cloudSighting : cloudSightings) {
			if (cloudSighting.getId().equals(id)) {
				return cloudSighting;
			}
		}
		
		// if we make it here, we didn't find it
		return null;
	}
	
	public static int getCount() {
		return cloudSightings.size();
	}

	public static void addCloudSighting(CloudSighting cloudSighting) {
		cloudSightings.add(cloudSighting);
	}
	
	public boolean saveCloudSightings() {
		try {
			serializer.saveCloudSightings(cloudSightings);
			Collections.sort(cloudSightings);
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSONException", e.getMessage());
			return false;
		} catch (IOException e) {
			Log.e("IOException", e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public static void deleteCloudSighting(CloudSighting cloudSighting) {
		cloudSightings.remove(cloudSighting);
	}

	public static CloudSightingCollection getInstance(Context context) {
		if (cloudSightingCollection == null) {
			cloudSightingCollection = new CloudSightingCollection(context);
		}

		// check to see if we should pre-fill with dummy data
		if (cloudSightingCollection.getCloudSightings() == null 
				|| cloudSightingCollection.getCloudSightings().size() < 1) {
			cloudSightingCollection.setUpDummyData();
			
		}
		
		return cloudSightingCollection;
	}
	
	private void setUpDummyData() {
		// bail out if we've already used this

		if (gotDummyData == true || (cloudSightings != null && cloudSightings.size() > 1)) {
			return;
		}
		
		// set the flag
		gotDummyData = true;
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -10);
		 
		ConditionInfo cs1Cond = new ConditionInfo(new double[]{37.762076, -122.412184}, 
				"San Francisco, CA");
		cs1Cond.setConditions("Cloudy");
		cs1Cond.setTemperature("67.2F");
		cs1Cond.setWind("No wind to speak of");
		cs1Cond.setHumidity("20% humidity");

		CloudSighting cs1 = new CloudSighting(
				UUID.randomUUID().toString(),
				cal.getTime(), "hey you", "", 
				cs1Cond);
		
		cal.add(Calendar.DAY_OF_MONTH, -5);
		cal.add(Calendar.HOUR_OF_DAY, +2);
		cal.add(Calendar.MINUTE, +35);
		
		ConditionInfo cs2Cond = new ConditionInfo(new double[]{
				47.661579, -122.316120}, 
				"Seattle, WA");
		cs2Cond.setConditions("Raining");
		cs2Cond.setTemperature("58.5F");
		cs2Cond.setWind("Very windy. Gusts all over the place.");
		cs2Cond.setHumidity("90% humidity");
		CloudSighting cs2 = new CloudSighting(
				UUID.randomUUID().toString(),
				cal.getTime(), "hey me", "", 
				cs2Cond);

		cloudSightings.add(cs1);
		cloudSightings.add(cs2);

	}
	
}
