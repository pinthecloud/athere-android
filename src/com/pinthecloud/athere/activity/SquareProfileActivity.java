package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.SquareProfileFragment;

public class SquareProfileActivity extends AhActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square_profile);


		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		SquareProfileFragment squareProfileFragment = new SquareProfileFragment();
		fragmentTransaction.add(R.id.square_profile_container, squareProfileFragment);
		fragmentTransaction.commit();
	}
}
