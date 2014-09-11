package com.pinthecloud.athere.activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.fragment.SquareDrawerFragment;
import com.pinthecloud.athere.fragment.SquareTabFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.Square;

public class SquareActivity extends AhActivity{

	private Square square;
	private AhUser user;

	private ActionBar mActionBar;

	private FragmentManager fragmentManager;
	private DrawerLayout mDrawerLayout; 
	private ActionBarDrawerToggle mDrawerToggle;
	private View mFragmentView;
	private SquareDrawerFragment mSquareDrawerFragment;

	private SquareHelper squareHelper;
	private MessageHelper messageHelper;
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
		messageHelper = app.getMessageHelper();
		user = userHelper.getMyUserInfo(true);
		square = squareHelper.getSquare();


		/*
		 * Set UI Component
		 */
		mActionBar = getActionBar();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.square_layout);
		mFragmentView = findViewById(R.id.square_drawer_fragment);
		fragmentManager = getFragmentManager();
		mSquareDrawerFragment = (SquareDrawerFragment) fragmentManager.findFragmentById(R.id.square_drawer_fragment);


		/*
		 * Set Action Bar
		 */
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setTitle(square.getName());


		/*
		 * Set tab
		 */
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		final SquareTabFragment mSquareTabFragment = new SquareTabFragment(square);
		fragmentTransaction.add(R.id.square_tab_layout, mSquareTabFragment);
		fragmentTransaction.commit();


		/*
		 * Set Drawer
		 */
		mSquareDrawerFragment.setUp(mFragmentView, mDrawerLayout, user);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
				mDrawerLayout, /* DrawerLayout object */
				R.drawable.sidebar, /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open, /* "open drawer" description for accessibility */
				R.string.drawer_close /* "close drawer" description for accessibility */
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


		/*
		 * Set Handler for forced logout
		 */
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage message) {
				if (message.getType().equals(AhMessage.TYPE.FORCED_LOGOUT.toString())) {
					String text = getResources().getString(R.string.forced_logout_title);
					Toast toast = Toast.makeText(thisActivity, text, Toast.LENGTH_LONG);
					toast.show();

					Intent intent = new Intent(SquareActivity.this, SquareListActivity.class);
					startActivity(intent);
					finish();
					return;
				}
				messageHelper.triggerMessageEvent(mSquareTabFragment, message);
			}
		});
	}


	@Override
	public void onBackPressed() {
		// Close drawer
		if(mDrawerLayout.isDrawerOpen(mFragmentView)){
			mDrawerLayout.closeDrawer(mFragmentView);
			return;
		}

		// Ask review
		if(pref.getBoolean(AhGlobalVariable.REVIEW_DIALOG_KEY)){
			pref.removePref(AhGlobalVariable.REVIEW_DIALOG_KEY);
			String message = getResources().getString(R.string.review_message);
			String cancelMessage = getResources().getString(R.string.no_today_message);
			AhAlertDialog reviewDialog = new AhAlertDialog(null, message, null, cancelMessage, true, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + AhGlobalVariable.GOOGLE_STORE_APP_ID));
					startActivity(intent);
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
					onBackPressed();
				}
			});
			reviewDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			return;
		}

		// Back
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
		case R.id.menu_square_drawer:
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
}
