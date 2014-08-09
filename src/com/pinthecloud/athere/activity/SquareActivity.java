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

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.SquareDrawerFragment;
import com.pinthecloud.athere.fragment.SquareDrawerFragment.SquareDrawerFragmentCallbacks;
import com.pinthecloud.athere.fragment.SquareTabFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;

public class SquareActivity extends AhActivity implements SquareDrawerFragmentCallbacks{

	private Square square;
	private User user;

	private ActionBar mActionBar;

	private FragmentManager fragmentManager;
	private DrawerLayout mDrawerLayout; 
	private ActionBarDrawerToggle mDrawerToggle;
	private View mFragmentView;
	private SquareDrawerFragment mSquareDrawerFragment;

	private ProgressBar progressBar;

	private SquareHelper squareHelper;
	private UserHelper userHelper;
	private UserDBHelper userDBHelper;
	private MessageHelper messageHelper;
	private MessageDBHelper messageDBHelper;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square);

		/*
		 * Set Helper and get square
		 */
		userHelper = app.getUserHelper();
		userDBHelper = app.getUserDBHelper();
		messageDBHelper = app.getMessageDBHelper();
		squareHelper = app.getSquareHelper();
		messageHelper = app.getMessageHelper();
		user = userHelper.getMyUserInfo(true);
		square = squareHelper.getSquare();


		/*
		 * Set UI Component
		 */
		mActionBar = getActionBar();
		progressBar = (ProgressBar) findViewById(R.id.square_progress_bar);
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
		SquareTabFragment mSquareTabFragment = new SquareTabFragment(square);
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

	@Override
	public void exitSquare() {
		progressBar.setVisibility(View.VISIBLE);

		new Thread(new Runnable() {

			@Override
			public void run() {
				userHelper.exitSquareSync(user.getId());
				userDBHelper.deleteAllUsers();
				messageDBHelper.deleteAllMessages();
				String exitMessage = getResources().getString(R.string.exit_square_message);
				String nickName = pref.getString(AhGlobalVariable.NICK_NAME_KEY);
				AhMessage.Builder messageBuilder = new AhMessage.Builder();
				messageBuilder.setContent(nickName + exitMessage)
				.setSender(pref.getString(AhGlobalVariable.NICK_NAME_KEY))
				.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY))
				.setReceiverId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY))
				.setType(AhMessage.TYPE.EXIT_SQUARE);
				AhMessage message = messageBuilder.build();
				messageHelper.sendMessageSync(message);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						progressBar.setVisibility(View.GONE);

						pref.removePref(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY);
						pref.removePref(AhGlobalVariable.USER_ID_KEY);
						pref.removePref(AhGlobalVariable.COMPANY_NUMBER_KEY);
						pref.removePref(AhGlobalVariable.SQUARE_ID_KEY);
						pref.removePref(AhGlobalVariable.SQUARE_NAME_KEY);
						pref.removePref(AhGlobalVariable.IS_CHUPA_ENABLE_KEY);
						pref.removePref(AhGlobalVariable.IS_CHAT_ALARM_ENABLE_KEY);

						Intent intent = new Intent(SquareActivity.this, SquareListActivity.class);
						startActivity(intent);
						finish();
					}
				});
			}
		}).start();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log(this, "Square Activity onStart");
	}
}
