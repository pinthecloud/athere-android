package com.pinthecloud.athere.fragment;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.BasicProfileActivity;
import com.pinthecloud.athere.adapter.GuidePagerAdapter;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.viewpagerindicator.CirclePageIndicator;

public class GuideFragment extends AhFragment{

	private ViewPager mPager;
	private CirclePageIndicator indicator;

	private LoginButton loginButton;
	private UiLifecycleHelper uiHelper;

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
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_guide, container, false);

		findComponent(view);
		setViewPager();
		setLoginButton();

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


	private void findComponent(View view){
		mPager = (ViewPager)view.findViewById(R.id.guide_frag_viewpager);
		indicator = (CirclePageIndicator)view.findViewById(R.id.guide_frag_indicator);
		loginButton = (LoginButton)view.findViewById(R.id.guide_frag_login_button);
	}


	private void setViewPager(){
		mPager.setAdapter(new GuidePagerAdapter(getFragmentManager()));
		indicator.setViewPager(mPager);
	}


	private void setLoginButton(){
		loginButton.setBackgroundResource(R.drawable.guide_login_btn);
		loginButton.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
		loginButton.setReadPermissions(Arrays.asList("user_birthday"));
		loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {

			@Override
			public void onUserInfoFetched(GraphUser user) {
				Session session = Session.getActiveSession();
				if (session != null && session.isOpened() || user != null) {
					login(session, user);
				}
			}
		});
	}


	private void login(Session session, GraphUser user){
		String birthday = user.getBirthday();
		if (birthday == null) {
			Toast.makeText(context, getResources().getString(R.string.birthday_access_message)
					, Toast.LENGTH_LONG).show();

			Session.NewPermissionsRequest newPermissionsRequest = 
					new Session.NewPermissionsRequest(activity, Arrays.asList("user_birthday"));
			session.requestNewReadPermissions(newPermissionsRequest);
			session.close();
			return;
		}

		String birthYear = birthday.substring(6, birthday.length());
		String name = user.getFirstName();
		String gender = (String)user.getProperty("gender");
		boolean isMale = true;
		if(!gender.equals("male")){
			isMale = false;
		}

		userHelper.setMyAhId(user.getId())
		.setMyNickName(name)
		.setMyMale(isMale)
		.setMyBirthYear(Integer.parseInt(birthYear));

		setSuperUser(user);

		Intent intent = new Intent(context, BasicProfileActivity.class);
		startActivity(intent);
		activity.finish();
	}


	private void setSuperUser(GraphUser user){
		ArrayList<String> arr = new ArrayList<String>();
		arr.add("1482905955291892"); // Pin the Cloud
		arr.add("643223775792443");  // Hongkun
		arr.add("834963903211098");	 // Seungmin
		arr.add("766458060085007");	 // Chaesoo
		arr.add("699691573453752");	 // Hwajeong
		if (arr.contains(user.getId())) {
			Toast.makeText(activity, "Super User Activated!", Toast.LENGTH_LONG)
			.show();
			PreferenceHelper.getInstance().putBoolean(AhGlobalVariable.SUPER_USER_KEY, true);
		}
	}


	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		// Do nothing
	}
}
