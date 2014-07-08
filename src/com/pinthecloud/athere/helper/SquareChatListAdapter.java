package com.pinthecloud.athere.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.model.AhMessage;

public class SquareChatListAdapter extends ArrayAdapter<AhMessage>{

	private Context context;
	private int layoutId;
	private ArrayList<AhMessage> items;


	public SquareChatListAdapter(Context context, int layoutId, ArrayList<AhMessage> items) {
		super(context, layoutId, items);
		this.context = context;
		this.layoutId = layoutId;
		this.items = items;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// TODO Set view by message
			view = inflater.inflate(this.layoutId, parent, false);
		}

		AhMessage message = items.get(position);
		if (message != null) {
			/*
			 * Find UI component
			 */
			ImageView profileImage = null;
			TextView nickNameText = null;
			TextView messageText = null;
			TextView timeText = null;
			TextView readText = null;

			if(this.layoutId == R.layout.row_square_chat_list_send){
				profileImage = (ImageView)view.findViewById(R.id.row_square_chat_list_send_profile);
				nickNameText = (TextView)view.findViewById(R.id.row_square_chat_list_send_nickname);
				messageText = (TextView)view.findViewById(R.id.row_square_chat_list_send_message);
				timeText = (TextView)view.findViewById(R.id.row_square_chat_list_send_time);
				readText = (TextView)view.findViewById(R.id.row_square_chat_list_send_read);
			}else if(this.layoutId == R.layout.row_square_chat_list_receive){
				profileImage = (ImageView)view.findViewById(R.id.row_square_chat_list_receive_profile);
				nickNameText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_nickname);
				messageText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_message);
				timeText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_time);
				readText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_read);
			}


			/*
			 * Set UI component
			 */
			messageText.setText(message.getContent());

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
			String time = sdf.format(calendar.getTime());
			timeText.setText(time);
		}
		return view;
	}
}
