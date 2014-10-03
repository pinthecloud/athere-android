package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.database.MessageDBHelper;
import com.pinthecloud.athere.fragment.AppDrawerFragment;
import com.pinthecloud.athere.fragment.ChupaListFragment;
import com.pinthecloud.athere.fragment.SquareListFragment;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;


public class SquareListActivity extends AhActivity {

	private DrawerLayout mDrawerLayout; 
	private View mFragmentView;
	private ChupaListFragment chupaListFragment;
	private SlidingMenu slidingMenu;

	private MessageHelper messageHelper;
	private MessageDBHelper messageDBHelper;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square_list);


		/*
		 * Set UI Component and drawer and helper
		 */
		mDrawerLayout = (DrawerLayout) findViewById(R.id.square_list_drawer_layout);
		mFragmentView = findViewById(R.id.square_list_notification_drawer_fragment);
		FragmentManager fragmentManager = getFragmentManager();
		chupaListFragment = (ChupaListFragment) fragmentManager.findFragmentById(R.id.square_list_notification_drawer_fragment);
		chupaListFragment.setUp(mDrawerLayout, R.drawable.actionbar_red_drawer_btn);
		messageHelper = app.getMessageHelper();
		messageDBHelper = app.getMessageDBHelper();


		/*
		 * Set Fragment to container
		 */
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		SquareListFragment squareListFragment = new SquareListFragment();
		fragmentTransaction.add(R.id.square_list_layout, squareListFragment);


		/*
		 * Set sliding menu
		 */
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
		fragmentTransaction.commit();


		/*
		 * Set Handler for forced logout
		 */
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage message) {
				messageHelper.triggerMessageEvent(chupaListFragment, message);
			}
		});
	}


	@Override
	public void onBackPressed() {
		if(mDrawerLayout.isDrawerOpen(mFragmentView)){
			mDrawerLayout.closeDrawer(mFragmentView);
			return;
		}

		if(slidingMenu.isMenuShowing()){
			slidingMenu.toggle();
			return;
		}

		super.onBackPressed();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.square_list, menu);
		return true;
	}


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem menuItem = menu.findItem(R.id.square_list_menu_notification);
		if(messageDBHelper.getAllChupaBadgeNum() > 0){
			menuItem.setIcon(R.drawable.actionbar_red_chupalist_highlight_btn);
		}else{
			menuItem.setIcon(R.drawable.actionbar_red_chupalist_btn);
		}
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case android.R.id.home:
			if(mDrawerLayout.isDrawerOpen(mFragmentView)){
				mDrawerLayout.closeDrawer(mFragmentView);
			}else{
				slidingMenu.toggle();
			}
			break;
		case R.id.square_list_menu_notification:
			if(mDrawerLayout.isDrawerOpen(mFragmentView)){
				mDrawerLayout.closeDrawer(mFragmentView);
			}else{
				mDrawerLayout.openDrawer(mFragmentView);
			}
			break;
		}
		return true;
	}
}
