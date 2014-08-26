package com.pinthecloud.athere.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ChupaChatActivity;
import com.pinthecloud.athere.activity.ProfileImageActivity;
import com.pinthecloud.athere.database.MessageDBHelper;
import com.pinthecloud.athere.database.UserDBHelper;
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.dialog.ProfileDialog;
import com.pinthecloud.athere.fragment.AhFragment;
import com.pinthecloud.athere.fragment.SquareChatFragment;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;

public class SquareChatListAdapter extends ArrayAdapter<AhMessage> {

	private final int NOTIFICATION = 0;
	private final int SEND = 1;
	private final int RECEIVE = 2;

	private Context context;
	private AhFragment frag;
	private LayoutInflater inflater;
	private List<AhMessage> items;

	private UserDBHelper userDBHelper;
	private MessageDBHelper messageDBHelper;
	private CachedBlobStorageHelper blobStorageHelper;

	public SquareChatListAdapter(Context context, AhFragment fragment, List<AhMessage> items) {
		super(context, 0, items);
		this.context = context;
		this.frag = fragment;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;

		AhApplication app = AhApplication.getInstance(); 
		this.userDBHelper = app.getUserDBHelper();
		this.blobStorageHelper = app.getBlobStorageHelper();
		this.messageDBHelper = app.getMessageDBHelper();
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		View view = convertView;
		int type = getItemViewType(position);
		if (view == null) {
			if(type == NOTIFICATION){
				view = inflater.inflate(R.layout.row_chat_notification_list, parent, false);
			} else if(type == SEND){
				view = inflater.inflate(R.layout.row_square_chat_list_send, parent, false);
			} else if(type == RECEIVE){
				view = inflater.inflate(R.layout.row_square_chat_list_receive, parent, false);
			}
		}

		final AhMessage message = items.get(position);
		if (message != null) {
			/*
			 * Find UI component
			 */
			TextView messageText = null;
			if(type == NOTIFICATION){
				messageText = (TextView)view.findViewById(R.id.row_chat_notification_text);
			} else if(type == SEND){
				messageText = (TextView)view.findViewById(R.id.row_square_chat_list_send_message);

				/*
				 * Find UI component only in receive list
				 */
				TextView timeText = (TextView)view.findViewById(R.id.row_square_chat_list_send_time);
				ImageButton failButton = (ImageButton)view.findViewById(R.id.row_square_chat_list_send_fail);
				ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.row_square_chat_list_send_progress_bar);

				/*
				 * Set UI component only in send list
				 */
				failButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Resources resources = context.getResources();
						String dialogMessage = resources.getString(R.string.message_fail_message);
						String resend = resources.getString(R.string.re_send);
						String delete = resources.getString(R.string.delete);
						AhAlertDialog reSendOrCancelDialog = new AhAlertDialog(null, dialogMessage, resend, delete, true, new AhDialogCallback() {

							@Override
							public void doPositiveThing(Bundle bundle) {
								messageDBHelper.deleteMessage(message.getId());
								items.remove(position);
								SquareChatFragment squareChatFragment = (SquareChatFragment)frag;
								squareChatFragment.sendTalk(message);
							}
							@Override
							public void doNegativeThing(Bundle bundle) {
								messageDBHelper.deleteMessage(message.getId());
								items.remove(position);
								notifyDataSetChanged();
							}
						});
						reSendOrCancelDialog.show(frag.getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
					}
				});
				int status = message.getStatus();
				if(status ==  AhMessage.STATUS.SENDING.getValue()){
					timeText.setVisibility(View.GONE);
					failButton.setVisibility(View.GONE);
					progressBar.setVisibility(View.VISIBLE);
				}else if(status ==  AhMessage.STATUS.SENT.getValue()){
					String time = message.getTimeStamp();
					String hour = time.substring(8, 10);
					String minute = time.substring(10, 12);
					timeText.setText(hour + ":" + minute);
					timeText.setVisibility(View.VISIBLE);
					failButton.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
				}else if(status ==  AhMessage.STATUS.FAIL.getValue()){
					timeText.setVisibility(View.GONE);
					failButton.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
				}
			} else if(type == RECEIVE){
				/*
				 * Get other user and find UI component
				 */
				final AhUser user = userDBHelper.getUser(message.getSenderId(), true);
				if (user == null) return view;
				messageText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_message);

				/*
				 * Find UI component only in receive list
				 */
				TextView timeText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_time);
				TextView nickNameText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_nick_name);
				final ImageView profileImage = (ImageView)view.findViewById(R.id.row_square_chat_list_receive_profile);
				ImageView profileGenderImage = (ImageView)view.findViewById(R.id.row_square_chat_list_receive_gender);

				/*
				 * Set UI component only in receive list
				 */
				String time = message.getTimeStamp();
				String hour = time.substring(8, 10);
				String minute = time.substring(10, 12);
				timeText.setText(hour + ":" + minute);
				nickNameText.setText(message.getSender());
				if(user.isMale()){
					profileGenderImage.setImageResource(R.drawable.chat_gender_m);
				} else{
					profileGenderImage.setImageResource(R.drawable.chat_gender_w);
				}
				//				int w = profileImage.getWidth();
				//				int h = profileImage.getHeight();
				//				blobStorageHelper.getBitmapAsync(frag, user.getId(), w, h, new AhEntityCallback<Bitmap>() {
				//
				//					@Override
				//					public void onCompleted(Bitmap entity) {
				//						profileImage.setImageBitmap(entity);
				//					}
				//				});
				blobStorageHelper.setImageViewAsync(frag, user.getId(), profileImage);
				profileImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ProfileDialog profileDialog = new ProfileDialog(frag, user, new AhDialogCallback() {

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
						profileDialog.show(frag.getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
					}
				});
			}


			/*
			 * Set Shared UI component
			 */
			messageText.setText(message.getContent());
		}
		return view;
	}


	@Override
	public boolean isEnabled(int position) {
		return false;
	}


	@Override
	public int getViewTypeCount() {
		return 3;
	}


	@Override
	public int getItemViewType(int position) {
		// Inflate different layout by user
		AhMessage message = getItem(position);
		if(message.isNotification()){
			return NOTIFICATION;
		}else{
			if(message.isMine()){
				return SEND;
			} else{
				return RECEIVE;
			}
		}
	}
}
