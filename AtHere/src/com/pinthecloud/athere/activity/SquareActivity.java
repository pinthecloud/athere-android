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

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.database.MessageDBHelper;
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.fragment.AhFragment;
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

public class SquareActivity extends AhActivity {

	private ProgressBar progressBar;
	private AhFragment contentFragment;
	private DrawerLayout mDrawerLayout; 
	private View mFragmentView;
	private ChupaListFragment chupaListFragment;
	private SlidingMenu slidingMenu;

	private MessageHelper messageHelper;
	private MessageDBHelper messageDBHelper;
	private UserHelper userHelper;
	private SquareHelper squareHelper;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square);
		
		setHelper();
		findComponent();
		
		Square square = squareHelper.getMySquareInfo();
		getSupportActionBar().setTitle(square.getName());

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		setFragment(fragmentTransaction, square);
		setDrawerFragment(fragmentManager);
		slidingMenu = app.getSlidingMenu(thisActivity, fragmentTransaction);
		
		fragmentTransaction.commit();
		
		setMessageHandler();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.square, menu);
		return true;
	}


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem menuItem = menu.findItem(R.id.square_menu_notification);
		if(messageDBHelper.getAllChupaBadgeNum() > 0){
			menuItem.setIcon(R.drawable.actionbar_white_chupalist_highlight_btn);
		}else{
			menuItem.setIcon(R.drawable.actionbar_white_chupalist_btn);
		}
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case android.R.id.home:
			// Handle app drawer
			if(mDrawerLayout.isDrawerOpen(mFragmentView)){
				mDrawerLayout.closeDrawer(mFragmentView);
			}else{
				slidingMenu.toggle();
			}
			break;
		case R.id.square_menu_notification:
			// Handle notification drawer
			if(mDrawerLayout.isDrawerOpen(mFragmentView)){
				mDrawerLayout.closeDrawer(mFragmentView);
			}else{
				mDrawerLayout.openDrawer(mFragmentView);
			}
			break;
		case R.id.square_menu_more:
			// Exit square
			String message = getResources().getString(R.string.exit_square_consent_message);
			AhAlertDialog escDialog = new AhAlertDialog(null, message, true, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					// Close opened drawer
					if(mDrawerLayout.isDrawerOpen(mFragmentView)){
						mDrawerLayout.closeDrawer(mFragmentView);
					}
					exitSquare();
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
					// Do nothing
				}
			});
			escDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			break;
		}
		return true;
	}


	@Override
	public void onBackPressed() {
		// Close drawer
		if(mDrawerLayout.isDrawerOpen(mFragmentView)){
			mDrawerLayout.closeDrawer(mFragmentView);
			return;
		}

		if(slidingMenu.isMenuShowing()){
			slidingMenu.toggle();
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


	private void findComponent(){
		progressBar = (ProgressBar) findViewById(R.id.square_progress_bar);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.square_drawer_layout);
		mFragmentView = findViewById(R.id.square_notification_drawer_fragment);
	}


	private void setHelper(){
		messageHelper = app.getMessageHelper();
		messageDBHelper = app.getMessageDBHelper();
		userHelper = app.getUserHelper();
		squareHelper = app.getSquareHelper();
	}


	private void setFragment(FragmentTransaction fragmentTransaction, Square square){
		contentFragment = new SquareTabFragment(square);
		fragmentTransaction.add(R.id.square_tab_layout, contentFragment);
	}


	private void setDrawerFragment(FragmentManager fragmentManager){
		chupaListFragment = (ChupaListFragment) fragmentManager.findFragmentById(R.id.square_notification_drawer_fragment);
		chupaListFragment.setUp(mDrawerLayout);
	}


	private void setMessageHandler(){
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


	private void exitSquare() {
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();

		AhUser user = userHelper.getMyUserInfo();
		userHelper.exitSquareAsync(contentFragment, user, new AhEntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean result) {
				app.removeMySquarePreference(contentFragment);
				Intent intent = new Intent(thisActivity, SquareListActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
