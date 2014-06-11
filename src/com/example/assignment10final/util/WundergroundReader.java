package com.example.assignment10final.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

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

    private static final String EXTRA_SMALL_URL = "url_s";

    private static final String XML_PHOTO = "photo";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
    
    private String makeJSONCall(String url) throws IOException {
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
        connection.setConnectTimeout(R.string.TIMEOUT_CONNECTION);
        connection.setReadTimeout(R.string.TIMEOUT_CONNECTION);
        */
        
        HttpURLConnection httpConnection = (HttpURLConnection) connection;
        httpConnection.setRequestProperty("Content-Type", CloudConstants.URL_JSON_CONTENT_TYPE);
        int responseCode = -1;
            responseCode = httpConnection.getResponseCode();

        
        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuilder answer = new StringBuilder(100000);

            BufferedReader in = null;
            try {
                in = new BufferedReader(
                		new InputStreamReader(httpConnection.getInputStream()));
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
            }
            httpConnection.disconnect();
            return answer.toString();
        } else {
            //connection is not OK
            httpConnection.disconnect();
            return null;
        }
    }
    
    public ConditionInfo fetchConditions(double[] coords) {
    	if (coords == null || coords.length != 2) {
            Log.e(TAG, "invalid coords");
    		return null;
    	}
    	
		Log.i(CloudConstants.LOG_KEY, "cords = "
				+ coords[0] + " / " + coords[1]);


    	String coordJSONQuery = 
    			((Double)coords[0]).toString()
    			+ ','
    			+ ((Double)coords[1]).toString()
    		    + JSON_QUERY_EXTENSION;
    	
		Log.i(CloudConstants.LOG_KEY, "coordJSONQuery = "
				+ coordJSONQuery);

    	
        ConditionInfo conditionInfo = new ConditionInfo();
        try {
            String url = Uri.parse(ENDPOINT).buildUpon()
            		.appendPath(API_KEY)
            		.appendPath(METHOD_GET_CONDITIONS)
            		.appendPath(METHOD_RUN_QUERY)
            		.appendPath(coordJSONQuery)
                    .build().toString();
            
			Log.i(CloudConstants.LOG_KEY, "url = "
					+ url);

            String jsonString = makeJSONCall(url);//getUrl(url);
			Log.i(CloudConstants.LOG_KEY, "jsonString = "
					+ jsonString);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return conditionInfo;
    }

}
