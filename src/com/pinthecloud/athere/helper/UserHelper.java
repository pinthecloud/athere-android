package com.pinthecloud.athere.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableDeleteCallback;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.interfaces.AhCarrier;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.interfaces.AhPairEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.util.BitmapUtil;
import com.pinthecloud.athere.util.FileUtil;

public class UserHelper {

	private AhApplication app;
	private PreferenceHelper pref;
	private Object lock;

	/**
	 * Model tables
	 */
	private MobileServiceTable<User> userTable;

	/*
	 * GCM server key
	 */
	private final String GCM_SENDER_ID = "838051405989";


	public UserHelper() {
		super();
		this.app = AhApplication.getInstance();
		this.pref = app.getPref();
		this.lock = app.getLock();
		this.userTable = app.getUserTable();
	}


	public boolean exitSquareSync(final AhFragment frag, String userId) throws AhException {
		
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "exitSquareSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return false;
		}
		
		final AhCarrier<Boolean> carrier = new AhCarrier<Boolean>();

		userTable.delete(userId, new TableDeleteCallback() {

			@Override
			public void onCompleted(Exception exception, ServiceFilterResponse arg1) {
				if (exception == null) {
					carrier.load(true);
					synchronized (lock) {
						lock.notify();
					}
				} else {
					ExceptionManager.fireException(new AhException(frag, "exitSquareSync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return carrier.getItem();
	}


	public void exitSquareAsync(final AhFragment frag, String userId, final AhEntityCallback<Boolean> callback) throws AhException {
		
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "exitSquareAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}
		
		userTable.delete(userId, new TableDeleteCallback() {

			@Override
			public void onCompleted(Exception e, ServiceFilterResponse response) {
				if (e == null) {
					callback.onCompleted(true);
				} else {
					ExceptionManager.fireException(new AhException(frag, "exitSquareAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}

	public void enterSquareAsync(final AhFragment frag, User user, final AhEntityCallback<String> callback) throws AhException {
		
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "enterSquareAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}
		
		userTable.insert(user, new TableOperationCallback<User>() {

			@Override
			public void onCompleted(User entity, Exception exception, ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(entity.getId());
				} else {
					ExceptionManager.fireException(new AhException(frag, "enterSquareAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}


	public String enterSquareSync(final AhFragment frag, User user) throws AhException {
		
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "enterSquareSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return null;
		}
		
		final AhCarrier<String> carrier = new AhCarrier<String>();

		userTable.insert(user, new TableOperationCallback<User>() {

			@Override
			public void onCompleted(User entity, Exception e, ServiceFilterResponse response) {
				if (e == null) {
					carrier.load(entity.getId());
					synchronized (lock) {
						lock.notify();
					}
				} else {
					ExceptionManager.fireException(new AhException(frag, "enterSquareSync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return carrier.getItem();
	}


	public void getUserListAsync(final AhFragment frag, String squareId, final AhListCallback<User> callback){
		
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getUserListAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}
		
		userTable.where().field("squareId").eq(squareId).execute(new TableQueryCallback<User>() {

			@Override
			public void onCompleted(List<User> result, int count, Exception exception,
					ServiceFilterResponse reponse) {
				if (exception == null) {
					callback.onCompleted(result, count);
				} else {
					ExceptionManager.fireException(new AhException(frag, "getUserListAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}

	public List<User> getUserListSync(final AhFragment frag, String squareId){
		
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getUserListSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return null;
		}
		
		final AhCarrier<List<User>> carrier = new AhCarrier<List<User>>();

		userTable.where().field("squareId").eq(squareId).execute(new TableQueryCallback<User>() {

			@Override
			public void onCompleted(List<User> result, int count, Exception exception,
					ServiceFilterResponse reponse) {
				if (exception == null) {
					carrier.load(result);
					synchronized (lock) {
						lock.notify();
					}
				} else {
					ExceptionManager.fireException(new AhException(frag, "getUserListSync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return carrier.getItem();
	}


	public void getUserAsync(final AhFragment frag, String id, final AhEntityCallback<User> callback) {
		
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getUserAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}
		
		userTable.where().field("id").eq(id).execute(new TableQueryCallback<User>() {

			@Override
			public void onCompleted(List<User> result, int count, Exception exception,
					ServiceFilterResponse reponse) {
				if (exception == null && result.size() == 1) {
					callback.onCompleted(result.get(0));
				} else {
					ExceptionManager.fireException(new AhException(frag, "getUserAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}


	public User getUserSync(final AhFragment frag, String id) {
		
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getUserSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return null;
		}

		if (id == null) if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getUserSync", AhException.TYPE.NO_USER_ID));
			return null;
		}
		final AhCarrier<User> carrier = new AhCarrier<User>();

		userTable.where().field("id").eq(id).execute(new TableQueryCallback<User>() {

			@Override
			public void onCompleted(List<User> result, int count, Exception exception,
					ServiceFilterResponse reponse) {
				if (exception == null && result.size() == 1) {
					carrier.load(result.get(0));
					synchronized (lock) {
						lock.notify();
					}
				} else {
					ExceptionManager.fireException(new AhException(frag, "getUserSync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return carrier.getItem();
	}


	public void updateUserAsync(final AhFragment frag, User user, final AhEntityCallback<User> callback){

		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "updateUserAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}
		
		userTable.update(user, new TableOperationCallback<User>() {

			@Override
			public void onCompleted(User entity, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(entity);
				} else {
					ExceptionManager.fireException(new AhException(frag, "updateUserAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}

	public void updateMyUserAsync(final AhFragment frag, AhEntityCallback<User> callback){
		User user = this.getMyUserInfo(true);
		this.updateUserAsync(frag, user, callback);
	}

	public User getMyUserInfo(boolean hasId) {
		Bitmap pictureBitmap = null;
		try {
			pictureBitmap = FileUtil.getImageFromInternalStorage(app, AhGlobalVariable.PROFILE_PICTURE_NAME);
		} catch (FileNotFoundException e) {
			pictureBitmap = BitmapFactory.decodeResource(app.getResources(), R.drawable.splash);
			Log.d(AhGlobalVariable.LOG_TAG, "SquareProfileFragment enterSquare : " + e.getMessage());
		}
		String profilePic = BitmapUtil.convertToString(pictureBitmap);

		User user = new User();
		if(hasId)
			user.setId(pref.getString(AhGlobalVariable.USER_ID_KEY));
		user.setNickName(pref.getString(AhGlobalVariable.NICK_NAME_KEY));
		user.setProfilePic(profilePic);
		user.setRegistrationId(pref.getString(AhGlobalVariable.REGISTRATION_ID_KEY));
		user.setCompanyNum(pref.getInt(AhGlobalVariable.COMPANY_NUMBER_KEY));
		user.setAge(pref.getInt(AhGlobalVariable.AGE_KEY));
		user.setMale(pref.getBoolean(AhGlobalVariable.IS_MALE_KEY));
		user.setSquareId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY));
		user.setChupaEnable(pref.getBoolean(AhGlobalVariable.IS_CHUPA_ENABLE_KEY));
		return user;
	}


	public String getRegistrationIdSync(final AhFragment frag) {
		
		if (!AhApplication.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getRegistrationIdSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return null;
		}
		
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(app);
		String registrationId = "";
		try {
			registrationId = gcm.register(GCM_SENDER_ID);
		} catch (IOException e) {
			ExceptionManager.fireException(new AhException(frag, "getRegistrationIdSync", AhException.TYPE.GCM_REGISTRATION_FAIL));
			return null;
		}
		return registrationId;
	}
	
	public String UnRegistrationIdSync() {
		
//		if (!AhApplication.isOnline()) throw new AhException(AhException.TYPE.INTERNET_NOT_CONNECTED);
//		
//		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(app);
//		String registrationId = "";
//		try {
//			registrationId = gcm.register(GCM_SENDER_ID);
//		} catch (IOException e) {
//			throw new AhException(e, "getRegistrationIdSync", AhException.TYPE.GCM_REGISTRATION_FAIL);
//		}
//		return registrationId;
		return null;
	}


	private Map<String, AhEntityCallback<User>> map = new HashMap<String, AhEntityCallback<User>>();

	private final String USER_RECEIVED = "USER_RECEIVED";

	public void setUserHandler(AhEntityCallback<User> callback){
		map.put(USER_RECEIVED, callback);
	}

	public void triggerUserEvent(User user){
		AhEntityCallback<User> callback = map.get(USER_RECEIVED);
		Log.d(AhGlobalVariable.LOG_TAG,"in triggerUserEvent");
		if(callback != null)
			callback.onCompleted(user);
		else 
			Log.d(AhGlobalVariable.LOG_TAG, "No Such method in triggerUserEvent");
	}
}
