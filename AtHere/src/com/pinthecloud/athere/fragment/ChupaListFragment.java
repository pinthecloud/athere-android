package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.adapter.ChupaListAdapter;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.Chupa;

public class ChupaListFragment extends AhFragment{

	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;

	private ChupaListAdapter squareChupaListAdapter;
	private ListView squareChupaListView;
	private ImageView blankImage;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_chupa_list, container, false);

		/*
		 * Set UI component
		 */
		squareChupaListView = (ListView)view.findViewById(R.id.chupa_list_frag_list);
		blankImage = (ImageView)view.findViewById(R.id.chupa_list_frag_blank_image);


		/*
		 * Set square chupa list view
		 */
		squareChupaListAdapter = new ChupaListAdapter(context, thisFragment);
		squareChupaListView.setAdapter(squareChupaListAdapter);
		squareChupaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(activity, ChupaChatActivity.class);
				intent.putExtra(AhGlobalVariable.USER_KEY, squareChupaListAdapter.getItem(position).getUserId());
				startActivity(intent);
			}
		});


		/*
		 * Set message handler for getting push
		 */
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage entity) {
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


	private void refreshView() {
		/*
		 * Set square chupa list view
		 */
		final List<AhMessage> lastChupaList = messageDBHelper.getLastChupas();
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				squareChupaListAdapter.clear();
				squareChupaListAdapter.addAll(convertToMap(lastChupaList));
				if(squareChupaListAdapter.getCount() < 1){
					blankImage.setVisibility(View.VISIBLE);
				} else{
					blankImage.setVisibility(View.GONE);
				}
			}
		});
	}


	private List<Chupa> convertToMap(List<AhMessage> lastChupaList) {
		List<Chupa> chupaList = new ArrayList<Chupa>();
		for(AhMessage message : lastChupaList){
			Chupa chupa = new Chupa();

			String userId = null;
			boolean isExit = false;

			if (message.isMine()) {
				// the other user is Receiver
				userId = message.getReceiverId();
			} else {
				// the other user is Sender
				userId = message.getSenderId();
			}

			chupa.setUserId(userId);
			AhUser user = userDBHelper.getUser(userId, true);
			chupa.setUserNickName(user != null ? user.getNickName() : "Unkown");

			// check whether it is exited.
			if (userDBHelper.isUserExit(userId)) {
				isExit = true;
			}
			chupa.setExit(isExit);

			chupa.setContent(message.getContent());
			chupa.setTimeStamp(message.getTimeStamp());
			chupa.setId(message.getChupaCommunId());
			chupa.setBadgeNum(messageDBHelper.getChupaBadgeNum(message.getChupaCommunId()));

			chupaList.add(chupa);
		}
		return chupaList;
	}


	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(DrawerLayout drawerLayout) {
		mDrawerLayout = drawerLayout;

		ActionBar actionBar = activity.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(activity, /* host Activity */
				mDrawerLayout, /* DrawerLayout object */
				R.drawable.indicator_drawer, /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open, /* "open drawer" description for accessibility */
				R.string.drawer_close /* "close drawer" description for accessibility */
				)
		{
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}
				activity.invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}
				activity.invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});
	}
}
