package com.pinthecloud.athere.manager;

import static com.microsoft.windowsazure.mobileservices.MobileServiceQueryOperations.val;

import java.net.MalformedURLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.athere.AhException;
import com.pinthecloud.athere.model.ToDoItem;
import com.pinthecloud.athere.model.User;

public class ServiceClient {
	private MobileServiceClient mClient;
	private String APP_URL = "https://athere.azure-mobile.net/";
	private String APP_KEY = "AyHtUuHXEwDSTuuLvvSYZtVSQZxtnT17";
	private Context context;
	
	MobileServiceTable<User> userTable;
	
	private MobileServiceTable<ToDoItem> mToDoTable;
	
	public ServiceClient(Context context){
		this.context = context;
		try {
			this.mClient = new MobileServiceClient(
					APP_URL,
					APP_KEY,
					this.context);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		userTable = mClient.getTable(User.class);
		
		mToDoTable = mClient.getTable(ToDoItem.class);
	}
	
	public User getRandomProfile() throws AhException {
		
		userTable.where().field("complete").eq(val(false)).execute(new TableQueryCallback<User>() {

			@Override
			public void onCompleted(List<User> result, int count, Exception exception, ServiceFilterResponse response) {
				// Succeed
				if (exception == null) {

					
				// failed
				} else {
					try {
						throw new AhException(exception, "getRandomProfile");
					} catch (AhException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		return null;
	}
	
	public void setProfile(User user) throws AhException {
		
		userTable.insert(user, new TableOperationCallback<User>() {

			public void onCompleted(User entity, Exception exception, ServiceFilterResponse response) {
				
				if (exception == null) {
					Log.e("ERROR","sestProfile OK");
				} else {
					throw new AhException(exception, "setProfile");
				}

			}
		});
	}
	
	public void addToDoItem(ToDoItem item) {
		item.setText("testing Service Client");
		item.setComplete(false);
		 
		// Insert the new item
		mToDoTable.insert(item, new TableOperationCallback<ToDoItem>() {

			public void onCompleted(ToDoItem entity, Exception exception, ServiceFilterResponse response) {
				
				if (exception == null) {
					if (!entity.isComplete()) {
						Log.e("ERROR","OK");
					}
				} else {
					throw new AhException(exception, "addToDoItem");
				}

			}
		});
	}
	
}
