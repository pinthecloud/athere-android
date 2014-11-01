package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ProfileImageActivity;
import com.pinthecloud.athere.activity.ProfileSettingsActivity;
import com.pinthecloud.athere.adapter.AppDrawerListAdapter;
import com.pinthecloud.athere.dialog.ProfileDialog;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.AppDrawerListItem;
import com.pinthecloud.athere.view.DividerItemDecoration;

public class AppDrawerFragment extends AhFragment{

	private ImageView profileImageView;
	private TextView nickNameText;
	private TextView ageText;
	private TextView genderText;
	private Button profileSettingsButton;

	private RecyclerView appDrawerListView;
	private AppDrawerListAdapter appDrawerListAdapter;
	private RecyclerView.LayoutManager appDrawerListLayoutManager;
	private List<AppDrawerListItem> appDrawerList;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_app_drawer, container, false);
		findComponent(view);
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		AhUser user = userHelper.getMyUserInfo();
		setComponent(user);
		setAppDrawerList(user);
		setAppDrawerListItem();
		setButtonEvent(user);
	}


	@Override
	public void onStop() {
		profileImageView.setImageBitmap(null);
		super.onStop();
	}


	private void findComponent(View view){
		profileImageView = (ImageView) view.findViewById(R.id.app_drawer_frag_profile_image);
		nickNameText = (TextView) view.findViewById(R.id.app_drawer_frag_nick_name);
		ageText = (TextView) view.findViewById(R.id.app_drawer_frag_age);
		genderText = (TextView) view.findViewById(R.id.app_drawer_frag_gender);
		profileSettingsButton = (Button) view.findViewById(R.id.app_drawer_frag_profile_settings_button);
		appDrawerListView = (RecyclerView) view.findViewById(R.id.app_drawer_frag_list);
	}


	private void setComponent(AhUser user){
		blobStorageHelper.setImageViewAsync(thisFragment, BlobStorageHelper.USER_PROFILE, 
				user.getId()+AhGlobalVariable.SMALL, R.drawable.profile_edit_profile_default_ico, profileImageView, true);
		nickNameText.setText(user.getNickName());
		ageText.setText(""+user.getAge());
		genderText.setText(user.getGenderString(context));
		if(user.isMale()){
			genderText.setTextColor(getResources().getColor(R.color.blue_man));
		}else{
			genderText.setTextColor(getResources().getColor(R.color.brand_red_color_dark));
		}
	}


	private void setButtonEvent(final AhUser user){
		profileImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ProfileDialog profileDialog = new ProfileDialog(thisFragment, user, new AhDialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						// Do noghing
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
						Intent intent = new Intent(context, ProfileImageActivity.class);
						intent.putExtra(AhGlobalVariable.USER_KEY, user);
						context.startActivity(intent);
					}
				});
				profileDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			}
		});
		profileSettingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gaHelper.sendEventGA(
						thisFragment.getClass().getSimpleName(),
						"DrawerProfileSetting",
						"ProfileSetting");

				Intent intent = new Intent(context, ProfileSettingsActivity.class);
				intent.putExtra(AhGlobalVariable.USER_KEY, user);
				startActivity(intent);
			}
		});
	}


	private void setAppDrawerList(AhUser user){
		appDrawerListView.setHasFixedSize(true);
		appDrawerListView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));

		appDrawerListLayoutManager = new LinearLayoutManager(context);
		appDrawerListView.setLayoutManager(appDrawerListLayoutManager);

		appDrawerList = new ArrayList<AppDrawerListItem>();
		appDrawerListAdapter = new AppDrawerListAdapter(context, thisFragment, user, appDrawerList);
		appDrawerListView.setAdapter(appDrawerListAdapter);
	}


	private void setAppDrawerListItem(){
		ArrayList<AppDrawerListItem> items = new ArrayList<AppDrawerListItem>();
		items.add(new AppDrawerListItem(R.drawable.drawer_setting_ico, getResources().getString(R.string.app_settings)));
		items.add(new AppDrawerListItem(R.drawable.drawer_share_ico, getResources().getString(R.string.share)));
		items.add(new AppDrawerListItem(R.drawable.drawer_request_ico, getResources().getString(R.string.question)));
		appDrawerList.clear();
		appDrawerList.addAll(items);
		appDrawerListAdapter.notifyDataSetChanged();
	}
}
