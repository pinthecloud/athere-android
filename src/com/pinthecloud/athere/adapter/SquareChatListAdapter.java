package com.pinthecloud.athere.adapter;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.util.FileUtil;

public class SquareChatListAdapter extends ArrayAdapter<AhMessage> {

	private Context context;
	private int layoutId;
	private ArrayList<AhMessage> items;

	private PreferenceHelper pref;


	public SquareChatListAdapter(Context context, int layoutId, ArrayList<AhMessage> items) {
		super(context, layoutId, items);
		this.context = context;
		this.layoutId = layoutId;
		this.items = items;

		this.pref = new PreferenceHelper(context);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;

		AhMessage message = items.get(position);
		if (message != null) {
			// If the view is new, inflate it
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater)
						context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				// Inflate different layout by user
				if(message.isMine(pref.getString(AhGlobalVariable.NICK_NAME_KEY))){
					this.layoutId = R.layout.row_square_chat_list_send;
				} else{
					this.layoutId = R.layout.row_square_chat_list_receive;
				}
				view = inflater.inflate(this.layoutId, parent, false);
			}


			/*
			 * Find UI component
			 */
			TextView nickNameText = null;
			TextView messageText = null;
			TextView timeText = null;
			ProgressBar progressBar = null;
			if(this.layoutId == R.layout.row_square_chat_list_send){
				nickNameText = (TextView)view.findViewById(R.id.row_square_chat_list_send_nickname);
				messageText = (TextView)view.findViewById(R.id.row_square_chat_list_send_message);
				timeText = (TextView)view.findViewById(R.id.row_square_chat_list_send_time);
				progressBar = (ProgressBar)view.findViewById(R.id.row_square_chat_list_send_progress_bar);
				
				/*
				 * Set UI component only in send list
				 */
				switch(message.getStatus()){
				case AhMessage.SENDING:
					progressBar.setVisibility(View.VISIBLE);
					break;
				case AhMessage.SENT:
					progressBar.setVisibility(View.GONE);
					break;
				case AhMessage.FAIL:
					progressBar.setVisibility(View.GONE);
					break;
				}
			} else{
				nickNameText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_nickname);
				messageText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_message);
				timeText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_time);

				/*
				 * Set UI component only in receive list
				 */
				ImageView profileImage = (ImageView)view.findViewById(R.id.row_square_chat_list_receive_profile);
				try {
					profileImage.setImageBitmap(FileUtil.getImageFromInternalStorage(context, AhGlobalVariable.PROFILE_PICTURE_CIRCLE_NAME));
				} catch (FileNotFoundException e) {
					Log.d(AhGlobalVariable.LOG_TAG, "Error of SquareChatListAdapter getView : " + e.getMessage());
				}
			}


			/*
			 * Set Shared UI component
			 */
			messageText.setText(message.getContent());
			nickNameText.setText(pref.getString(AhGlobalVariable.NICK_NAME_KEY));

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
			String time = sdf.format(calendar.getTime());
			timeText.setText(time);
		}
		return view;
	}


	@Override
	public boolean isEnabled(int position) {
		return false;
	}
}
