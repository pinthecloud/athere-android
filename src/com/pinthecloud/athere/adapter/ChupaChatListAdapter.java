package com.pinthecloud.athere.adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

	private Context context;
	private Fragment fragment;
	private LayoutInflater inflater;
	private int layoutId;
//	private List<AhMessage> items;
	private ChupaChatListAdapter _this;
	private MessageDBHelper messageDBHelper;


	public ChupaChatListAdapter(Context context, Fragment fragment, int layoutId) {
		super(context, layoutId);
		this.context = context;
		this.fragment = fragment;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutId = layoutId;
//		this.items = items;

		this._this = this;
		AhApplication app = AhApplication.getInstance();
		this.messageDBHelper = app.getMessageDBHelper();
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		View view = convertView;

		final AhMessage message = this.getItem(position);
		if (message != null) {
			// Inflate different layout by user
			if(message.isNotification()){
				this.layoutId = R.layout.row_chat_notification_list;
			} else{
				if(message.isMine()){
					this.layoutId = R.layout.row_chupa_chat_list_send;
				} else{
					this.layoutId = R.layout.row_chupa_chat_list_receive;
				}
			}
			view = inflater.inflate(this.layoutId, parent, false);


			/*
			 * Find UI component
			 */
			TextView messageText = null;
			if(this.layoutId == R.layout.row_chat_notification_list){
				messageText = (TextView)view.findViewById(R.id.row_chat_notification_text);
			}else if(this.layoutId == R.layout.row_chupa_chat_list_send){
				messageText = (TextView)view.findViewById(R.id.row_chupa_chat_list_send_message);

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
						Resources resources = context.getResources();
						String dialogMessage = resources.getString(R.string.message_fail_message);
						String resend = resources.getString(R.string.re_send);
						String delete = resources.getString(R.string.delete);
						AhAlertDialog reSendOrCancelDialog = new AhAlertDialog(null, dialogMessage, resend, delete, true, new AhDialogCallback() {

							@Override
							public void doPositiveThing(Bundle bundle) {
								messageDBHelper.deleteMessage(message.getId());
								_this.remove(_this.getItem(position));
//								items.remove(position);
								ChupaChatFragment chupaChatFragment = (ChupaChatFragment)fragment;
								chupaChatFragment.sendChupa(message);
							}
							@Override
							public void doNegativeThing(Bundle bundle) {
								_this.remove(_this.getItem(position));
//								items.remove(position);
								notifyDataSetChanged();
							}
						});
						reSendOrCancelDialog.show(fragment.getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
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
			} else if(this.layoutId == R.layout.row_chupa_chat_list_receive){
				/*
				 * Get other user and find UI component
				 */
				messageText = (TextView)view.findViewById(R.id.row_chupa_chat_list_receive_message);
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
}
