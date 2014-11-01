package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.AppDrawerFragment;
import com.pinthecloud.athere.fragment.SquareListFragment;


public class SquareListActivity extends AhActivity {

	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private SlidingMenu slidingMenu;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square_list);

		setActionBar();
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		setFragment(fragmentTransaction);
		setSlidingMenu(fragmentTransaction);
		setDrawerIndicator();
		fragmentTransaction.commit();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			slidingMenu.toggle();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onBackPressed() {
		if(slidingMenu.isMenuShowing()){
			slidingMenu.toggle();
			return;
		}
		super.onBackPressed();
	}


	private void setActionBar(){
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}


	private void setFragment(FragmentTransaction fragmentTransaction){
		SquareListFragment fragment = new SquareListFragment ();
		fragmentTransaction.add(R.id.square_list_layout, fragment);
	}


	private void setSlidingMenu(FragmentTransaction fragmentTransaction){
		slidingMenu = new SlidingMenu(thisActivity);
		slidingMenu.setMenu(R.layout.app_drawer_frame);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		slidingMenu.setShadowWidthRes(R.dimen.app_drawer_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.app_drawer_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.app_drawer_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(thisActivity, SlidingMenu.SLIDING_WINDOW);

		AppDrawerFragment appDrawerFragment = new AppDrawerFragment();
		fragmentTransaction.replace(R.id.app_drawer_container, appDrawerFragment);
	}


	private void setDrawerIndicator() {
		drawerLayout = (DrawerLayout) findViewById(R.id.square_list_drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(thisActivity, /* host Activity */
				drawerLayout, /* DrawerLayout object */
				R.string.drawer_open, /* "open drawer" description for accessibility */
				R.string.drawer_close /* "close drawer" description for accessibility */
				)
		{
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
		
		// Defer code dependent on restoration of previous instance state.
		drawerLayout.post(new Runnable() {
			@Override
			public void run() {
				drawerToggle.syncState();
			}
		});
	}
}
