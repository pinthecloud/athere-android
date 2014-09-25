package com.pinthecloud.athere.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.GuidePagerAdapter;
import com.viewpagerindicator.UnderlinePageIndicator;

public class GuideFragment extends AhFragment{

	private GuidePagerAdapter mAdapter;
	private ViewPager mPager;
	private UnderlinePageIndicator indicator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_guide, container, false);

		/*
		 * Find UI component
		 */
		mPager = (ViewPager)view.findViewById(R.id.guide_frag_viewpager);
		indicator = (UnderlinePageIndicator)view.findViewById(R.id.guide_frag_indicator);

		/*
		 * Set view pager and indicator
		 */
		mAdapter = new GuidePagerAdapter(getFragmentManager());
		mPager.setAdapter(mAdapter);
		indicator.setViewPager(mPager);
		indicator.setFades(false);

		return view;
	}
}
