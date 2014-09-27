package com.pinthecloud.athere.helper;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.util.AsyncChainer;
import com.pinthecloud.athere.util.JsonConverter;

public class SquareHelper {

	private final String SQUARE_ID_KEY = "SQUARE_ID_KEY";
	private final String SQUARE_NAME_KEY = "SQUARE_NAME_KEY";
	private final String SQUARE_RESET_KEY = "SQUARE_RESET_KEY";
	private final String SQUARE_EXIT_TAB_KEY = "SQUARE_EXIT_TAB_KEY";
	private final String IS_LOGGED_IN_SQUARE_KEY = "IS_LOGGED_IN_SQUARE_KEY";
	private final String TIME_STAMP_AT_LOGGED_IN_SQUARE_KEY = "TIME_STAMP_AT_LOGGED_IN_SQUARE_KEY";
	private final String IS_PREVIEW_KEY = "IS_PREVIEW_KEY";
	

	private final String GET_NEAR_SQUARE = "get_near_square";
	
	private final String currentLatitude = "currentLatitude";
	private final String currentLongitude = "currentLongitude";
	
	private AhApplication app;
	private MobileServiceClient mClient;
	private PreferenceHelper pref;
	private MobileServiceTable<Square> squareTable;


	public SquareHelper() {
		super();
		this.app = AhApplication.getInstance();
		this.mClient = app.getmClient();
		this.pref = PreferenceHelper.getInstance();
		this.squareTable = app.getmClient().getTable(Square.class);
	}

	public boolean isLoggedInSquare() {
		return pref.getBoolean(IS_LOGGED_IN_SQUARE_KEY);
	}
	public int getSquareExitTab() {
		return pref.getInt(SQUARE_EXIT_TAB_KEY);
	}
	public String getTimeStampAtLoggedInSquare() {
		return pref.getString(TIME_STAMP_AT_LOGGED_IN_SQUARE_KEY);
	}
	public Boolean isPreview() {
		return pref.getBoolean(IS_PREVIEW_KEY);
	}
	public SquareHelper setMySquareId(String id) {
		pref.putString(SQUARE_ID_KEY, id);
		return this;
	}
	public SquareHelper setMySquareName(String name) {
		pref.putString(SQUARE_NAME_KEY, name);
		return this;
	} 
	public SquareHelper setMySquareResetTime(int resetTime) {
		pref.putInt(SQUARE_RESET_KEY, resetTime);
		return this;
	} 
	public SquareHelper setLoggedInSquare(boolean isLogged) {
		pref.putBoolean(IS_LOGGED_IN_SQUARE_KEY, isLogged);
		return this;
	}
	public SquareHelper setSquareExitTab(int pos) {
		pref.putInt(SQUARE_EXIT_TAB_KEY, pos);
		return this;
	}
	public SquareHelper setTimeStampAtLoggedInSquare(String time) {
		pref.putString(TIME_STAMP_AT_LOGGED_IN_SQUARE_KEY, time);
		return this;
	}
	public SquareHelper setPreview(Boolean isPreview) {
		pref.putBoolean(IS_PREVIEW_KEY, isPreview);
		return this;
	}
	
	
	public Square getMySquareInfo(){
		Square square = new Square();
		square.setId(pref.getString(SQUARE_ID_KEY));
		square.setName(pref.getString(SQUARE_NAME_KEY));
		square.setResetTime(pref.getInt(SQUARE_RESET_KEY));
		return square;
	}
	
	
	public void removeMySquareInfo() {
		pref.removePref(SQUARE_ID_KEY);
		pref.removePref(SQUARE_NAME_KEY);
		pref.removePref(SQUARE_RESET_KEY);
		pref.removePref(IS_LOGGED_IN_SQUARE_KEY);
		pref.removePref(SQUARE_EXIT_TAB_KEY);
//		pref.removePref(TIME_STAMP_AT_LOGGED_IN_SQUARE_KEY);
	}	
	
	
	/*
	 *  Async Task Methods
	 */
	public void getSquareListAsync(final AhFragment frag, double latitude, double longitude, final AhListCallback<Square> callback) throws AhException{
		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getSquareListAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		JsonObject jo = new JsonObject();
		jo.addProperty(currentLatitude, latitude);
		jo.addProperty(currentLongitude, longitude);

		Gson g = new Gson();
		JsonElement json = g.fromJson(jo, JsonElement.class);

		mClient.invokeApi(GET_NEAR_SQUARE, json, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					List<Square> list = JsonConverter.convertToSquareList(json.getAsJsonArray());
					if (list == null) {
						ExceptionManager.fireException(new AhException(frag, "getSquareListAsync", AhException.TYPE.PARSING_ERROR));
						return;
					}
					callback.onCompleted(list, list.size());
					AsyncChainer.notifyNext(frag);
				} else {
					ExceptionManager.fireException(new AhException(frag, "getSquareListAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}


	public void createSquareAsync(final AhFragment frag, String name, String whoMade, double latitude, double longitude, boolean isAdmin, 
			String code, int showRange, int entryRange, int resetTime, final AhEntityCallback<Square> callback) throws AhException {
		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "createSquareAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		Square square = new Square();
		square.setName(name);
		square.setLatitude(latitude);
		square.setLongitude(longitude);
		square.setWhoMade(whoMade);
		square.setCode(code);
		square.setAdmin(isAdmin);
		square.setShowRange(showRange);
		square.setEntryRange(entryRange);
		square.setResetTime(resetTime);
		square.setMaleNum(0);
		square.setFemaleNum(0);

		squareTable.insert(square, new TableOperationCallback<Square>() {

			public void onCompleted(Square entity, Exception exception, ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(entity);
					AsyncChainer.notifyNext(frag);
				} else {
					ExceptionManager.fireException(new AhException(frag, "createSquareAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}
	
	
	/*
	 * Sync Method
	 * NOT USING
	 */

	//	public List<Square> _getSquareListSync(final AhFragment frag, double latitude, double longitude) throws AhException {
	//		
	//		if (!app.isOnline()) {
	//			ExceptionManager.fireException(new AhException(frag, "getSquareListSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
	//			return null;
	//		}
	//		
	//		final AhCarrier<List<Square>> carrier = new AhCarrier<List<Square>>();
	//		
	//		JsonObject jo = new JsonObject();
	//		jo.addProperty(currentLatitude, latitude);
	//		jo.addProperty(currentLongitude, longitude);
	//
	//		Gson g = new Gson();
	//		JsonElement json = g.fromJson(jo, JsonElement.class);
	//
	//		mClient.invokeApi(GET_NEAR_SQUARE, json, new ApiJsonOperationCallback() {
	//
	//			@Override
	//			public void onCompleted(JsonElement json, Exception exception,
	//					ServiceFilterResponse response) {
	//				if ( exception == null) {
	//					List<Square> list = JsonConverter.convertToSquareList(json.getAsJsonArray());
	//					if (list == null) {
	//						ExceptionManager.fireException(new AhException(frag, "getSquareListSync", AhException.TYPE.PARSING_ERROR));
	//						return;
	//					}
	//					carrier.load(list);
	//					synchronized (lock) {
	//						lock.notify();
	//					}
	//				} else {
	//					ExceptionManager.fireException(new AhException(frag, "getSquareListSync", AhException.TYPE.SERVER_ERROR));
	//				}
	//			}
	//		});
	//
	//		synchronized (lock) {
	//			try {
	//				lock.wait();
	//			} catch (InterruptedException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//		return carrier.getItem();
	//	}
	//
	//	public Square _createSquareSync(AhFragment frag, String name, double latitude, double longitude) throws AhException {
	//		
	//		String whoMade = pref.getString(AhGlobalVariable.REGISTRATION_ID_KEY);
	//
	//		if (whoMade.equals(PreferenceHelper.DEFAULT_STRING)) {
	//			throw new AhException(frag, "createSquareSync", AhException.TYPE.GCM_REGISTRATION_FAIL);
	//		}
	//		Square square = new Square();
	//
	//		square.setName(name);
	//		square.setLatitude(latitude);
	//		square.setLongitude(longitude);
	//		square.setWhoMade(whoMade);
	//		
	//		int maleNum = 0;
	//		int femaleNum = 0;
	//		
	//		if(pref.getBoolean(AhGlobalVariable.IS_MALE_KEY)) maleNum++;
	//		else femaleNum++;
	//		
	//		square.setMaleNum(maleNum);
	//		square.setFemaleNum(femaleNum);
	//
	//		return this._createSquareSync(frag, square);
	//	}
	//
	//
	//	public Square _createSquareSync(final AhFragment frag, Square square) throws AhException {
	//		
	//		if (!app.isOnline()) {
	//			ExceptionManager.fireException(new AhException(frag, "createSquareSync", AhException.TYPE.INTERNET_NOT_CONNECTED));
	//			return null;
	//		}
	//		final AhCarrier<Square> carrier = new AhCarrier<Square>();
	//
	//		squareTable.insert(square, new TableOperationCallback<Square>() {
	//
	//			public void onCompleted(Square entity, Exception exception, ServiceFilterResponse response) {
	//
	//				if (exception == null) {
	//					carrier.load(entity);
	//					synchronized (lock) {
	//						lock.notify();
	//					}
	//				} else {
	//					ExceptionManager.fireException(new AhException(frag, "createSquareSync", AhException.TYPE.SERVER_ERROR));
	//				}
	//			}
	//		});
	//
	//		synchronized (lock) {
	//			try {
	//				lock.wait();
	//			} catch (InterruptedException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//
	//		return carrier.getItem();
	//	}
}
