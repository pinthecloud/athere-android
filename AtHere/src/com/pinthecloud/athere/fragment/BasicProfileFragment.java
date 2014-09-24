package com.pinthecloud.athere.fragment;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhIdUser;


public class BasicProfileFragment extends AhFragment{

	private ProgressBar progressBar;
	private EditText nickNameEditText;
	private EditText birthYearEditText;
//	private NumberPickerDialog yearPickerDialog;
	private RadioButton maleButton;
	private RadioButton femaleButton;
	private ImageButton completeButton;

	private boolean isTypedNickName = false;
//	private boolean isPickedBirthYear = false;

	private ImageView sudoImage;
	
	private int countSudo = 0;
	
	private LoginButton fbBtn;
	public UiLifecycleHelper uiHelper;
	
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log(thisFragment, error);
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log(thisFragment, "onComplete", data);
        }
    };


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_basic_profile, container, false);		

		uiHelper = new UiLifecycleHelper(this.getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
		/*
		 * Find UI component
		 */
		progressBar = (ProgressBar) view.findViewById(R.id.basic_profile_frag_progress_bar);
		nickNameEditText = (EditText) view.findViewById(R.id.basic_profile_frag_nick_name_edit_text);
		birthYearEditText = (EditText) view.findViewById(R.id.basic_profile_frag_year_text);
		maleButton = (RadioButton) view.findViewById(R.id.basic_profile_frag_male_button);
		femaleButton = (RadioButton) view.findViewById(R.id.basic_profile_frag_female_button);
		completeButton = (ImageButton) view.findViewById(R.id.basic_profile_frag_complete_button);
		sudoImage = (ImageView) view.findViewById(R.id.basic_profile_frag_image_view_for_su);
		fbBtn = (LoginButton) view.findViewById(R.id.facebook_login_button);
		
		/*
		 * Super user
		 */
		sudoImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				countSudo++;
				if (countSudo == 10) {
					pref.putBoolean(AhGlobalVariable.SUDO_KEY, true);
					Toast.makeText(activity, "Super User Activated!", Toast.LENGTH_LONG)
					.show();;
				}
			}
		});


		/*
		 * Set nick name edit text
		 */
		nickNameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String nickName = s.toString().trim();
				if(nickName.length() < 1){
					isTypedNickName = false;
				}else{
					isTypedNickName = true;
				}
				completeButton.setEnabled(isCompleteButtonEnable());
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});


		/*
		 * Set birth year edit text and year picker dialog
		 */
//		yearPickerDialog = new NumberPickerDialog(1950, 2000, 1990, new AhDialogCallback() {
//
//			@Override
//			public void doPositiveThing(Bundle bundle) {
//				// Set edit text to birth year use picked
//				int birthYear = bundle.getInt(AhGlobalVariable.NUMBER_PICKER_VALUE_KEY);
//				birthYearEditText.setText("" + birthYear);
//			}
//
//			@Override
//			public void doNegativeThing(Bundle bundle) {
//				// do nothing				
//			}
//		});
//		birthYearEditText.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				String birthYear = s.toString().trim();
//				if(birthYear.length() < 1){
//					isPickedBirthYear = false;
//				}else{
//					isPickedBirthYear = true;
//				}
//				completeButton.setEnabled(isCompleteButtonEnable());
//			}
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//			}
//			@Override
//			public void afterTextChanged(Editable s) {
//			}
//		});
//		birthYearEditText.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				yearPickerDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
//			}
//		});
//		birthYearEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if(hasFocus){
//					yearPickerDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
//				}
//			}
//		});
		birthYearEditText.setInputType(InputType.TYPE_NULL);
//		birthYearEditText.setEnabled(false);
		birthYearEditText.setClickable(false);
//		birthYearEditText.setFocusable(false);
//		birthYearEditText.setFocusableInTouchMode(false);
		
		maleButton.setEnabled(false);
		femaleButton.setEnabled(false);
//		maleButton.setClickable(false);
//		maleButton.setFocusable(false);
//		maleButton.setFocusableInTouchMode(false);

		/*
		 * Set Start Button
		 */
		completeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Remove blank of along side.
				Session session = Session.getActiveSession();
				if (session.isClosed()) {
					return;
				}
				String nickName = nickNameEditText.getText().toString().trim();
				nickNameEditText.setText(nickName);
				nickNameEditText.setSelection(nickName.length());


				// Save gender and birth year infomation to preference
				String message = app.checkNickName(nickName);
				if(!message.equals("")){
					// Unproper nick name
					// Show warning toast for each situation
					Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
					toast.show();
				} else{
					// Proper nick name
					// Show progress bar
					// Disable complete button for preventing double click
					progressBar.setVisibility(View.VISIBLE);
					completeButton.setEnabled(false);


					// Save this setting and go to next activity
					String androidId = pref.getString(AhGlobalVariable.ANDROID_ID_KEY);
					String facebookId = pref.getString(AhGlobalVariable.FACEBOOK_ID);
					AhIdUser user = new AhIdUser();
					user.setAhId(facebookId);
					user.setPassword("");
					user.setAndroidId(androidId);
					userHelper.addIdUserAsync(thisFragment, user, new AhEntityCallback<AhIdUser>() {

						@Override
						public void onCompleted(AhIdUser entity) {
							progressBar.setVisibility(View.GONE);

							/*
							 * Save setting and move to next activity
							 */
							String gender = "Male";
							if(!maleButton.isChecked()){
								gender = "Female";
							}
							int birthYear = Integer.parseInt(birthYearEditText.getText().toString());
							int age = AhApplication.calculateAge(birthYearEditText.getText().toString());

							gaHelper.sendEventGA(
									thisFragment.getClass().getSimpleName(),
									"CheckGender",
									gender);
							gaHelper.sendEventGA(
									thisFragment.getClass().getSimpleName(),
									"CheckAge",
									"" + age);

							pref.putInt(AhGlobalVariable.BIRTH_YEAR_KEY, birthYear);
							pref.putInt(AhGlobalVariable.AGE_KEY, age);
							pref.putBoolean(AhGlobalVariable.IS_LOGGED_IN_USER_KEY, true);
							pref.putBoolean(AhGlobalVariable.IS_MALE_KEY, maleButton.isChecked());
							pref.putString(AhGlobalVariable.NICK_NAME_KEY, nickNameEditText.getText().toString());
							pref.putString(AhGlobalVariable.USER_AH_ID_KEY, entity.getAhId());

							Intent intent = new Intent(context, SquareListActivity.class);
							startActivity(intent);
							activity.finish();
						}
					});
				}
			}
		});
		completeButton.setEnabled(false);
		
		fbBtn.setReadPermissions(Arrays.asList("user_birthday"));
		fbBtn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                Log(thisFragment, user);
                if (user == null) {
                	maleButton.setChecked(true);
                	birthYearEditText.setText("");
                    nickNameEditText.setText("");
                    return;
                }
                
                String birthday = user.getBirthday();
                String birthYear = birthday.substring(6, birthday.length());
                String gender = (String)user.getProperty("gender");
                String name = user.getFirstName();
                if ("male".equals(gender))
                	maleButton.setChecked(true);
                else
                	maleButton.setChecked(false);
                
                pref.putString(AhGlobalVariable.FACEBOOK_ID, user.getId());
                birthYearEditText.setText(birthYear);
                nickNameEditText.setText(name);
                completeButton.setEnabled(true);
            }
        });
		
		return view;
	}


	private boolean isCompleteButtonEnable(){
		return isTypedNickName && Session.getActiveSession().isOpened();
	}
	
	@Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null &&
               (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();
    }
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        
    }
}