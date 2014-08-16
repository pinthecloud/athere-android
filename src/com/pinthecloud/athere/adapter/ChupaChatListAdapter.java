package com.pinthecloud.athere.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.AhMessage;

public class ChupaChatListAdapter extends ArrayAdapter<AhMessage> {

	private LayoutInflater inflater;
	private int layoutId;
	private List<AhMessage> items;


	public ChupaChatListAdapter(Context context, int layoutId, List<AhMessage> items) {
		super(context, layoutId, items);
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutId = layoutId;
		this.items = items;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;

		AhMessage message = items.get(position);
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
			TextView timeText = null;
			if(this.layoutId == R.layout.row_chat_notification_list){
				messageText = (TextView)view.findViewById(R.id.row_chat_notification_text);
				timeText = (TextView)view.findViewById(R.id.row_chat_notification_time);
			}else if(this.layoutId == R.layout.row_chupa_chat_list_send){
				messageText = (TextView)view.findViewById(R.id.row_chupa_chat_list_send_message);
				timeText = (TextView)view.findViewById(R.id.row_chupa_chat_list_send_time);
				ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.row_chupa_chat_list_send_progress_bar);


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
			} else if(this.layoutId == R.layout.row_chupa_chat_list_receive){
				/*
				 * Get other user and find UI component
				 */
				messageText = (TextView)view.findViewById(R.id.row_chupa_chat_list_receive_message);
				timeText = (TextView)view.findViewById(R.id.row_chupa_chat_list_receive_time);
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
