package com.pinthecloud.athere.fragment;

import java.util.List;

import android.app.ActionBar;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.ProfileImageActivity;
import com.pinthecloud.athere.adapter.ChupaChatListAdapter;
import com.pinthecloud.athere.dialog.ProfileDialog;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;

public class ChupaChatFragment extends AhFragment {

	private EditText messageEditText;
	private ImageButton sendButton;

	private ImageView otherProfileImage;
	private TextView otherNickName;
	private TextView otherAgeGender;

	private ListView messageListView;
	private ChupaChatListAdapter messageListAdapter;
	private List<AhMessage> chupas;
	private AhMessage chupa;

	public static AhUser otherUser;
	private AhUser myUser;
	private String chupaCommunId;
	private boolean isOtherUserExit;
	private boolean isTypedMessage = false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get parameter from previous activity intent
		Intent intent = activity.getIntent();
		otherUser = intent.getParcelableExtra(AhGlobalVariable.USER_KEY);
		myUser = userHelper.getMyUserInfo();
		chupaCommunId = AhMessage.buildChupaCommunId(myUser.getId(), otherUser.getId());
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_chupa_chat, container, false);


		/*
		 * Remove Notification when the user enters the Chupa chat room.
		 * Set chupaCommunId and Remove badge.
		 */
		NotificationManager mNotificationManager = (NotificationManager) activity
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);


		/*
		 * Set UI component
		 */
		otherProfileImage = (ImageView) view.findViewById(R.id.chupa_chat_frag_other_profile);
		otherNickName = (TextView) view.findViewById(R.id.chupa_chat_frag_other_nick_name);
		otherAgeGender = (TextView) view.findViewById(R.id.chupa_chat_frag_other_age_gender);
		messageListView = (ListView) view.findViewById(R.id.chupa_chat_frag_list);
		messageEditText = (EditText) view.findViewById(R.id.chupa_chat_frag_message_text);
		sendButton = (ImageButton) view.findViewById(R.id.chupa_chat_frag_send_button);


		/*
		 * Set Action Bar
		 */
		ActionBar actionBar = activity.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);


		/*
		 * Set message listview
		 */
		messageListAdapter = new ChupaChatListAdapter(context, this);
		messageListView.setAdapter(messageListAdapter);


		/*
		 * Set other user bar
		 */
		otherNickName.setText(otherUser.getNickName());
		otherAgeGender.setText(""+otherUser.getAge());

		if (otherUser.isMale()) {
			otherAgeGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.general_gender_m, 0);
		} else {
			otherAgeGender.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.general_gender_w, 0);
		}

		otherProfileImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gaHelper.sendEventGA(
						thisFragment.getClass().getSimpleName(),
						"ViewOthersProfile",
						"ChupaProfile");

				ProfileDialog profileDialog = new ProfileDialog(thisFragment, otherUser,
						new AhDialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						// Do nothing
					}

					@Override
					public void doNegativeThing(Bundle bundle) {
						Intent intent = new Intent(context, ProfileImageActivity.class);
						intent.putExtra(AhGlobalVariable.USER_KEY, otherUser);
						context.startActivity(intent);
					}
				});
				profileDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			}
		});


		/*
		 * Set edit text
		 */
		messageEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String message = s.toString().trim();
				if (message.length() < 1) {
					isTypedMessage = false;
				} else {
					isTypedMessage = true;
				}
				sendButton.setEnabled(isSenderButtonEnable());
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});


		/*
		 * Set event on button
		 */
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Make message
				AhMessage.Builder messageBuilder = new AhMessage.Builder();
				messageBuilder.setContent(messageEditText.getText().toString())
				.setSender(myUser.getNickName())
				.setSenderId(myUser.getId())
				.setReceiver(otherUser.getNickName())
				.setReceiverId(otherUser.getId())
				.setType(AhMessage.TYPE.CHUPA)
				.setStatus(AhMessage.STATUS.SENDING);
				sendChupa(messageBuilder.build());
			}
		});
		sendButton.setEnabled(false);


		/**
		 * See 1) com.pinthecloud.athere.helper.MessageEventHelper class, which
		 * is the implementation of the needed structure 2)
		 * com.pinthecloud.athere.AhIntentService class Line #47, which has the
		 * event time when to trigger
		 * 
		 * This method sets the MessageHandler received on app running
		 */
		messageHelper.setMessageHandler(this, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(final AhMessage message) {
				// Only Chupa & Exit & Update & Enter Message can go through
				// Only other user who is going chupa with me can go through
				if (!(message.getType().equals(AhMessage.TYPE.CHUPA.toString()) 
						|| message.getType().equals(AhMessage.TYPE.EXIT_SQUARE.toString())
						|| message.getType().equals(AhMessage.TYPE.UPDATE_USER_INFO.toString())
						|| message.getType().equals(AhMessage.TYPE.ENTER_SQUARE.toString()))
						|| !otherUser.getId().equals(message.getSenderId())){
					return;
				}


				// If update message, check if it's related update
				// (Don't go through other User Exit message)
				if(message.getType().equals(AhMessage.TYPE.UPDATE_USER_INFO.toString())){
					otherUser = userDBHelper.getUser(otherUser.getId(), true);
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							blobStorageHelper.setImageViewAsync(thisFragment, BlobStorageHelper.USER_PROFILE, 
									otherUser.getId()+AhGlobalVariable.SMALL, R.drawable.profile_default, otherProfileImage, true);
							otherNickName.setText(otherUser.getNickName());
						}
					});
					return;
				}


				//If enter message, refresh with null message value
				if(message.getType().equals(AhMessage.TYPE.ENTER_SQUARE.toString())){
					message.setId(null);
				}
				Log(thisFragment, message.getType()+ " id : " + message.getId());
				refreshView(chupaCommunId, message.getId());
			}
		});

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		blobStorageHelper.setImageViewAsync(thisFragment, BlobStorageHelper.USER_PROFILE, 
				otherUser.getId()+AhGlobalVariable.SMALL, R.drawable.profile_default, otherProfileImage, true);
		refreshView(chupaCommunId, null);
	}


	@Override
	public void onStop() {
		otherProfileImage.setImageBitmap(null);
		super.onStop();
	}


	@Override
	public void handleException(AhException ex) {
		if(ex.getMethodName().equals("sendMessageAsync")){
			AhMessage exMessage = (AhMessage)ex.getParameter();
			exMessage.setStatus(AhMessage.STATUS.FAIL);
			messageDBHelper.updateMessages(exMessage);
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					messageListAdapter.notifyDataSetChanged();
				}
			});
		}else{
			super.handleException(ex);
		}
	}


	public void sendChupa(final AhMessage message){
		message.setStatus(AhMessage.STATUS.SENDING);
		messageListAdapter.add(message);
		messageListView.setSelection(messageListView.getCount() - 1);
		messageEditText.setText("");

		int id = messageDBHelper.addMessage(message);
		message.setId(""+id);

		// Send message to server
		messageHelper.sendMessageAsync(thisFragment, message, new AhEntityCallback<AhMessage>() {

			@Override
			public void onCompleted(AhMessage entity) {
				gaHelper.sendEventGA(
						thisFragment.getClass().getSimpleName(),
						"SendChupa",
						"ChupaChat");

				message.setStatus(AhMessage.STATUS.SENT);
				message.setTimeStamp();
				messageDBHelper.updateMessages(message);
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						messageListAdapter.remove(message);
					}
				});
				refreshView(message.getChupaCommunId(), message.getId());
			}
		});
	}


	/*
	 * Set sent and received chupas to list view
	 */
	private void refreshView(String chupaCommunId, final String id) {
		if (chupaCommunId == null || chupaCommunId.equals("")) throw new AhException("No chupaCommunId");


		/*
		 * Clear badge numbers displayed on chupa list
		 */
		messageDBHelper.clearChupaBadgeNum(chupaCommunId);


		/*
		 * Get every chupa by chupaCommunId
		 */
		if (id == null) {
			chupas = messageDBHelper.getChupasByCommunId(chupaCommunId);
		} else {
			int _id = Integer.parseInt(id);
			chupa = messageDBHelper.getMessage(_id);
		}
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (id == null) {
					messageListAdapter.clear();
					messageListAdapter.addAll(chupas);
				} else {
					messageListAdapter.add(chupa);
				}
			}
		});


		/*
		 * If other user exit, add exit message and set nick name text color
		 */
		if (userDBHelper.isUserExit(otherUser.getId())) {
			isOtherUserExit = true;

			String exitMessage = getResources().getString(R.string.exit_square_message);
			String nickName = otherUser.getNickName();
			final AhMessage exitChupa = new AhMessage.Builder()
			.setContent(" " + exitMessage)
			.setSender(nickName)
			.setSenderId(otherUser.getId())
			.setReceiverId(squareHelper.getMySquareInfo().getId())
			.setType(AhMessage.TYPE.EXIT_SQUARE)
			.setStatus(AhMessage.STATUS.SENT)
			.setTimeStamp().build();

			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					sendButton.setEnabled(false);
					otherNickName.setTextColor(context.getResources().getColor(R.color.chupa_list_time));
					messageListAdapter.add(exitChupa);
				}
			});
		}else{
			isOtherUserExit = false;

			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					sendButton.setEnabled(isSenderButtonEnable());
					otherNickName.setTextColor(context.getResources().getColor(R.color.chupa_list_text));
				}
			});
		}


		/*
		 * Set message listview
		 */
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				messageListView.setSelection(messageListView.getCount() - 1);
			}
		});
	}


	private boolean isSenderButtonEnable() {
		return isTypedMessage && !isOtherUserExit;
	}
}
