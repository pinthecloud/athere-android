package com.pinthecloud.athere.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableDeleteCallback;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.interfaces.AhCarrier;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhException;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.UserDBHelper;
import com.pinthecloud.athere.util.BitmapUtil;
import com.pinthecloud.athere.util.FileUtil;
import com.pinthecloud.athere.util.JsonConverter;

public class UserHelper {

	private AhApplication app;
	private MobileServiceClient mClient;
	private PreferenceHelper pref;
	private Object lock;

	/**
	 * Model tables
	 */
	private MobileServiceTable<User> userTable;

	/*
	 * Methods name
	 */
	private final String ENTER_SQUARE = "enter_square";

	/*
	 * GCM server key
	 */
	private final String GCM_SENDER_ID = "838051405989";


	public UserHelper() {
		super();
		this.app = AhApplication.getInstance();
		this.mClient = app.getmClient();
		this.pref = app.getPref();
		this.lock = app.getLock();
		this.userTable = app.getUserTable();
	}


	public boolean exitSquareSync(String squareId) throws AhException {
		final AhCarrier<Boolean> carrier = new AhCarrier<Boolean>();

		userTable.delete(squareId, new TableDeleteCallback() {

			@Override
			public void onCompleted(Exception exception, ServiceFilterResponse arg1) {
				if (exception == null) {
					carrier.load(true);
					synchronized (lock) {
						lock.notify();
					}
				} else {
					carrier.load(false);
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


	public void enterSquareAsync(User user, final AhEntityCallback<Boolean> callback) throws AhException {
		userTable.insert(user, new TableOperationCallback<User>() {

			@Override
			public void onCompleted(User entity, Exception exception, ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(true);
				} else {
					throw new AhException(exception, "enterSquareAsync");
				}
			}
		});
	}


	public boolean enterSquareSync(User user) throws AhException, InterruptedException {
		final AhCarrier<List<User>> carrier = new AhCarrier<List<User>>();

		JsonObject jo = user.toJson();

		Gson g = new Gson();
		JsonElement requestJson = g.fromJson(jo, JsonElement.class);

		mClient.invokeApi(ENTER_SQUARE, requestJson, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null){
					List<User> list = JsonConverter.convertToUserList(json);
					carrier.load(list);
					synchronized (lock) {
						lock.notify();
					}
				} else {
					throw new AhException(exception, "enterSquareSync");
				}
			}
		});

		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				throw e;
			}
		}

		UserDBHelper userHelper = app.getUserDBHelper();
		userHelper.addAllUsers(carrier.getItem());
		return true;
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
		user.setSquareId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY));
		user.setMale(pref.getBoolean(AhGlobalVariable.IS_MALE_KEY));
		user.setAge(pref.getInt(AhGlobalVariable.AGE_KEY));
		user.setRegistrationId(pref.getString(AhGlobalVariable.REGISTRATION_ID_KEY));
		user.setCompanyNum(pref.getInt(AhGlobalVariable.COMPANY_NUMBER_KEY));
		return user;
	}


	public String getRegistrationIdSync(){
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(app);
		String registrationId = "";
		try {
			registrationId = gcm.register(GCM_SENDER_ID);
		} catch (IOException e) {
			throw new AhException(e, "getRegistrationIdSync");
		}
		return registrationId;
	}


	//	public void isAvailableNickName(User user, final AhEntityCallback<Boolean> callback) throws AhException {
	//	
	//	userTable.where().field("nickName").eq(val(true)).execute(new TableQueryCallback<User>() {
	//
	//		@Override
	//		public void onCompleted(List<User> result, int count, Exception exception, ServiceFilterResponse response) {
	//			// Succeed
	//			if (exception == null) {
	//				if (count > 0) callback.onCompleted(false);
	//				else callback.onCompleted(true);
	//			// failed
	//			} else {
	//				throw new AhException(exception, "isAvailableNickName");
	//			}
	//		}
	//	});
	//}
}
