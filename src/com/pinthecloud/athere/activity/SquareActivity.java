package com.pinthecloud.athere.activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.SquareDrawerFragment;
import com.pinthecloud.athere.fragment.SquareTabFragment;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.model.User;

public class SquareActivity extends AhActivity implements SquareDrawerFragment.SquareDrawerFragmentCallbacks{

	private Square square;
	private User user;

	private ActionBar mActionBar;
	private View mCustomActionBarView;
	private TextView mTitleTextView;

	private ProgressBar progressBar;

	private FragmentManager fragmentManager;

	private DrawerLayout mDrawerLayout; 
	private ActionBarDrawerToggle mDrawerToggle;

	private View mFragmentView;
	private SquareDrawerFragment mSquareDrawerFragment;

	private SquareHelper squareHelper;
	private UserHelper userHelper;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square);


		/*
		 * Set Helper and get square
		 */
		userHelper = app.getUserHelper();
		squareHelper = app.getSquareHelper();
		user = userHelper.getMyUserInfo(true);
		square = squareHelper.getSquare();


		/*
		 * Set UI Component
		 */
		mActionBar = getActionBar();
		mActionBar.setCustomView(R.layout.action_bar_general);
		mCustomActionBarView = mActionBar.getCustomView();
		mTitleTextView = (TextView) mCustomActionBarView.findViewById(R.id.action_bar_general_title);

		progressBar = (ProgressBar) findViewById(R.id.square_progress_bar);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.square_layout);
		mFragmentView = findViewById(R.id.square_drawer_fragment);
		fragmentManager = getFragmentManager();
		mSquareDrawerFragment = (SquareDrawerFragment) fragmentManager.findFragmentById(R.id.square_drawer_fragment);


		/*
		 * Set Action Bar
		 */
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(false);
		mTitleTextView.setText(square.getName());


		/*
		 * Set tab
		 */
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		SquareTabFragment mSquareTabFragment = new SquareTabFragment(square);
		fragmentTransaction.add(R.id.square_tab_layout, mSquareTabFragment);
		fragmentTransaction.commit();


		/*
		 * Set Drawer
		 */
		mSquareDrawerFragment.setUp(mFragmentView, mDrawerLayout);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
				mDrawerLayout, /* DrawerLayout object */
				R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
				R.string.des_drawer_open, /* "open drawer" description for accessibility */
				R.string.des_drawer_close /* "close drawer" description for accessibility */
				)
		{
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!mSquareDrawerFragment.isAdded()) {
					return;
				}
				invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!mSquareDrawerFragment.isAdded()) {
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

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
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
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.square, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.square_menu_drawer:
			if(mDrawerLayout.isDrawerOpen(mFragmentView)){
				mDrawerLayout.closeDrawer(mFragmentView);
			}else{
				mDrawerLayout.openDrawer(mFragmentView);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public void exitSquare() {
		progressBar.setVisibility(View.VISIBLE);

		userHelper.exitSquareAsync(user.getId(), new AhEntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				progressBar.setVisibility(View.GONE);

				pref.removePref(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY);
				pref.removePref(AhGlobalVariable.USER_ID_KEY);
				pref.removePref(AhGlobalVariable.COMPANY_NUMBER_KEY);
				pref.removePref(AhGlobalVariable.SQUARE_ID_KEY);

				Intent intent = new Intent(SquareActivity.this, SquareListActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
