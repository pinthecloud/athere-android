package com.pinthecloud.athere.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.pinthecloud.athere.AhEntityCallback;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PrefHelper;
import com.pinthecloud.athere.model.User;

public class HongkunTestAcitivity extends AhActivity {
	Button btn;
	int count = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hongkun_test_acitivity);
		btn = (Button)findViewById(R.id.button1);
	}
	
	public void addItem(View view) {
		final User user = new User();
		
		user.setNickName("nick");
		user.setMobileId("mobildId");
		user.setProfilePic("pic");
		
		PrefHelper pref = new PrefHelper(this);
		pref.putInt("sdf",3);
		pref.getInt("sdf");
		
		serviceClient.setProfile(user, new AhEntityCallback<User>() {
			
			@Override
			public void onCompleted(User entity) {
				// TODO Auto-generated method stub
				Log.e("ERROR",user.getNickName());
			}
		});
	}
}
