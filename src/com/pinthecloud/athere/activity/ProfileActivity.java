package com.pinthecloud.athere.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ProfileFragment;

public class ProfileActivity extends Activity {

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
