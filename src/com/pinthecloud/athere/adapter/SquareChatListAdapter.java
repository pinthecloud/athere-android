package com.pinthecloud.athere.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.UserDBHelper;
import com.pinthecloud.athere.util.BitmapUtil;

public class SquareChatListAdapter extends ArrayAdapter<AhMessage> {

	private int layoutId;
	private ArrayList<AhMessage> items;

	private AhApplication app;
	private LayoutInflater inflater;
	private PreferenceHelper pref;
	private UserDBHelper userDBHelper;


	public SquareChatListAdapter(Context context, int layoutId, ArrayList<AhMessage> items) {
		super(context, layoutId, items);
		this.layoutId = layoutId;
		this.items = items;

		this.app = AhApplication.getInstance(); 
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.pref = new PreferenceHelper(context);
		this.userDBHelper = app.getUserDBHelper();
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;

		AhMessage message = items.get(position);
		if (message != null) {
			// Inflate different layout by user
			if(message.isMine(pref.getString(AhGlobalVariable.USER_ID_KEY))){
				this.layoutId = R.layout.row_square_chat_list_send;
			} else{
				this.layoutId = R.layout.row_square_chat_list_receive;
			}
			view = inflater.inflate(this.layoutId, parent, false);

			/*
			 * Find UI component
			 */
			TextView nickNameText = null;
			TextView messageText = null;
			TextView timeText = null;
			if(this.layoutId == R.layout.row_square_chat_list_send){
				nickNameText = (TextView)view.findViewById(R.id.row_square_chat_list_send_nickname);
				messageText = (TextView)view.findViewById(R.id.row_square_chat_list_send_message);
				timeText = (TextView)view.findViewById(R.id.row_square_chat_list_send_time);
				ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.row_square_chat_list_send_progress_bar);

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
				User user = userDBHelper.getUser(message.getSenderId());
				
				// TODO need to be changed!!
				if(user != null){
					Bitmap pictureBitmap = BitmapUtil.convertToBitmap(user.getProfilePic());
					profileImage.setImageBitmap(pictureBitmap);
				}
				
			}


			/*
			 * Set Shared UI component
			 */
			messageText.setText(message.getContent());
			nickNameText.setText(message.getSender());
			timeText.setText(message.getTimeStamp());
		}
		return view;
	}


	@Override
	public boolean isEnabled(int position) {
		return false;
	}
}
