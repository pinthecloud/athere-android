package com.pinthecloud.athere.helper;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableDeleteCallback;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.pinthecloud.athere.AhEntityCallback;
import com.pinthecloud.athere.AhException;
import com.pinthecloud.athere.AhListCallback;
import com.pinthecloud.athere.model.AhMessage;
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
	
	public MobileServiceClient getClient() { return mClient; }
	
	public Context getContext() { return context; }
	
//	public void isAvailableNickName(User user, final AhEntityCallback<Boolean> callback) throws AhException {
//		
//		userTable.where().field("nickName").eq(val(true)).execute(new TableQueryCallback<User>() {
//
//			@Override
//			public void onCompleted(List<User> result, int count, Exception exception, ServiceFilterResponse response) {
//				// Succeed
//				if (exception == null) {
//					if (count > 0) callback.onCompleted(false);
//					else callback.onCompleted(true);
//				// failed
//				} else {
//					throw new AhException(exception, "isAvailableNickName");
//				}
//			}
//		});
//	}
	
	public void setProfile(String nickName, boolean isMale, int birthYear, String registrationId) {
		Calendar c = Calendar.getInstance();
		int age = c.get(Calendar.YEAR) - (birthYear-1);
		
		PrefHelper pref = new PrefHelper(context);
		pref.putUser(nickName, isMale, age, registrationId);
	}

	public void getSquareListAsync(Location loc, final AhListCallback<Square> callback) throws AhException {
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
					throw new AhException(exception, "getSquareListAsync");
				}
			}
		});
	}
	
	public String createSquareWithoutFuture() {
		final ServiceClient _this = this;
		final StringBuilder sb = new StringBuilder();
		
		Log.e("ERROR","start create--withoutFuture");
		final Object log = new Object();
		_this.createSquareAsync("squareName", 10.0, 10.0, new AhEntityCallback<Square>() {
			
			@Override
			public void onCompleted(Square entity) {
				// TODO Auto-generated method stub
				sb.append("return String");
				Log.e("ERROR","on complete");
				synchronized (log) {
					log.notify();
				}
				
			}
		});
		Log.e("ERROR","before sync");
		synchronized (log) {
			try {
				log.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.e("ERROR","after sync");
		return sb.toString();
	}
	
	public Future<String> createSquareAsync() {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		final ServiceClient _this = this;
		final StringBuilder sb = new StringBuilder();
		Log.e("ERROR","start submit");
		return pool.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
				// TODO Auto-generated method stub
				Log.e("ERROR","start call");
				final Object log = new Object();
				_this.createSquareAsync("squareName", 10.0, 10.0, new AhEntityCallback<Square>() {
					
					@Override
					public void onCompleted(Square entity) {
						// TODO Auto-generated method stub
						sb.append("return String");
						Log.e("ERROR","on complete");
						synchronized (log) {
							log.notify();
						}
						
					}
				});
				Log.e("ERROR","before sync");
				synchronized (log) {
					try {
						log.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Log.e("ERROR","after sync");
				return sb.toString();
			}
		});
	}
	
	public void createSquareAsync(String name, double latitude, double longitude, final AhEntityCallback<Square> callback) throws AhException {
		PrefHelper pref = new PrefHelper(context);
		String whoMade = pref.getRegistrationId();
		
		if (whoMade == PrefHelper.DEFAULT_STRING) {
			throw new AhException("createSquare NO Registration");
		}
		Square square = new Square();
		
		square.setName(name);
		square.setLatitude(latitude);
		square.setLongitude(longitude);
		square.setWhoMade(whoMade);
		
		this.createSquareAsync(square, callback);
	}
	
	public void createSquareAsync(Square square, final AhEntityCallback<Square> callback) throws AhException {
		squareTable.insert(square, new TableOperationCallback<Square>() {

			public void onCompleted(Square entity, Exception exception, ServiceFilterResponse response) {
				
				if (exception == null) {
					callback.onCompleted(entity);
				} else {
					throw new AhException(exception, "createSquareAsync");
				}
			}
		});
	}
	
//	public void updateSquareAsync(Square square, final AhEntityCallback<Square> callback) throws AhException {
//		squareTable.update(square, new TableOperationCallback<Square>() {
//
//			public void onCompleted(Square entity, Exception exception, ServiceFilterResponse response) {
//				
//				if (exception == null) {
//					callback.onCompleted(entity);
//				} else {
//					throw new AhException(exception, "updateSquare");
//				}
//			}
//		});
//	}
	
	public void enterSquareAsync(String squareId, Bitmap img, int companyNum, String mobileId, final AhEntityCallback<Boolean> callback) throws AhException {
		PrefHelper pref = new PrefHelper(context);
		User user = pref.getUser();
		
		user.setSquareId(squareId);
		String profilePic = null;
		profilePic = ImageConverter.convertToString(img);
		user.setProfilePic(profilePic);
		user.setCompanyNum(companyNum);
		user.setMobileId(mobileId);
		
		userTable.insert(user, new TableOperationCallback<User>() {

			@Override
			public void onCompleted(User entity, Exception exception,
					ServiceFilterResponse response) {
				// TODO Auto-generated method stub
				if (exception == null) {
					callback.onCompleted(true);
				} else {
					throw new AhException(exception, "enterSquareAsync");
				}
			}
		});
		
//		JsonElement json = null;
//		
//		mClient.invokeApi("enter_square", json, new ApiJsonOperationCallback() {
//			
//			@Override
//			public void onCompleted(JsonElement json, Exception exception,
//					ServiceFilterResponse response) {
//				// TODO Auto-generated method stub
//				if ( exception == null) {
//					List<Square> list = JsonConverter.convertToSquareList(json.getAsJsonArray());
//					if (list == null) throw new AhException(exception, "getSquareList");
//					callback.onCompleted(list, list.size());
//					
//				} else {
//					throw new AhException(exception, "enterSquareAsync");
//				}
//			}
//		});
	}
	
	public void exitSquareAsync(String squareId, final AhEntityCallback<Boolean> callback) throws AhException {
		
		userTable.delete(squareId, new TableDeleteCallback() {
			
			@Override
			public void onCompleted(Exception exception, ServiceFilterResponse arg1) {
				// TODO Auto-generated method stub
				if (exception == null) {
					callback.onCompleted(true);
				} else {
					throw new AhException(exception, "exitSquareAsync");
				}
			}
		});
		
	}
	
	public void sendMessageAsync(AhMessage message, final AhEntityCallback<AhMessage> callback) throws AhException {
		
		JsonObject jo = new JsonObject();
		jo.addProperty("type", message.getType());
		jo.addProperty("content", message.getContent());
		jo.addProperty("sender", message.getSender());
		jo.addProperty("senderId", message.getSenderId());
		jo.addProperty("receiver", message.getReceiver());
		jo.addProperty("receiverId", message.getReceiverId());
		
		Gson g = new Gson();
		JsonElement json = g.fromJson(jo, JsonElement.class);
		
		mClient.invokeApi("send_message", json, new ApiJsonOperationCallback() {
			
			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse response) {
				// TODO Auto-generated method stub
				callback.onCompleted(null);
			}
		});
		
	}
}
