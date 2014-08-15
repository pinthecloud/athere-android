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
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;

/**
 *  Basic Fragment class for At here application
 *  Provides each instances that are needed in fragments
 * 
 */
public class AhFragment extends Fragment implements ExceptionManager.Handler{

	protected AhApplication app;
	protected Context context;
	protected AhActivity activity;
	protected PreferenceHelper pref;
	protected AhFragment _thisFragment;


	public AhFragment(){
		_thisFragment = this;
		app = AhApplication.getInstance();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * Set static value
		 */
		context = getActivity();
		activity = (AhActivity) context;
		pref = app.getPref();
		ExceptionManager.setHandler(_thisFragment);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = null;
		try {
			view = onAhCreateView(inflater, container, savedInstanceState);
		} catch (AhException ex) {
			Log(this, ex);
		} catch (Exception ex) {
			Log(this, ex);
		}
		return view;
	}


	protected View onAhCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return null;
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


	/*
	 * Check nick name EditText
	 */
	protected String checkNickName(String nickName){
		Log.d(AhGlobalVariable.LOG_TAG, "CheckNickNameEditText");

		// Set regular expression for checking nick name
		String nickNameRegx = "^[a-zA-Z0-9가-힣_-]{2,15}$";
		String message = "";

		/*
		 * Check logic whether this nick name is valid or not
		 * If user doesn't type in proper nick name,
		 * can't go to next activity
		 */
		// Check length of nick name
		if(nickName.length() < 2){
			message = getResources().getString(R.string.min_nick_name_message);
		} else if(!nickName.matches(nickNameRegx)){
			message = getResources().getString(R.string.bad_nick_name_message);
		} else if(nickName.length() > 15){
			message = getResources().getString(R.string.max_nick_name_message);
		}
		return message;
	}


	@Override
	public void handleException(final AhException ex) {
		Log(_thisFragment, "in AhFragment : " + ex.toString());
		AhAlertDialog exceptionDialog = new AhAlertDialog(ex.getType().toString(), ex.toString(), false, 
				new AhDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				// do nothing
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
				// Do nothing
			}
		}); 
		exceptionDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
	}


	/**
	 * 
	 * NOT USING METHOD
	 * BUT NEED FOR REFERENCE!!
	 * 
	 */
	//	@ExceptionHandler(target = AhFragment.class)
	//	public void myhandleException(final AhException ex) {
	//		// TODO Auto-generated method stub
	//		
	//		
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
	//				// TODO Auto-generated method stub
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
