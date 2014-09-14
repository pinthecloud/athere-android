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
import android.widget.ImageView;
import android.widget.ListView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.adapter.SquareChupaListAdapter;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;

public class SquareChupaListFragment extends AhFragment{

	private SquareChupaListAdapter squareChupaListAdapter;
	private ListView squareChupaListView;
	private ImageView blankImage;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_chupa_list, container, false);

		/*
		 * Set UI component
		 */
		squareChupaListView = (ListView)view.findViewById(R.id.square_chupa_list_frag_list);
		blankImage = (ImageView)view.findViewById(R.id.square_chupa_list_frag_blank_image);


		/*
		 * Set square chupa list view
		 */
		squareChupaListAdapter = new SquareChupaListAdapter(context, thisFragment, R.layout.row_square_chupa_list);
		squareChupaListView.setAdapter(squareChupaListAdapter);
		squareChupaListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(activity, ChupaChatActivity.class);
				intent.putExtra(AhGlobalVariable.USER_KEY, squareChupaListAdapter.getItem(position).get("userId"));
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

			if (message.isMine()) {
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
			AhUser user = userDBHelper.getUser(userId);

			// check whether it is exited.
			if (userDBHelper.isUserExit(userId)) {
				user = userDBHelper.getUser(userId, true);
				isExit = "true";
			}

			profilePic = user.getProfilePic();
			content = message.getContent();
			timeStamp = message.getTimeStamp();
			chupaCommunId = message.getChupaCommunId();
			chupaBadge = "" + messageDBHelper.getChupaBadgeNum(message.getChupaCommunId());

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
