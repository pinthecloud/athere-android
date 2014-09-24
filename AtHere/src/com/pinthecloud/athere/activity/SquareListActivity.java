package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.SquareListFragment;


public class SquareListActivity extends AhActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square_list);


		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		SquareListFragment squareListFragment = new SquareListFragment();
		fragmentTransaction.add(R.id.square_list_container, squareListFragment);
		fragmentTransaction.commit();
	}


	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		getMenuInflater().inflate(R.menu.settings, menu);
	//		return super.onCreateOptionsMenu(menu);
	//	}
	//
	//
	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item) {
	//		switch(item.getItemId()){
	//		case R.id.menu_setting :
	//			return true;
	//		default:
	//			return super.onOptionsItemSelected(item);
	//		}
	//	}
}
