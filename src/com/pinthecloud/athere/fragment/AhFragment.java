package com.pinthecloud.athere.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.AhActivity;
import com.pinthecloud.athere.analysis.GAHelper;
import com.pinthecloud.athere.database.MessageDBHelper;
import com.pinthecloud.athere.database.UserDBHelper;
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.helper.SquareHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;

/**
 *  Basic Fragment class for At here application
 *  Provides each instances that are needed in fragments
 * 
 */
public class AhFragment extends Fragment implements ExceptionManager.Handler{

	protected AhFragment _thisFragment;
	protected AhApplication app;
	protected Context context;
	protected AhActivity activity;
	protected PreferenceHelper pref;

	protected MessageHelper messageHelper;
	protected MessageDBHelper messageDBHelper;
	protected UserHelper userHelper;
	protected UserDBHelper userDBHelper;
	protected SquareHelper squareHelper;
	protected CachedBlobStorageHelper blobStorageHelper;
	protected GAHelper gaHelper;
	protected String simpleClassName;


	public AhFragment(){
		_thisFragment = this;
		app = AhApplication.getInstance();
		pref = app.getPref();
		messageHelper = app.getMessageHelper();
		messageDBHelper = app.getMessageDBHelper();
		userHelper = app.getUserHelper();
		userDBHelper = app.getUserDBHelper();
		squareHelper = app.getSquareHelper();
		blobStorageHelper = app.getBlobStorageHelper();
		gaHelper = app.getGAHelper();
		simpleClassName = _thisFragment.getClass().getSimpleName();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		context = getActivity();
		activity = (AhActivity) context;
		super.onCreate(savedInstanceState);
		Log.d(AhGlobalVariable.LOG_TAG, simpleClassName + " onCreate");


		/* 
		 * Set google analytics
		 */
		gaHelper.sendScreenGA(simpleClassName);


		/*
		 * Set static value
		 */
		ExceptionManager.setHandler(_thisFragment);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(AhGlobalVariable.LOG_TAG, simpleClassName + " onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}


	@Override
	public void onStart() {
		super.onStart();
		Log.d(AhGlobalVariable.LOG_TAG, simpleClassName + " onStart");
		gaHelper.reportActivityStart(activity);
	}


	@Override
	public void onStop() {
		Log.d(AhGlobalVariable.LOG_TAG, simpleClassName + " onStop");
		super.onStop();
		gaHelper.reportActivityStop(activity);
	}


	@Override
	public void handleException(final AhException ex) {
		Log(_thisFragment, "AhFragment handleException : " + ex.toString());

		AhAlertDialog exceptionDialog = null;
		String title = ex.getType().toString();
		String message = ex.toString();
		if(ex.getType().equals(AhException.TYPE.INTERNET_NOT_CONNECTED)){
			title = null;
			message = getResources().getString(R.string.internet_not_connected_message);
		}
		exceptionDialog = new AhAlertDialog(title, message, false, new AhDialogCallback() {
			@Override
			public void doPositiveThing(Bundle bundle) {
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(1);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
				// Do nothing
			}
		}); 
		exceptionDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
	}


	protected void Log(AhFragment fragment, Object... params){
		Log.e("ERROR", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		Log.e("ERROR", "[ "+fragment.getClass().getName() + " ]");
		for(Object str : params) {
			if (str == null) {
				Log.e("ERROR", "null");
				continue;
			}
			Log.e("ERROR", str.toString());
		}
		Log.e("ERROR", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}


	/**
	 * 
	 * NOT USING METHOD
	 * BUT NEED FOR REFERENCE!!
	 * 
	 */
	//	@ExceptionHandler(target = AhFragment.class)
	//	public void myhandleException(final AhException ex) {
	//		
	//		Method[] ms = pair.getClazz().getMethods();
	//		for(Method method : ms) {
	//			ExceptionHandler annos = method.getAnnotation(ExceptionHandler.class);
	//            if (annos != null) {
	//                try {
	//                	Class<?> target = annos.target();
	//                	Log(_thisFragment, "target : " + target.getName());
	//                	
	//                	method.invoke(pair.getFrag(), ex);
	//                } catch (Exception e) {
	//                    e.printStackTrace();
	//                }
	//            }
	//		}
	//	}
	//		
	//		activity.runOnUiThread(new Runnable() {
	//		
	//			@Override
	//			public void run() {
	//				new AlertDialog.Builder(_thisFragment.getActivity())
	//				.setTitle(ex.getType().toString())
	//				.setMessage(ex.toString())
	//				.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
	//			        public void onClick(DialogInterface dialog, int whichButton) {
	//			            //Your action here
	//			        	dialog.dismiss();
	//			        }
	//			    })
	//		        .show();
	//			}
	//		});
	//	}
}
