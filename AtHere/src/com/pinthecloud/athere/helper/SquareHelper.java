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
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.util.AsyncChainer;
import com.pinthecloud.athere.util.JsonConverter;

public class SquareHelper {

	private AhApplication app;
	private MobileServiceClient mClient;
	private PreferenceHelper pref;

	private String currentSquareId;
	/**
	 * Model tables
	 */
	private MobileServiceTable<Square> squareTable;


	/*
	 * Methods name
	 */
	private final String GET_NEAR_SQUARE = "get_near_square";


	/*
	 * String
	 */
	private final String currentLatitude = "currentLatitude";
	private final String currentLongitude = "currentLongitude";


	public SquareHelper() {
		super();
		this.app = AhApplication.getInstance();
		this.mClient = app.getmClient();
		this.pref = app.getPref();
		this.squareTable = app.getSquareTable();
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


	public Square getSquare(){
		Square square = new Square();
		square.setId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY));
		square.setName(pref.getString(AhGlobalVariable.SQUARE_NAME_KEY));
		square.setResetTime(pref.getInt(AhGlobalVariable.SQUARE_RESET_KEY));
		return square;
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
