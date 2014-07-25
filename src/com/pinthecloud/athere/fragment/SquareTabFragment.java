package com.pinthecloud.athere.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquarePagerAdapter;
import com.pinthecloud.athere.interfaces.PagerSlidingTabStrip;
import com.pinthecloud.athere.model.Square;

public class SquareTabFragment extends AhFragment{

	private ViewPager mViewPager;
	private SquarePagerAdapter mSquarePagerAdapter;
	private PagerSlidingTabStrip tabs;
	
	private Square square;

	
	public SquareTabFragment(Square square) {
		super();
		this.square = square;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_tab, container, false);


		/*
		 * Set UI Component
		 */
		mViewPager = (ViewPager) view.findViewById(R.id.square_tab_frag_pager);
		tabs = (PagerSlidingTabStrip) view.findViewById(R.id.square_tab_frag_tab);


		/*
		 * Set tab
		 */
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSquarePagerAdapter = new SquarePagerAdapter(context, getFragmentManager(), square);

		// Set up the ViewPager with the sections adapter.
		mViewPager.setAdapter(mSquarePagerAdapter);
		int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
				getResources().getDisplayMetrics());
		mViewPager.setPageMargin(pageMargin);
		
		// Set up tabs with the view pager
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
		return view;
	}
}
