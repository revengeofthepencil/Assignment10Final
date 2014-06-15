package com.example.assignment10final.model;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.assignment10final.util.CloudConstants;

public class CloudSighting {

	private Date date;
	private String description;
	private String cloudImage;
	private ConditionInfo conditionInfo;
	
	public CloudSighting(Date date) {
		super();
		this.date = date;
	}

	public CloudSighting(Date date, String description, String cloudImage,
			ConditionInfo conditionInfo) {
		super();
		this.date = date;
		this.description = description;
		this.cloudImage = cloudImage;
		this.conditionInfo = conditionInfo;
	}
	
	public CloudSighting(JSONObject jsonObject) throws JSONException {

		if (jsonObject.has(CloudConstants.JSON_CLOUD_DATE)) {
			try {
				this.date = CloudConstants.FORMATTER.parse(
								jsonObject.getString(
										CloudConstants.JSON_CLOUD_DATE));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (jsonObject.has(CloudConstants.JSON_CLOUD_IMAGE)) {
			this.cloudImage = jsonObject.getString(
					CloudConstants.JSON_CLOUD_IMAGE);
		}
		

		if (jsonObject.has(CloudConstants.JSON_CLOUD_DESC)) {
			this.description = jsonObject.getString(
					CloudConstants.JSON_CLOUD_DESC);
		}
		
		
		if (jsonObject.has(CloudConstants.JSON_CLOUD_CONDITION_INFO)) {
			JSONObject jsonConditions = (JSONObject) jsonObject.get(CloudConstants.JSON_CLOUD_CONDITION_INFO);
			this.conditionInfo = new ConditionInfo(jsonConditions);
		}	
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
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(CloudConstants.JSON_CLOUD_DATE, 
				CloudConstants.FORMATTER.format(date));
		jsonObject.put(CloudConstants.JSON_CLOUD_CONDITION_INFO, 
				conditionInfo.toJSON());
		jsonObject.put(CloudConstants.JSON_CLOUD_DESC, 
				description);
		
		if (cloudImage != null) {
			jsonObject.put(CloudConstants.JSON_CLOUD_IMAGE, 
					cloudImage);
		}

		return jsonObject;
	}

	@Override
	public String toString() {
		return getConditionInfo().getLocation() + ", " 
				+ CloudConstants.FORMATTER.format(getDate());
	}
	
}
