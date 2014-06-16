package com.myownbadself.assignment10final.model;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.myownbadself.assignment10final.util.CloudConstants;

public class CloudSighting {

	private Date date;
	private String description;
	private String cloudImage;
	private ConditionInfo conditionInfo;
	private String id;
	
	public CloudSighting(String id, Date date) {
		super();
		this.date = date;
		this.id = id;
	}

	public CloudSighting(String id, Date date, String description, String cloudImage,
			ConditionInfo conditionInfo) {
		super();
		this.id = id;
		this.date = date;
		this.description = description;
		this.cloudImage = cloudImage;
		this.conditionInfo = conditionInfo;
	}
	
	public CloudSighting(JSONObject jsonObject) throws JSONException {
		
		if(jsonObject.has(CloudConstants.JSON_CLOUD_ID)) {
			this.id = jsonObject.getString(CloudConstants.JSON_CLOUD_ID);
		}
		
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
		jsonObject.put(CloudConstants.JSON_CLOUD_ID, 
				id);
		
		if (cloudImage != null) {
			jsonObject.put(CloudConstants.JSON_CLOUD_IMAGE, 
					cloudImage);
		}
		
		return jsonObject;
	}

	
	
	
	public String getId() {
		return id;
	}

	// wrapper function to get coords from conditionInfo
	public double[] getCoords() {
		if (conditionInfo != null) {
			return conditionInfo.getCoords();
		} else {
	 		return null;
		}
	}

	@Override
	public String toString() {
		return getConditionInfo().getLocation() + ", " 
				+ CloudConstants.FORMATTER.format(getDate());
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CloudSighting other = (CloudSighting) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
