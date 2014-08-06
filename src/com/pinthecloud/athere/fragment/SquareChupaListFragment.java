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

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.adapter.SquareChupaListAdapter;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;

public class SquareChupaListFragment extends AhFragment{

	private Square square;

	private SquareChupaListAdapter squareChupaListAdapter;
	private ListView squareChupaListView;

	private List<Map<String,String>> lastChupaCommunList;
	private MessageDBHelper messageDBHelper;
	private UserDBHelper userDBHelper;

	public SquareChupaListFragment(Square square) {
		super();
		this.square = square;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		messageDBHelper = app.getMessageDBHelper();
		userDBHelper = app.getUserDBHelper();
		List<AhMessage> lastChupaList = messageDBHelper.getLastChupas();
		lastChupaCommunList = convertToMap(lastChupaList);
	}

	private List<Map<String, String>> convertToMap(List<AhMessage> lastChupaList) {
		// TODO Auto-generated method stub
		List<Map<String,String>> list = new ArrayList<Map<String, String>>();
		for(AhMessage message : lastChupaList){
			Map<String, String> map = new HashMap<String, String>();
			User user = userDBHelper.getUser(message.getSenderId());
			map.put("profilePic", user.getProfilePic());
			map.put("sender", message.getSender());
			map.put("senderId", message.getSenderId());
			map.put("receiver", message.getReceiver());
			map.put("receiverId", message.getReceiverId());
			map.put("content", message.getContent());
			map.put("timeStamp", message.getTimeStamp());
			map.put("chupaCommunId", message.getChupaCommunId());
			
			list.add(map);
		}
		
		return list;
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
				Intent i = new Intent(getActivity(), ChupaChatActivity.class);
				i.putExtra("user", userDBHelper.getUser(lastChupaCommunList.get(position).get("senderId")));
				
				startActivity(i);
			}
		});
		getChupas();

		return view;
	}


	/*
	 * Get square near from user
	 * Now it just gets all squares cause of location law. (lati and longi is 0)
	 */
	private void getChupas(){
	}
}
