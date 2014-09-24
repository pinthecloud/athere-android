package com.pinthecloud.athere.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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

	public static final int CHAT_TAB = 0;
	public static final int CHUPA_TAB = 1;
	private final int BADGE_SIZE = 21;

	private ViewPager mViewPager;
	private SquarePagerAdapter mSquarePagerAdapter;
	private PagerSlidingTabStrip tabs;

	private View chupaTabBadge;
	private BadgeView chupaBadge;

	private Square square;


	public SquareTabFragment() {
		super();
	}


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
		super.onCreateView(inflater, container, savedInstanceState);
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
		mSquarePagerAdapter = new SquarePagerAdapter(context, getFragmentManager(), tabs, square);

		// Set up the ViewPager with the sections adapter.
		int startTab = pref.getInt(AhGlobalVariable.SQUARE_EXIT_TAB_KEY);
		mViewPager.setAdapter(mSquarePagerAdapter);
		mViewPager.setCurrentItem(startTab);

		// Set up tabs with the view pager
		tabs.setStartTab(startTab);
		tabs.setViewPager(mViewPager);
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
		chupaTabBadge = tabs.getTabBadgeView(CHUPA_TAB);


		/*
		 * Set badge
		 */
		chupaBadge = new BadgeView(context, chupaTabBadge);
		chupaBadge.setTextColor(Color.RED);
		chupaBadge.setBadgeBackgroundColor(Color.WHITE);
		chupaBadge.setTextSize(BADGE_SIZE);
		chupaBadge.setBadgePosition(BadgeView.POSITION_CENTER_VERTICAL_RIGHT);


		/*
		 * Set message handle
		 */
		messageHelper.setMessageHandler(thisFragment, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(final AhMessage message) {
				messageHelper.triggerMessageEvent(mSquarePagerAdapter.squareChatFragment, message);
				messageHelper.triggerMessageEvent(mSquarePagerAdapter.squareChupaListFragment, message);
				refreshView();
			}
		});

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		refreshView();
	}


	private void refreshView(){
		final int chupaBadgeTotalNum = messageDBHelper.getAllChupaBadgeNum();
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if(chupaBadgeTotalNum > 0){
					chupaBadge.setText("" + chupaBadgeTotalNum);
					chupaBadge.show();	
				}else{
					chupaBadge.hide();
				}
			}
		});
	}
}