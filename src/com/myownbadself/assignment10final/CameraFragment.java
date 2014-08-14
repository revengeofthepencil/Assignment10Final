package com.myownbadself.assignment10final;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.assignment10final.R;
import com.myownbadself.assignment10final.util.CloudConstants;
import com.myownbadself.assignment10final.util.PictureUtils;

@SuppressLint("NewApi")
public class CameraFragment extends Fragment {
	private Camera camera;
	private SurfaceView surfaceView;
	private View progressContainer;
	private String randomImageName;

	private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {

		@Override
		public void onShutter() {
			progressContainer.setVisibility(View.VISIBLE);

		}
	};

	private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			FileOutputStream os = null;
			boolean success = true;
			String fileName = PictureUtils
					.getUniqueImageNameForLocation(randomImageName);

			try {
				os = getActivity().openFileOutput(fileName,
						Context.MODE_PRIVATE);
				os.write(data);

			} catch (Exception e) {
				success = false;
			} finally {
				try {
					if (os != null) {
						os.close();
					}
				} catch (Exception e) {
					success = false;
				}
			}

			if (success) {
				
				// send the image back
				Intent intent = new Intent();
				getActivity().setResult(Activity.RESULT_OK, intent);
				intent.putExtra(CloudConstants.EXTRA_NEW_IMAGE_NAME, fileName);
			} else {
				getActivity().setResult(Activity.RESULT_CANCELED);
			}

			getActivity().finish();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		randomImageName = random();
	}

	@Override
	public void onResume() {
		super.onResume();

		// check for android vesion when initializing camera
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			camera = Camera.open(0);
		} else {
			camera = Camera.open();
		}

	}

	@Override
	public void onPause() {
		super.onPause();

		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater
				.inflate(R.layout.fragment_camera, container, false);

		progressContainer = view.findViewById(R.id.progress_container);
		progressContainer.setVisibility(View.INVISIBLE);

		Button snapButton = (Button) view.findViewById(R.id.camera_button);
		snapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				camera.takePicture(shutterCallback, null, jpegCallback);
			}

		});

		initSurfaceView(view);

		return view;

	}

	@SuppressWarnings("deprecation")
	private void initSurfaceView(View view) {
		surfaceView = (SurfaceView) view.findViewById(R.id.camera_surface_view);
		SurfaceHolder holder = surfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if (camera != null) {
						camera.setPreviewDisplay(holder);
					}
				} catch (IOException ioe) {
				}
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (camera != null) {
					camera.stopPreview();
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {

				if (camera == null) {
					return;
				}

				Parameters parameters = camera.getParameters();
				Size optimalSize = getBestSupportedSize(
						parameters.getSupportedPreviewSizes(), width, height);
				parameters
						.setPreviewSize(optimalSize.width, optimalSize.height);

				optimalSize = getBestSupportedSize(
						parameters.getSupportedPictureSizes(), width, height);
				parameters
						.setPictureSize(optimalSize.width, optimalSize.height);

				camera.startPreview();

			}
		});
	}

	private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;

		// loop the sizes to get the largest possible area
		for (Size size : sizes) {
			int area = size.width * size.height;
			// if this is larger than the largest area, use this one instead
			if (area > largestArea) {
				bestSize = size;
				largestArea = area;
			}
		}

		return bestSize;

	}


	public static String random() {
		Random generator = new Random();
		StringBuilder randomStringBuilder = new StringBuilder();
		int randomLength = generator
				.nextInt(CloudConstants.RANDOM_STRING_LENGTH);
		char tempChar;
		for (int i = 0; i < randomLength; i++) {
			tempChar = (char) (generator.nextInt(96) + 32);
			randomStringBuilder.append(tempChar);
		}
		return randomStringBuilder.toString();
	}
}
