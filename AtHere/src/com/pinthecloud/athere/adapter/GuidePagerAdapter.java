package com.pinthecloud.athere.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.GuideImageFragment;

public class GuidePagerAdapter extends FragmentStatePagerAdapter {

	private int[] contents = {R.drawable.guide_1_img, R.drawable.guide_2_img, R.drawable.guide_3_img};

	public GuidePagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "";
	}

	@Override
	public Fragment getItem(int position) {
		return new GuideImageFragment(contents, position);
	}

	@Override
	public int getCount() {
		return contents.length;
	}
}
