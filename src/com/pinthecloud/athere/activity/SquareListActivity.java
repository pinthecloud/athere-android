package com.pinthecloud.athere.activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.SquareListFragment;
import com.pinthecloud.athere.fragment.SquareListMenuDrawerFragment;

public class SquareListActivity extends AhActivity
implements SquareListMenuDrawerFragment.NavigationDrawerCallbacks {

	private SquareListMenuDrawerFragment mMenuDrawerFragment;
	private DrawerLayout mDrawerLayout;
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square_list);
		
		
		/*
		 * Set up drawer and fragment and interactions.
		 */
		mMenuDrawerFragment = (SquareListMenuDrawerFragment)
				getFragmentManager().findFragmentById(R.id.square_list_menu_drawer_frag);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.square_list_drawer_layout);
		mMenuDrawerFragment.setUp(R.id.square_list_menu_drawer_frag, mDrawerLayout);
	}


	/*
	 * update the main content by replacing fragments
	 * (non-Javadoc)
	 * @see com.pinthecloud.athere.fragment.MenuDrawerFragment.NavigationDrawerCallbacks#onNavigationDrawerItemSelected(int)
	 */
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
		.replace(R.id.square_list_container, SquareListFragment.newInstance(position + 1))
		.commit();
	}


	/*
	 * Only show items in the action bar relevant to this screen
	 * if the drawer is not showing. Otherwise, let the drawer
	 * decide what to show in the action bar.
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mMenuDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.square_list, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}


	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}


	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}
}
