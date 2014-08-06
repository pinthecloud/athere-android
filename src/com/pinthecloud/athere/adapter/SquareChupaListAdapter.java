package com.pinthecloud.athere.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.util.BitmapUtil;

public class SquareChupaListAdapter extends ArrayAdapter<Map<String,String>> {

	// TODO change ahmessage to chupa

	private Context context;
	private int layoutId;
	private List<Map<String,String>> items;

	private ImageView profilePic;
	private TextView sender;
	private TextView content;
	private TextView timeStamp;
	
	public SquareChupaListAdapter(Context context, int layoutId, List<Map<String,String>> items) {
		super(context, layoutId, items);
		this.context = context;
		this.layoutId = layoutId;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(this.layoutId, parent, false);
			
			profilePic = (ImageView)view.findViewById(R.id.square_chupa_list_profile_pic);
			sender = (TextView)view.findViewById(R.id.square_chupa_list_sender);
			content = (TextView)view.findViewById(R.id.square_chupa_list_content);
			timeStamp = (TextView)view.findViewById(R.id.square_chupa_list_timestamp);
			
		} else return view;

		Map<String,String> lastChupaMap = items.get(position);
		if (lastChupaMap != null) {
			
//			map.put("profilePic", user.getProfilePic());
//			map.put("sender", message.getSender());
//			map.put("senderId", message.getSenderId());
//			map.put("receiver", message.getReceiver());
//			map.put("receiverId", message.getReceiverId());
//			map.put("content", message.getContent());
//			map.put("timeStamp", message.getTimeStamp());
//			map.put("chupaCommunId", message.getChupaCommunId());
			
			String picStr = lastChupaMap.get("profilePic");
			profilePic.setImageBitmap(BitmapUtil.convertToBitmap(picStr));
			sender.setText(lastChupaMap.get("sender"));
			content.setText(lastChupaMap.get("content"));
			timeStamp.setText(lastChupaMap.get("timeStamp"));
		}
		
		return view;
	}
}
