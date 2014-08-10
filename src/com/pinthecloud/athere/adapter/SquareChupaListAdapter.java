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
import com.pinthecloud.athere.util.BitmapUtil;

public class SquareChupaListAdapter extends ArrayAdapter<Map<String,String>> {

	private Context context;
	private int layoutId;
	private List<Map<String,String>> items;


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
		} 

		Map<String,String> lastChupaMap = items.get(position);
		
		if (lastChupaMap != null) {
			/*
			 * Find UI component
			 */
			ImageView profilePic = (ImageView)view.findViewById(R.id.square_chupa_list_profile_pic);
			TextView sender = (TextView)view.findViewById(R.id.square_chupa_list_sender);
			TextView content = (TextView)view.findViewById(R.id.square_chupa_list_content);
			TextView timeStamp = (TextView)view.findViewById(R.id.square_chupa_list_timestamp);
			TextView badgeNum = (TextView)view.findViewById(R.id.square_chupa_list_badge_num);
			
			/*
			 * Set UI component
			 */
			String isExit = lastChupaMap.get("isExit");
			String userNickName = lastChupaMap.get("userNickName");
			if (isExit.equals("true")) {
				userNickName += "has been Exit";
			}
			String picStr = lastChupaMap.get("profilePic");
			profilePic.setImageBitmap(BitmapUtil.convertToBitmap(picStr));
			sender.setText(userNickName);
			content.setText(lastChupaMap.get("content"));
			timeStamp.setText(lastChupaMap.get("timeStamp"));
			String chupaBadge = lastChupaMap.get("chupaBadge");
			if (chupaBadge.equals("0")) {
				chupaBadge = "";
				badgeNum.setVisibility(View.GONE);
			}
			badgeNum.setText(chupaBadge);
			
		}

		return view;
	}
}
