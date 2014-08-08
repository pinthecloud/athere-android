package com.pinthecloud.athere.fragment;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.adapter.SquareDrawerParticipantListAdapter;
import com.pinthecloud.athere.dialog.ExitSquareConsentDialog;
import com.pinthecloud.athere.helper.MessageHelper;
import com.pinthecloud.athere.helper.UserHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.User;
import com.pinthecloud.athere.sqlite.MessageDBHelper;
import com.pinthecloud.athere.sqlite.UserDBHelper;

public class SquareDrawerFragment extends AhFragment {

	private SquareDrawerFragmentCallbacks callbacks;
	private DrawerLayout mDrawerLayout;
	private View mFragmentView;
	private SquareDrawerParticipantListAdapter participantListAdapter; 

	private ProgressBar progressBar;
	private ToggleButton chatOnButton;
	private ToggleButton chupaOnButton;
	private ImageButton profileSettingButton;
	private TextView maleNumText;
	private TextView femaleNumText;
	private ListView participantListView;
	private Button exitButton;

	private List<User> userList;

	private UserDBHelper userDBHelper;
	private UserHelper userHelper;
	private MessageHelper messageHelper;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDBHelper = app.getUserDBHelper();
		userHelper = app.getUserHelper();
		messageHelper = app.getMessageHelper();
		userList = userDBHelper.getAllUsers();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_drawer, container, false);

		/*
		 * Set Ui Component
		 */
		progressBar = (ProgressBar) view.findViewById(R.id.square_drawer_frag_progress_bar);
		chatOnButton = (ToggleButton) view.findViewById(R.id.square_drawer_frag_chat_on_button);
		chupaOnButton = (ToggleButton) view.findViewById(R.id.square_drawer_frag_chupa_on_button);
		profileSettingButton = (ImageButton) view.findViewById(R.id.square_drawer_frag_profile_setting_button);
		maleNumText = (TextView) view.findViewById(R.id.square_drawer_frag_member_male_text);
		femaleNumText = (TextView) view.findViewById(R.id.square_drawer_frag_member_female_text);
		participantListView = (ListView) view.findViewById(R.id.square_drawer_frag_participant_list);
		exitButton = (Button) view.findViewById(R.id.square_drawer_frag_exit_button);


		/*
		 * Set member number text
		 * TODO set proper male and female member number text
		 */
		maleNumText.setText("" + userList.size());
		femaleNumText.setText("" + userList.size());


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
				User user = userList.get(position);
				new AlertDialog.Builder(context)
				.setTitle("User Information")
				.setMessage("Show User Detail Info")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
			}
		});


		/*
		 * Set handler for refresh new and old user
		 */
		userHelper.setUserHandler(new AhEntityCallback<User>() {

			@Override
			public void onCompleted(final User user) {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						participantListAdapter.add(user);
						participantListAdapter.notifyDataSetChanged();
					}
				});
			}
		});


		/*
		 * Set setting toggle button
		 */
		chupaOnButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO send push
				pref.putBoolean(AhGlobalVariable.IS_CHUPA_ENABLE_KEY, isChecked);
			}
		});


		/*
		 * Set Button
		 */
		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ExitSquareConsentDialog escDialog = new ExitSquareConsentDialog(new AhDialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						progressBar.setVisibility(View.VISIBLE);
						callbacks.exitSquare();
					}

					@Override
					public void doNegativeThing(Bundle bundle) {
						// Do nothing
					}
				});
				escDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			}
		});
		profileSettingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO setting profile
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
