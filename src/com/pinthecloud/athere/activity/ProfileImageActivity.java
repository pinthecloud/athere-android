package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ProfileImageFragment;

public class ProfileImageActivity extends AhActivity{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_image);

		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		ProfileImageFragment profileImageFragment = new ProfileImageFragment();
		fragmentTransaction.add(R.id.profile_image_container, profileImageFragment);
		fragmentTransaction.commit();
	}
}
