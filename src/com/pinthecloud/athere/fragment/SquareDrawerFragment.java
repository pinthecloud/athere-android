package com.pinthecloud.athere.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquareDrawerParticipantListAdapter;
import com.pinthecloud.athere.model.User;

public class SquareDrawerFragment extends AhFragment {

	private DrawerLayout mDrawerLayout;
	private View mFragmentView;

	private ListView participantListView;
	private SquareDrawerParticipantListAdapter participantListAdapter; 

	private Button exitButton;

	private ArrayList<User> userList = new ArrayList<User>();


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_drawer, container, false);

		/*
		 * Set Ui Component
		 */
		participantListView = (ListView) view.findViewById(R.id.square_drawer_frag_participant_list);
		exitButton = (Button) view.findViewById(R.id.square_drawer_frag_exit_button);


		/*
		 * Set participant list view
		 */
		participantListAdapter = new SquareDrawerParticipantListAdapter
				(context, R.layout.row_square_drawer_participant_list, userList);
		participantListView.setAdapter(participantListAdapter);
		participantListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});


		/*
		 * Set Button
		 */
		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

		return view;
	}


	public void setUp(View fragmentView, DrawerLayout drawerLayout) {
		mFragmentView = fragmentView;
		mDrawerLayout = drawerLayout;
	}
}
