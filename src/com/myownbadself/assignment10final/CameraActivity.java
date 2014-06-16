package com.myownbadself.assignment10final;

import android.support.v4.app.Fragment;

public class CameraActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new CameraFragment();
	}

}
