package com.pinthecloud.athere.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.SquareChatFragment;
import com.pinthecloud.athere.fragment.SquareChupaFragment;
import com.pinthecloud.athere.model.Square;

public class SquarePagerAdapter extends FragmentStatePagerAdapter {

	private String[] titles;
	private Square square;


	public SquarePagerAdapter(Context context, FragmentManager fm, Square square) {
		super(fm);
		this.titles = context.getResources().getStringArray(R.array.square_tab_string_array);
		this.square = square;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch(position){
		case 0:
			return fragment = new SquareChatFragment(square);
		case 1:
			return fragment = new SquareChupaFragment(square);
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return this.titles.length;
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return this.titles[position];
	}
}
