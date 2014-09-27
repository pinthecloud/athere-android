package com.pinthecloud.athere.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.AppDrawerListAdapter;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.AppDrawerListItem;

public class AppDrawerFragment extends AhFragment{

	private ImageView profileImageView;
	private TextView nickNameText;
	private TextView ageText;
	private TextView genderText;

	private ListView list;
	private AppDrawerListAdapter appDrawerListAdapter; 
	
	private AhUser user;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_app_drawer, container, false);


		/*
		 * Find UI component
		 */
		profileImageView = (ImageView) view.findViewById(R.id.app_drawer_frag_profile_image);
		nickNameText = (TextView) view.findViewById(R.id.app_drawer_frag_nick_name);
		ageText = (TextView) view.findViewById(R.id.app_drawer_frag_age);
		genderText = (TextView) view.findViewById(R.id.app_drawer_frag_gender);
		list = (ListView) view.findViewById(R.id.app_drawer_frag_list);
		user = userHelper.getMyUserInfo();


		/*
		 * Set UI
		 */
		nickNameText.setText(user.getNickName());
		ageText.setText(""+user.getAge());
		genderText.setText(user.getGenderString(context));


		/*
		 * Set list 
		 */
		appDrawerListAdapter = new AppDrawerListAdapter(context);
		list.setAdapter(appDrawerListAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO
			}
		});
		
		
		/*
		 * Set list item
		 */
		ArrayList<AppDrawerListItem> items = new ArrayList<AppDrawerListItem>();
		items.add(new AppDrawerListItem(R.drawable.tabbar_chat_text, "content1", "badge1"));
		items.add(new AppDrawerListItem(R.drawable.tabbar_chupa_text, "content2", "badge2"));
		appDrawerListAdapter.addAll(items);
		
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		blobStorageHelper.setImageViewAsync(thisFragment, BlobStorageHelper.USER_PROFILE, 
				user.getId()+AhGlobalVariable.SMALL, R.drawable.profile_default, profileImageView, true);
	}


	@Override
	public void onStop() {
		profileImageView.setImageBitmap(null);
		super.onStop();
	}
}
