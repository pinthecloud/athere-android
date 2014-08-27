package com.pinthecloud.athere.adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.database.MessageDBHelper;
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.fragment.ChupaChatFragment;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.AhMessage;

public class ChupaChatListAdapter extends ArrayAdapter<AhMessage> {

	private final int NOTIFICATION = 0;
	private final int SEND = 1;
	private final int RECEIVE = 2;

	private Context context;
	private Fragment frag;
	private LayoutInflater inflater;
	private MessageDBHelper messageDBHelper;


	public ChupaChatListAdapter(Context context, Fragment frag) {
		super(context, 0);
		this.context = context;
		this.frag = frag;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		AhApplication app = AhApplication.getInstance();
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
				view = inflater.inflate(R.layout.row_chupa_chat_list_send, parent, false);
			} else if(type == RECEIVE){
				view = inflater.inflate(R.layout.row_chupa_chat_list_receive, parent, false);
			}
		}

		final AhMessage message = this.getItem(position);
		if (message != null) {
			/*
			 * Find UI component
			 */
			TextView messageText = null;
			if(type == NOTIFICATION){
				messageText = (TextView)view.findViewById(R.id.row_chat_notification_text);
			}else if(type == SEND){
				/*
				 * Find UI component only in receive list
				 */
				TextView timeText = (TextView)view.findViewById(R.id.row_chupa_chat_list_send_time);
				ImageButton failButton = (ImageButton)view.findViewById(R.id.row_chupa_chat_list_send_fail);
				ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.row_chupa_chat_list_send_progress_bar);

				/*
				 * Set UI component only in send list
				 */
				failButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						showReSendOrCancelDialog(message, position, true);
					}
				});
				final int status = message.getStatus();
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
				
				
				/*
				 * Common UI component
				 */
				messageText = (TextView)view.findViewById(R.id.row_chupa_chat_list_send_message);
				messageText.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						boolean cancel = true;
						if(status ==  AhMessage.STATUS.SENT.getValue()){
							cancel = false;
						}
						showReSendOrCancelDialog(message, position, cancel);
						return false;
					}
				});
			} else if(type == RECEIVE){
				/*
				 * Get other user and find Common UI component
				 */
				messageText = (TextView)view.findViewById(R.id.row_chupa_chat_list_receive_message);
				messageText.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						showReSendOrCancelDialog(message, position, false);
						return false;
					}
				});
				TextView timeText = (TextView)view.findViewById(R.id.row_chupa_chat_list_receive_time);
				String time = message.getTimeStamp();
				String hour = time.substring(8, 10);
				String minute = time.substring(10, 12);
				timeText.setText(hour + ":" + minute);
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


	private void showReSendOrCancelDialog(final AhMessage message, final int position, boolean cancel){
		Resources resources = context.getResources();
		String dialogMessage = resources.getString(R.string.message_fail_message);
		String resend = resources.getString(R.string.re_send);
		String delete = resources.getString(R.string.delete);
		AhAlertDialog reSendOrCancelDialog = new AhAlertDialog(null, dialogMessage, delete, resend, cancel, new AhDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				// Delete
				messageDBHelper.deleteMessage(message.getId());
				remove(getItem(position));
				notifyDataSetChanged();
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
				// Re Send
				messageDBHelper.deleteMessage(message.getId());
				remove(getItem(position));
				ChupaChatFragment squareChatFragment = (ChupaChatFragment)frag;
				squareChatFragment.sendChupa(message);
			}
		});
		reSendOrCancelDialog.show(frag.getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
	}
}
