package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.BasicProfileFragment;

public class BasicProfileActivity extends AhActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basic_profile);

		/*
		 * Set Fragment to container
		 */
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        BasicProfileFragment basicProfileFragment = new BasicProfileFragment();
        fragmentTransaction.add(R.id.basic_profile_container, basicProfileFragment);
        fragmentTransaction.commit();
	}
	
	
	/*
	 * Callback from confirming dialog in fragment
	 * Move to next page
	 */
	public void doPositiveClick() {
		Intent intent = new Intent(this, SquareListActivity.class);
		startActivity(intent);
		finish();
	}
}