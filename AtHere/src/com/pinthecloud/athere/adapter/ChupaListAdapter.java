package com.pinthecloud.athere.adapter;

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
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.model.Chupa;

public class ChupaListAdapter extends ArrayAdapter<Chupa> {

	private Context context;
	private AhFragment fragment;
	private CachedBlobStorageHelper blobStorageHelper;


	public ChupaListAdapter(Context context, AhFragment frag) {
		super(context, 0);
		this.context = context;
		this.fragment = frag;

		AhApplication app = AhApplication.getInstance();
		this.blobStorageHelper = app.getBlobStorageHelper();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_chupa_list, parent, false);
		}

		Chupa chupa = getItem(position);
		if (chupa != null) {
			/*
			 * Find UI component
			 */
			ImageView profileImage = (ImageView)view.findViewById(R.id.chupa_list_profile_image);
			TextView sender = (TextView)view.findViewById(R.id.chupa_list_sender);
			TextView content = (TextView)view.findViewById(R.id.chupa_list_content);
			TextView timeStamp = (TextView)view.findViewById(R.id.chupa_list_timestamp);
			TextView badgeNum = (TextView)view.findViewById(R.id.chupa_list_badge_num);


			/*
			 * Set UI component
			 */
			boolean isExit = chupa.isExit();
			String userId = chupa.getUserId();
			String userNickName = chupa.getUserNickName();
			int chupaBadge = chupa.getBadgeNum();
			String contentString = chupa.getContent();
			String time = chupa.getTimeStamp();
			String hour = time.substring(8, 10);
			String minute = time.substring(10, 12);

			blobStorageHelper.setImageViewAsync(fragment, BlobStorageHelper.USER_PROFILE, 
					userId+AhGlobalVariable.SMALL, R.drawable.profile_default, profileImage, true);

			sender.setText(userNickName);
			content.setText(contentString);
			timeStamp.setText(hour + ":" + minute);

			if(isExit) {
				sender.setTextColor(context.getResources().getColor(R.color.chupa_list_time));
				content.setTextColor(context.getResources().getColor(R.color.chupa_list_time));
			} else {
				sender.setTextColor(context.getResources().getColor(R.color.chupa_list_text));
				content.setTextColor(context.getResources().getColor(R.color.chupa_list_text));
			}

			if(chupaBadge > 0) {
				badgeNum.setText(""+chupaBadge);
				badgeNum.setVisibility(View.VISIBLE);
			}else{
				badgeNum.setVisibility(View.INVISIBLE);
			}
		}

		return view;
	}
}