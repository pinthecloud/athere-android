package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.SquareProfileFragment;

public class SquareProfileActivity extends AhActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_square_profile);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


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
