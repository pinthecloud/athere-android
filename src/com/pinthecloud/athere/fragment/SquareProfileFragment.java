package com.pinthecloud.athere.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareActivity;
import com.pinthecloud.athere.dialog.NumberPickerDialog;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhPairEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.util.AsyncChainer;
import com.pinthecloud.athere.util.AsyncChainer.Chainable;
import com.pinthecloud.athere.util.BitmapUtil;
import com.pinthecloud.athere.util.CameraUtil;
import com.pinthecloud.athere.util.FileUtil;
import com.pinthecloud.athere.view.CameraPreview;

public class SquareProfileFragment extends AhFragment{

	private Intent intent;
	private Square square;
	private ProgressBar progressBar;

	private Camera mCamera;
	private CameraPreview mCameraPreview;
	private FrameLayout cameraView;
	private ImageView profilePictureView;
	private ImageButton cameraButton;
	private ImageButton cameraRotateButton;

	private int cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
	private boolean isTookPicture = false;
	private boolean isTypedNickName = false;
	private boolean isTypedMember = false;

	private LinearLayout profileInfoLayout;
	private ImageButton completeButton;
	private NumberPickerDialog companyNumberPickerDialog;
	private EditText nickNameEditText;
	private EditText companyNumberEditText;
	private Bitmap pictureBitmap;

	private ShutterCallback mShutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() {
		}
	};


	// JPEG 이미지를 생성 후 호출
	private PictureCallback mPicutureListener = new PictureCallback(){
		public void onPictureTaken(byte[] data, Camera camera){
			if (data != null){
				try {
					// Stop preview before processing image
					mCamera.stopPreview();

					// Get file from taken data
					File pictureFile = FileUtil.getOutputMediaFile(FileUtil.MEDIA_TYPE_IMAGE);
					if (pictureFile == null) {
						ExceptionManager.fireException(new AhException(_thisFragment, "onPictureTaken", AhException.TYPE.SD_CARD_FAIL));
						return;
					}
					Uri pictureFileUri = Uri.fromFile(pictureFile);
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
					pictureBitmap = BitmapFactory.decodeStream
							(app.getContentResolver().openInputStream(pictureFileUri));
					pictureFile.delete();

					// Crop picture
					// Rotate picture
					int height = pictureBitmap.getHeight();
					if(cameraFacing == CameraInfo.CAMERA_FACING_BACK){
						pictureBitmap = BitmapUtil.crop(pictureBitmap, 0, 0, height, height);
						pictureBitmap = BitmapUtil.rotate(pictureBitmap, AhGlobalVariable.ANGLE_90);
					}else{
						pictureBitmap = BitmapUtil.crop(pictureBitmap, AhGlobalVariable.DEVICE_HEIGHT - height, 0, height, height);
						pictureBitmap = BitmapUtil.rotate(pictureBitmap, AhGlobalVariable.ANGLE_270);
						pictureBitmap = BitmapUtil.flip(pictureBitmap);
					}

					// Set taken picture to view
					profilePictureView.setImageBitmap(pictureBitmap);

					// Save pictures to internal storage
					FileUtil.saveImageToInternalStorage(app, pictureBitmap, AhGlobalVariable.PROFILE_PICTURE_NAME);

					// Release camera and set button to re take
					releaseCameraAndRemoveView();
					isTookPicture = true;
					cameraButton.setEnabled(true);
					cameraButton.setImageResource(R.drawable.camera_take_re);
					cameraRotateButton.setEnabled(true);
					completeButton.setEnabled(isCompleteButtonEnable());
				} catch (FileNotFoundException e) {
					Log.d(AhGlobalVariable.LOG_TAG, "Error of SquareProfileFragment mPicutureListener : " + e.getMessage());
				} catch (IOException e) {
					Log.d(AhGlobalVariable.LOG_TAG, "Error of SquareProfileFragment mPicutureListener : " + e.getMessage());
				}
			}
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get parameter from previous activity intent
		intent = activity.getIntent();
		square = intent.getParcelableExtra(AhGlobalVariable.SQUARE_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(AhGlobalVariable.LOG_TAG, "SquareProfileFragment onCreateView");
		View view = inflater.inflate(R.layout.fragment_square_profile, container, false);

		/*
		 * Find UI component
		 */
		progressBar = (ProgressBar) view.findViewById(R.id.square_profile_frag_progress_bar);
		cameraView = (FrameLayout) view.findViewById(R.id.square_profile_frag_camera_view);
		cameraButton = (ImageButton) view.findViewById(R.id.square_profile_frag_camera_button);
		cameraRotateButton = (ImageButton) view.findViewById(R.id.square_profile_frag_self_camera_button);
		profileInfoLayout = (LinearLayout) view.findViewById(R.id.square_profile_frag_profile_info_layout);
		profilePictureView = (ImageView) view.findViewById(R.id.square_profile_frag_profile_picture);
		nickNameEditText = (EditText) view.findViewById(R.id.square_profile_frag_nick_name_text);
		companyNumberEditText = (EditText) view.findViewById(R.id.square_profile_frag_company_text);
		completeButton = (ImageButton) view.findViewById(R.id.square_profile_frag_start_button);


		/*
		 * Set event on nick name text
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
		nickNameEditText.setText(pref.getString(AhGlobalVariable.NICK_NAME_KEY));


		/*
		 * Set event on Company EditText
		 */
		String title = getResources().getString(R.string.number_of_member);
		companyNumberPickerDialog = new NumberPickerDialog(title, 1, 10, 2, new AhDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				// Set edit text to birth year use picked
				int companyNumber = bundle.getInt(AhGlobalVariable.NUMBER_PICKER_VALUE_KEY);
				companyNumberEditText.setText("" + companyNumber);
			}

			@Override
			public void doNegativeThing(Bundle bundle) {
				// do nothing				
			}
		});
		companyNumberEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String memberNumber = s.toString().trim();
				if(memberNumber.length() < 1){
					isTypedMember = false;
				}else{
					isTypedMember = true;
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
		companyNumberEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				companyNumberPickerDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			}
		});
		companyNumberEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					companyNumberPickerDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
				}
			}
		});
		companyNumberEditText.setInputType(InputType.TYPE_NULL);


		/*
		 * Set event on button
		 */
		cameraButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!isTookPicture){
					cameraButton.setEnabled(false);
					cameraRotateButton.setEnabled(false);
					mCamera.autoFocus(new AutoFocusCallback(){

						@Override
						public void onAutoFocus(boolean success, Camera camera) {
							mCamera.takePicture(
									mShutterCallback, // 셔터
									null, // Raw 이미지 생성
									mPicutureListener); // JPE 이미지 생성
						}
					});
				}else{
					openCameraAndSetView();
					isTookPicture = false;
					cameraButton.setImageResource(R.drawable.camera_take);
					completeButton.setEnabled(false);
				}
			}
		});
		cameraRotateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(cameraFacing == CameraInfo.CAMERA_FACING_BACK){
					cameraFacing = CameraInfo.CAMERA_FACING_FRONT;
				}else{
					cameraFacing = CameraInfo.CAMERA_FACING_BACK;
				}

				// Set new camera by facing direction
				releaseCameraAndRemoveView();
				openCameraAndSetView();
				isTookPicture = false;
				cameraButton.setImageResource(R.drawable.camera_take);
			}
		});
		completeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				/*
				 * Check whether user took profile picture or not
				 * Check nick name edit text and save setting
				 */

				// Remove blank of along side.
				String nickName = nickNameEditText.getText().toString().trim();
				nickNameEditText.setText(nickName);
				nickNameEditText.setSelection(nickName.length());

				// Save gender and birth year infomation to preference
				String message = checkNickName(nickName);
				if(!message.equals("")){
					// Unproper nick name
					// Show warning toast for each situation
					Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
					toast.show();
				} else{
					// Disable UI component for preventing double action
					completeButton.setEnabled(false);
					cameraButton.setEnabled(false);
					cameraRotateButton.setEnabled(false);
					nickNameEditText.setEnabled(false);
					companyNumberEditText.setEnabled(false);

					// Enter Square
					enterSquare();
				}
			}
		});
		completeButton.setEnabled(false);

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		Log.d(AhGlobalVariable.LOG_TAG, "SquareProfilFragment onStart");

		/*
		 * Set camera for taking picture OR
		 * Set taken picture to image view
		 */
		if(!isTookPicture){
			openCameraAndSetView();
		}else{
			Bitmap pictureBitmap = FileUtil.getImageFromInternalStorage(app, AhGlobalVariable.PROFILE_PICTURE_NAME);
			profilePictureView.setImageBitmap(pictureBitmap);
		}
	}


	@Override
	public void onStop() {
		Log.d(AhGlobalVariable.LOG_TAG, "SquareProfilFragment onStop");

		/*
		 * Release resources
		 */
		profilePictureView.setImageBitmap(null);
		releaseCameraAndRemoveView();
		super.onStop();
	}


	/*
	 * Create our Preview view and set it as the content of our activity.
	 * Create orientation event listener
	 */
	private void openCameraAndSetView(){
		mCamera = CameraUtil.getCameraInstance(cameraFacing);
		mCameraPreview = new CameraPreview(context, mCamera);
		cameraView.addView(mCameraPreview);
		profilePictureView.setImageBitmap(null);
		profileInfoLayout.bringToFront();
	}


	/*
	 * Remove our Preview view and release camera and orientation event listener
	 */
	private void releaseCameraAndRemoveView(){
		if(mCamera != null){
			cameraView.removeAllViews();
			mCamera.release();
			mCamera = null;	
		}
	}


	/*
	 * Enter a square 
	 */
	private void enterSquare(){
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();

		// Save info for user
		String nickName = nickNameEditText.getText().toString();
		int companyNumber = Integer.parseInt(companyNumberEditText.getText().toString());
		pref.putString(AhGlobalVariable.NICK_NAME_KEY, nickName);
		pref.putInt(AhGlobalVariable.COMPANY_NUMBER_KEY, companyNumber);
		pref.putString(AhGlobalVariable.SQUARE_ID_KEY, square.getId());
		pref.putBoolean(AhGlobalVariable.IS_CHUPA_ENABLE_KEY, true);
		pref.putBoolean(AhGlobalVariable.IS_CHAT_ALARM_ENABLE_KEY, true);

		AsyncChainer.asyncChain(_thisFragment, new Chainable(){

			@Override
			public void doNext(AhFragment frag) {
				// Get a user object from preference settings
				// Enter a square with the user
				final AhUser user = userHelper.getMyUserInfo(false);
				
				userHelper.newEnterSquareAsync(_thisFragment, user, new AhPairEntityCallback<String, List<AhUser>>() {
					
					@Override
					public void onCompleted(String userId, List<AhUser> list) {
						pref.putString(AhGlobalVariable.USER_ID_KEY, userId);
						userDBHelper.addAllUsers(list);
					}
				});
			}
		}, new Chainable() {
			
			@Override
			public void doNext(AhFragment frag) {
				String userId = pref.getString(AhGlobalVariable.USER_ID_KEY);
				blobStorageHelper.uploadBitmapAsync(_thisFragment, userId, pictureBitmap, new AhEntityCallback<String>() {
					
					@Override
					public void onCompleted(String entity) {
						
					}
				});
			}
		}, new Chainable() {
			
			@Override
			public void doNext(AhFragment frag) {
				// TODO Auto-generated method stub
				String enterMessage = getResources().getString(R.string.enter_square_message);
				
				AhUser user = userHelper.getMyUserInfo(true);
				AhMessage message = new AhMessage.Builder()
				.setContent(user.getNickName() + " : " + enterMessage)
				.setSender(user.getNickName())
				.setSenderId(user.getId())
				.setReceiverId(square.getId())
				.setType(AhMessage.TYPE.ENTER_SQUARE).build();
				messageHelper.sendMessageAsync(frag, message, new AhEntityCallback<AhMessage>() {

					@Override
					public void onCompleted(AhMessage entity) {
						// Save this setting and go to next activity
						pref.putString(AhGlobalVariable.SQUARE_NAME_KEY, square.getName());
						pref.putBoolean(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY, true);
						Time time = new Time();
						time.setToNow();
						pref.putString(AhGlobalVariable.TIME_STAMP_AT_LOGGED_IN_SQUARE_KEY, time.format("%Y:%m:%d:%H"));
						pref.putInt(AhGlobalVariable.SQUARE_EXIT_TAB_KEY, SquareTabFragment.SQUARE_CHAT_TAB);
						
						
						// Set and move to next activity after clear previous activity
						intent.setClass(context, SquareActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

						// Dimiss progress bar
						progressBar.setVisibility(View.GONE);
						startActivity(intent);
					}
				});
			}
		});
				

		//		, new Chainable() {
		//
		//			@Override
		//			public void doNext(AhFragment frag) {
		//				userHelper.getUserListAsync(_thisFragment, square.getId(), new AhListCallback<AhUser>() {
		//
		//					@Override
		//					public void onCompleted(List<AhUser> list, int count) {
		////						userDBHelper.addAllUsers(list);
		//						for(AhUser user : list) {
		//							Bitmap bm = BitmapUtil.convertToBitmap(user.getProfilePic());
		//							String imagePath = FileUtil.saveImageToInternalStorage(app, bm, user.getId());
		//							user.setProfilePic(imagePath);
		//							userDBHelper.addUser(user);
		//						}
		//
		//						// Remove Me from User DB Table.
		//						userDBHelper.deleteUser(pref.getString(AhGlobalVariable.USER_ID_KEY));
		//					}
		//				});
		//
		//			}
		//		}, new Chainable() {
		//
		//			@Override
		//			public void doNext(AhFragment frag) {
		//				// Send message to server for notifying entering
		//				String enterMessage = getResources().getString(R.string.enter_square_message);
		//				AhMessage.Builder messageBuilder = new AhMessage.Builder();
		//				AhUser user = userHelper.getMyUserInfo(true);
		//				messageBuilder.setContent(user.getNickName() + " : " + enterMessage)
		//				.setSender(user.getNickName())
		//				.setSenderId(user.getId())
		//				.setReceiverId(square.getId())
		//				.setType(AhMessage.TYPE.ENTER_SQUARE);
		//				AhMessage message = messageBuilder.build();
		//				messageHelper.sendMessageAsync(_thisFragment, message, new AhEntityCallback<AhMessage>() {
		//
		//					@Override
		//					public void onCompleted(AhMessage entity) {
		//						// Do nothing
		//					}
		//				});
		//
		//				// Save this setting and go to next activity
		//				pref.putString(AhGlobalVariable.SQUARE_NAME_KEY, square.getName());
		//				pref.putBoolean(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY, true);
		//				Time time = new Time();
		//				time.setToNow();
		//				pref.putString(AhGlobalVariable.TIME_STAMP_AT_LOGGED_IN_SQUARE_KEY, time.format("%Y:%m:%d:%H"));
		//				pref.putInt(AhGlobalVariable.SQUARE_EXIT_TAB_KEY, SquareTabFragment.SQUARE_CHAT_TAB);
		//				
		//				
		//				// Set and move to next activity after clear previous activity
		//				intent.setClass(context, SquareActivity.class);
		//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		//
		//				activity.runOnUiThread(new Runnable(){
		//
		//					@Override
		//					public void run() {
		//						// Dimiss progress bar
		//						progressBar.setVisibility(View.GONE);
		//						startActivity(intent);
		//					}
		//				});
		//			}
		//		});



		/**
		 *  DO NOT REMOVE
		 *  NEED FOR REFERENCE
		 */
		//		new AhThread(new Runnable() {
		//
		//			@Override
		//			public void run() {
		//				/*
		//				 * If user haven't got registration key, get it
		//				 */
		//				if(pref.getString(AhGlobalVariable.REGISTRATION_ID_KEY)
		//						.equals(PreferenceHelper.DEFAULT_STRING)){
		//					try {
		//						String registrationId = userHelper.getRegistrationIdSync(_thisFragment);
		//						pref.putString(AhGlobalVariable.REGISTRATION_ID_KEY, registrationId);
		//					} catch (AhException e) {
		//						Log.d(AhGlobalVariable.LOG_TAG, "SquareProfileFragment enterSquare : " + e.getMessage());
		//					}
		//				}
		//
		//				// Save info for user
		//				String nickName = nickNameEditText.getText().toString();
		//				int companyNumber = Integer.parseInt(companyNumberEditText.getText().toString());
		//				pref.putString(AhGlobalVariable.NICK_NAME_KEY, nickName);
		//				pref.putInt(AhGlobalVariable.COMPANY_NUMBER_KEY, companyNumber);
		//				pref.putString(AhGlobalVariable.SQUARE_ID_KEY, square.getId());
		//				pref.putBoolean(AhGlobalVariable.IS_CHUPA_ENABLE_KEY, true);
		//				pref.putBoolean(AhGlobalVariable.IS_CHAT_ALARM_ENABLE_KEY, true);
		//
		//
		//				// Get a user object from preference settings
		//				// Enter a square with the user
		//				final User user = userHelper.getMyUserInfo(false);
		//				String id = userHelper.enterSquareSync(_thisFragment, user);
		//				pref.putString(AhGlobalVariable.USER_ID_KEY, id);
		//
		//
		//				// Get user list in the square and save it without me
		//				List<User> userList = userHelper.getUserListSync(_thisFragment, square.getId());
		//				userDBHelper.addAllUsers(userList);
		//				userDBHelper.deleteUser(id);
		//
		//
		//				// Send message to server for notifying entering
		//				String enterMessage = getResources().getString(R.string.enter_square_message);
		//				AhMessage.Builder messageBuilder = new AhMessage.Builder();
		//				messageBuilder.setContent(nickName + " : " + enterMessage)
		//				.setSender(user.getNickName())
		//				.setSenderId(id)
		//				.setReceiverId(square.getId())
		//				.setType(AhMessage.TYPE.ENTER_SQUARE);
		//				AhMessage message = messageBuilder.build();
		//				messageHelper.sendMessageAsync(_thisFragment, message, new AhEntityCallback<AhMessage>() {
		//
		//					@Override
		//					public void onCompleted(AhMessage entity) {
		//						// Do nothing
		//					}
		//				});
		//
		//				// Save this setting and go to next activity
		//				pref.putString(AhGlobalVariable.SQUARE_NAME_KEY, square.getName());
		//				pref.putBoolean(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY, true);
		//				pref.putInt(AhGlobalVariable.SQUARE_EXIT_TAB_KEY, AhGlobalVariable.SQUARE_CHAT_TAB);
		//
		//				// Set and move to next activity after clear previous activity
		//				intent.setClass(context, SquareActivity.class);
		//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		//
		//				activity.runOnUiThread(new Runnable(){
		//
		//					@Override
		//					public void run() {
		//						// Dimiss progress bar
		//						progressBar.setVisibility(View.GONE);
		//						startActivity(intent);
		//					}
		//				});
		//			}
		//		}).start();
	}


	private boolean isCompleteButtonEnable(){
		return isTookPicture && isTypedMember && isTypedNickName;
	}


	//	@Override
	//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	//		profileImageOnActivityResult(requestCode, resultCode, data);
	//	}
	//	
	//	
	//	private void profileImageOnClick(){
	//		String select = getResources().getString(R.string.select);
	//		final String[] selectList = getResources().getStringArray(R.array.profile_image_select_string_array);
	//
	//		// Show dialog for selecting where to get profile image
	//		altBuilder = new AlertDialog.Builder(context);
	//		altBuilder.setTitle(select);
	//		altBuilder.setItems(selectList, new DialogInterface.OnClickListener() {
	//			public void onClick(DialogInterface dialog, int item) {
	//				final int ALBUM = 0;
	//				final int CAMERA = 1;
	//				final int DELETE = 2;
	//				Intent intent = null;
	//
	//				switch(item){
	//				case ALBUM:
	//					// Get image from gallery
	//					intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
	//					intent.setType("image/*");
	//					startActivityForResult(intent, GET_IMAGE_GALLERY_CODE);
	//					break;
	//
	//				case CAMERA:
	//					// create Intent to take a picture and return control to the calling application
	//					// create a file to save the image
	//					// set the image file name
	//					// start the image capture Intent
	//					intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	//					fileUri = FileHelper.getOutputMediaFileUri(FileHelper.MEDIA_TYPE_IMAGE);
	//					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
	//					startActivityForResult(intent, CAPTURE_IMAGE_CAMERA_CODE);
	//					break;
	//
	//				case DELETE:
	//					String delete = getResources().getString(R.string.delete);
	//					String deleteMessage = getResources().getString(R.string.delete_profile_image_message);
	//					String yes = getResources().getString(R.string.yes);
	//					String no = getResources().getString(R.string.no);
	//
	//					// Show dialog for confirming to delete profile image
	//					altBuilder = new AlertDialog.Builder(context);
	//					altBuilder.setTitle(delete);
	//					altBuilder.setMessage(deleteMessage);
	//					altBuilder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
	//
	//						@Override
	//						public void onClick(DialogInterface dialog, int which) {
	//							// Set profile image default
	//							profileImage.setImageDrawable(getResources()
	//									.getDrawable(R.drawable.profile_default_image));
	//						}
	//					});
	//					altBuilder.setNegativeButton(no, new DialogInterface.OnClickListener() {
	//
	//						@Override
	//						public void onClick(DialogInterface dialog, int which) {
	//							// Keep current profile image
	//						}
	//					});
	//					AlertDialog alertDialog = altBuilder.create();
	//					alertDialog.setCanceledOnTouchOutside(true);
	//					alertDialog.show();
	//					break;
	//				}
	//			}
	//		});
	//		AlertDialog alertDialog = altBuilder.create();
	//		alertDialog.setCanceledOnTouchOutside(true);
	//		alertDialog.show();
	//	}
	//
	//
	//	private void profileImageOnActivityResult(int requestCode, int resultCode, Intent data){
	//		switch(requestCode){
	//		case CAPTURE_IMAGE_CAMERA_CODE:
	//			if (resultCode == Activity.RESULT_OK) {
	//				try {
	//					/*
	//					 * Get image from camera
	//					 * Set the image to profile image after resize
	//					 */
	//					Bitmap profileImageBitmap = BitmapHelper.resize(context, fileUri, 
	//							profileImage.getWidth(), profileImage.getHeight());
	//					profileImage.setImageBitmap(profileImageBitmap);
	//				} catch (FileNotFoundException e) {
	//					// If it thorws error,
	//					// Set profile image default
	//					profileImage.setImageDrawable(getResources()
	//							.getDrawable(R.drawable.profile_default_image));
	//				}
	//			} else if (resultCode == Activity.RESULT_CANCELED) {
	//				// User cancelled the image capture
	//			} else {
	//				// Image capture failed, advise user
	//				String message = getResources().getString(R.string.bad_camera_message);
	//				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	//			}
	//			break;
	//
	//		case GET_IMAGE_GALLERY_CODE:
	//			if (resultCode == Activity.RESULT_OK) {
	//				/*
	//				 * Get image from gallery
	//				 */
	//				Uri imageUri = data.getData();
	//				String[] filePathColumn = { MediaStore.Images.Media.DATA };
	//
	//				Cursor cursor = context.getContentResolver().query(imageUri,
	//						filePathColumn, null, null, null);
	//				cursor.moveToFirst();
	//
	//				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	//				String imagePath = cursor.getString(columnIndex);
	//				cursor.close();
	//
	//
	//				/*
	//				 * Set the image to profile image after resize
	//				 */
	//				try {
	//					File file = new File(imagePath);
	//					Uri absoluteImageUri = Uri.fromFile(file);
	//					Bitmap imageBitmap = BitmapHelper.resize(context, absoluteImageUri, 
	//							profileImage.getWidth(), profileImage.getHeight());
	//					profileImage.setImageBitmap(imageBitmap);
	//				} catch (FileNotFoundException e) {
	//					// If it thorws error,
	//					// Set profile image default
	//					profileImage.setImageDrawable(getResources()
	//							.getDrawable(R.drawable.profile_default_image));
	//				}
	//			} else if (resultCode == Activity.RESULT_CANCELED) {
	//				// User cancelled the get image
	//			} else {
	//				// Get image failed, advise user
	//				String message = getResources().getString(R.string.bad_gallery_message);
	//				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	//			}
	//			break;
	//		}	
	//	}
}
