package com.pinthecloud.athere.fragment;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalMethod;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareListActivity;
import com.pinthecloud.athere.helper.BItmapUtil;
import com.pinthecloud.athere.helper.FileUtil;
import com.pinthecloud.athere.helper.PrefHelper;

public class ProfileFragment extends Fragment{

	private static final int CAPTURE_IMAGE_CAMERA_CODE = 100;
	private static final int GET_IMAGE_GALLERY_CODE = 200;

	private ImageView profileImage;
	private EditText nickNameEditText;
	private Button startButton;
	private RelativeLayout relativeLayout; 
	
	private AlertDialog.Builder altBuilder;
	private PrefHelper pref;
	private Uri fileUri;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		/*
		 * Find UI component
		 */
		profileImage = (ImageView) view.findViewById(R.id.profile_frag_image);
		nickNameEditText = (EditText)view.findViewById(R.id.profile_frag_nick_name_text);
		startButton = (Button) view.findViewById(R.id.profile_frag_start_button);
		relativeLayout = (RelativeLayout) view.findViewById(R.id.profile_frag_layout);

		
		/*
		 * Set event on layout
		 */
		relativeLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AhGlobalMethod.hideKeyboard(getActivity(), nickNameEditText);
			}
		});

		
		/*
		 * Set event on button
		 */
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
				if(nickName.length() < 3){
					message = getResources().getString(R.string.min_nick_name_message);
				} else if(!nickName.matches(nickNameRegx)){
					message = getResources().getString(R.string.bad_nick_name_message);
				} else if(nickName.length() > 15){
					message = getResources().getString(R.string.max_nick_name_message);
				}

				if(!message.equals("")){
					// Unproper nick name
					// Show warning toast for each situation
					Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else{
					// Proper nick name
					// Save this setting and go to next activity
					pref = new PrefHelper(getActivity());
					pref.putString(AhGlobalVariable.NICK_NAME_KEY, nickName);
					//					pref.putBoolean(AtHereGlobal.IS_LOGGED_IN_USER_KEY, true);

					Intent intent = new Intent(getActivity(), SquareListActivity.class);
					startActivity(intent);
					getActivity().finish();
				}
			}
		});


		/*
		 * Set Edit Text
		 */
		nickNameEditText.setHint(getResources().getString(R.string.type_nick_name_hint));
		nickNameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus)
					AhGlobalMethod.hideKeyboard(getActivity(), nickNameEditText);
			}
		});
		
		/*
		 * Set Profile Image
		 */
		profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String select = getResources().getString(R.string.profile_image_select);
				final String[] selectList = getResources().getStringArray(R.array.profile_image_select_string_array);

				// Show dialog for selecting where to get profile image
				altBuilder = new AlertDialog.Builder(getActivity());
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
							altBuilder = new AlertDialog.Builder(getActivity());
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
		});

		return view;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_CAMERA_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				// Get image from camera
				// Set the image to profile image after resize
				Bitmap imageBitmap = BItmapUtil.resize(getActivity(), fileUri, 
						profileImage.getWidth(), profileImage.getHeight());
				profileImage.setImageBitmap(imageBitmap);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
				String message = getResources().getString(R.string.bad_camera_message);
				Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
			}
		}
		else if(requestCode == GET_IMAGE_GALLERY_CODE){
			if (resultCode == Activity.RESULT_OK) {
				/*
				 * Get image from gallery
				 */
				Uri imageUri = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getActivity().getContentResolver().query(imageUri,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String imagePath = cursor.getString(columnIndex);
				cursor.close();
				
				/*
				 * Set the image to profile image after resize
				 */
				File file = new File(imagePath);
				Uri absoluteImageUri = Uri.fromFile(file);
				
				Bitmap imageBitmap = BItmapUtil.resize(getActivity(), absoluteImageUri, 
						profileImage.getWidth(), profileImage.getHeight());
			    profileImage.setImageBitmap(imageBitmap);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled the get image
			} else {
				// Get image failed, advise user
				String message = getResources().getString(R.string.bad_gallery_message);
				Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
			}
		}
	}
}
