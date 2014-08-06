package com.pinthecloud.athere.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.model.AhMessage;
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
	private Button chupaBtn;

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

		final User user = items.get(position);
		if (user != null) {
			// TODO
			profilePic = (ImageView)view.findViewById(R.id.drawer_user_pro_pic);
			nickName = (TextView)view.findViewById(R.id.drawer_user_nick_name);
			age = (TextView)view.findViewById(R.id.drawer_user_age);
			companyNum = (TextView)view.findViewById(R.id.drawer_user_company_num);
			isMale = (TextView)view.findViewById(R.id.drawer_user_is_male);
			chupaBtn = (Button)view.findViewById(R.id.drawer_user_chupa_btn);
			
			profilePic.setImageBitmap(BitmapUtil.convertToBitmap(user.getProfilePic()));
			nickName.setText(user.getNickName());
			age.setText(""+user.getAge());
			companyNum.setText(""+user.getCompanyNum());
			isMale.setText(user.isMale() ? "M" : "F");
			
			chupaBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context.getApplicationContext(), ChupaChatActivity.class);
					
					intent.putExtra("user", user);
					context.startActivity(intent);
				}
			});
		}
		return view;
	}
}
