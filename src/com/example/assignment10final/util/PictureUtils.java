package com.example.assignment10final.util;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.ImageView;

public class PictureUtils {


	@SuppressWarnings("deprecation")
	public static BitmapDrawable getScaledDrawable(Activity a, String path)
	{
		Display display = a.getWindowManager().getDefaultDisplay();
		// we want to reduce the image size just a bit so it doesn't blow out
		// the display
		float destWidth = (float) (display.getWidth());
		float destHeight = (float) (display.getHeight());

		//read in the dimensions of the image on disk
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap mapOrig = BitmapFactory.decodeFile(path, options);
		
		// bail out if we don't find the image
		if (mapOrig == null) {
			return null;
		}
		
		int srcWidth = mapOrig.getWidth();
		int srcHeight = mapOrig.getHeight();
		
		/*
		float heightDiff = srcHeight - destHeight;
		float widthDiff = srcWidth - destWidth;
		double reducePercent = 0.0;
		if (heightDiff > widthDiff && heightDiff > 0) {
			reducePercent = heightDiff / srcHeight;
		} else if (widthDiff > 0) {
			reducePercent = widthDiff / srcWidth;
		}
		*/
		
		if (srcHeight > srcWidth && srcHeight > destHeight) {
			double reducePercent = (srcHeight - destHeight) / srcHeight;
			srcHeight = (int)destHeight;
			srcWidth = (int)Math.round(srcWidth  - (srcWidth * reducePercent));
			
		} else if (srcWidth > srcHeight && srcWidth > destWidth) {
			double reducePercent = (srcWidth - destWidth) / srcWidth;
			srcWidth = (int)destWidth;
			srcHeight = (int)Math.round(srcHeight  - (srcHeight * reducePercent));
		}
		

		Bitmap scaled = Bitmap.createScaledBitmap(mapOrig, 
				srcWidth,  
				srcHeight,
				true);
		return new BitmapDrawable(a.getResources(), scaled);

	}

	@SuppressWarnings("deprecation")
	public static BitmapDrawable getScaledDrawable_bak(Activity a, String path)
	{
		Display display = a.getWindowManager().getDefaultDisplay();
		
		// we want to reduce the image size just a bit so it doesn't blow out
		// the display
		float destWidth = (float) (display.getWidth());
		float destHeight = (float) (display.getHeight());
		

		//read in the dimensions of the image on disk
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		Bitmap mapOrig = BitmapFactory.decodeFile(path, options);
		
		float srcWidth = options.outWidth;
		float srcHeight = options.outHeight;

		float heightDiff = srcHeight - destHeight;
		float widthDiff = srcWidth - destWidth;
		double reducePercent = 0.0;
		if (heightDiff > widthDiff && heightDiff > 0) {
			reducePercent = heightDiff / srcHeight;
		} else if (widthDiff > 0) {
			reducePercent = heightDiff / srcHeight;
		}
		
		int inSampleSize = 1;
		if(srcHeight > destHeight || srcWidth > destWidth)
		{
			if(srcWidth > srcHeight)
			{
				inSampleSize = Math.round(srcHeight/destHeight);
			}
			else
			{
				inSampleSize = Math.round(srcWidth / destWidth); 
			}
		}
		
		
		options = new BitmapFactory.Options();
		options.inSampleSize = inSampleSize;
		
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return new BitmapDrawable(a.getResources(), bitmap);

	}
	
	
	public static void cleanImageView(ImageView imageView)
	{
		if(!(imageView.getDrawable()instanceof BitmapDrawable)) return;
		
		//cleanup the views image for the sake of memory
		BitmapDrawable b = (BitmapDrawable)imageView.getDrawable();
		b.getBitmap().recycle();
		imageView.setImageDrawable(null);
	}
	

	/**
	 * Use the sightings location and a date stamp to generate a unique image
	 * name. This should give us a unique image as long as we don't try for two
	 * from the same location in the same millisecond. That's a chance I'm
	 * willing to take.
	 * 
	 * @param sightingLocation
	 * @return a unique string for the sighting image
	 */
	@SuppressLint("DefaultLocale")
	public static String getUniqueImageNameForLocation(String sightingLocation) {
		Date now = new Date();

		if (sightingLocation != null) {
			// strip any non alphanumeric characters
			return sightingLocation.replaceAll("[^A-Za-z0-9]", "").toLowerCase()
					+ '_' + now.getTime() 
					+ ".jpg";
			
		} else {
			return now.getTime() + ".jpg";
			
		}
	}	
}
