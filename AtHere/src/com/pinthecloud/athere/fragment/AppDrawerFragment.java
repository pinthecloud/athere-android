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
import com.pinthecloud.athere.activity.ChupaChatActivity;
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

	private final int QUESTION = 0;
	private final int SHARE = 1;
	private final int SETTINGS = 2;

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
		user = userHelper.getMyUserInfo();


		/*
		 * Find UI component
		 */
		profileImageView = (ImageView) view.findViewById(R.id.app_drawer_frag_profile_image);
		nickNameText = (TextView) view.findViewById(R.id.app_drawer_frag_nick_name);
		ageText = (TextView) view.findViewById(R.id.app_drawer_frag_age);
		genderText = (TextView) view.findViewById(R.id.app_drawer_frag_gender);
		profileSettingsButton = (Button) view.findViewById(R.id.app_drawer_frag_profile_settings_button);
		list = (ListView) view.findViewById(R.id.app_drawer_frag_list);


		/*
		 * Set UI
		 */
		nickNameText.setText(user.getNickName());
		ageText.setText(""+user.getAge());
		genderText.setText(user.getGenderString(context));
		if(user.isMale()){
			genderText.setTextColor(getResources().getColor(R.color.blue_man));
		}else{
			genderText.setTextColor(getResources().getColor(R.color.red_woman));
		}


		/*
		 * Set image view event
		 */
		profileImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ProfileDialog profileDialog = new ProfileDialog(thisFragment, user, new AhDialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						Intent intent = new Intent(context, ChupaChatActivity.class);
						intent.putExtra(AhGlobalVariable.USER_KEY, user.getId());
						context.startActivity(intent);
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
				Intent intent = new Intent(context, ProfileSettingsActivity.class);
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
					intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:pinthecloud@gmail.com"));
					startActivity(intent);
					break;
				case SHARE:
					intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.share_sentence));
					intent.setType("text/plain");
					startActivity(Intent.createChooser(intent, getResources().getText(R.string.share_to)));
					break;
				case SETTINGS:
					intent = new Intent(context, SettingsActivity.class);
					startActivity(intent);
					break;
				}
			}
		});


		/*
		 * Set list item
		 */
		ArrayList<AppDrawerListItem> items = new ArrayList<AppDrawerListItem>();
		items.add(new AppDrawerListItem(R.drawable.drawer_request_ico, getResources().getString(R.string.question)));
		items.add(new AppDrawerListItem(R.drawable.drawer_share_ico, getResources().getString(R.string.share)));
		items.add(new AppDrawerListItem(R.drawable.drawer_setting_ico, getResources().getString(R.string.app_settings)));
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
