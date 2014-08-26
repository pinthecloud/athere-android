package com.pinthecloud.athere.adapter;

import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;

public class SquareChupaListAdapter extends ArrayAdapter<Map<String,String>> {

	private Context context;
	private AhFragment fragment;
	private int layoutId;
	private CachedBlobStorageHelper blobStorageHelper;

	public SquareChupaListAdapter(Context context, AhFragment frag, int layoutId) {
		super(context, layoutId);
		this.context = context;
		this.fragment = frag;
		this.layoutId = layoutId;
		
		AhApplication app = AhApplication.getInstance();
		this.blobStorageHelper = app.getBlobStorageHelper();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(this.layoutId, parent, false);
		}

		Map<String,String> lastChupaMap = getItem(position);
		if (lastChupaMap != null) {
			/*
			 * Find UI component
			 */
			final ImageView profileImage = (ImageView)view.findViewById(R.id.square_chupa_list_profile_pic);
			TextView sender = (TextView)view.findViewById(R.id.square_chupa_list_sender);
			TextView content = (TextView)view.findViewById(R.id.square_chupa_list_content);
			TextView timeStamp = (TextView)view.findViewById(R.id.square_chupa_list_timestamp);
			TextView badgeNum = (TextView)view.findViewById(R.id.square_chupa_list_badge_num);


			/*
			 * Set UI component
			 */
			String isExit = lastChupaMap.get("isExit");
			String userNickName = lastChupaMap.get("userNickName");
			String userId = lastChupaMap.get("userId");
			//			String picStr = lastChupaMap.get("profilePic");
			String chupaBadge = lastChupaMap.get("chupaBadge");

			if (isExit.equals("true")) {
				sender.setTextColor(context.getResources().getColor(R.color.gray_line));
			}
			sender.setText(userNickName);
			blobStorageHelper.setImageViewAsync(fragment, userId, profileImage);

			content.setText(lastChupaMap.get("content"));
			String time = lastChupaMap.get("timeStamp");
			String hour = time.substring(8, 10);
			String minute = time.substring(10, 12);
			timeStamp.setText(hour + ":" + minute);
			if (!chupaBadge.equals("0")) {
				badgeNum.setText(chupaBadge);
				badgeNum.setVisibility(View.VISIBLE);
			}else{
				badgeNum.setVisibility(View.INVISIBLE);
			}
		}
		
		return view;
	}
}
