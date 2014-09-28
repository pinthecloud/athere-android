package com.pinthecloud.athere.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.GuideImageFragment;

public class GuidePagerAdapter extends FragmentPagerAdapter {

	private int[] contents = {R.drawable.tabbar_chat_text, R.drawable.tabbar_chupa_text};

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
