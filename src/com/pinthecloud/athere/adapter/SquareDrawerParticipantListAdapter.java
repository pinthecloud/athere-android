package com.pinthecloud.athere.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.util.BitmapUtil;

public class SquareDrawerParticipantListAdapter extends ArrayAdapter<User> {

	private Context context;
	private int layoutId;
	private List<User> items;
	private ImageView profilePic;
	private TextView nickName;
	private TextView age;
	private TextView companyNum;
	private TextView isMale;

	public SquareDrawerParticipantListAdapter(Context context, 
			int layoutId, List<User> items) {
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

		User user = items.get(position);
		if (user != null) {
			// TODO
			profilePic = (ImageView)view.findViewById(R.id.drawer_user_pro_pic);
			nickName = (TextView)view.findViewById(R.id.drawer_user_nick_name);
			age = (TextView)view.findViewById(R.id.drawer_user_age);
			companyNum = (TextView)view.findViewById(R.id.drawer_user_company_num);
			isMale = (TextView)view.findViewById(R.id.drawer_user_is_male);
			
			profilePic.setImageBitmap(BitmapUtil.convertToBitmap(user.getProfilePic()));
			nickName.setText(user.getNickName());
			age.setText(""+user.getAge());
			companyNum.setText(""+user.getCompanyNum());
			isMale.setText(user.isMale() ? "M" : "F");
		}
		return view;
	}
}
