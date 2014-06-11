package com.example.assignment10final.model;

import java.util.Map;

public class ConditionInfo {
	private Map<String, String> conditions;
	private double[] coords;
	private String location;
	
	public ConditionInfo() {}
	
	public ConditionInfo(Map<String, String> conditions, double[] coords,
			String location) {
		super();
		this.conditions = conditions;
		this.coords = coords;
		this.location = location;
	}
	public Map<String, String> getConditions() {
		return conditions;
	}
	public double[] getCoords() {
		return coords;
	}
	public String getLocation() {
		return location;
	}
	
	
}
