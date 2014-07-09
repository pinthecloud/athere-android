package com.pinthecloud.athere.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PrefHelper;
import com.pinthecloud.athere.model.MyHandler;
import com.pinthecloud.athere.model.ToDoItem;
import com.pinthecloud.athere.model.User;

public class HongkunTestAcitivity extends AhActivity {
	Button btn;
	int count = 0;
	
	private MobileServiceTable<ToDoItem> mToDoTable;
	private MobileServiceClient mClient;
	
	public static final String SENDER_ID = "838051405989";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hongkun_test_acitivity);
		btn = (Button)findViewById(R.id.button1);
		
		mClient = serviceClient.getClient();
		mToDoTable = mClient.getTable(ToDoItem.class);
		
		NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
	}
	
	public void addItem(View view) {
		final User user = new User();
		
		user.setNickName("nick");
		user.setMobileId("mobildId");
		user.setProfilePic("pic");
		ToDoItem item = new ToDoItem();
		item.setComplete(true);
		item.setText("TESTING!!!!!!!");
		
		item.setHandle(MyHandler.getHandle());
		PrefHelper pref = new PrefHelper(this);
		pref.putInt("sdf",3);
		pref.getInt("sdf");
		
		//Location loc = new Location(provider);
		
//		serviceClient.getSquareList(null, new AhListCallback<Square>() {
//			
//			@Override
//			public void onCompleted(List<Square> list, int count) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		item.setComplete(true);
		
		mToDoTable.insert(item, new TableOperationCallback<ToDoItem>() {

			public void onCompleted(ToDoItem entity, Exception exception, ServiceFilterResponse response) {
				
				if (exception == null) {
					if (!entity.isComplete()) {
						//mAdapter.add(entity);
						Log.e("ERROR","in callback");
						//latch.countDown();
					}
				} else {
					Log.e("ERROR", exception.toString());
					Log.e("ERROR","ERROR in callback");
					//createAndShowDialog(exception, "Error");
				}

			}
		});

	}
}
