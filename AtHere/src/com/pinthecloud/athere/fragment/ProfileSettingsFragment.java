package com.pinthecloud.athere.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.dialog.AhAlertListDialog;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.util.AsyncChainer;
import com.pinthecloud.athere.util.AsyncChainer.Chainable;
import com.pinthecloud.athere.util.BitmapUtil;
import com.pinthecloud.athere.util.FileUtil;

public class ProfileSettingsFragment extends AhFragment{

	private ProgressBar progressBar;
	private ImageView profileImageView;

	private Uri imageUri;
	private Bitmap profileImageBitmap;
	private Bitmap smallProfileImageBitmap;

	private TextView nickNameWarningText;
	private EditText nickNameEditText;
	private TextView ageText;
	private TextView genderText;
	private ImageButton startButton;

	private AhUser user;
	private boolean isTypedNickName = true;
	private boolean isTakenProfileImage = true;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = activity.getIntent();
		user = intent.getParcelableExtra(AhGlobalVariable.USER_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);

		findComponent(view);
		setActionBar();
		setComponent();
		setEditText();


		/*
		 * Set Event on profile image view
		 */
		profileImageBitmap = FileUtil.getBitmapFromInternalStorage(context, user.getId());
		profileImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String title = getResources().getString(R.string.select);
				String[] list = null;
				if(isTakenProfileImage){
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
						startActivityForResult(intent, FileUtil.MEDIA_TYPE_GALLERY);
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
						// Do nothing
					}
				};
				callbacks[1] = new AhDialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						// create Intent to take a picture and return control to the calling application
						// create a file to save the image
						// set the image file name
						// start the image capture Intent
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						imageUri = FileUtil.getOutputMediaFileUri(FileUtil.MEDIA_TYPE_IMAGE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
						startActivityForResult(intent, FileUtil.MEDIA_TYPE_CAMERA);
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
							profileImageView.setImageResource(R.drawable.profile_edit_profile_default_ico);
							isTakenProfileImage = false;
							startButton.setEnabled(isStartButtonEnable());
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
		});


		/*
		 * Set Start Button
		 */
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String nickName = nickNameEditText.getText().toString().trim();
				nickNameEditText.setText(nickName);
				nickNameEditText.setSelection(nickName.length());


				/*
				 * Unproper nick name
				 * Show warning toast for each situation
				 */
				String message = app.checkNickName(nickName);
				if(!message.equals("")){
					nickNameWarningText.setText(message);
					return;
				}


				/*
				 * Proper nick name
				 * Show progress bar
				 * Disable UI components for preventing double click
				 * Save this setting and go to next activity
				 */
				progressBar.setVisibility(View.VISIBLE);
				progressBar.bringToFront();
				profileImageView.setEnabled(false);
				nickNameEditText.setEnabled(false);
				startButton.setEnabled(false);
				nickNameWarningText.setText("");


				/*
				 * Enter Square
				 */
				AsyncChainer.asyncChain(thisFragment, new Chainable(){

					@Override
					public void doNext(AhFragment frag) {
						user.setNickName(nickName);
						userHelper.updateUserAsync(frag, user, new AhEntityCallback<AhUser>() {

							@Override
							public void onCompleted(AhUser entity) {
								userHelper.setMyNickName(entity.getNickName());
							}
						});
					}
				}, new Chainable() {

					@Override
					public void doNext(AhFragment frag) {
						// Upload the resized image to server
						smallProfileImageBitmap = BitmapUtil.decodeInSampleSize(profileImageBitmap, BitmapUtil.SMALL_PIC_SIZE, BitmapUtil.SMALL_PIC_SIZE);
						blobStorageHelper.uploadBitmapAsync(frag, BlobStorageHelper.USER_PROFILE, user.getId(), profileImageBitmap, null);
						blobStorageHelper.uploadBitmapAsync(frag, BlobStorageHelper.USER_PROFILE, user.getId()+AhGlobalVariable.SMALL, smallProfileImageBitmap, null);
					}
				}, new Chainable() {

					@Override
					public void doNext(AhFragment frag) {
						if(squareHelper.isLoggedInSquare() && !squareHelper.isPreview()){
							final AhUser user = userHelper.getMyUserInfo();
							AhMessage message = new AhMessage.Builder()
							.setContent(user.getNickName())
							.setSender(user.getNickName())
							.setSenderId(user.getId())
							.setReceiverId(squareHelper.getMySquareInfo().getId())
							.setType(AhMessage.TYPE.UPDATE_USER_INFO).build();
							messageHelper.sendMessageAsync(frag, message, null);
						} else{
							AsyncChainer.notifyNext(frag);
						}
					}
				}, new Chainable(){

					@Override
					public void doNext(AhFragment frag) {
						progressBar.setVisibility(View.GONE);
						profileImageView.setEnabled(true);
						nickNameEditText.setEnabled(true);
						startButton.setEnabled(true);

						// Save profile to internal storage
						String userId = user.getId();
						FileUtil.saveBitmapToInternalStorage(app, userId, profileImageBitmap);
						FileUtil.saveBitmapToInternalStorage(app, userId+AhGlobalVariable.SMALL, smallProfileImageBitmap);
						blobStorageHelper.clearCache(userId);
						blobStorageHelper.clearCache(userId+AhGlobalVariable.SMALL);

						Toast.makeText(context, getResources().getString(R.string.profile_settings_complete_message)
								, Toast.LENGTH_LONG).show();
					}
				});
			}
		});

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		if(!isTakenProfileImage){
			profileImageView.setImageResource(R.drawable.profile_edit_profile_default_ico);
		}else{
			profileImageView.setImageBitmap(profileImageBitmap);
		}
	}


	@Override
	public void onStop() {
		profileImageView.setImageBitmap(null);
		super.onStop();
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK){
			/*
			 * Get image URI and path from gallery or camera
			 */
			String imagePath = null;
			switch(requestCode){
			case FileUtil.MEDIA_TYPE_GALLERY:
				imageUri = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = context.getContentResolver().query(imageUri, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				imagePath = cursor.getString(columnIndex);
				cursor.close();
				break;

			case FileUtil.MEDIA_TYPE_CAMERA:
				Uri tempImageUri = null;
				if(imageUri == null){
					if(data == null){
						tempImageUri = FileUtil.getLastCaptureBitmapUri(context);
					} else{
						tempImageUri = data.getData();

						// Intent pass data as Bitmap
						if(tempImageUri == null){
							Bitmap bitmap = (Bitmap) data.getExtras().get("data");
							tempImageUri = FileUtil.getOutputMediaFileUri(FileUtil.MEDIA_TYPE_IMAGE);
							FileUtil.saveBitmapToFile(context, tempImageUri, bitmap);
						}
					}
				} else{
					tempImageUri = imageUri;
				}

				imageUri = tempImageUri;
				imagePath = imageUri.getPath();
				break;
			}


			/*
			 * Set the image
			 */
			try {
				profileImageBitmap = BitmapUtil.decodeInSampleSize(context, imageUri, BitmapUtil.BIG_PIC_SIZE, BitmapUtil.BIG_PIC_SIZE);

				int width = profileImageBitmap.getWidth();
				int height = profileImageBitmap.getHeight();
				if(height < width){
					profileImageBitmap = BitmapUtil.crop(profileImageBitmap, 0, 0, height, height);
				} else{
					profileImageBitmap = BitmapUtil.crop(profileImageBitmap, 0, 0, width, width);
				}

				int degree = BitmapUtil.getImageOrientation(imagePath);
				profileImageBitmap = BitmapUtil.rotate(profileImageBitmap, degree);
			} catch (FileNotFoundException e) {
				// Do nothing
			} catch (IOException e) {
				// Do nothing
			}
			isTakenProfileImage = true;
			startButton.setEnabled(isStartButtonEnable());


			/*
			 * If get image from camera, delete file
			 */
			if(requestCode == FileUtil.MEDIA_TYPE_CAMERA){
				File file = new File(imagePath);
				file.delete();
			}
		}
	}


	private void findComponent(View view){
		progressBar = (ProgressBar) view.findViewById(R.id.profile_settings_frag_progress_bar);
		profileImageView = (ImageView) view.findViewById(R.id.profile_settings_frag_profile_image);
		nickNameWarningText = (TextView) view.findViewById(R.id.profile_settings_frag_nick_name_warning_text);
		nickNameEditText = (EditText) view.findViewById(R.id.profile_settings_frag_nick_name_text);
		ageText = (TextView) view.findViewById(R.id.profile_settings_frag_age_text);
		genderText = (TextView) view.findViewById(R.id.profile_settings_frag_gender_text);
		startButton = (ImageButton) view.findViewById(R.id.profile_settings_frag_start_button);
	}


	private void setActionBar(){
		ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}


	private void setComponent(){
		ageText.setText(""+user.getAge());
		genderText.setText(user.getGenderString(context));
		if(user.isMale()){
			genderText.setTextColor(getResources().getColor(R.color.blue_man));
		}else{
			genderText.setTextColor(getResources().getColor(R.color.red_woman));
		}
	}


	private void setEditText(){
		nickNameEditText.setText(user.getNickName());
		nickNameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String nickName = s.toString().trim();
				if(nickName.length() < 1){
					isTypedNickName = false;
				}else{
					isTypedNickName = true;
				}
				startButton.setEnabled(isStartButtonEnable());
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}


	private boolean isStartButtonEnable(){
		return isTypedNickName && isTakenProfileImage;
	}
}
