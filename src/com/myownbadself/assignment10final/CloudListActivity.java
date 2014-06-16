package com.myownbadself.assignment10final;

import android.support.v4.app.Fragment;

public class CloudListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new CloudListFragment();
	}

}
