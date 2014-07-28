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


	public boolean exitSquareSync(String squareId) throws AhException {
		final AhCarrier<Boolean> carrier = new AhCarrier<Boolean>();
		
		this.exitSquareAsync(squareId, new AhEntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean entity) {
				// TODO Auto-generated method stub
				carrier.load(true);
				synchronized (lock) {
					lock.notify();
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


	public void exitSquareAsync(String squareId, final AhEntityCallback<Boolean> callback) throws AhException {
		userTable.delete(squareId, new TableDeleteCallback() {

			@Override
			public void onCompleted(Exception exception, ServiceFilterResponse arg1) {
				if (exception == null) {
					callback.onCompleted(true);
				} else {
					throw new AhException(exception, "exitSquareAsync");
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
		
		this.enterSquareAsync(user, new AhEntityCallback<String>() {

			@Override
			public void onCompleted(String entity) {
				// TODO Auto-generated method stub
				carrier.load(entity);
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

		//		JsonObject jo = user.toJson();
		//
		//		Gson g = new Gson();
		//		JsonElement requestJson = g.fromJson(jo, JsonElement.class);
		//
		//		mClient.invokeApi(ENTER_SQUARE, requestJson, new ApiJsonOperationCallback() {
		//
		//			@Override
		//			public void onCompleted(JsonElement json, Exception exception,
		//					ServiceFilterResponse response) {
		//				if (exception == null){
		//					List<User> list = JsonConverter.convertToUserList(json);
		//					carrier.load(list);
		//					synchronized (lock) {
		//						lock.notify();
		//					}
		//				} else {
		//					throw new AhException(exception, "enterSquareSync");
		//				}
		//			}
		//		});
		//
		//		synchronized (lock) {
		//			try {
		//				lock.wait();
		//			} catch (InterruptedException e) {
		//				throw new AhException(e, "enterSquareSync");
		//			}
		//		}
		//
		//		UserDBHelper userDBHelper = app.getUserDBHelper();
		//		userDBHelper.addAllUsers(carrier.getItem());
		//		return true;
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

		this.getUserListAsync(squareId, new AhListCallback<User>() {

			@Override
			public void onCompleted(List<User> list, int count) {
				// TODO Auto-generated method stub
				carrier.load(list);
				synchronized (lock) {
					lock.notify();
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
				if (exception == null) {
					callback.onCompleted(result.get(0));
				} else {
					throw new AhException(exception, "enterSquareAsync");
				}
			}
		});
	}


	public User getUserSync(String id) {
		final AhCarrier<User> carrier = new AhCarrier<User>();

		this.getUserAsync(id, new AhEntityCallback<User>() {

			@Override
			public void onCompleted(User entity) {
				// TODO Auto-generated method stub
				carrier.load(entity);
				synchronized (lock) {
					lock.notify();
				}
			}
		});
		
//		userTable.where().field("id").eq(id).execute(new TableQueryCallback<User>() {
//
//			@Override
//			public void onCompleted(List<User> result, int count, Exception exception,
//					ServiceFilterResponse reponse) {
//				if (exception == null) {
//					carrier.load(result.get(0));
//					synchronized (lock) {
//						lock.notify();
//					}
//				} else {
//					throw new AhException(exception, "enterSquareAsync");
//				}
//			}
//		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				throw new AhException(e, "enterSquareSync");
			}
		}

		return carrier.getItem();
	}


	public User getUser() {
		Bitmap pictureBitmap = null;
		try {
			pictureBitmap = FileUtil.getImageFromInternalStorage
					(app, AhGlobalVariable.PROFILE_PICTURE_CIRCLE_NAME);
		} catch (FileNotFoundException e) {
			pictureBitmap = BitmapFactory.decodeResource(app.getResources(), R.drawable.profile_default_image);
			pictureBitmap = BitmapUtil.cropRound(pictureBitmap);
			Log.d(AhGlobalVariable.LOG_TAG, "SquareProfileFragment enterSquare : " + e.getMessage());
		}
		String profilePic = BitmapUtil.convertToString(pictureBitmap);

		User user = new User();
		user.setNickName(pref.getString(AhGlobalVariable.NICK_NAME_KEY));
		user.setProfilePic(profilePic);
		user.setRegistrationId(pref.getString(AhGlobalVariable.REGISTRATION_ID_KEY));
		user.setCompanyNum(pref.getInt(AhGlobalVariable.COMPANY_NUMBER_KEY));
		user.setAge(pref.getInt(AhGlobalVariable.AGE_KEY));
		user.setMale(pref.getBoolean(AhGlobalVariable.IS_MALE_KEY));
		user.setSquareId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY));
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
