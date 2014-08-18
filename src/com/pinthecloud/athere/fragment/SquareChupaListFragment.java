package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;

public class SquareChupaListFragment extends AhFragment{

	private SquareChupaListAdapter squareChupaListAdapter;
	private ListView squareChupaListView;
	private List<Map<String,String>> lastChupaCommunList = new ArrayList<Map<String,String>>();

	public SquareChupaListFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
				intent.putExtra(AhGlobalVariable.USER_KEY, lastChupaCommunList.get(position).get("userId"));
				startActivity(intent);
			}
		});
		
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {
			
			@Override
			public void onCompleted(AhMessage entity) {
				// TODO Auto-generated method stub
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						refreshView();
					}
				});
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
		List<AhMessage> lastChupaList = messageDBHelper.getLastChupas();
		lastChupaCommunList.clear();
		lastChupaCommunList.addAll(convertToMap(lastChupaList));
		squareChupaListAdapter.notifyDataSetChanged();
	}

	private List<Map<String, String>> convertToMap(List<AhMessage> lastChupaList) {
		List<Map<String,String>> list = new ArrayList<Map<String, String>>();
		for(AhMessage message : lastChupaList){
			Map<String, String> map = new HashMap<String, String>();

			String profilePic = "";
			String userNickName = "";
			String userId = "";
			String content = "";
			String timeStamp = "";
			String chupaCommunId = "";
			String isExit = "false";
			String chupaBadge = "";
			if (pref.getString(AhGlobalVariable.USER_ID_KEY).equals(message.getSenderId())) {
				// the other user is Receiver
				userId = message.getReceiverId();
				userNickName = message.getReceiver();
			} else if (pref.getString(AhGlobalVariable.USER_ID_KEY).equals(message.getReceiverId())) {
				// the other user is Sender
				userId = message.getSenderId();
				userNickName = message.getSender();
			} else {
				throw new AhException("No User in Sender or Receive");
			}

			User user = userDBHelper.getUser(userId);

			// if there is No such User
			if (user == null) {
				// check whether it is exited.
				if (userDBHelper.isUserExit(userId)) {
					user = userDBHelper.getUser(userId, true);
					isExit = "true";
				} else {
					throw new AhException("No User in UserDBHelper");
				}
			}

			profilePic = user.getProfilePic();
			content = message.getContent();
			timeStamp = message.getTimeStamp();
			chupaCommunId = message.getChupaCommunId();
			chupaBadge = ""+messageDBHelper.getBadgeNum(message.getChupaCommunId());
			map.put("profilePic", profilePic);
			map.put("userNickName", userNickName);
			map.put("userId", userId);
			map.put("content", content);
			map.put("timeStamp", timeStamp);
			map.put("chupaCommunId", chupaCommunId);
			map.put("isExit", isExit);
			map.put("chupaBadge", chupaBadge);

			list.add(map);
		}
		return list;
	}
}
