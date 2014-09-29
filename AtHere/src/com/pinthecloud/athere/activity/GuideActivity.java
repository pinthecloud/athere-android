package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.GuideFragment;

public class GuideActivity extends AhActivity{

	private GuideFragment guideFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame_activity);


		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		guideFragment = new GuideFragment();
		fragmentTransaction.add(R.id.activity_container, guideFragment);
		fragmentTransaction.commit();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		guideFragment.onActivityResult(requestCode, resultCode, data);
	}
}
