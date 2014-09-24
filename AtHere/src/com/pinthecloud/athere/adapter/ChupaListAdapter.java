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
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.database.UserDBHelper;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.model.AhUser;

public class ChupaListAdapter extends ArrayAdapter<Map<String,String>> {

	private Context context;
	private AhFragment fragment;
	private int layoutId;
	private CachedBlobStorageHelper blobStorageHelper;
	private UserDBHelper userDBHelper;

	public ChupaListAdapter(Context context, AhFragment frag, int layoutId) {
		super(context, layoutId);
		this.context = context;
		this.fragment = frag;
		this.layoutId = layoutId;

		AhApplication app = AhApplication.getInstance();
		this.blobStorageHelper = app.getBlobStorageHelper();
		this.userDBHelper = app.getUserDBHelper();
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
			final ImageView profileImage = (ImageView)view.findViewById(R.id.chupa_list_profile_pic);
			TextView sender = (TextView)view.findViewById(R.id.chupa_list_sender);
			TextView content = (TextView)view.findViewById(R.id.chupa_list_content);
			TextView timeStamp = (TextView)view.findViewById(R.id.chupa_list_timestamp);
			TextView badgeNum = (TextView)view.findViewById(R.id.chupa_list_badge_num);


			/*
			 * Set UI component
			 */
			String isExit = lastChupaMap.get("isExit");
			String userId = lastChupaMap.get("userId");
			AhUser user = userDBHelper.getUser(userId, true);
			String userNickName = user.getNickName();
			String chupaBadgeString = lastChupaMap.get("chupaBadge");
			int chupaBadge = Integer.parseInt(chupaBadgeString);
			String contentString = lastChupaMap.get("content");
			String time = lastChupaMap.get("timeStamp");
			String hour = time.substring(8, 10);
			String minute = time.substring(10, 12);

			if (isExit.equals("true")) {
				sender.setTextColor(context.getResources().getColor(R.color.gray_line));
			} else {
				sender.setTextColor(context.getResources().getColor(android.R.color.black));
			}
			sender.setText(userNickName);
			blobStorageHelper.setImageViewAsync(fragment, BlobStorageHelper.USER_PROFILE, 
					userId+AhGlobalVariable.SMALL, R.drawable.profile_default, profileImage, true);
			content.setText(contentString);
			timeStamp.setText(hour + ":" + minute);
			if (chupaBadge > 0) {
				badgeNum.setText(""+chupaBadge);
				badgeNum.setVisibility(View.VISIBLE);
			}else{
				badgeNum.setVisibility(View.INVISIBLE);
			}
		}

		return view;
	}
}