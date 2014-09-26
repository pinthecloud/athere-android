package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.BasicProfileFragment;


public class BasicProfileActivity extends AhActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basic_profile);

		//		uiHelper = new UiLifecycleHelper(this, callback);
		//		uiHelper.onCreate(savedInstanceState);
		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		BasicProfileFragment basicProfileFragment = new BasicProfileFragment();
		fragmentTransaction.add(R.id.basic_profile_container, basicProfileFragment);
		fragmentTransaction.commit();
	}
}
