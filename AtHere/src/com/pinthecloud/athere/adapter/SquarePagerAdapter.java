package com.pinthecloud.athere.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ChatFragment;
import com.pinthecloud.athere.fragment.ChupaListFragment;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.view.PagerSlidingTabStrip.IconTabProvider;

public class SquarePagerAdapter extends FragmentStatePagerAdapter implements IconTabProvider{

	private String[] titles;
	private int[] titleIcons = {R.drawable.tabbar_chat_text, R.drawable.tabbar_chupa_text};
	public ChatFragment squareChatFragment;
	public ChupaListFragment squareChupaListFragment;


	public SquarePagerAdapter(Context context, FragmentManager fm, Square square) {
		super(fm);
		this.titles = context.getResources().getStringArray(R.array.square_tab_string_array);
		squareChatFragment = new ChatFragment(square);
		squareChupaListFragment = new ChupaListFragment();
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return this.titles[position];
	}


	@Override
	public int getPageIconResId(int position) {
		return this.titleIcons[position];
	}


	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch(position){
		case 0:
			return this.squareChatFragment;
		case 1:
			return this.squareChupaListFragment;
		}
		return fragment;
	}


	@Override
	public int getCount() {
		return this.titles.length;
	}
}
