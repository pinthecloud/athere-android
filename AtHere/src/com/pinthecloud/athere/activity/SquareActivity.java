package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.fragment.ChatFragment;
import com.pinthecloud.athere.fragment.ChupaListFragment;
import com.pinthecloud.athere.fragment.SquareTabFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.Square;

public class SquareActivity extends AhSlidingActivity {

	private ProgressBar progressBar;
	private AhFragment contentFragment;
	private DrawerLayout mDrawerLayout; 
	private View mFragmentView;
	private ChupaListFragment chupaListFragment;

	private MessageHelper messageHelper;
	private UserHelper userHelper;
	private SquareHelper squareHelper;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square);


		/*
		 * Set Helper and get square
		 */
		messageHelper = app.getMessageHelper();
		userHelper = app.getUserHelper();
		squareHelper = app.getSquareHelper();
		Square square = squareHelper.getMySquareInfo();


		/*
		 * Set UI Component and drawer
		 */
		getActionBar().setTitle(square.getName());
		progressBar = (ProgressBar) findViewById(R.id.square_progress_bar);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.square_drawer_layout);
		mFragmentView = findViewById(R.id.square_notification_drawer_fragment);
		FragmentManager fragmentManager = getFragmentManager();
		chupaListFragment = (ChupaListFragment) fragmentManager.findFragmentById(R.id.square_notification_drawer_fragment);
		chupaListFragment.setUp(mDrawerLayout);


		/*
		 * Set tab
		 */
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if(squareHelper.isPreview()){
			contentFragment = new ChatFragment(square);	
		}else{
			contentFragment = new SquareTabFragment(square);
		}
		fragmentTransaction.add(R.id.square_tab_layout, contentFragment);
		fragmentTransaction.commit();


		/*
		 * Set Handler for forced logout
		 */
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage message) {
				if (message.getType().equals(AhMessage.TYPE.FORCED_LOGOUT.toString())) {
					final String toastMessage = getResources().getString(R.string.forced_logout_title);
					thisActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(thisActivity, toastMessage, Toast.LENGTH_LONG)
							.show();
						}
					});

					Intent intent = new Intent(thisActivity, SquareListActivity.class);
					startActivity(intent);
					finish();
					return;
				}
				messageHelper.triggerMessageEvent(contentFragment, message);
				messageHelper.triggerMessageEvent(chupaListFragment, message);
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
		if(squareHelper.isReview()){
			String message = getResources().getString(R.string.review_message);
			String cancelMessage = getResources().getString(R.string.no_today_message);
			AhAlertDialog reviewDialog = new AhAlertDialog(null, message, null, cancelMessage, true, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					squareHelper.setReview(false);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + AhGlobalVariable.GOOGLE_PLAY_APP_ID));
					startActivity(intent);
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
					squareHelper.setReview(false);
					finish();
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
		getMenuInflater().inflate(R.menu.square, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
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
		case R.id.menu_more:
			String message = getResources().getString(R.string.exit_square_consent_message);
			AhAlertDialog escDialog = new AhAlertDialog(null, message, true, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					exitSquare();
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
					// Do nothing
				}
			});
			escDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void exitSquare() {
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();

		AhUser user = userHelper.getMyUserInfo();
		userHelper.exitSquareAsync(contentFragment, user, new AhEntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean result) {
				progressBar.setVisibility(View.GONE);

				app.removeSquarePreference(contentFragment);
				Intent intent = new Intent(thisActivity, SquareListActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
