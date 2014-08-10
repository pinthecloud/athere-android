package com.pinthecloud.athere.adapter;

import java.util.List;

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

	private LayoutInflater inflater;
	private int layoutId;
	private List<AhMessage> items;

	private AhApplication app;
	private PreferenceHelper pref;
	private UserDBHelper userDBHelper;


	public SquareChatListAdapter(Context context, int layoutId, List<AhMessage> items) {
		super(context, layoutId, items);
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layoutId = layoutId;
		this.items = items;
		
		this.app = AhApplication.getInstance(); 
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
			TextView messageText = null;
			TextView timeText = null;
			if(this.layoutId == R.layout.row_square_chat_list_send){
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
				/*
				 * Get other user and find UI component
				 */
				User user = userDBHelper.getUser(message.getSenderId());
				messageText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_message);
				timeText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_time);

				/*
				 * Find UI component only in receive list
				 */
				TextView nickNameText = (TextView)view.findViewById(R.id.row_square_chat_list_receive_nickname);
				ImageView profileImage = (ImageView)view.findViewById(R.id.row_square_chat_list_receive_profile);
				
				/*
				 * Set UI component only in receive list
				 */
				nickNameText.setText(message.getSender());
				if(user.isMale()){
					nickNameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chat_gender_m, 0);
				}else{
					nickNameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chat_gender_w, 0);	
				}
				
				Bitmap pictureBitmap = BitmapUtil.convertToBitmap(user.getProfilePic());
				profileImage.setImageBitmap(pictureBitmap);
				profileImage.bringToFront();
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
