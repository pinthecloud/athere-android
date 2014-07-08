package com.pinthecloud.athere.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareChatActivity;
import com.pinthecloud.athere.helper.BItmapUtil;
import com.pinthecloud.athere.helper.FileUtil;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.model.Square;

public class SquareProfileFragment extends AhFragment{

	private final int MIN_NUMBER_PERSON = 1;
	private final int MAX_NUMBER_PERSON = 30;

	private final int GET_IMAGE_GALLERY_CODE = 0;
	private final int CAPTURE_IMAGE_CAMERA_CODE = 1;

	private Intent intent;
	
	private ImageView profileImage;
	private EditText nickNameEditText;
	private NumberPicker numberPicker;
	private Button completeButton;

	private AlertDialog.Builder altBuilder;
	private Uri fileUri;

	private Square square;


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
		View view = inflater.inflate(R.layout.fragment_square_profile, container, false);

		/*
		 * Find UI component
		 */
		profileImage = (ImageView) view.findViewById(R.id.square_profile_frag_image);
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
				 * Save profile image
				 */
				try {
					Bitmap profileImageBitmap = ((BitmapDrawable)profileImage.getDrawable()).getBitmap();
					FileUtil.saveImageToInternalStorage(context, profileImageBitmap);
				} catch (IOException e) {
					// Error of saving profile image to Internal storage
				}


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


		/*
		 * Set Profile Image
		 */
		profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				profileImageOnClick();
			}
		});

		return view;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		profileImageOnActivityResult(requestCode, resultCode, data);
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


	private void profileImageOnClick(){
		String select = getResources().getString(R.string.select);
		final String[] selectList = getResources().getStringArray(R.array.profile_image_select_string_array);

		// Show dialog for selecting where to get profile image
		altBuilder = new AlertDialog.Builder(context);
		altBuilder.setTitle(select);
		altBuilder.setItems(selectList, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				final int ALBUM = 0;
				final int CAMERA = 1;
				final int DELETE = 2;
				Intent intent = null;

				switch(item){
				case ALBUM:
					// Get image from gallery
					intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(intent, GET_IMAGE_GALLERY_CODE);
					break;

				case CAMERA:
					// create Intent to take a picture and return control to the calling application
					// create a file to save the image
					// set the image file name
					// start the image capture Intent
					intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					fileUri = FileUtil.getOutputMediaFileUri(FileUtil.MEDIA_TYPE_IMAGE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					startActivityForResult(intent, CAPTURE_IMAGE_CAMERA_CODE);
					break;

				case DELETE:
					String delete = getResources().getString(R.string.delete);
					String deleteMessage = getResources().getString(R.string.delete_profile_image_message);
					String yes = getResources().getString(R.string.yes);
					String no = getResources().getString(R.string.no);

					// Show dialog for confirming to delete profile image
					altBuilder = new AlertDialog.Builder(context);
					altBuilder.setTitle(delete);
					altBuilder.setMessage(deleteMessage);
					altBuilder.setPositiveButton(yes, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Set profile image default
							profileImage.setImageDrawable(getResources()
									.getDrawable(R.drawable.profile_default_image));
						}
					});
					altBuilder.setNegativeButton(no, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Keep current profile image
						}
					});
					AlertDialog alertDialog = altBuilder.create();
					alertDialog.setCanceledOnTouchOutside(true);
					alertDialog.show();
					break;
				}
			}
		});
		AlertDialog alertDialog = altBuilder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();
	}


	private void profileImageOnActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode){
		case CAPTURE_IMAGE_CAMERA_CODE:
			if (resultCode == Activity.RESULT_OK) {
				try {
					/*
					 * Get image from camera
					 * Set the image to profile image after resize
					 */
					Bitmap profileImageBitmap = BItmapUtil.resize(context, fileUri, 
							profileImage.getWidth(), profileImage.getHeight());
					profileImage.setImageBitmap(profileImageBitmap);
				} catch (FileNotFoundException e) {
					// If it thorws error,
					// Set profile image default
					profileImage.setImageDrawable(getResources()
							.getDrawable(R.drawable.profile_default_image));
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
				String message = getResources().getString(R.string.bad_camera_message);
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
			break;

		case GET_IMAGE_GALLERY_CODE:
			if (resultCode == Activity.RESULT_OK) {
				/*
				 * Get image from gallery
				 */
				Uri imageUri = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = context.getContentResolver().query(imageUri,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String imagePath = cursor.getString(columnIndex);
				cursor.close();


				/*
				 * Set the image to profile image after resize
				 */
				try {
					File file = new File(imagePath);
					Uri absoluteImageUri = Uri.fromFile(file);
					Bitmap imageBitmap = BItmapUtil.resize(context, absoluteImageUri, 
							profileImage.getWidth(), profileImage.getHeight());
					profileImage.setImageBitmap(imageBitmap);
				} catch (FileNotFoundException e) {
					// If it thorws error,
					// Set profile image default
					profileImage.setImageDrawable(getResources()
							.getDrawable(R.drawable.profile_default_image));
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled the get image
			} else {
				// Get image failed, advise user
				String message = getResources().getString(R.string.bad_gallery_message);
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
			break;
		}	
	}
}
