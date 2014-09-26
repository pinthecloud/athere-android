package com.pinthecloud.athere.fragment;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.AhUser;

public class NotificationDrawerFragment extends AhFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_notification_drawer, container, false);
		return view;
	}
	
	
	public void setUp(View fragmentView, DrawerLayout drawerLayout, final AhUser user) {
		// TODO Do nothing
	}
}
