package com.pinthecloud.athere.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

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
import com.pinthecloud.athere.interfaces.AhCarrier;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhException;
import com.pinthecloud.athere.interfaces.AhListCallback;
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


	public boolean exitSquareSync(String userId) throws AhException {
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
					throw new AhException(exception, "exitSquareAsync");
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


	public void exitSquareAsync(String userId, final AhEntityCallback<Boolean> callback) throws AhException {
		userTable.delete(userId, new TableDeleteCallback() {

			@Override
			public void onCompleted(Exception e, ServiceFilterResponse response) {
				if (e == null) {
					callback.onCompleted(true);
				} else {
					throw new AhException(e, "exitSquareAsync : " + e.getMessage());
				}
			}
		});
	}


	public void enterSquareAsync(User user, final AhEntityCallback<String> callback) throws AhException {
		userTable.insert(user, new TableOperationCallback<User>() {

			@Override
			public void onCompleted(User entity, Exception exception, ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(entity.getId());
				} else {
					throw new AhException(exception, "enterSquareAsync");
				}
			}
		});
	}


	public String enterSquareSync(User user) throws AhException {
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
					throw new AhException(e, "enterSquareAsync : " + e.getMessage());
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				throw new AhException(e, "enterSquareSync");
			}
		}

		return carrier.getItem();
	}


	public void getUserListAsync(String squareId, final AhListCallback<User> callback){
		userTable.where().field("squareId").eq(squareId).execute(new TableQueryCallback<User>() {

			@Override
			public void onCompleted(List<User> result, int count, Exception exception,
					ServiceFilterResponse reponse) {
				if (exception == null) {
					callback.onCompleted(result, count);
				} else {
					throw new AhException(exception, "enterSquareAsync");
				}
			}
		});
	}


	public List<User> getUserListSync(String squareId){
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
					throw new AhException(exception, "enterSquareAsync");
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				throw new AhException(e, "enterSquareSync");
			}
		}

		return carrier.getItem();
	}


	public void getUserAsync(String id, final AhEntityCallback<User> callback) {
		userTable.where().field("id").eq(id).execute(new TableQueryCallback<User>() {

			@Override
			public void onCompleted(List<User> result, int count, Exception exception,
					ServiceFilterResponse reponse) {
				if (exception == null && result.size() == 1) {
					callback.onCompleted(result.get(0));
				} else {
					throw new AhException(exception, "enterSquareAsync");
				}
			}
		});
	}


	public User getUserSync(String id) {
		
		if (id == null) return null;
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
					throw new AhException(exception, "enterSquareAsync");
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				throw new AhException(e, "enterSquareSync");
			}
		}

		return carrier.getItem();
	}
	
	public User getMyUserInfo(boolean hasId) {
		Bitmap pictureBitmap = null;
		try {
			pictureBitmap = FileUtil.getImageFromInternalStorage
					(app, AhGlobalVariable.PROFILE_PICTURE_CIRCLE_NAME);
		} catch (FileNotFoundException e) {
			pictureBitmap = BitmapFactory.decodeResource(app.getResources(), R.drawable.splash);
			pictureBitmap = BitmapUtil.cropRound(pictureBitmap);
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


	public String getRegistrationIdSync() throws IOException{
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(app);
		String registrationId = "";
		try {
			registrationId = gcm.register(GCM_SENDER_ID);
		} catch (IOException e) {
			throw e;
		}
		return registrationId;
	}
}
