package com.pinthecloud.athere.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquarePagerAdapter;
import com.pinthecloud.athere.interfaces.PagerSlidingTabStrip;

public class SquareActivity extends AhActivity{

	private SquarePagerAdapter mSquarePagerAdapter;
	private ViewPager mViewPager;
	private PagerSlidingTabStrip tabs;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_square);

		/*
		 * Set UI Component
		 */
		mViewPager = (ViewPager) findViewById(R.id.square_pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.square_tabs);


		/*
		 * Set tab
		 */
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSquarePagerAdapter = new SquarePagerAdapter(this, getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager.setAdapter(mSquarePagerAdapter);
		int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
				getResources().getDisplayMetrics());
		mViewPager.setPageMargin(pageMargin);
		tabs.setViewPager(mViewPager);
		tabs.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO
			}			

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO
			}
		});
	}
}
