package com.myownbadself.assignment10final.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

public class CloudSightingJSONSerializer {
	private Context context;
	private String fileName;

	public CloudSightingJSONSerializer(Context context, String fileName) {
		this.context = context;
		this.fileName = fileName;
	}

	public List<CloudSighting> loadCloudSightings() throws IOException, JSONException {
		List<CloudSighting> cloudSightings = new ArrayList<CloudSighting>();
		BufferedReader reader = null;

		try {
			InputStream in = context.openFileInput(fileName);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null) {
				jsonString.append(line);
			}

			JSONArray jsonArray = (JSONArray) new JSONTokener(
					jsonString.toString()).nextValue();
			for (int i = 0; i < jsonArray.length(); i++) {
				cloudSightings
						.add(new CloudSighting(jsonArray.getJSONObject(i)));
			}
		} catch (FileNotFoundException e) {
			// this happens when starting a new file. it's ok
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return cloudSightings;
	}

	public void saveCloudSightings(List<CloudSighting> cloudSightings)
			throws JSONException, IOException {
		JSONArray jsonArray = new JSONArray();
		for (CloudSighting cloudSighting : cloudSightings) {
			jsonArray.put(cloudSighting.toJSON());
		}

		Writer writer = null;

		try {
			OutputStream out = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(jsonArray.toString());
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}