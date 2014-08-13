package com.pinthecloud.athere.fragment;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareProfileActivity;
import com.pinthecloud.athere.adapter.SquareDrawerParticipantListAdapter;
import com.pinthecloud.athere.dialog.ExitSquareConsentDialog;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhPairEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.UserDBHelper;
import com.pinthecloud.athere.util.BitmapUtil;
import com.pinthecloud.athere.util.FileUtil;

public class SquareDrawerFragment extends AhFragment {

	private SquareDrawerFragmentCallbacks callbacks;
	private DrawerLayout mDrawerLayout;
	private View mFragmentView;

	private ProgressBar progressBar;
	private ToggleButton chatOnButton;
	private ToggleButton chupaOnButton;
	private ImageButton profileSettingButton;
	private TextView maleNumText;
	private TextView femaleNumText;
	private Button exitButton;

	private ImageView profileCircleImage;
	private ImageView profileGenderImage;
	private TextView profileNickNameText;
	private TextView profileAgeText;
	private TextView profileCompanyNumText;

	private ListView participantListView;
	private SquareDrawerParticipantListAdapter participantListAdapter;
	private List<User> userList = new ArrayList<User>();

	private UserDBHelper userDBHelper;
	private UserHelper userHelper;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDBHelper = app.getUserDBHelper();
		userHelper = app.getUserHelper();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_drawer, container, false);

		/*
		 * Set Ui Component
		 */
		progressBar = (ProgressBar) view.findViewById(R.id.square_drawer_frag_progress_bar);
		chatOnButton = (ToggleButton) view.findViewById(R.id.square_drawer_frag_chat_on_button);
		chupaOnButton = (ToggleButton) view.findViewById(R.id.square_drawer_frag_chupa_on_button);
		profileSettingButton = (ImageButton) view.findViewById(R.id.square_drawer_frag_profile_setting_button);
		maleNumText = (TextView) view.findViewById(R.id.square_drawer_frag_member_male_text);
		femaleNumText = (TextView) view.findViewById(R.id.square_drawer_frag_member_female_text);
		profileCircleImage = (ImageView) view.findViewById(R.id.square_drawer_frag_profile_circle_image);
		profileGenderImage = (ImageView) view.findViewById(R.id.square_drawer_frag_profile_gender_image);
		profileNickNameText= (TextView) view.findViewById(R.id.square_drawer_frag_profile_nick_name_text);
		profileAgeText = (TextView) view.findViewById(R.id.square_drawer_frag_profile_age_text);
		profileCompanyNumText = (TextView) view.findViewById(R.id.square_drawer_frag_profile_company_number_text);
		participantListView = (ListView) view.findViewById(R.id.square_drawer_frag_participant_list);
		exitButton = (Button) view.findViewById(R.id.square_drawer_frag_exit_button);


		/*
		 * Set user list
		 */
		participantListAdapter = new SquareDrawerParticipantListAdapter
				(context, this, R.layout.row_square_drawer_participant_list, userList);
		participantListView.setAdapter(participantListAdapter);


		/*
		 * Set setting toggle button
		 */
		chupaOnButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
				progressBar.setVisibility(View.VISIBLE);
				pref.putBoolean(AhGlobalVariable.IS_CHUPA_ENABLE_KEY, isChecked);
				userHelper.updateMyUserAsync(new AhEntityCallback<User>() {

					@Override
					public void onCompleted(User entity) {
						progressBar.setVisibility(View.GONE);
					}
				});
			}
		});
		chatOnButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				pref.putBoolean(AhGlobalVariable.IS_CHAT_ALARM_ENABLE_KEY, isChecked);
			}
		});


		/*
		 * Set Button
		 */
		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ExitSquareConsentDialog escDialog = new ExitSquareConsentDialog(new AhDialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						progressBar.setVisibility(View.VISIBLE);
						callbacks.exitSquare();
					}

					@Override
					public void doNegativeThing(Bundle bundle) {
						// Do nothing
					}
				});
				escDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			}
		});
		profileSettingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, SquareProfileActivity.class);
				startActivity(intent);
			}
		});

		return view;
	}


	@Override
	public void onResume() {
		super.onResume();
		updateUserList();


		/*
		 * Set handler for refresh new and old user
		 */
		userHelper.setUserHandler(new AhPairEntityCallback<AhMessage.TYPE, User>() {

			@Override
			public void onCompleted(final AhMessage.TYPE type, final User user) {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						updateUserList();
					}
				});
			}
		});
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callbacks = (SquareDrawerFragmentCallbacks) activity;
	}


	@Override
	public void onDetach() {
		super.onDetach();
		callbacks = null;
	}


	public void setUp(View fragmentView, DrawerLayout drawerLayout, User user) {
		this.mFragmentView = fragmentView;
		this.mDrawerLayout = drawerLayout;

		/*
		 * Set profile images 
		 */
		Bitmap profileBitmap = null;
		try {
			profileBitmap = FileUtil.getImageFromInternalStorage(context, AhGlobalVariable.PROFILE_PICTURE_CIRCLE_NAME);
		} catch (FileNotFoundException e) {
			profileBitmap = BitmapFactory.decodeResource(app.getResources(), R.drawable.splash);
			profileBitmap = BitmapUtil.cropRound(profileBitmap);
			Log.d(AhGlobalVariable.LOG_TAG, "Error of SquareDrawerFragmet : " + e.getMessage());
		}
		profileCircleImage.setImageBitmap(profileBitmap);
		if(user.isMale()){
			profileGenderImage.setImageResource(R.drawable.profile_gender_m);
		} else{
			profileGenderImage.setImageResource(R.drawable.profile_gender_w);
		}


		/*
		 * Set profile infomation text
		 */
		profileNickNameText.setText(user.getNickName());
		profileAgeText.setText("" + user.getAge());
		profileCompanyNumText.setText("" + user.getCompanyNum());
	}


	private void updateUserList() {
		/*
		 * Set participant list view
		 */
		userList.clear();
		userList.addAll(userDBHelper.getAllUsers());
		participantListAdapter.notifyDataSetChanged();


		/*
		 * Set member number text
		 */
		maleNumText.setText("" + getMaleNum(userList));
		femaleNumText.setText("" + getFemaleNum(userList));
	}


	private int getMaleNum(List<User> list){
		int count = 0;
		for(User user : list){
			if (user.isMale()) count++;
		}
		return count;
	}


	private int getFemaleNum(List<User> list){
		int count = 0;
		for(User user : list){
			if (!user.isMale()) count++;
		}
		return count;
	}


	public interface SquareDrawerFragmentCallbacks {
		public void exitSquare();
	}
}
