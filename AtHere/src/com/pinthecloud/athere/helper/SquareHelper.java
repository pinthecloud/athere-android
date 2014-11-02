package com.pinthecloud.athere.helper;

import java.util.List;

import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.util.AsyncChainer;

public class SquareHelper {

	private final String SQUARE_ID_KEY = "SQUARE_ID_KEY";
	private final String SQUARE_NAME_KEY = "SQUARE_NAME_KEY";
	private final String SQUARE_RESET_KEY = "SQUARE_RESET_KEY";
	private final String SQUARE_EXIT_TAB_KEY = "SQUARE_EXIT_TAB_KEY";
	private final String IS_LOGGED_IN_SQUARE_KEY = "IS_LOGGED_IN_SQUARE_KEY";
	private final String TIME_STAMP_AT_LOGGED_IN_SQUARE_KEY = "TIME_STAMP_AT_LOGGED_IN_SQUARE_KEY";
	private final String IS_REVIEW_KEY = "IS_REVIEW_KEY";

	private AhApplication app;
	private PreferenceHelper pref;
	private MobileServiceTable<Square> squareTable;


	public SquareHelper() {
		super();
		this.app = AhApplication.getInstance();
		this.pref = PreferenceHelper.getInstance();
		this.squareTable = app.getmClient().getTable(Square.class);
	}

	public boolean isLoggedInSquare() {
		return pref.getBoolean(IS_LOGGED_IN_SQUARE_KEY);
	}
	public Boolean isReview() {
		return pref.getBoolean(IS_REVIEW_KEY);
	}
	public int getSquareExitTab() {
		return pref.getInt(SQUARE_EXIT_TAB_KEY);
	}
	public String getTimeStampAtLoggedInSquare() {
		return pref.getString(TIME_STAMP_AT_LOGGED_IN_SQUARE_KEY);
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
	public SquareHelper setReview(boolean isReview) {
		pref.putBoolean(IS_REVIEW_KEY, isReview);
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
		pref.removePref(SQUARE_EXIT_TAB_KEY);
		pref.removePref(IS_LOGGED_IN_SQUARE_KEY);
		pref.removePref(IS_REVIEW_KEY);
	}


	/*
	 *  Async Task Methods
	 */
	public void getAllSquareList(final AhFragment frag, final AhListCallback<Square> callback) throws AhException{
		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "getAllSquareList", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		squareTable.where().execute(new TableQueryCallback<Square>() {

			@Override
			public void onCompleted(List<Square> result, int count, Exception exception,
					ServiceFilterResponse responset) {
				if (exception == null) {
					if (callback != null){
						callback.onCompleted(result, result.size());
					}
					AsyncChainer.notifyNext(frag);
				} else {
					ExceptionManager.fireException(new AhException(frag, "getAllSquareList", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}


	public void createSquareAsync(final AhFragment frag, Square square, final AhEntityCallback<Square> callback) throws AhException {
		if (!app.isOnline()) {
			ExceptionManager.fireException(new AhException(frag, "createSquareAsync", AhException.TYPE.INTERNET_NOT_CONNECTED));
			return;
		}

		squareTable.insert(square, new TableOperationCallback<Square>() {

			public void onCompleted(Square entity, Exception exception, ServiceFilterResponse response) {
				if (exception == null) {
					if(callback != null){
						callback.onCompleted(entity);	
					}
					AsyncChainer.notifyNext(frag);
				} else {
					ExceptionManager.fireException(new AhException(frag, "createSquareAsync", AhException.TYPE.SERVER_ERROR));
				}
			}
		});
	}
}
