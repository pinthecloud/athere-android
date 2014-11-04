package com.pinthecloud.athere.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
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
import com.pinthecloud.athere.fragment.ChatFragment;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.helper.CachedBlobStorageHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum TYPE{
		ADMIN,
		ENTER_NOTIFICATION,
		NOTIFICATION,
		SEND,
		RECEIVE
	}

	private AhApplication app;
	private Context context;
	private AhFragment frag;
	private List<AhMessage> chatList;

	private UserDBHelper userDBHelper;
	private MessageDBHelper messageDBHelper;
	private CachedBlobStorageHelper blobStorageHelper;


	public ChatListAdapter(Context context, AhFragment frag, List<AhMessage> chatList) {
		this.app = AhApplication.getInstance();
		this.context = context;
		this.frag = frag;
		this.chatList = chatList;
		this.userDBHelper = app.getUserDBHelper();
		this.blobStorageHelper = app.getBlobStorageHelper();
		this.messageDBHelper = app.getMessageDBHelper();
	}


	public static class AdminViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView messageText;
		public TextView timeText;

		public AdminViewHolder(View view) {
			super(view);
			this.view = view;
			this.messageText = (TextView)view.findViewById(R.id.row_chat_list_admin_message);
			this.timeText = (TextView)view.findViewById(R.id.row_chat_list_admin_time);
		}
	}


	public static class EnterNotificationViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView nickNameText;
		public TextView messageText;

		public EnterNotificationViewHolder(View view) {
			super(view);
			this.view = view;
			this.nickNameText = (TextView)view.findViewById(R.id.row_chat_list_enter_exit_notification_nick_name);
			this.messageText = (TextView)view.findViewById(R.id.row_chat_list_enter_exit_notification_message);
		}
	}


	public static class NotificationViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView messageText;

		public NotificationViewHolder(View view) {
			super(view);
			this.view = view;
			this.messageText = (TextView)view.findViewById(R.id.row_chat_list_notification_message);
		}
	}


	public static class SendViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView timeText;
		public ImageButton failButton;
		public ProgressBar progressBar;
		public TextView messageText;

		public SendViewHolder(View view) {
			super(view);
			this.view = view;
			this.timeText = (TextView)view.findViewById(R.id.row_chat_list_send_time);
			this.failButton = (ImageButton)view.findViewById(R.id.row_chat_list_send_fail);
			this.progressBar = (ProgressBar)view.findViewById(R.id.row_chat_list_send_progress_bar);
			this.messageText = (TextView)view.findViewById(R.id.row_chat_list_send_message);
		}
	}


	public static class ReceiveViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView timeText;
		public TextView nickNameText;
		public ImageView profileImage;
		public TextView messageText;

		public ReceiveViewHolder(View view) {
			super(view);
			this.view = view;
			this.timeText = (TextView)view.findViewById(R.id.row_chat_list_receive_time);
			this.nickNameText = (TextView)view.findViewById(R.id.row_chat_list_receive_nick_name_gender);
			this.profileImage = (ImageView)view.findViewById(R.id.row_chat_list_receive_profile_image);
			this.messageText = (TextView)view.findViewById(R.id.row_chat_list_receive_message);
		}
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		ViewHolder viewHolder = null;
		if(viewType == TYPE.ADMIN.ordinal()){
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_list_admin, parent, false);
			viewHolder = new AdminViewHolder(view);
		} else if(viewType == TYPE.ENTER_NOTIFICATION.ordinal()){
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_list_enter_notification, parent, false);
			viewHolder = new EnterNotificationViewHolder(view);
		} else if(viewType == TYPE.NOTIFICATION.ordinal()){
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_list_notification, parent, false);
			viewHolder = new NotificationViewHolder(view);
		} else if(viewType == TYPE.SEND.ordinal()){
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_list_send, parent, false);
			viewHolder = new SendViewHolder(view);
		} else if(viewType == TYPE.RECEIVE.ordinal()){
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_list_receive, parent, false);
			viewHolder = new ReceiveViewHolder(view);
		}
		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		AhMessage chat = chatList.get(position);
		int viewType = getItemViewType(position);
		if(viewType == TYPE.ADMIN.ordinal()){
			AdminViewHolder viewHolder = (AdminViewHolder)holder;
			setAdminComponent(viewHolder, chat);
		} else if(viewType == TYPE.ENTER_NOTIFICATION.ordinal()){
			EnterNotificationViewHolder viewHolder = (EnterNotificationViewHolder)holder;
			setEnterNotificationComponent(viewHolder, chat);
		} else if(viewType == TYPE.NOTIFICATION.ordinal()){
			NotificationViewHolder viewHolder = (NotificationViewHolder)holder;
			setNotificationComponent(viewHolder, chat);
		} else if(viewType == TYPE.SEND.ordinal()){
			SendViewHolder viewHolder = (SendViewHolder)holder;
			setSendComponent(viewHolder, chat);
		} else if(viewType == TYPE.RECEIVE.ordinal()){
			ReceiveViewHolder viewHolder = (ReceiveViewHolder)holder;
			setReceiveComponent(viewHolder, chat);
		}
	}


	@Override
	public int getItemCount() {
		return this.chatList.size();
	}


	@Override
	public int getItemViewType(int position) {
		AhMessage message = chatList.get(position);
		if(message.isAdmin()){
			return TYPE.ADMIN.ordinal();
		} else if (message.isEnterExitNotification()){
			return TYPE.ENTER_NOTIFICATION.ordinal();
		} else if (message.isNotification()){
			return TYPE.NOTIFICATION.ordinal();
		} else {
			if(message.isMine()){
				return TYPE.SEND.ordinal();
			} else{
				return TYPE.RECEIVE.ordinal();
			}
		}
	}


	private void setAdminComponent(AdminViewHolder holder, AhMessage chat){
		String time = chat.getTimeStamp();
		String hour = time.substring(8, 10);
		String minute = time.substring(10, 12);
		holder.timeText.setText(hour + ":" + minute);
		holder.messageText.setText(chat.getContent());
	}


	private void setEnterNotificationComponent(EnterNotificationViewHolder holder, AhMessage chat){
		holder.nickNameText.setText(chat.getSender());
		holder.messageText.setText(chat.getContent());
	}


	private void setNotificationComponent(NotificationViewHolder holder, AhMessage chat){
		holder.messageText.setText(chat.getContent());
	}


	private void setSendComponent(SendViewHolder holder, final AhMessage chat){
		holder.failButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showReSendOrCancelDialog(chat, true);
			}
		});


		final int status = chat.getStatus();
		if(status ==  AhMessage.STATUS.SENDING.getValue()){
			holder.timeText.setVisibility(View.GONE);
			holder.failButton.setVisibility(View.GONE);
			holder.progressBar.setVisibility(View.VISIBLE);
		}else if(status ==  AhMessage.STATUS.SENT.getValue()){
			String time = chat.getTimeStamp();
			String hour = time.substring(8, 10);
			String minute = time.substring(10, 12);
			holder.timeText.setText(hour + ":" + minute);
			holder.timeText.setVisibility(View.VISIBLE);
			holder.failButton.setVisibility(View.GONE);
			holder.progressBar.setVisibility(View.GONE);
		}else if(status ==  AhMessage.STATUS.FAIL.getValue()){
			holder.timeText.setVisibility(View.GONE);
			holder.failButton.setVisibility(View.VISIBLE);
			holder.progressBar.setVisibility(View.GONE);
		}


		/*
		 * Find and Set Common UI component
		 */
		holder.messageText.setText(chat.getContent());
		holder.messageText.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				boolean cancel = true;
				if(status ==  AhMessage.STATUS.SENT.getValue()){
					cancel = false;
				}
				showReSendOrCancelDialog(chat, cancel);
				return false;
			}
		});
	}


	private void setReceiveComponent(ReceiveViewHolder holder, final AhMessage chat){
		final AhUser user = userDBHelper.getUser(chat.getSenderId(), true);
		if (user == null) return;

		holder.messageText.setText(chat.getContent());
		holder.messageText.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				showReSendOrCancelDialog(chat, false);
				return false;
			}
		});

		holder.nickNameText.setText(chat.getSender());
		if(user.isMale()){
			holder.nickNameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.general_gender_m, 0);
		} else{
			holder.nickNameText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.general_gender_w, 0);
		}

		String time = chat.getTimeStamp();
		String hour = time.substring(8, 10);
		String minute = time.substring(10, 12);
		holder.timeText.setText(hour + ":" + minute);

		blobStorageHelper.setImageViewAsync(frag, BlobStorageHelper.USER_PROFILE, 
				user.getId()+AhGlobalVariable.SMALL, R.drawable.profile_default, holder.profileImage, true);
		holder.profileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				app.getGAHelper().sendEventGA(
						frag.getClass().getSimpleName(),
						"ViewOthersProfile",
						"ChatProfile");

				ProfileDialog profileDialog = new ProfileDialog(frag, user, 
						new AhDialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						app.getGAHelper().sendEventGA(
								frag.getClass().getSimpleName(),
								"SendChupa",
								"ChatSendChupa");

						Intent intent = new Intent(context, ChupaChatActivity.class);
						intent.putExtra(AhGlobalVariable.USER_KEY, user);
						context.startActivity(intent);
					}

					@Override
					public void doNegativeThing(Bundle bundle) {
						Intent intent = new Intent(context, ProfileImageActivity.class);
						intent.putExtra(AhGlobalVariable.USER_KEY, user);
						context.startActivity(intent);
					}
				});
				profileDialog.show(frag.getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			}
		});
	}


	private void showReSendOrCancelDialog(final AhMessage chat, boolean cancel){
		Resources resources = context.getResources();
		String dialogMessage = resources.getString(R.string.send_message_fail_message);
		String resend = resources.getString(R.string.re_send);
		String delete = resources.getString(R.string.delete);
		final ChatFragment chatFragment = (ChatFragment)frag;
		AhAlertDialog reSendOrCancelDialog = new AhAlertDialog(null, dialogMessage, delete, resend, cancel, new AhDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				removeChat(chat);
			}

			@Override
			public void doNegativeThing(Bundle bundle) {
				// Re Send
				removeChat(chat);
				chatFragment.sendChat(chat);
			}
		});
		reSendOrCancelDialog.show(frag.getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
	}


	private void removeChat(AhMessage chat){
		messageDBHelper.deleteMessage(chat.getId());
		notifyItemRemoved(chatList.indexOf(chat));
		chatList.remove(chat);
	}
}
