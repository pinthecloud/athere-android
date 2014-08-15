package com.pinthecloud.athere.adapter;

import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.activity.ProfileImageActivity;
import com.pinthecloud.athere.dialog.ProfileDialog;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.util.BitmapUtil;

public class SquareDrawerParticipantListAdapter extends ArrayAdapter<User> {

	private Context context;
	private Fragment fragment;
	private int layoutId;
	private List<User> items;


	public SquareDrawerParticipantListAdapter(Context context, Fragment fragment, int layoutId, List<User> items) {
		super(context, layoutId, items);
		this.context = context;
		this.fragment = fragment;
		this.layoutId = layoutId;
		this.items = items;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(this.layoutId, parent, false);
		}

		final User user = items.get(position);
		if (user != null) {
			/*
			 * Find UI Component
			 */
			ImageView profilePic = (ImageView)view.findViewById(R.id.drawer_user_pro_pic);
			TextView nickName = (TextView)view.findViewById(R.id.drawer_user_nick_name);
			ImageView isMale = (ImageView)view.findViewById(R.id.drawer_user_is_male);
			ImageButton chupaButton = (ImageButton)view.findViewById(R.id.drawer_user_chupa_btn);


			/*
			 * Set UI component
			 */
			nickName.setText(user.getNickName());
			if(user.isMale()){
				isMale.setImageResource(R.drawable.sidebar_member_gender_m);
			}else{
				isMale.setImageResource(R.drawable.sidebar_member_gender_w);
			}
			profilePic.setImageBitmap(BitmapUtil.cropRound(BitmapUtil.convertToBitmap(user.getProfilePic())));
			profilePic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ProfileDialog profileDialog = new ProfileDialog(user, new AhDialogCallback() {

						@Override
						public void doPositiveThing(Bundle bundle) {
							Intent intent = new Intent(context, ChupaChatActivity.class);
							intent.putExtra(AhGlobalVariable.USER_KEY, user.getId());
							context.startActivity(intent);
						}
						@Override
						public void doNegativeThing(Bundle bundle) {
							Intent intent = new Intent(context, ProfileImageActivity.class);
							intent.putExtra(AhGlobalVariable.USER_KEY, user.getId());
							context.startActivity(intent);
						}
					});
					profileDialog.show(fragment.getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
				}
			});


			/*
			 * Set event on button
			 */
			chupaButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ChupaChatActivity.class);
					intent.putExtra(AhGlobalVariable.USER_KEY, user.getId());
					context.startActivity(intent);
				}
			});
		}
		return view;
	}
}
