package com.pinthecloud.athere.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalVariable;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		messageHelper = app.getMessageHelper();
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

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		/*
		 *  Need to set Handler for Chupa on app running state.
		 */
		messageHelper.setMessageHandler(new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(final AhMessage message) {
				if (message.getType().equals(AhMessage.TYPE.CHUPA.toString())){
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mSquarePagerAdapter.notifyDataSetChanged();
							Toast toast = Toast.makeText(context,
									"Seungmin You should handle chupa receive at Here!\n"
											+ "(chupa badge and new 1)", Toast.LENGTH_LONG);
							toast.show();
						}
					});
				} else if (message.getType().equals(AhMessage.TYPE.EXIT_SQUARE.toString())){
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mSquarePagerAdapter.notifyDataSetChanged();
						}
					});
				}
			}
		});
	}
}
