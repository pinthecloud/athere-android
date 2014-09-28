package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ChupaListFragment;
import com.pinthecloud.athere.fragment.SquareListFragment;


public class SquareListActivity extends AhSlidingActivity {

	private DrawerLayout mDrawerLayout; 
	private View mFragmentView;
	private ChupaListFragment mChupaListFragment;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square_list);


		/*
		 * Set UI component
		 */
		mDrawerLayout = (DrawerLayout) findViewById(R.id.square_list_drawer_layout);
		mFragmentView = findViewById(R.id.square_list_notification_drawer_fragment);
		FragmentManager fragmentManager = getFragmentManager();
		mChupaListFragment = (ChupaListFragment) fragmentManager.findFragmentById(R.id.square_list_notification_drawer_fragment);
		mChupaListFragment.setUp(mDrawerLayout);


		/*
		 * Set Fragment to container
		 */
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		SquareListFragment squareListFragment = new SquareListFragment();
		fragmentTransaction.add(R.id.square_list_layout, squareListFragment);
		fragmentTransaction.commit();
	}


	@Override
	public void onBackPressed() {
		if(mDrawerLayout.isDrawerOpen(mFragmentView)){
			mDrawerLayout.closeDrawer(mFragmentView);
			return;
		}
		super.onBackPressed();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.square_list, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if(mDrawerLayout.isDrawerOpen(mFragmentView)){
				mDrawerLayout.closeDrawer(mFragmentView);
			}else{
				toggle();
			}
			return true;
		case R.id.menu_notification:
			if(mDrawerLayout.isDrawerOpen(mFragmentView)){
				mDrawerLayout.closeDrawer(mFragmentView);
			}else{
				mDrawerLayout.openDrawer(mFragmentView);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
