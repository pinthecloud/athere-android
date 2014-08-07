package com.pinthecloud.athere.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.util.BitmapUtil;

public class SquareDrawerParticipantListAdapter extends ArrayAdapter<User> {

	private Context context;
	private int layoutId;
	private List<User> items;

	private ImageView profilePic;
	private TextView nickName;
	private ImageView isMale;
	private ImageButton chupaBtn;


	public SquareDrawerParticipantListAdapter(Context context, int layoutId, List<User> items) {
		super(context, layoutId, items);
		this.context = context;
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
			profilePic = (ImageView)view.findViewById(R.id.drawer_user_pro_pic);
			nickName = (TextView)view.findViewById(R.id.drawer_user_nick_name);
			isMale = (ImageView)view.findViewById(R.id.drawer_user_is_male);
			chupaBtn = (ImageButton)view.findViewById(R.id.drawer_user_chupa_btn);

			/*
			 * Set image
			 */
			profilePic.setImageBitmap(BitmapUtil.convertToBitmap(user.getProfilePic()));
			nickName.setText(user.getNickName());
			if(user.isMale()){
				isMale.setImageResource(R.drawable.sidebar_member_gender_m);
			}else{
				isMale.setImageResource(R.drawable.sidebar_member_gender_w);
			}

			/*
			 * Set event on button
			 */
			chupaBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ChupaChatActivity.class);
					intent.putExtra(AhGlobalVariable.USER_KEY, user);
					context.startActivity(intent);
				}
			});
		}
		return view;
	}
}
