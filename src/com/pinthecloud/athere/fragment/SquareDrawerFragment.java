package com.pinthecloud.athere.fragment;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquareDrawerParticipantListAdapter;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhException;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.UserDBHelper;

public class SquareDrawerFragment extends AhFragment {

	private DrawerLayout mDrawerLayout;
	private View mFragmentView;

	private ListView participantListView;
	private SquareDrawerParticipantListAdapter participantListAdapter; 

	private ProgressBar progressBar;
	private Button exitButton;

	private List<User> userList;

	private SquareDrawerFragmentCallbacks callbacks;
	
	private UserDBHelper userDBHelper;
	private MessageHelper messageHelper;
	
	public SquareDrawerFragment _this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDBHelper = app.getUserDBHelper();
		messageHelper = app.getMessageHelper();
		userList = userDBHelper.getAllUsers();
		Log("userList : ",userList.toString());
		_this = this;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_drawer, container, false);
		/*
		 * Set Ui Component
		 */
		progressBar = (ProgressBar) view.findViewById(R.id.square_drawer_frag_progress_bar);
		participantListView = (ListView) view.findViewById(R.id.square_drawer_frag_participant_list);
		exitButton = (Button) view.findViewById(R.id.square_drawer_frag_exit_button);


		/*
		 * Set participant list view
		 */
		participantListAdapter = new SquareDrawerParticipantListAdapter
				(context, R.layout.row_square_drawer_participant_list, userList);
		participantListView.setAdapter(participantListAdapter);
		participantListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				final User user = userList.get(position);
//				final EditText et = new EditText(_this.context);
//				
//				new AlertDialog.Builder(_this.context)
//				.setTitle("Chupa")
//				.setMessage("Sending Chupa to " + user.getNickName())
//				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						AhMessage message = new AhMessage();
//						message.setType(AhMessage.MESSAGE_TYPE.CHUPA);
//						message.setSender(pref.getString(AhGlobalVariable.NICK_NAME_KEY));
//						message.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
//						message.setReceiver(user.getNickName());
//						message.setReceiverId(user.getId());
//						message.setContent(et.getText().toString());
//						
//						try{
//							messageHelper.sendMessageAsync(message, new AhEntityCallback<AhMessage>() {
//								
//								@Override
//								public void onCompleted(AhMessage entity) {
//									// TODO Auto-generated method stub
//									Toast.makeText(context, "Successfully sent Chupa", Toast.LENGTH_SHORT).show();
//								}
//							});
//						} catch (AhException e){
//							Toast.makeText(context, "Failed sending Chupa", Toast.LENGTH_SHORT).show();
//						}
////						InputMethodManager inputManager = (InputMethodManager) _this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
////					    //check if no view has focus:
////					    if(v==null)
////					        return;
////					    inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//						et.clearFocus();
//					}
//				})
//				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						et.clearFocus();
//						dialog.cancel();
//					}
//				})
//				.setIcon(android.R.drawable.ic_dialog_alert)
//				.setView(et)
//				.show();
			}
		});


		/*
		 * Set Button
		 */
		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				progressBar.setVisibility(View.VISIBLE);
				AhMessage.Builder messageBuilder = new AhMessage.Builder();
				messageBuilder.setContent("Exit Square");
				messageBuilder.setSender(pref.getString(AhGlobalVariable.NICK_NAME_KEY));
				messageBuilder.setSenderId(pref.getString(AhGlobalVariable.USER_ID_KEY));
				messageBuilder.setReceiverId(pref.getString(AhGlobalVariable.SQUARE_ID_KEY));
				messageBuilder.setType(AhMessage.MESSAGE_TYPE.EXIT_SQUARE);
				
				final AhMessage message = messageBuilder.build();
				message.setStatus(AhMessage.SENDING);
				userDBHelper.deleteAllUsers();
				messageHelper.sendMessageAsync(message, new AhEntityCallback<AhMessage>() {

					@Override
					public void onCompleted(AhMessage entity) {
						message.setStatus(AhMessage.SENT);

						activity.runOnUiThread(new Runnable(){

							@Override
							public void run() {
								callbacks.exitSquare();
							}
						});
					}
				});
			}
		});

		return view;
	}
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callbacks = (SquareDrawerFragmentCallbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		callbacks = null;
	}


	public void setUp(View fragmentView, DrawerLayout drawerLayout) {
		this.mFragmentView = fragmentView;
		this.mDrawerLayout = drawerLayout;
	}


	public static interface SquareDrawerFragmentCallbacks {
		public void exitSquare();
	}
}
