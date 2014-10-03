package com.pinthecloud.athere.fragment;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.GuideActivity;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.util.AsyncChainer;
import com.pinthecloud.athere.util.AsyncChainer.Chainable;

public class SettingsFragment extends AhFragment{

	private ProgressBar progressBar;
	private LoginButton logoutButton;
	private UiLifecycleHelper uiHelper;

	private ToggleButton chatAlarmButton;
	private ToggleButton chupaAlarmButton;
	private RelativeLayout questionButton;
	private RelativeLayout shareButton;

	private AhUser user;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
		}
		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(activity, callback);
		uiHelper.onCreate(savedInstanceState);
		
		Intent intent = activity.getIntent();
		user = intent.getParcelableExtra(AhGlobalVariable.USER_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_settings, container, false);


		/*
		 * Find UI component
		 */
		progressBar = (ProgressBar)view.findViewById(R.id.settings_frag_progress_bar);
		logoutButton = (LoginButton)view.findViewById(R.id.settings_frag_logout_button);
		chatAlarmButton = (ToggleButton)view.findViewById(R.id.settings_frag_chat_alarm);
		chupaAlarmButton = (ToggleButton)view.findViewById(R.id.settings_frag_chupa_alarm);
		questionButton = (RelativeLayout)view.findViewById(R.id.settings_frag_question);
		shareButton = (RelativeLayout)view.findViewById(R.id.settings_frag_share);


		/*
		 * Set Action Bar
		 */
		ActionBar actionBar = activity.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);


		/*
		 * Set logout button
		 */
		logoutButton.setBackgroundResource(R.drawable.guide_logout_btn);
		logoutButton.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
		logoutButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {

			@Override
			public void onUserInfoFetched(GraphUser user) {
				Session session = Session.getActiveSession();
				if (session != null && session.isOpened() && user != null) {
					return;
				}

				/*
				 * Logout
				 */
				progressBar.setVisibility(View.VISIBLE);
				progressBar.bringToFront();

				AsyncChainer.asyncChain(thisFragment, new Chainable(){

					@Override
					public void doNext(AhFragment frag) {
						if(squareHelper.isLoggedInSquare()){
							AhUser myUser = userHelper.getMyUserInfo();
							userHelper.exitSquareAsync(thisFragment, myUser, new AhEntityCallback<Boolean>() {

								@Override
								public void onCompleted(Boolean result) {
									app.removeMySquarePreference(thisFragment);
								}
							});
						}else{
							AsyncChainer.notifyNext(frag);
						}
					}
				}, new Chainable() {

					@Override
					public void doNext(AhFragment frag) {
						app.removeMyUserPreference(thisFragment);

						Intent intent = new Intent(context, GuideActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
			}
		});


		/*
		 * Set event on settings list button
		 */
		chatAlarmButton.setChecked(userHelper.isChatEnable());
		chatAlarmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userHelper.setChatEnable(chatAlarmButton.isChecked());
			}
		});
		chupaAlarmButton.setChecked(user.isChupaEnable());
		chupaAlarmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				progressBar.setVisibility(View.VISIBLE);
				progressBar.bringToFront();
				chupaAlarmButton.setEnabled(false);

				user.setChupaEnable(chupaAlarmButton.isChecked());
				userHelper.updateUserAsync(thisFragment, user, new AhEntityCallback<AhUser>() {

					@Override
					public void onCompleted(AhUser entity) {
						progressBar.setVisibility(View.GONE);
						chupaAlarmButton.setEnabled(true);
						userHelper.setMyChupaEnable(chupaAlarmButton.isChecked());
					}
				});
			}
		});
		questionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:pinthecloud@gmail.com"));
				startActivity(intent);
			}
		});
		shareButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_SEND);
				intent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.share_sentence));
				intent.setType("text/plain");
				startActivity(Intent.createChooser(intent, getResources().getText(R.string.share_to)));
			}
		});

		return view;
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null &&
				(session.isOpened() || session.isClosed()) ) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();
		AppEventsLogger.activateApp(context);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		AppEventsLogger.deactivateApp(context);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	}
}
