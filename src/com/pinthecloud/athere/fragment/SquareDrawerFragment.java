package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.activity.ProfileImageActivity;
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.adapter.SquareDrawerParticipantListAdapter;
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.dialog.ProfileDialog;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.util.FileUtil;

public class SquareDrawerFragment extends AhFragment {

	private ProgressBar progressBar;

	private ToggleButton chatAlarmButton;
	private ToggleButton chupaAlarmButton;
	private ImageView profileImage;
	private ImageView profileGenderImage;
	private TextView profileNickNameText;
	private TextView profileAgeText;
	private TextView profileCompanyNumText;
	private TextView maleNumText;
	private TextView femaleNumText;
	private Button exitButton;

	private ListView participantListView;
	private SquareDrawerParticipantListAdapter participantListAdapter;
	private List<AhUser> userList = new ArrayList<AhUser>();



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_drawer, container, false);

		/*
		 * Set Ui Component
		 */
		progressBar = (ProgressBar) view.findViewById(R.id.square_drawer_frag_progress_bar);
		chatAlarmButton = (ToggleButton) view.findViewById(R.id.square_drawer_frag_chat_alarm_button);
		chupaAlarmButton = (ToggleButton) view.findViewById(R.id.square_drawer_frag_chupa_alarm_button);
		maleNumText = (TextView) view.findViewById(R.id.square_drawer_frag_member_male_text);
		femaleNumText = (TextView) view.findViewById(R.id.square_drawer_frag_member_female_text);
		profileImage = (ImageView) view.findViewById(R.id.square_drawer_frag_profile_image);
		profileGenderImage = (ImageView) view.findViewById(R.id.square_drawer_frag_profile_gender);
		profileNickNameText= (TextView) view.findViewById(R.id.square_drawer_frag_profile_nick_name);
		profileAgeText = (TextView) view.findViewById(R.id.square_drawer_frag_profile_age);
		profileCompanyNumText = (TextView) view.findViewById(R.id.square_drawer_frag_profile_company_num);
		participantListView = (ListView) view.findViewById(R.id.square_drawer_frag_participant_list);
		exitButton = (Button) view.findViewById(R.id.square_drawer_frag_exit_button);


		/*
		 * Set user list
		 */
		participantListAdapter = new SquareDrawerParticipantListAdapter
				(context, _thisFragment, R.layout.row_square_drawer_participant_list, userList);
		participantListView.setAdapter(participantListAdapter);
		participantListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final AhUser user = userList.get(position);
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
				profileDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			}
		});


		/*
		 * Set general buttons
		 */
		chatAlarmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isChecked = chatAlarmButton.isChecked();
				pref.putBoolean(AhGlobalVariable.IS_CHAT_ALARM_ENABLE_KEY, isChecked);
			}
		});
		chupaAlarmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isChecked = chupaAlarmButton.isChecked();
				pref.putBoolean(AhGlobalVariable.IS_CHUPA_ENABLE_KEY, isChecked);
				progressBar.setVisibility(View.VISIBLE);
				progressBar.bringToFront();
				userHelper.updateMyUserAsync(_thisFragment, new AhEntityCallback<AhUser>() {

					@Override
					public void onCompleted(AhUser entity) {
						progressBar.setVisibility(View.GONE);
					}
				});
			}
		});
		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Resources resources = getResources();
				String title = resources.getString(R.string.exit_square);
				String message = resources.getString(R.string.exit_square_consent_message);
				AhAlertDialog escDialog = new AhAlertDialog(title, message, true, new AhDialogCallback() {

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
		userHelper.newExitSquareAsync(_thisFragment, user, new AhEntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean result) {
				// TODO Auto-generated method stub
				String id = pref.getString(AhGlobalVariable.USER_ID_KEY);
				blobStorageHelper.deleteBitmapAsync(_thisFragment, id, null);
				removeSquarePreference();
				progressBar.setVisibility(View.GONE);
				final Intent intent = new Intent(activity, SquareListActivity.class);
				startActivity(intent);
				activity.finish();
			}
		});

		//		AsyncChainer.asyncChain(_thisFragment, new Chainable(){
		//
		//			@Override
		//			public void doNext(AhFragment frag) {
		//				AhUser user = userHelper.getMyUserInfo(true);
		//				userHelper.exitSquareAsync(_thisFragment, user.getId(), new AhEntityCallback<Boolean>() {
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
		//				messageHelper.sendMessageAsync(_thisFragment, message, new AhEntityCallback<AhMessage>() {
		//
		//					@Override
		//					public void onCompleted(AhMessage entity) {
		//						final Intent intent = new Intent(_thisFragment.getActivity(), SquareListActivity.class);
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
		//				userHelper.exitSquareSync(_thisFragment, user.getId());
		////				userHelper.unRegisterGcmSync(_thisFragment);
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
		//				messageHelper.sendMessageSync(_thisFragment, message);
		//
		//				pref.removePref(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY);
		//				pref.removePref(AhGlobalVariable.USER_ID_KEY);
		//				pref.removePref(AhGlobalVariable.COMPANY_NUMBER_KEY);
		//				pref.removePref(AhGlobalVariable.SQUARE_ID_KEY);
		//				pref.removePref(AhGlobalVariable.SQUARE_NAME_KEY);
		//				pref.removePref(AhGlobalVariable.IS_CHUPA_ENABLE_KEY);
		//				pref.removePref(AhGlobalVariable.IS_CHAT_ALARM_ENABLE_KEY);
		//				final Intent intent = new Intent(_thisFragment.getActivity(), SquareListActivity.class);
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
	}	


	@Override
	public void onStart() {
		super.onStart();
		Log.d(AhGlobalVariable.LOG_TAG, "SquareDrawerFragment onStart");
		updateUserList();


		/*
		 * Set profile images 
		 */
		Bitmap profileBitmap = null;
		profileBitmap = FileUtil.getImageFromInternalStorage(context, AhGlobalVariable.PROFILE_PICTURE_NAME);
		profileImage.setImageBitmap(profileBitmap);
	}


	@Override
	public void onStop() {
		Log.d(AhGlobalVariable.LOG_TAG, "SquareDrawerFragment onStop");

		/*
		 * Release image resources
		 */
		profileImage.setImageBitmap(null);
		super.onStop();
	}


	public void setUp(View fragmentView, DrawerLayout drawerLayout, final AhUser user) {
		/*
		 * Set alarm toggle button
		 */
		chatAlarmButton.setChecked(pref.getBoolean(AhGlobalVariable.IS_CHAT_ALARM_ENABLE_KEY));
		chupaAlarmButton.setChecked(user.isChupaEnable());


		/*
		 * Set profile image
		 */
		profileImage.setOnClickListener(new OnClickListener() {

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
				profileDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			}
		});


		/*
		 * Set profile information text and gender image
		 */
		profileNickNameText.setText(user.getNickName());
		profileAgeText.setText("" + user.getAge());
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
		userList.clear();
		userList.addAll(userDBHelper.getAllUsers());
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				participantListAdapter.notifyDataSetChanged();


				/*
				 * Set member number text
				 */
				maleNumText.setText("" + getMaleNum(userList));
				femaleNumText.setText("" + getFemaleNum(userList));
			}
		});
	}


	private int getMaleNum(List<AhUser> list){
		int count = 0;
		for(AhUser user : list){
			if (user.isMale()) count++;
		}
		return count;
	}


	private int getFemaleNum(List<AhUser> list){
		int count = 0;
		for(AhUser user : list){
			if (!user.isMale()) count++;
		}
		return count;
	}
}
