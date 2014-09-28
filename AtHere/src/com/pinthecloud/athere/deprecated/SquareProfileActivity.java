package com.pinthecloud.athere.deprecated;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.AhActivity;


public class SquareProfileActivity extends AhActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame_activity);


		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		SquareProfileFragment squareProfileFragment = new SquareProfileFragment();
		fragmentTransaction.add(R.id.activity_container, squareProfileFragment);
		fragmentTransaction.commit();
	}
}
