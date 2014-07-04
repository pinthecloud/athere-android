package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ProfileFragment;

public class ProfileActivity extends AhActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		/*
		 * Set Fragment to container
		 */
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        ProfileFragment profileFragment = new ProfileFragment();
        fragmentTransaction.add(R.id.profile_container, profileFragment);
        fragmentTransaction.commit();
	}
}
