package com.example.assignment10final.model;


public class ConditionInfo {
	private String conditions;
	private double[] coords;
	private String location;
	private String temperature;
	private String humidity;
	private String wind;

	public ConditionInfo() {}
	
	public ConditionInfo(double[] coords,
			String location) {
		super();
		this.coords = coords;
		this.location = location;
	}

	public double[] getCoords() {
		return coords;
	}
	public String getLocation() {
		return location;
	}

	public void setCoords(double[] coords) {
		this.coords = coords;
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
	
	
	
}
