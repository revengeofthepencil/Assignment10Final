package com.myownbadself.assignment10final.util;

import java.io.File;
import java.io.FileInputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.ImageView;

import com.myownbadself.assignment10final.model.CloudSighting;

public class PictureUtils {


	@SuppressWarnings("deprecation")
	public static BitmapDrawable getScaledDrawable(Activity a, String path) {
		Display display = a.getWindowManager().getDefaultDisplay();
		// we want to reduce the image size just a bit so it doesn't blow out
		// the display
		float destWidth = (float) (display.getWidth());
		float destHeight = (float) (display.getHeight());
		return getScaledDrawable(a, destWidth, destHeight, path);

	}
	

	@SuppressWarnings("deprecation")
	public static BitmapDrawable getScaledDrawable(Activity a, float destWidth, 
			float destHeight, String path) {

		//read in the dimensions of the image on disk
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap mapOrig = BitmapFactory.decodeFile(path, options);
		
		// bail out if we don't find the image
		if (mapOrig == null) {
			return null;
		}
		
		int srcWidth = mapOrig.getWidth();
		int srcHeight = mapOrig.getHeight();
		
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
	 * @param baseImageName
	 * @return a unique string for the sighting image
	 */
	@SuppressLint("DefaultLocale")
	public static String getUniqueImageNameForLocation(String baseImageName) {
		
		if (baseImageName != null) {
			// strip any non alphanumeric characters
			return baseImageName.replaceAll("[^A-Za-z0-9]", "").toLowerCase()
					+ '_' + System.currentTimeMillis()
					+ ".jpg";
			
		} else {
			return System.currentTimeMillis() + ".jpg";
			
		}
	}	
	
	public static boolean populateImageViewWithoutScaling(ImageView imageView, 
			String photoPath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap myMap = BitmapFactory.decodeFile(photoPath, options);
		if (myMap == null) {
			return false;
		}
		imageView.setImageBitmap(myMap);
		return true;
	}
	
	public static boolean populateImageViewFromFile(Activity activity, 
			ImageView imageView, 
			String photoPath, Integer width, Integer height) {
		
		BitmapDrawable bmDrawable = null;
		
		// bail out if we don't have the photo
		if (activity.getFileStreamPath(photoPath) == null) {
			return false;
		}
		
		String path = activity.getFileStreamPath(photoPath)
				.getAbsolutePath();
		if (path == null) {
			return false;
		}

		
		// check to see if we passed in a width/height or if we should just use
		// the view extents
		if (width != null && height != null) {
			bmDrawable = PictureUtils.getScaledDrawable(activity, width, 
					height, path);
		} else {
			bmDrawable = PictureUtils.getScaledDrawable(activity, path);
			
		}
	

		if (bmDrawable == null) {
			return false;
		}

		imageView.setImageDrawable(bmDrawable);			
		return true;
	}
	

	public static void deleteImageForCloudSighting(CloudSighting sighting,
			Activity activity) {
		if (sighting.getCloudImage() != null) {
			
			File existingImage = activity.getFileStreamPath(
					sighting.getCloudImage());
			if (existingImage != null && existingImage.exists()) {
				existingImage.delete();
			}
		}
		


	}
}
