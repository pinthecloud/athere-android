package com.pinthecloud.athere.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ProfileImageActivity;
import com.pinthecloud.athere.activity.ProfileSettingsActivity;
import com.pinthecloud.athere.activity.SettingsActivity;
import com.pinthecloud.athere.adapter.AppDrawerListAdapter;
import com.pinthecloud.athere.dialog.ProfileDialog;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.AppDrawerListItem;

public class AppDrawerFragment extends AhFragment{

	private final int SETTINGS = 0;
	private final int SHARE = 1;
	private final int QUESTION = 2;

	private ImageView profileImageView;
	private TextView nickNameText;
	private TextView ageText;
	private TextView genderText;
	private Button profileSettingsButton;

	private ListView list;
	private AppDrawerListAdapter appDrawerListAdapter; 

	private AhUser user;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_app_drawer, container, false);

		findComponent(view);
		
		
		/*
		 * Set image view event
		 */
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


		/*
		 * Set event on button 
		 */
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


		/*
		 * Set list 
		 */
		appDrawerListAdapter = new AppDrawerListAdapter(context);
		list.setAdapter(appDrawerListAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				switch(position){
				case QUESTION:
					gaHelper.sendEventGA(
							thisFragment.getClass().getSimpleName(),
							"Question",
							"DrawerQuestion");
					intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:pinthecloud@gmail.com"));
					startActivity(intent);
					break;
				case SHARE:
					gaHelper.sendEventGA(
							thisFragment.getClass().getSimpleName(),
							"Share",
							"DrawerShare");
					intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.share_sentence));
					intent.setType("text/plain");
					startActivity(Intent.createChooser(intent, getResources().getText(R.string.share_to)));
					break;
				case SETTINGS:
					gaHelper.sendEventGA(
							thisFragment.getClass().getSimpleName(),
							"Settings",
							"DrawerSettings");
					intent = new Intent(context, SettingsActivity.class);
					intent.putExtra(AhGlobalVariable.USER_KEY, user);
					startActivity(intent);
					break;
				}
			}
		});


		/*
		 * Set list item
		 */
		ArrayList<AppDrawerListItem> items = new ArrayList<AppDrawerListItem>();
		items.add(new AppDrawerListItem(R.drawable.drawer_setting_ico, getResources().getString(R.string.app_settings)));
		items.add(new AppDrawerListItem(R.drawable.drawer_share_ico, getResources().getString(R.string.share)));
		items.add(new AppDrawerListItem(R.drawable.drawer_request_ico, getResources().getString(R.string.question)));
		appDrawerListAdapter.addAll(items);

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		user = userHelper.getMyUserInfo();


		/*
		 * Set UI
		 */
		blobStorageHelper.setImageViewAsync(thisFragment, BlobStorageHelper.USER_PROFILE, 
				user.getId()+AhGlobalVariable.SMALL, R.drawable.profile_edit_profile_default_ico, profileImageView, true);
		nickNameText.setText(user.getNickName());
		ageText.setText(""+user.getAge());
		genderText.setText(user.getGenderString(context));
		if(user.isMale()){
			genderText.setTextColor(getResources().getColor(R.color.blue_man));
		}else{
			genderText.setTextColor(getResources().getColor(R.color.red_woman));
		}
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
		list = (ListView) view.findViewById(R.id.app_drawer_frag_list);
	}
}
