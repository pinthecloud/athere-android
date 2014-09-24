package com.pinthecloud.athere.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.activity.ProfileImageActivity;
import com.pinthecloud.athere.activity.ProfileSettingsActivity;
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.adapter.SquareDrawerParticipantListAdapter;
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.dialog.ProfileDialog;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhUser;

public class SquareDrawerFragment extends AhFragment {

	private ProgressBar progressBar;
	private ToggleButton chatAlarmButton;
	private ToggleButton chupaAlarmButton;
	private RelativeLayout profileLayout;
	private ImageView profileImage;
	private ImageView profileGenderImage;
	private TextView profileNickNameText;
	private TextView profileCompanyNumText;
	private ImageButton profileSettingsButton;
	private TextView maleNumText;
	private TextView femaleNumText;
	private Button exitButton;

	private ListView participantListView;
	private SquareDrawerParticipantListAdapter participantListAdapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_square_drawer, container, false);
		gaHelper.sendTimeingGA(
				thisFragment.getClass().getSimpleName(),
				"DrawerTime",
				"getDrawerTime");


		/*
		 * Set Ui Component
		 */
		progressBar = (ProgressBar) view.findViewById(R.id.square_drawer_frag_progress_bar);
		chatAlarmButton = (ToggleButton) view.findViewById(R.id.square_drawer_frag_chat_alarm_button);
		chupaAlarmButton = (ToggleButton) view.findViewById(R.id.square_drawer_frag_chupa_alarm_button);
		profileLayout = (RelativeLayout) view.findViewById(R.id.square_drawer_frag_profile_layout);
		profileImage = (ImageView) view.findViewById(R.id.square_drawer_frag_profile_image);
		profileGenderImage = (ImageView) view.findViewById(R.id.square_drawer_frag_profile_gender);
		profileNickNameText= (TextView) view.findViewById(R.id.square_drawer_frag_profile_nick_name);
		profileCompanyNumText = (TextView) view.findViewById(R.id.square_drawer_frag_profile_company_num);
		profileSettingsButton = (ImageButton) view.findViewById(R.id.square_drawer_frag_profile_bar_settings);
		participantListView = (ListView) view.findViewById(R.id.square_drawer_frag_participant_list);
		maleNumText = (TextView) view.findViewById(R.id.square_drawer_frag_member_male_text);
		femaleNumText = (TextView) view.findViewById(R.id.square_drawer_frag_member_female_text);
		exitButton = (Button) view.findViewById(R.id.square_drawer_frag_exit_button);


		/*
		 * Set user list
		 */
		participantListAdapter = new SquareDrawerParticipantListAdapter
				(context, thisFragment, R.layout.row_square_drawer_participant_list);
		participantListView.setAdapter(participantListAdapter);
		participantListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				gaHelper.sendEventGA(
						thisFragment.getClass().getSimpleName(),
						"ViewOthersProfile",
						"DrawerProfile");

				final AhUser user = participantListAdapter.getItem(position);
				showProfileDialog(user);
			}
		});


		/*
		 * Set general buttons
		 */
		chatAlarmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isChecked = chatAlarmButton.isChecked();
				pref.putBoolean(AhGlobalVariable.IS_CHAT_ENABLE_KEY, isChecked);
			}
		});
		chupaAlarmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				progressBar.setVisibility(View.VISIBLE);
				progressBar.bringToFront();
				chupaAlarmButton.setEnabled(false);

				boolean isChecked = chupaAlarmButton.isChecked();
				pref.putBoolean(AhGlobalVariable.IS_CHUPA_ENABLE_KEY, isChecked);
				userHelper.updateMyUserAsync(thisFragment, new AhEntityCallback<AhUser>() {

					@Override
					public void onCompleted(AhUser entity) {
						progressBar.setVisibility(View.GONE);
						chupaAlarmButton.setEnabled(true);
					}
				});
			}
		});
		profileSettingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ProfileSettingsActivity.class);
				startActivity(intent);
			}
		});
		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = getResources().getString(R.string.exit_square_consent_message);
				AhAlertDialog escDialog = new AhAlertDialog(null, message, true, new AhDialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						exitSquare();
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
						// Do nothing
					}
				});
				escDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			}
		});


		/*
		 * Set handler for refresh new and old user
		 */
		userHelper.setUserHandler(new AhEntityCallback<AhUser>() {

			@Override
			public void onCompleted(final AhUser user) {
				updateUserList();
			}
		});

		return view;
	}


	private void exitSquare() {
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();

		AhUser user = userHelper.getMyUserInfo(true);
		userHelper.exitSquareAsync(thisFragment, user, new AhEntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean result) {
				progressBar.setVisibility(View.GONE);

				app.removeSquarePreference(thisFragment);
				Intent intent = new Intent(activity, SquareListActivity.class);
				startActivity(intent);
				activity.finish();
			}
		});
	}	


	@Override
	public void onStart() {
		super.onStart();
		updateUserList();
		String userId = pref.getString(AhGlobalVariable.USER_ID_KEY);
		blobStorageHelper.setImageViewAsync(thisFragment, BlobStorageHelper.USER_PROFILE, 
				userId+AhGlobalVariable.SMALL, R.drawable.profile_default, profileImage, true);
	}


	@Override
	public void onStop() {
		profileImage.setImageBitmap(null);
		super.onStop();
	}


	public void setUp(View fragmentView, DrawerLayout drawerLayout, final AhUser user) {
		/*
		 * Set alarm toggle button
		 */
		chatAlarmButton.setChecked(pref.getBoolean(AhGlobalVariable.IS_CHAT_ENABLE_KEY));
		chupaAlarmButton.setChecked(user.isChupaEnable());


		/*
		 * Set profile
		 */
		profileLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gaHelper.sendEventGA(
						thisFragment.getClass().getSimpleName(),
						"ViewMyProfile",
						"DrawerProfile");
				showProfileDialog(user);
			}
		});


		/*
		 * Set profile information text and gender image
		 */
		profileNickNameText.setText(user.getNickName());
		profileCompanyNumText.setText("" + user.getCompanyNum());
		Resources resources = getResources();
		if(user.isMale()){
			profileGenderImage.setImageResource(R.drawable.profile_gender_m);
			profileCompanyNumText.setTextColor(resources.getColor(R.color.blue));
		} else{
			profileGenderImage.setImageResource(R.drawable.profile_gender_w);
			profileCompanyNumText.setTextColor(resources.getColor(R.color.dark_red));
		}
	}


	private void updateUserList() {
		/*
		 * Set participant list view
		 */
		final int maleNum = getMaleNum(participantListAdapter);
		final int femaleNum = getFemaleNum(participantListAdapter);
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				participantListAdapter.clear();
				participantListAdapter.addAll(userDBHelper.getAllUsers());


				/*
				 * Set member number text
				 */
				maleNumText.setText("" + maleNum);
				femaleNumText.setText("" + femaleNum);
			}
		});
	}


	private int getMaleNum(SquareDrawerParticipantListAdapter list){
		int count = 0;
		for(int i = 0 ; i < list.getCount() ; i++) {
			if (list.getItem(i).isMale()) count++;
		}
		return count;
	}


	private int getFemaleNum(SquareDrawerParticipantListAdapter list){
		int count = 0;
		for(int i = 0 ; i < list.getCount() ; i++) {
			if (!list.getItem(i).isMale()) count++;
		}
		return count;
	}


	private void showProfileDialog(final AhUser user){
		ProfileDialog profileDialog = new ProfileDialog(thisFragment, user, new AhDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				gaHelper.sendEventGA(
						thisFragment.getClass().getSimpleName(),
						"SendChupa",
						"DrawerProfileSendChupa");

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
}


/**
 * This was placed inside exitSqure
 */

//AsyncChainer.asyncChain(thisFragment, new Chainable(){
//
//			@Override
//			public void doNext(AhFragment frag) {
//				AhUser user = userHelper.getMyUserInfo(true);
//				userHelper.exitSquareAsync(thisFragment, user.getId(), new AhEntityCallback<Boolean>() {
//
//					@Override
//					public void onCompleted(Boolean entity) {
//						removeSquarePreference();
//					}
//				});
//			}
//
//		}, new Chainable() {
//
//			@Override
//			public void doNext(AhFragment frag) {
//				String exitMessage = getResources().getString(R.string.exit_square_message);
//				String nickName = pref.getString(AhGlobalVariable.NICK_NAME_KEY);
//				AhMessage.Builder messageBuilder = new AhMessage.Builder();
//				messageBuilder.setContent(nickName + " : " + exitMessage)
//				.setSender(nickName)
//				.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY))
//				.setReceiverId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY))
//				.setType(AhMessage.TYPE.EXIT_SQUARE);
//				AhMessage message = messageBuilder.build();
//				messageHelper.sendMessageAsync(thisFragment, message, new AhEntityCallback<AhMessage>() {
//
//					@Override
//					public void onCompleted(AhMessage entity) {
//						final Intent intent = new Intent(thisFragment.getActivity(), SquareListActivity.class);
//						activity.runOnUiThread(new Runnable() {
//
//							@Override
//							public void run() {
//								progressBar.setVisibility(View.GONE);
//								startActivity(intent);
//								activity.finish();
//							}
//						});
//					}
//				});
//			}
//		});


/**
 *  DO NOT REMOVE
 *  NEED FOR REFERENCE
 */
//		new AhThread(new Runnable() {
//
//			@Override
//			public void run() {
//				User user = userHelper.getMyUserInfo(true);
//				userHelper.exitSquareSync(thisFragment, user.getId());
////				userHelper.unRegisterGcmSync(thisFragment);
//				userDBHelper.deleteAllUsers();
//				messageDBHelper.deleteAllMessages();
//
//				String exitMessage = getResources().getString(R.string.exit_square_message);
//				String nickName = pref.getString(AhGlobalVariable.NICK_NAME_KEY);
//				AhMessage.Builder messageBuilder = new AhMessage.Builder();
//				messageBuilder.setContent(nickName + " : " + exitMessage)
//				.setSender(nickName)
//				.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY))
//				.setReceiverId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY))
//				.setType(AhMessage.TYPE.EXIT_SQUARE);
//				AhMessage message = messageBuilder.build();
//				messageHelper.sendMessageSync(thisFragment, message);
//
//				pref.removePref(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY);
//				pref.removePref(AhGlobalVariable.USER_ID_KEY);
//				pref.removePref(AhGlobalVariable.COMPANY_NUMBER_KEY);
//				pref.removePref(AhGlobalVariable.SQUARE_ID_KEY);
//				pref.removePref(AhGlobalVariable.SQUARE_NAME_KEY);
//				pref.removePref(AhGlobalVariable.IS_CHUPA_ENABLE_KEY);
//				pref.removePref(AhGlobalVariable.IS_CHAT_ALARM_ENABLE_KEY);
//				final Intent intent = new Intent(thisFragment.getActivity(), SquareListActivity.class);
//				activity.runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						progressBar.setVisibility(View.GONE);
//						startActivity(intent);
//						activity.finish();
//					}
//				});
//			}
//		}).start();