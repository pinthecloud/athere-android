package com.pinthecloud.athere.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.ProgressAsyncTask;
import com.pinthecloud.athere.model.ToDoItem;
import com.pinthecloud.athere.model.User;

public class HongkunTestAcitivity extends AhActivity {
	private MobileServiceTable<ToDoItem> mToDoTable;
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hongkun_test_acitivity);
		
		
		
	}
	
	public void addItem(View view) {
		final User user = new User();
		
		user.setNickName("nick");
		user.setMobileId("mobildId");
		user.setProfilePic("pic");
		
//		serviceClient.setProfile(user);
		
		(new ProgressAsyncTask<Void, Void, Void>(this) {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				ToDoItem item = new ToDoItem();
				serviceClient.addToDoItem(item);
				
				return null;
			}
			
		}).execute();
		
	}
}
