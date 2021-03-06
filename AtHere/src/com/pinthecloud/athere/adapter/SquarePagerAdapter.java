package com.pinthecloud.athere.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.ChatFragment;
import com.pinthecloud.athere.fragment.MemberFragment;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.view.PagerSlidingTabStrip.IconTabProvider;

public class SquarePagerAdapter extends FragmentStatePagerAdapter implements IconTabProvider{

	private int[] titleIcons = {R.drawable.tabbar_chat_icon, R.drawable.tabbar_member_icon};
	public ChatFragment chatFragment;
	public MemberFragment memberFragment;


	public SquarePagerAdapter(FragmentManager fm, Square square) {
		super(fm);
		chatFragment = new ChatFragment(square);
		memberFragment = new MemberFragment();
	}


	@Override
	public CharSequence getPageTitle(int position) {
		return "";
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
			return this.chatFragment;
		case 1:
			return this.memberFragment;
		}
		return fragment;
	}


	@Override
	public int getCount() {
		return this.titleIcons.length;
	}
}
