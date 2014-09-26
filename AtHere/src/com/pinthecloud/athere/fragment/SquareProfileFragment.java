package com.pinthecloud.athere.fragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.Time;
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
import com.pinthecloud.athere.dialog.AhAlertListDialog;
import com.pinthecloud.athere.dialog.NumberPickerDialog;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.helper.PreferenceHelper;
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
import com.pinthecloud.athere.util.WindowUtil;
import com.pinthecloud.athere.view.CameraPreview;

public class SquareProfileFragment extends AhFragment{

	private final int GET_IMAGE_GALLERY_CODE = 1;

	private Square square;
	private ProgressBar progressBar;

	private Camera mCamera;
	private CameraPreview mCameraPreview;
	private FrameLayout cameraView;
	private ImageView profilePictureView;
	private ImageButton cameraButton;
	private ImageButton cameraRotateButton;

	private int cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
	private boolean isCamera = false;
	private boolean isTookPicture = false;
	private boolean isTypedNickName = false;
	private boolean isTypedMember = false;

	private LinearLayout profileInfoLayout;
	private ImageButton completeButton;
	private EditText nickNameEditText;
	private EditText companyNumberEditText;
	private Bitmap pictureBitmap;
	private Bitmap smallPictureBitmap;

	private ShutterCallback mShutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() {
		}
	};

	// JPEG 이미지를 생성 후 호출
	private PictureCallback mPictureListener = new PictureCallback(){
		public void onPictureTaken(byte[] data, Camera camera){
			if (data != null){
				// Stop preview before processing image
				mCamera.stopPreview();

				// Get file from taken data
				//					File pictureFile = FileUtil.getOutputMediaFile(FileUtil.MEDIA_TYPE_IMAGE);
				//					if (pictureFile == null) {
				//						ExceptionManager.fireException(new AhException(thisFragment, "onPictureTaken", AhException.TYPE.SD_CARD_FAIL));
				//					}
				//					Uri pictureFileUri = Uri.fromFile(pictureFile);
				//					FileOutputStream fos = new FileOutputStream(pictureFile);
				//					fos.write(data);
				//					fos.close();
				//					pictureBitmap = BitmapFactory.decodeStream(app.getContentResolver().openInputStream(pictureFileUri));
				//					pictureFile.delete();
				pictureBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

				// Crop picture
				// Rotate picture
				// Resize picture
				int height = pictureBitmap.getHeight();
				if(cameraFacing == CameraInfo.CAMERA_FACING_BACK){
					pictureBitmap = BitmapUtil.crop(pictureBitmap, 0, 0, height, height);
					pictureBitmap = BitmapUtil.rotate(pictureBitmap, WindowUtil.ANGLE_90);
				}else{
					pictureBitmap = BitmapUtil.crop(pictureBitmap, WindowUtil.getDeviceHeight(activity) - height, 0, height, height);
					pictureBitmap = BitmapUtil.rotate(pictureBitmap, WindowUtil.ANGLE_270);
					pictureBitmap = BitmapUtil.flip(pictureBitmap);
				}
				pictureBitmap = BitmapUtil.decodeInSampleSize(pictureBitmap, BitmapUtil.BIG_PIC_SIZE, BitmapUtil.BIG_PIC_SIZE);

				// Set taken picture to view
				profilePictureView.setImageBitmap(pictureBitmap);

				// Release camera and set button to re take
				releaseCameraAndRemoveView();
				isCamera = false;
				isTookPicture = true;
				cameraButton.setEnabled(true);
				cameraButton.setImageResource(R.drawable.camera_take_re);
				cameraRotateButton.setEnabled(true);
				completeButton.setEnabled(isCompleteButtonEnable());
			}
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get parameter from previous activity intent
		Intent intent = activity.getIntent();
		square = intent.getParcelableExtra(AhGlobalVariable.SQUARE_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
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
		 * Set Event on picture image view
		 */
		profilePictureView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				profilePictureOnClick();
			}
		});


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
		AhUser myUser = userHelper.getMyUserInfo();
		nickNameEditText.setText(myUser.getNickName());


		/*
		 * Set event on Company EditText
		 */
		final NumberPickerDialog companyNumberPickerDialog = new NumberPickerDialog(1, 10, 2, new AhDialogCallback() {

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
									mPictureListener); // JPE 이미지 생성
						}
					});
				}else{
					openCameraAndSetView();
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
				String message = app.checkNickName(nickName);
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
					profilePictureView.setEnabled(false);
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
		if(!isTookPicture){
			if(isCamera){
				openCameraAndSetView();
			}else{
				profilePictureView.setImageResource(R.drawable.bg_ground_profile_default);
			}
		}else{
			profilePictureView.setImageBitmap(pictureBitmap);
		}
	}


	@Override
	public void onStop() {
		profilePictureView.setImageBitmap(null);
		releaseCameraAndRemoveView();
		super.onStop();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case GET_IMAGE_GALLERY_CODE:
			if (resultCode == Activity.RESULT_OK) {
				/*
				 * Get image from gallery
				 */
				Uri imageUri = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = context.getContentResolver().query(imageUri, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String imagePath = cursor.getString(columnIndex);
				cursor.close();


				/*
				 * Set the image
				 */
				try {
					pictureBitmap = BitmapUtil.decodeInSampleSize(context, imageUri, BitmapUtil.BIG_PIC_SIZE, BitmapUtil.BIG_PIC_SIZE);
					int degree = BitmapUtil.getImageOrientation(imagePath);
					pictureBitmap = BitmapUtil.rotate(pictureBitmap, degree);
				} catch (FileNotFoundException e) {
					// Do nothing
				} catch (IOException e) {
					// Do nothing
				}


				/*
				 * Release camera and set button to re take
				 */
				releaseCameraAndRemoveView();
				isCamera = false;
				isTookPicture = true;
				cameraButton.setVisibility(View.GONE);
				cameraRotateButton.setVisibility(View.GONE);
				completeButton.setEnabled(isCompleteButtonEnable());
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled the get image
			} else {
				// Get image failed
			}
			break;
		}
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

		isCamera = true;
		isTookPicture = false;
		cameraButton.setEnabled(true);
		cameraButton.setVisibility(View.VISIBLE);
		cameraButton.setImageResource(R.drawable.camera_take);
		cameraRotateButton.setEnabled(true);
		cameraRotateButton.setVisibility(View.VISIBLE);
		completeButton.setEnabled(false);
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
	 * Profile Image Click Listener
	 */
	private void profilePictureOnClick(){
		String title = getResources().getString(R.string.select);
		String[] list = null;
		if(isTookPicture){
			list = getResources().getStringArray(R.array.profile_image_select_delete_string_array);
		}else{
			list = getResources().getStringArray(R.array.profile_image_select_string_array);
		}
		AhDialogCallback[] callbacks = new AhDialogCallback[list.length];
		callbacks[0] = new AhDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				// Get image from gallery
				Intent intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
				intent.setType("image/*");
				startActivityForResult(intent, GET_IMAGE_GALLERY_CODE);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
				// Do nothing
			}
		};
		callbacks[1] = new AhDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				// Get image from camera
				// Set new camera by facing direction
				releaseCameraAndRemoveView();
				openCameraAndSetView();
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
				// Do nothing
			}
		};
		if(list.length == 3){
			callbacks[2] = new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					// Set profile image default
					profilePictureView.setImageResource(R.drawable.bg_ground_profile_default);

					// Release camera and set button to re take
					isCamera = false;
					isTookPicture = false;
					cameraButton.setVisibility(View.GONE);
					cameraRotateButton.setVisibility(View.GONE);
					completeButton.setEnabled(isCompleteButtonEnable());
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
					// Do nothing
				}
			};
		}
		AhAlertListDialog listDialog = new AhAlertListDialog(title, list, callbacks);
		listDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
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
		gaHelper.sendEventGA(
				thisFragment.getClass().getSimpleName(),
				"CheckMemberNumber",
				"" + companyNumber);

		userHelper.setMyNickName(nickName)
		.setMyCompanyNum(companyNumber);
		squareHelper.setMySquareId(square.getId());

		AsyncChainer.asyncChain(thisFragment, new Chainable(){

			@Override
			public void doNext(AhFragment frag) {
				// Get a user object from preference settings
				// Enter a square with the user
				final AhUser user = userHelper.getMyUserInfo();
				userHelper.enterSquareAsync(frag, user, square.getId(), new AhPairEntityCallback<String, List<AhUser>>() {

					@Override
					public void onCompleted(String userId, List<AhUser> list) {
						userDBHelper.addAllUsers(list);
					}
				});
			}
		}, new Chainable() {

			@Override
			public void doNext(AhFragment frag) {
				// Upload the resized image to server
				String userId = userHelper.getMyUserInfo().getId();
				smallPictureBitmap = BitmapUtil.decodeInSampleSize(pictureBitmap, BitmapUtil.SMALL_PIC_SIZE, BitmapUtil.SMALL_PIC_SIZE);
				blobStorageHelper.uploadBitmapAsync(frag, BlobStorageHelper.USER_PROFILE, userId, pictureBitmap, null);
				blobStorageHelper.uploadBitmapAsync(frag, BlobStorageHelper.USER_PROFILE, userId+AhGlobalVariable.SMALL, smallPictureBitmap, null);
			}
		}, new Chainable() {

			@Override
			public void doNext(AhFragment frag) {
				String enterMessage = getResources().getString(R.string.enter_square_message);
				AhUser user = userHelper.getMyUserInfo();
				AhMessage message = new AhMessage.Builder()
				.setContent(user.getNickName() + " " + enterMessage)
				.setSender(user.getNickName())
				.setSenderId(user.getId())
				.setReceiverId(square.getId())
				.setType(AhMessage.TYPE.ENTER_SQUARE).build();
				messageHelper.sendMessageAsync(frag, message, new AhEntityCallback<AhMessage>() {

					@Override
					public void onCompleted(AhMessage entity) {
						progressBar.setVisibility(View.GONE);

						// Save this setting and go to next activity
						Time time = new Time();
						time.setToNow();

						userHelper.setMyChatEnable(true);
						PreferenceHelper.getInstance().putBoolean(AhGlobalVariable.REVIEW_DIALOG_KEY, true);
						squareHelper.setMySquareName(square.getName())
						.setMySquareResetTime(square.getResetTime())
						.setLoggedInSquare(true)
						.setSquareExitTab(SquareTabFragment.CHAT_TAB)
						.setTimeStampAtLoggedInSquare(time.format("%Y:%m:%d:%H"));

						// Save pictures to internal storage
						String userId = userHelper.getMyUserInfo().getId();
						FileUtil.saveImageToInternalStorage(app, userId, pictureBitmap);
						FileUtil.saveImageToInternalStorage(app, userId+AhGlobalVariable.SMALL, smallPictureBitmap);

						// Set and move to next activity after clear previous activity
						Intent intent = new Intent(context, SquareActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
			}
		});
	}


	private boolean isCompleteButtonEnable(){
		return isTookPicture && isTypedMember && isTypedNickName;
	}
}
