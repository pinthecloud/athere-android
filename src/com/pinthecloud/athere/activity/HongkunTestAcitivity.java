package com.pinthecloud.athere.activity;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pinthecloud.athere.AhListCallback;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PrefHelper;
import com.pinthecloud.athere.model.Square;
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
		
		//Location loc = new Location(provider);
		
		serviceClient.getSquareList(null, new AhListCallback<Square>() {
			
			@Override
			public void onCompleted(List<Square> list, int count) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
