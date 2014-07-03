package com.pinthecloud.athere.helper;

import static com.microsoft.windowsazure.mobileservices.MobileServiceQueryOperations.val;

import java.net.MalformedURLException;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.athere.AhEntityCallback;
import com.pinthecloud.athere.AhException;
import com.pinthecloud.athere.AhListCallback;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.model.User;

public class ServiceClient {
	/**
	 * Mobile Service Connection Information
	 */
	private MobileServiceClient mClient;
	private String APP_URL = "https://athere.azure-mobile.net/";
	private String APP_KEY = "AyHtUuHXEwDSTuuLvvSYZtVSQZxtnT17";
	
	/**
	 * Activity UI variables
	 */
	private Context context;
	private ProgressDialog progressDialog;

	/**
	 * Model tables
	 */
	MobileServiceTable<User> userTable;
	MobileServiceTable<Square> squareTable;
	
	
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
		progressDialog = new ProgressDialog(context, com.pinthecloud.athere.R.style.NewProgressDialog);
		
		userTable = mClient.getTable(User.class);
		squareTable = mClient.getTable(Square.class);
	}
	
	public Context getContext() { return context; }
	
	public void getRandomProfile(final AhListCallback<User> callback) throws AhException {
		
		userTable.where().field("complete").eq(val(false)).execute(new TableQueryCallback<User>() {

			@Override
			public void onCompleted(List<User> result, int count, Exception exception, ServiceFilterResponse response) {
				// Succeed
				if (exception == null) {
					callback.onCompleted(result, count);
				// failed
				} else {
					throw new AhException(exception, "getRandomProfile");
				}
			}
		});
	}
	
	public void setProfile(User user, final AhEntityCallback<User> callback) throws AhException {
		progressDialog.show();
		userTable.insert(user, new TableOperationCallback<User>() {

			public void onCompleted(User entity, Exception exception, ServiceFilterResponse response) {
				
				if (exception == null) {
					callback.onCompleted(entity);
					progressDialog.dismiss();
				} else {
					throw new AhException(exception, "setProfile");
				}
			}
		});
		
	}

	public void getSquareList(Location loc, AhListCallback<Square> callback){
		progressDialog.show();
		
		//userTable.where().
		
	}
}
