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
import com.pinthecloud.athere.fragment.AppDrawerFragment;
import com.pinthecloud.athere.fragment.ChupaListFragment;
import com.pinthecloud.athere.fragment.SquareListFragment;


public class SquareListActivity extends AhActivity {

	private DrawerLayout mDrawerLayout; 
	private View mFragmentView;
	private ChupaListFragment mChupaListFragment;
	private SlidingMenu slidingMenu;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square_list);


		/*
		 * Set UI Component and drawer
		 */
		mDrawerLayout = (DrawerLayout) findViewById(R.id.square_list_drawer_layout);
		mFragmentView = findViewById(R.id.square_list_notification_drawer_fragment);
		FragmentManager fragmentManager = getFragmentManager();
		mChupaListFragment = (ChupaListFragment) fragmentManager.findFragmentById(R.id.square_list_notification_drawer_fragment);
		mChupaListFragment.setUp(mDrawerLayout, R.drawable.actionbar_red_drawer_btn);


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
				slidingMenu.toggle();
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
