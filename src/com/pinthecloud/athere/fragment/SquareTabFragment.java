package com.pinthecloud.athere.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquarePagerAdapter;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.PagerSlidingTabStrip;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.Square;

public class SquareTabFragment extends AhFragment{

	private ViewPager mViewPager;
	private SquarePagerAdapter mSquarePagerAdapter;
	private PagerSlidingTabStrip tabs;

	private Square square;
	private MessageHelper messageHelper;


	public SquareTabFragment(Square square) {
		super();
		this.square = square;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_tab, container, false);
		
		messageHelper = app.getMessageHelper();

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
		// Set up tabs with the view pager
		tabs.setViewPager(mViewPager);
		tabs.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
		});
		
		
		/**
		 *  Need to set Handler for Chupa on app running state.
		 */
		
		messageHelper.setMessageHandler(AhMessage.TYPE.CHUPA, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(final AhMessage message) {
				mSquarePagerAdapter.notifyDataSetChanged();
				
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tabs.setTextColor(Color.YELLOW);
						EditText et = new EditText(context);
						tabs.addView(et, 1);
						Toast toast = Toast.makeText(activity.getApplicationContext(),
								   "Handle in Square Activity", Toast.LENGTH_LONG);
						toast.show();
					}
				});
			}
		});
		
		return view;
	}
}
