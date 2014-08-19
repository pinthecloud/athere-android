package com.pinthecloud.athere.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquarePagerAdapter;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.view.BadgeView;
import com.pinthecloud.athere.view.PagerSlidingTabStrip;

public class SquareTabFragment extends AhFragment{

	private final int CHUPA_TAB = 1;
	private final int BADGE_SIZE = 21;

	private ViewPager mViewPager;
	private SquarePagerAdapter mSquarePagerAdapter;
	private PagerSlidingTabStrip tabs;
	private View chupaTabBadge;
	private BadgeView badge;

	private Square square;


	public SquareTabFragment(Square square) {
		super();
		this.square = square;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		int startTab = pref.getInt(AhGlobalVariable.SQUARE_EXIT_TAB_KEY);
		mViewPager.setAdapter(mSquarePagerAdapter);
		mViewPager.setCurrentItem(startTab);

		// Set up tabs with the view pager
		tabs.setStartTab(startTab);
		tabs.setViewPager(mViewPager);
		chupaTabBadge = tabs.getTabBadge(CHUPA_TAB);
		tabs.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				pref.putInt(AhGlobalVariable.SQUARE_EXIT_TAB_KEY, position);
			}
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});


		/*
		 * Set badge
		 */
		badge = new BadgeView(context, chupaTabBadge);
		badge.setTextColor(Color.RED);
		badge.setBadgeBackgroundColor(Color.WHITE);
		badge.setTextSize(BADGE_SIZE);
		badge.setBadgePosition(BadgeView.POSITION_CENTER_VERTICAL_RIGHT);

		/*
		 * Set message handle
		 */
		messageHelper.setMessageHandler(_thisFragment, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(final AhMessage message) {
				// Chupa & Exit Message can go through here
				// Chupa & Exit Message need to be update visually in ChupaChatList Fragment
				//				if (message.getType().equals(AhMessage.TYPE.CHUPA.toString())) {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						refreshView();
					}
				});
				messageHelper.triggerMessageEvent(mSquarePagerAdapter.squareChatFragment, message);
				messageHelper.triggerMessageEvent(mSquarePagerAdapter.squareChupaListFragment, message);
			}
		});

		return view;
	}

	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(AhGlobalVariable.LOG_TAG, "SquareTabFragment onStart");
		refreshView();
	}

	
	private void refreshView(){
		int totalNum = messageDBHelper.getAllBadgeNum();
		if(totalNum != 0){
			badge.setText("" + totalNum);
			badge.show();	
		}else{
			badge.hide();
		}
	}
}
