package com.example.assignment10final.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

public class CloudSightingCollection {
	private static final String CLOUD_SIGHTING_FILE_NAME = "cloud_sightings.json";
	private static CloudSightingCollection cloudSightingCollection;
	private static List<CloudSighting> cloudSightings = 
			new LinkedList<CloudSighting>();
	private CloudSightingJSONSerializer serializer;

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
	
	public static int getCount() {
		return cloudSightings.size();
	}

	public static void addCloudSighting(CloudSighting cloudSighting) {
		cloudSightings.add(cloudSighting);
	}
	
	public boolean saveCloudSightings() {
		try {
			serializer.saveCloudSightings(cloudSightings);
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

		cloudSightingCollection.setUpDummyData();
		return cloudSightingCollection;
	}
	
	private void setUpDummyData() {
		if (cloudSightings != null && cloudSightings.size() > 1) {
			return;
		}
		
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -10);

		CloudSighting cs1 = new CloudSighting(
				cal.getTime(), "hey you", "", 
				new ConditionInfo(new double[]{37.762076, -122.412184}, 
						"San Francisco, CA"));
		
		cal.add(Calendar.DAY_OF_MONTH, -5);
		cal.add(Calendar.HOUR_OF_DAY, +2);
		cal.add(Calendar.MINUTE, +35);
		
		CloudSighting cs2 = new CloudSighting(
				cal.getTime(), "hey me", "", 
				new ConditionInfo(new double[]{47.661579, -122.316120}, 
						"Seattle, WA"));
		cloudSightings.add(cs1);
		cloudSightings.add(cs2);

	}
	
}
