package com.myownbadself.assignment10final.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.myownbadself.assignment10final.util.CloudConstants;


public class ConditionInfo {
	private String conditions;
	private double[] coords;
	private String location;
	private String temperature;
	private String humidity;
	private String wind;

	public ConditionInfo() {}
	
	public ConditionInfo(JSONObject jsonObject) throws JSONException {
		
		if (jsonObject.has(CloudConstants.JSON_CLOUD_CONDITIONS)) {
			this.conditions = jsonObject
					.getString(CloudConstants.JSON_CLOUD_CONDITIONS);
		}
		
		if (jsonObject.has(CloudConstants.JSON_CLOUD_TEMPERATURE)) {
			this.temperature = jsonObject
					.getString(CloudConstants.JSON_CLOUD_TEMPERATURE);
		}

		if (jsonObject.has(CloudConstants.JSON_CLOUD_HUMIDITY)) {
			this.humidity = jsonObject
					.getString(CloudConstants.JSON_CLOUD_HUMIDITY);
		}

		if (jsonObject.has(CloudConstants.JSON_CLOUD_LOCATION)) {
			this.location = jsonObject
					.getString(CloudConstants.JSON_CLOUD_LOCATION);
		}

		if (jsonObject.has(CloudConstants.JSON_CLOUD_WIND)) {
			this.wind = jsonObject
					.getString(CloudConstants.JSON_CLOUD_WIND);
		}
		
		if (jsonObject.has(CloudConstants.JSON_CLOUD_READING_COORDS)) {
			JSONArray locationCoords = (JSONArray) jsonObject
					.get(CloudConstants.JSON_CLOUD_READING_COORDS);
			
			if (locationCoords.length() == 2) {
				this.coords = new double[]{
						locationCoords.getDouble(0),
						locationCoords.getDouble(1)
				};
			}
		}

		
	}
	
	public ConditionInfo(double[] coords,
			String location) {
		super();
		this.coords = coords;
		this.location = location;
	}

	public double[] getCoords() {
		return coords;
	}
	
	public void setCoords(double[] coords) {
		this.coords = coords;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(CloudConstants.JSON_CLOUD_CONDITIONS, conditions);
		
		if (coords != null && coords.length == 2) {
			JSONArray coordArray = new JSONArray();
			coordArray.put(coords[0]);
			coordArray.put(coords[1]);
			jsonObject.put(CloudConstants.JSON_CLOUD_READING_COORDS, 
					coordArray);
		}
		
		jsonObject.put(CloudConstants.JSON_CLOUD_LOCATION, location);
		jsonObject.put(CloudConstants.JSON_CLOUD_TEMPERATURE, temperature);
		jsonObject.put(CloudConstants.JSON_CLOUD_HUMIDITY, humidity);
		jsonObject.put(CloudConstants.JSON_CLOUD_WIND, wind);
		return jsonObject;

	}
	
}
