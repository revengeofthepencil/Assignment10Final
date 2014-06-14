package com.example.assignment10final.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.assignment10final.util.CloudConstants;

public class CloudSighting {

	private Date date;
	private String description;
	private String cloudImage;
	private ConditionInfo conditionInfo;
	public CloudSighting(Date date, String description, String cloudImage,
			ConditionInfo conditionInfo) {
		super();
		this.date = date;
		this.description = description;
		this.cloudImage = cloudImage;
		this.conditionInfo = conditionInfo;
	}
	
	public CloudSighting(JSONObject jsonObject) throws JSONException {
		// TODO: serialize
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCloudImage() {
		return cloudImage;
	}
	public void setCloudImage(String cloudImage) {
		this.cloudImage = cloudImage;
	}
	public ConditionInfo getConditionInfo() {
		return conditionInfo;
	}
	public void setConditionInfo(ConditionInfo conditionInfo) {
		this.conditionInfo = conditionInfo;
	}
	
	public JSONObject toJSON() throws JSONException {
		// TODO: serialize
		return null;
	}

	@Override
	public String toString() {
		return getConditionInfo().getLocation() + ", " 
				+ CloudConstants.FORMATTER.format(getDate());
	}
	
}
