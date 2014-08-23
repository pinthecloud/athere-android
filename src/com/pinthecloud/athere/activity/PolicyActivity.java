package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.PolicyFragment;

public class PolicyActivity extends AhActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_policy);

		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		PolicyFragment policyFragment = new PolicyFragment();
		fragmentTransaction.add(R.id.basic_profile_container, policyFragment);
		fragmentTransaction.commit();
	}
}
