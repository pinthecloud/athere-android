package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.adapter.SquareChupaListAdapter;
import com.pinthecloud.athere.interfaces.AhException;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;

public class SquareChupaListFragment extends AhFragment{

	public SquareChupaListAdapter squareChupaListAdapter;
	private ListView squareChupaListView;

	public List<Map<String,String>> lastChupaCommunList;
	private MessageDBHelper messageDBHelper;
	private UserDBHelper userDBHelper;

	
	public SquareChupaListFragment(Square square) {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		messageDBHelper = app.getMessageDBHelper();
		userDBHelper = app.getUserDBHelper();
		List<AhMessage> lastChupaList = messageDBHelper.getLastChupas();
		
		lastChupaCommunList = convertToMap(lastChupaList);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_chupa_list, container, false);

		/*
		 * Set UI component
		 */
		squareChupaListView = (ListView)view.findViewById(R.id.square_chupa_list_frag_list);


		/*
		 * Set square chupa list view
		 */
		squareChupaListAdapter = new SquareChupaListAdapter(context, R.layout.row_square_chupa_list, lastChupaCommunList);
		squareChupaListView.setAdapter(squareChupaListAdapter);
		squareChupaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(activity, ChupaChatActivity.class);
				intent.putExtra(AhGlobalVariable.USER_KEY, userDBHelper.getUser(lastChupaCommunList.get(position).get("senderId")));
				startActivity(intent);
			}
		});

		return view;
	}


	@Override
	public void onResume() {
		super.onResume();
		updateList();
	}


	private List<Map<String, String>> convertToMap(List<AhMessage> lastChupaList) {
		List<Map<String,String>> list = new ArrayList<Map<String, String>>();
		for(AhMessage message : lastChupaList){
			Map<String, String> map = new HashMap<String, String>();
			User user = userDBHelper.getUser(message.getSenderId());
			if (user == null) user = userDBHelper.getUser(message.getReceiverId());
			if( user != null){
				map.put("profilePic", user.getProfilePic());
				map.put("sender", message.getSender());
				map.put("senderId", message.getSenderId());
				map.put("receiver", message.getReceiver());
				map.put("receiverId", message.getReceiverId());
				map.put("content", message.getContent());
				map.put("timeStamp", message.getTimeStamp());
				map.put("chupaCommunId", message.getChupaCommunId());
			} else {
				throw new AhException("convertToMap user NULL");
			}
			
			list.add(map);
		}
		return list;
	}


	public void updateList() {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				List<AhMessage> lastChupaList = messageDBHelper.getLastChupas();
				lastChupaCommunList = convertToMap(lastChupaList);
				squareChupaListAdapter = new SquareChupaListAdapter(context, R.layout.row_square_chupa_list, lastChupaCommunList);
				squareChupaListView.setAdapter(squareChupaListAdapter);
			}
		});
	}
}
