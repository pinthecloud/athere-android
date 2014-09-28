package com.pinthecloud.athere.fragment;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.NotificationDrawerListAdapter;
import com.pinthecloud.athere.model.AhUser;

public class NotificationDrawerFragment extends AhFragment{

	private ListView list;
	private NotificationDrawerListAdapter notificationDrawerListAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_notification_drawer, container, false);


		/*
		 * Find UI component
		 */
		list = (ListView) view.findViewById(R.id.notification_drawer_frag_list);


		/*
		 * Set list 
		 */
		notificationDrawerListAdapter = new NotificationDrawerListAdapter(context);
		list.setAdapter(notificationDrawerListAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO
			}
		});

		return view;
	}


	public void setUp(View fragmentView, DrawerLayout drawerLayout, final AhUser user) {
		// TODO
	}
}
