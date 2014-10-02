package com.pinthecloud.athere.adapter;

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
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.model.AhUser;

public class MemberGridAdapter extends ArrayAdapter<AhUser>{

	private Context context;
	private AhFragment frag;
	private UserHelper userHelper;
	private CachedBlobStorageHelper blobStorageHelper;


	public MemberGridAdapter(Context context, AhFragment frag) {
		super(context, 0);
		this.context = context;
		this.frag = frag;

		AhApplication app = AhApplication.getInstance();
		this.userHelper = app.getUserHelper();
		this.blobStorageHelper = app.getBlobStorageHelper();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_member_grid, parent, false);
		}

		AhUser user = getItem(position);
		if (user != null) {
			/*
			 * Find UI component
			 */
			ImageView profileImage = (ImageView)view.findViewById(R.id.row_member_grid_profile_image);
			ImageView myProfile = (ImageView)view.findViewById(R.id.row_member_grid_my_profile);
			TextView nickName = (TextView)view.findViewById(R.id.row_member_grid_nick_name);
			ImageView gender = (ImageView)view.findViewById(R.id.row_member_grid_gender);


			/*
			 * Set UI component
			 */
			blobStorageHelper.setImageViewAsync(frag, BlobStorageHelper.USER_PROFILE,
					user.getId(), R.drawable.launcher, profileImage, true);
			nickName.setText(user.getNickName());
			if(userHelper.isMyUser(user)){
				myProfile.setVisibility(View.VISIBLE);
			}else{
				myProfile.setVisibility(View.GONE);
			}
			if(user.isMale()){
				gender.setImageResource(R.drawable.general_gender_m);
			}else{
				gender.setImageResource(R.drawable.general_gender_w);
			}

		}

		return view;
	}
}
