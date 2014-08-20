package com.pinthecloud.athere.adapter;

import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.util.BitmapUtil;
import com.pinthecloud.athere.util.FileUtil;

public class SquareDrawerParticipantListAdapter extends ArrayAdapter<AhUser> {

	private Context context;
	private int layoutId;
	private List<AhUser> items;


	public SquareDrawerParticipantListAdapter(Context context, Fragment fragment, int layoutId, List<AhUser> items) {
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

		final AhUser user = items.get(position);
		if (user != null) {
			/*
			 * Find UI Component
			 */
			ImageView profileImage = (ImageView)view.findViewById(R.id.drawer_user_pro_pic);
			TextView nickName = (TextView)view.findViewById(R.id.drawer_user_nick_name);
			ImageView gender = (ImageView)view.findViewById(R.id.drawer_user_gender);
			ImageButton chupaButton = (ImageButton)view.findViewById(R.id.drawer_user_chupa_btn);
			TextView companyNumber = (TextView) view.findViewById(R.id.drawer_user_company_num);

			/*
			 * Set UI component
			 */
			nickName.setText(user.getNickName());
			companyNumber.setText("" + user.getCompanyNum());
			Resources resources = context.getResources();
			if(user.isMale()){
				gender.setImageResource(R.drawable.profile_gender_m);
				companyNumber.setTextColor(resources.getColor(R.color.blue));
			}else{
				gender.setImageResource(R.drawable.profile_gender_w);
				companyNumber.setTextColor(resources.getColor(R.color.dark_red));
			}
			int w = profileImage.getWidth();
			int h = profileImage.getHeight();
//			Bitmap profileBitmap = BitmapUtil.convertToBitmap(user.getProfilePic(), w, h);
			Bitmap profileBitmap = FileUtil.getImageFromInternalStorage(context, user.getProfilePic(), w, h);
			profileImage.setImageBitmap(profileBitmap);


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
