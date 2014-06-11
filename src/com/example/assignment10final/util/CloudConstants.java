package com.example.assignment10final.util;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;

@SuppressLint("SimpleDateFormat")
public class CloudConstants {

	public static final String LOG_KEY = "cloud_log";
	public static final SimpleDateFormat FORMATTER = 
			new SimpleDateFormat("MMM d, yyyy hh:mm a");

	public static final String URL_JSON_CONTENT_TYPE = "application/json";
	
	// JSON keys for serializing records
	public static final String JSON_CLOUD_COORDS = "cloud_coords";
	public static final String JSON_CLOUD_LOCATION = "cloud_location";
	public static final String JSON_CLOUD_DESC = "cloud_desc";
	public static final String JSON_CLOUD_IMAGE = "cloud_image";
	public static final String JSON_CLOUD_DATE = "cloud_date";
	public static final String JSON_CLOUD_CONDITIONS = "cloud_conditions";

}
