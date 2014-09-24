package com.pinthecloud.athere.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.fragment.BasicProfileFragment;


public class BasicProfileActivity extends AhActivity{

//	private LoginButton fbBtn;
//	private UiLifecycleHelper uiHelper;
	public BasicProfileFragment basicProfileFragment;
	
//	private Session.StatusCallback callback = new Session.StatusCallback() {
//        @Override
//        public void call(Session session, SessionState state, Exception exception) {
//            onSessionStateChange(session, state, exception);
//        }
//    };
//
//    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
//        @Override
//        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
//            Log(thisActivity, error);
//        }
//
//        @Override
//        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
//            Log(thisActivity, "onComplete", data);
//        }
//    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basic_profile);

//		uiHelper = new UiLifecycleHelper(this, callback);
//		uiHelper.onCreate(savedInstanceState);
		/*
		 * Set Fragment to container
		 */
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		basicProfileFragment = new BasicProfileFragment();
		fragmentTransaction.add(R.id.basic_profile_container, basicProfileFragment);
		fragmentTransaction.commit();
		
//		fbBtn = (LoginButton) findViewById(R.id.facebook_login_button_act);
//		
//		fbBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Log(thisActivity, "clicked");
//			}
//		});
//		
//		fbBtn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
//	        @Override
//	        public void onUserInfoFetched(GraphUser user) {
//	            Log(thisActivity, user);
//	        }
//	    });
	}
	
//	@Override
//    public void onResume() {
//        super.onResume();
//        Session session = Session.getActiveSession();
//        if (session != null &&
//               (session.isOpened() || session.isClosed()) ) {
//            onSessionStateChange(session, session.getState(), null);
//        }
//        uiHelper.onResume();
//    }
//	
//	@Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        uiHelper.onSaveInstanceState(outState);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log(thisActivity, "in Frag onActivityResults");
    	basicProfileFragment.onActivityResult(requestCode, resultCode, data);
        
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        uiHelper.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        uiHelper.onDestroy();
//    }
//
//    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
//        if (session.isOpened()) {
//        	Log(thisActivity, "Logged in...");
//        } else {
//        	Log(thisActivity, "Logged out...");
//        }
//    }

	
}
