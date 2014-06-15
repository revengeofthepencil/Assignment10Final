package com.example.assignment10final.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.example.assignment10final.model.ConditionInfo;

public class WundergroundReader {

	public static final String TAG = "PhotoFetcher";

	private static final String ENDPOINT = "http://api.wunderground.com/api/";
	private static final String API_KEY = "4dc4b2ba07fc6077";
	private static final String METHOD_GET_CONDITIONS = "conditions";
	private static final String METHOD_RUN_QUERY = "q";
	private static final String JSON_QUERY_EXTENSION = ".json";

	private JSONObject makeJSONCall(String url) {
		URL myurl = null;
		try {
			myurl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		URLConnection connection = null;
		try {
			connection = myurl.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// TODO: set timeout
		/*
		 * connection.setConnectTimeout(R.string.TIMEOUT_CONNECTION);
		 * connection.setReadTimeout(R.string.TIMEOUT_CONNECTION);
		 */

		HttpURLConnection httpConnection = (HttpURLConnection) connection;
		httpConnection.setRequestProperty("Content-Type",
				CloudConstants.URL_JSON_CONTENT_TYPE);
		int responseCode = -1;
		try {
			responseCode = httpConnection.getResponseCode();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (responseCode == HttpURLConnection.HTTP_OK) {
			StringBuilder answer = new StringBuilder(100000);

			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(
						httpConnection.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			String inputLine;

			try {
				while ((inputLine = in.readLine()) != null) {
					answer.append(inputLine);
					answer.append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				httpConnection.disconnect();
			}
			
			try {
				JSONObject json = new JSONObject(answer.toString());
				if (json.has("current_observation")) {
					return json.getJSONObject("current_observation");
					
				}
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
		} else {
			httpConnection.disconnect();
			return null;
		}
		
		// if we make it here, we were unable to parse the JSON object
		return null;
	}

	public ConditionInfo fetchConditions(String zipcode) {
		return fetchConditionsWithQuery(zipcode + JSON_QUERY_EXTENSION, true);
	}
	
	public ConditionInfo fetchConditions(double[] coords) {
		if (coords == null || coords.length != 2) {
			Log.e(TAG, "invalid coords");
			return null;
		}

		String coordJSONQuery = ((Double) coords[0]).toString() + ','
				+ ((Double) coords[1]).toString() + JSON_QUERY_EXTENSION;

		Log.i(CloudConstants.LOG_KEY, "coordJSONQuery = " + coordJSONQuery);
		ConditionInfo conditionInfo = fetchConditionsWithQuery(coordJSONQuery, false);
		
		// hold on to the coords passed from the activity
		conditionInfo.setCoords(coords);
		return conditionInfo;
	}
	
	private ConditionInfo fetchConditionsWithQuery(String queryString, boolean cacheLatLong) {


		ConditionInfo conditionInfo = new ConditionInfo();

		String url = Uri.parse(ENDPOINT).buildUpon().appendPath(API_KEY)
				.appendPath(METHOD_GET_CONDITIONS).appendPath(METHOD_RUN_QUERY)
				.appendPath(queryString).build().toString();

		Log.i(CloudConstants.LOG_KEY, "url = " + url);

		JSONObject jsonWeather = makeJSONCall(url);
		try {
			populateConditionInfoFromJSON(conditionInfo, jsonWeather, cacheLatLong);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conditionInfo;
	}

	private void populateConditionInfoFromJSON(ConditionInfo conditionInfo, 
			JSONObject jsonWeather, boolean cacheLatLong) throws JSONException {
		if (jsonWeather.has("display_location")) {
			JSONObject displayLoc = jsonWeather.getJSONObject("display_location");
			if (displayLoc.has("full")) {
				conditionInfo.setLocation(displayLoc.getString("full"));
			}
		}

		
		if (jsonWeather.has("temperature_string")) {
			conditionInfo.setTemperature(jsonWeather
					.getString("temperature_string"));
		}
		
		if (jsonWeather.has("weather")) {
			conditionInfo.setConditions(jsonWeather
					.getString("weather"));
		}
		
		if (jsonWeather.has("relative_humidity")) {
			conditionInfo.setHumidity(jsonWeather
					.getString("relative_humidity"));
		}
		
		if (jsonWeather.has("wind_string")) {
			conditionInfo.setWind(jsonWeather
					.getString("wind_string"));
		}

		
		// hold on to the coords if we have them and cacheLatLong is true
		if (cacheLatLong == true && jsonWeather.has("latitude")
				&& jsonWeather.has("longitude")) {
			//provider name is not needed
		    Location targetLocation = new Location("");
		    targetLocation.setLatitude(jsonWeather.getDouble("latitude"));
		    targetLocation.setLongitude(jsonWeather.getDouble("longitude"));
			LocationCachingUtil.getInstance().setLocation(targetLocation);
		}
		
	}
	
	
}
