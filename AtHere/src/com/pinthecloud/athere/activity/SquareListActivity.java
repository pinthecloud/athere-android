package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.NotificationDrawerFragment;
import com.pinthecloud.athere.fragment.SquareListFragment;
import com.pinthecloud.athere.model.AhUser;


public class SquareListActivity extends AhSlidingActivity {

	private FragmentManager fragmentManager;
	private DrawerLayout mDrawerLayout; 
	private ActionBarDrawerToggle mDrawerToggle;
	private View mFragmentView;
	private NotificationDrawerFragment mNotificationDrawerFragment;
	private AhUser user;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square_list);
		getActionBar().setDisplayHomeAsUpEnabled(true);


		/*
		 * Set UI component
		 */
		mDrawerLayout = (DrawerLayout) findViewById(R.id.square_list_drawer_layout);
		mFragmentView = findViewById(R.id.square_list_notification_drawer_fragment);
		fragmentManager = getFragmentManager();
		mNotificationDrawerFragment = (NotificationDrawerFragment) fragmentManager.findFragmentById(R.id.square_list_notification_drawer_fragment);


		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		SquareListFragment squareListFragment = new SquareListFragment();
		fragmentTransaction.add(R.id.square_list_layout, squareListFragment);
		fragmentTransaction.commit();


		/*
		 * Set Notification Drawer
		 */
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
				mDrawerLayout, /* DrawerLayout object */
				R.drawable.indicator_notification_drawer, /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open, /* "open drawer" description for accessibility */
				R.string.drawer_close /* "close drawer" description for accessibility */
				)
		{
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!mNotificationDrawerFragment.isAdded()) {
					return;
				}
				invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!mNotificationDrawerFragment.isAdded()) {
					return;
				}
				invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Set drawer fragment up with information got from activity
		mNotificationDrawerFragment.setUp(mFragmentView, mDrawerLayout, user);
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
			toggle();
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


	@Override
	public void onBackPressed() {
		if(mDrawerLayout.isDrawerOpen(mFragmentView)){
			mDrawerLayout.closeDrawer(mFragmentView);
			return;
		}
		super.onBackPressed();
	}
}
