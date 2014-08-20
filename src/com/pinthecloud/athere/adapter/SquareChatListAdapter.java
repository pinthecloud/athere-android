package com.pinthecloud.athere.adapter;

import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.activity.ProfileImageActivity;
import com.pinthecloud.athere.database.UserDBHelper;
import com.pinthecloud.athere.dialog.ProfileDialog;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.util.BitmapUtil;
import com.pinthecloud.athere.util.FileUtil;

public class SquareChatListAdapter extends ArrayAdapter<AhMessage> {

	private Context context;
	private Fragment fragment;
	private LayoutInflater inflater;
	private int layoutId;
	private List<AhMessage> items;

	private AhApplication app;
	private UserDBHelper userDBHelper;


	public SquareChatListAdapter(Context context, Fragment fragment, int layoutId, List<AhMessage> items) {
		super(context, layoutId, items);
		this.context = context;
		this.fragment = fragment;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutId = layoutId;
		this.items = items;

		this.app = AhApplication.getInstance(); 
		this.userDBHelper = app.getUserDBHelper();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;

		AhMessage message = items.get(position);
		if (message != null) {
			// Inflate different layout by user
			if(message.isNotification()){
				this.layoutId = R.layout.row_chat_notification_list;
			}else{
				if(message.isMine()){
					this.layoutId = R.layout.row_square_chat_list_send;
				} else{
					this.layoutId = R.layout.row_square_chat_list_receive;
				}
			}
			view = inflater.inflate(this.layoutId, parent, false);


			/*
			 * Find UI component
			 */
			TextView messageText = null;
			TextView timeText = null;
			if(this.layoutId == R.layout.row_chat_notification_list){
				messageText = (TextView)view.findViewById(R.id.row_chat_notification_text);
				timeText = (TextView)view.findViewById(R.id.row_chat_notification_time);
			} else if(this.layoutId == R.layout.row_square_chat_list_send){
				messageText = (TextView)view.findViewById(R.id.row_square_chat_list_send_message);
				timeText = (TextView)view.findViewById(R.id.row_square_chat_list_send_time);
				ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.row_square_chat_list_send_progress_bar);

				/*
				 * Set UI component only in send list
				 */
				int status = message.getStatus();
				if(status ==  AhMessage.STATUS.SENDING.getValue()){
					progressBar.setVisibility(View.VISIBLE);
				}else if(status ==  AhMessage.STATUS.SENT.getValue()){
					progressBar.setVisibility(View.GONE);
				}else if(status ==  AhMessage.STATUS.FAIL.getValue()){
					progressBar.setVisibility(View.GONE);
				}
			} else if(this.layoutId == R.layout.row_square_chat_list_receive){
				/*
				 * Get other user and find UI component
				 */
				final AhUser user = userDBHelper.getUser(message.getSenderId(), true);
				messageText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_message);
				timeText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_time);

				/*
				 * Find UI component only in receive list
				 */
				TextView nickNameText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_nick_name);
				ImageView profileImage = (ImageView)view.findViewById(R.id.row_square_chat_list_receive_profile);
				ImageView profileGenderImage = (ImageView)view.findViewById(R.id.row_square_chat_list_receive_gender);

				/*
				 * Set UI component only in receive list
				 */
				nickNameText.setText(message.getSender());
				if(user.isMale()){
					profileGenderImage.setImageResource(R.drawable.chat_gender_m);
				} else{
					profileGenderImage.setImageResource(R.drawable.chat_gender_w);
				}
				int w = profileImage.getWidth();
				int h = profileImage.getHeight();
				//				Bitmap profileBitmap = BitmapUtil.convertToBitmap(user.getProfilePic(), w, h);
				Bitmap profileBitmap = FileUtil.getImageFromInternalStorage(context, user.getProfilePic(), w, h);
				profileImage.setImageBitmap(profileBitmap);
				profileImage.bringToFront();
				profileImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ProfileDialog profileDialog = new ProfileDialog(user, new AhDialogCallback() {

							@Override
							public void doPositiveThing(Bundle bundle) {
								Intent intent = new Intent(context, ChupaChatActivity.class);
								intent.putExtra(AhGlobalVariable.USER_KEY, user.getId());
								context.startActivity(intent);
							}
							@Override
							public void doNegativeThing(Bundle bundle) {
								Intent intent = new Intent(context, ProfileImageActivity.class);
								intent.putExtra(AhGlobalVariable.USER_KEY, user.getId());
								context.startActivity(intent);
							}
						});
						profileDialog.show(fragment.getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
					}
				});
			}


			/*
			 * Set Shared UI component
			 */
			messageText.setText(message.getContent());
			timeText.setText(message.getTimeStamp());
		}
		return view;
	}


	@Override
	public boolean isEnabled(int position) {
		return false;
	}
}
