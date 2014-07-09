package com.pinthecloud.athere.helper;

import static com.microsoft.windowsazure.mobileservices.MobileServiceQueryOperations.val;

import java.net.MalformedURLException;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.athere.AhException;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhListCallback;
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
	
	public void isAvailableNickName(User user, final AhEntityCallback<Boolean> callback) throws AhException {
		
		userTable.where().field("nickName").eq(val(true)).execute(new TableQueryCallback<User>() {

			@Override
			public void onCompleted(List<User> result, int count, Exception exception, ServiceFilterResponse response) {
				// Succeed
				if (exception == null) {
					if (count > 0) callback.onCompleted(false);
					else callback.onCompleted(true);
				// failed
				} else {
					throw new AhException(exception, "isAvailableNickName");
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

	public void getSquareList(Location loc, final AhListCallback<Square> callback) throws AhException {
		progressDialog.show();
		
		JsonObject jo = new JsonObject();
		jo.addProperty("currentLatitude", loc.getLatitude());
		jo.addProperty("currentLongtitude", loc.getLongitude());
		
		Gson g = new Gson();
		JsonElement json = g.fromJson(jo, JsonElement.class);
		
		mClient.invokeApi("getnearsquare", json, new ApiJsonOperationCallback() {
			
			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse response) {
				// TODO Auto-generated method stub
				if ( exception == null) {
					List<Square> list = JsonConverter.convertToSquareList(json.getAsJsonArray());
					if (list == null) throw new AhException(exception, "getSquareList");
					callback.onCompleted(list, list.size());
					progressDialog.dismiss();
				} else {
					throw new AhException(exception, "getSquareList");
				}
			}
		});
	}
	
	public void createSquare(Square square, final AhEntityCallback<Square> callback) throws AhException {
		squareTable.insert(square, new TableOperationCallback<Square>() {

			public void onCompleted(Square entity, Exception exception, ServiceFilterResponse response) {
				
				if (exception == null) {
					callback.onCompleted(entity);
					progressDialog.dismiss();
				} else {
					throw new AhException(exception, "createSquare");
				}
			}
		});
	}
	
	public void updateSquare(Square square, final AhEntityCallback<Square> callback) throws AhException {
		squareTable.update(square, new TableOperationCallback<Square>() {

			public void onCompleted(Square entity, Exception exception, ServiceFilterResponse response) {
				
				if (exception == null) {
					callback.onCompleted(entity);
					progressDialog.dismiss();
				} else {
					throw new AhException(exception, "updateSquare");
				}
			}
		});
	}
}
