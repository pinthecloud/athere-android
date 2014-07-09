package com.pinthecloud.athere.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareChatActivity;
import com.pinthecloud.athere.helper.BitmapHelper;
import com.pinthecloud.athere.helper.CameraHelper;
import com.pinthecloud.athere.helper.FileHelper;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.interfaces.CameraPreview;
import com.pinthecloud.athere.model.Square;

public class SquareProfileFragment extends AhFragment{

	private final int MIN_NUMBER_PERSON = 1;
	private final int MAX_NUMBER_PERSON = 30;

	private Intent intent;

	private Camera mCamera;
	private CameraPreview mCameraPreview;
	private FrameLayout cameraLayout;
	private ImageView profileImageView;
	private Button cameraButton;
	private Button selfCameraButton;

	private boolean takePicture = true;

	private LinearLayout profileInfoLayout;
	private EditText nickNameEditText;
	private NumberPicker numberPicker;
	private Button completeButton;

	private Square square;


	// JPEG 이미지를 생성 후 호출
	private PictureCallback mPicutureListener = new PictureCallback(){
		public void onPictureTaken(byte[] data, Camera camera){
			if (data != null){
				try {
					// Get file from taken data
					File pictureFile = FileHelper.getOutputMediaFile(FileHelper.MEDIA_TYPE_IMAGE);
					Uri pictureFileUri = Uri.fromFile(pictureFile);
					if (pictureFile == null){
						return;
					}
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();


					// Rotate picture by orientation
					// Save cropped and rotated bitmap image
					Bitmap profileImageBitmap = BitmapFactory.decodeStream
							(context.getContentResolver().openInputStream(pictureFileUri));
					profileImageBitmap = BitmapHelper.rotate(profileImageBitmap, AhGlobalVariable.RIGHT_ANGLE);
					int width = profileImageBitmap.getWidth();
					int height = profileImageBitmap.getHeight();
					profileImageBitmap = BitmapHelper.crop(profileImageBitmap, width, height/2);
					FileHelper.saveImageToInternalStorage(context, profileImageBitmap);


					// Stop preview and set button to re take
					mCamera.stopPreview();
					takePicture = false;
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
		pref = new PreferenceHelper(context);

		intent = getActivity().getIntent();
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
		cameraLayout = (FrameLayout) view.findViewById(R.id.square_profile_frag_camera_view);
		cameraButton = (Button) view.findViewById(R.id.square_profile_frag_camera_button);
		selfCameraButton = (Button) view.findViewById(R.id.square_profile_frag_self_camera_button);
		profileInfoLayout = (LinearLayout) view.findViewById(R.id.square_profile_frag_profile_info_layout);
		profileImageView = (ImageView) view.findViewById(R.id.square_profile_frag_profile_image);
		nickNameEditText = (EditText)view.findViewById(R.id.square_profile_frag_nick_name_edit_text);
		completeButton = (Button) view.findViewById(R.id.square_profile_frag_start_button);
		numberPicker = (NumberPicker) view.findViewById(R.id.square_profile_frag_number_picker);


		/*
		 * Set nick name edit text
		 */
		nickNameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String nickName = s.toString().trim();
				if(nickName.length() < 1){
					completeButton.setEnabled(false);
				}else{
					completeButton.setEnabled(true);
				}
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
		 * Set Number Picker
		 */
		numberPicker.setMinValue(MIN_NUMBER_PERSON);
		numberPicker.setMaxValue(MAX_NUMBER_PERSON);


		/*
		 * Set event on button
		 */
		completeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * Check nick name edit text and save setting
				 */
				String message = checkNickNameEditText();
				if(!message.equals("")){
					// Unproper nick name
					// Show warning toast for each situation
					Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else{
					// Proper nick name
					// Save this setting and go to next activity
					pref.putString(AhGlobalVariable.NICK_NAME_KEY, nickNameEditText.getText().toString());
					pref.putBoolean(AhGlobalVariable.IS_LOGGED_IN_SQUARE_KEY, true);
					pref.putString(AhGlobalVariable.SQUARE_ID_KEY, square.getId());

					intent.setClass(context, SquareChatActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			}
		});
		cameraButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(takePicture){
					mCamera.takePicture(
							null, // 셔터
							null, // Raw 이미지 생성
							mPicutureListener); // JPE 이미지 생성
				}else{
					if(mCamera != null){
						mCamera.startPreview();
					}else{
						openCameraAndSetView();
					}
					takePicture = true;
				}
			}
		});
		selfCameraButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO convert camera mode to selfi
			}
		});

		return view;
	}


	@Override
	public void onResume() {
		super.onResume();

		if(takePicture){
			openCameraAndSetView();
		}else{
			try {
				// Set taken picture to view
				Bitmap profileImageBitmap = FileHelper.getImageFromInternalStorage(context, AhGlobalVariable.PROFILE_IMAGE_FILE_NAME);
				profileImageView.setImageBitmap(profileImageBitmap);
			} catch (FileNotFoundException e) {
				Log.d(AhGlobalVariable.LOG_TAG, "Error of SquareProfileFragment onResume : " + e.getMessage());
			}
		}
	}


	@Override
	public void onPause() {
		Log.d(AhGlobalVariable.LOG_TAG, "SquareProfilFragment onPause");
		super.onPause();
		releaseCameraAndRemoveView();
	}


	/*
	 * Check nick name EditText
	 */
	private String checkNickNameEditText(){
		// Remove blank of along side.
		String nickName = nickNameEditText.getText().toString().trim();
		nickNameEditText.setText(nickName);
		nickNameEditText.setSelection(nickName.length());

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


	/*
	 * Create our Preview view and set it as the content of our activity.
	 */
	private void openCameraAndSetView(){
		mCamera = CameraHelper.getCameraInstance();
		mCameraPreview = new CameraPreview(context, mCamera);
		cameraLayout.addView(mCameraPreview);
		profileImageView.setImageBitmap(null);
		profileInfoLayout.bringToFront();
	}


	/*
	 * Remove our Preview view and release camera
	 */
	private void releaseCameraAndRemoveView(){
		if(mCamera != null){
			cameraLayout.removeAllViews();
			mCamera.release();
			mCamera = null;	
		}
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
