package com.pinthecloud.athere.fragment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareActivity;
import com.pinthecloud.athere.adapter.SquareListAdapter;
import com.pinthecloud.athere.dialog.SquareEnterDialog;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.helper.LocationHelper;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.interfaces.AhPairEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.util.AsyncChainer;
import com.pinthecloud.athere.util.AsyncChainer.Chainable;



public class SquareListFragment extends AhFragment implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{

	private ProgressBar progressBar;
	private Button locationAccessButton;
	private PullToRefreshListView pullToRefreshListView;
	private ListView squareListView;
	private SquareListAdapter squareListAdapter;
	private View squareListHeader;
	private TextView addressText;

	private LocationHelper locationHelper;
	private LocationListener locationListener;


	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_square_list, container, false);

		/*
		 * Set UI component
		 */
		locationAccessButton = (Button)view.findViewById(R.id.square_list_frag_location_access_button);
		progressBar = (ProgressBar)view.findViewById(R.id.square_list_frag_progress_bar);
		pullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.square_list_frag_list);
		squareListView = pullToRefreshListView.getRefreshableView();
		registerForContextMenu(squareListView);
		squareListHeader = inflater.inflate(R.layout.row_square_list_header, null, false);
		addressText = (TextView)squareListHeader.findViewById(R.id.row_square_list_header_address);


		/*
		 * For easy developing, make back button to super user
		 * DO NOT REMOVE THIS SCRIPT!
		 */
		//		if (PreferenceHelper.getInstance().getBoolean(AhGlobalVariable.SUDO_KEY)) {
		//			Button b = new Button(activity);
		//			b.setText("Go to BasicProfile");
		//			b.setOnClickListener(new OnClickListener() {
		//
		//				@Override
		//				public void onClick(View v) {
		//					Session session = Session.getActiveSession();
		//					if (session != null) {
		//						session.close();
		//					}
		//
		//					Intent intent = new Intent(context, BasicProfileActivity.class);
		//					startActivity(intent);
		//					activity.finish();
		//				}
		//			});
		//			activity.addContentView(b, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		//		}


		/*
		 * Set button
		 */
		locationAccessButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				locationHelper.connect();
			}
		});


		/*
		 * Set square list view
		 */
		squareListView.addHeaderView(squareListHeader);
		squareListHeader.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateNearSquares(View.VISIBLE, locationListener);
			}
		});

		squareListAdapter = new SquareListAdapter(context, thisFragment);
		squareListView.setAdapter(squareListAdapter);
		squareListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/*
				 * Check location service
				 */
				if(!locationHelper.isLocationEnabled()){
					locationHelper.getLocationAccess(activity, new AhDialogCallback() {

						@Override
						public void doPositiveThing(Bundle bundle) {
							// Do nothing
						}
						@Override
						public void doNegativeThing(Bundle bundle) {
							locationAccessButton.setVisibility(View.VISIBLE);
							pullToRefreshListView.setVisibility(View.GONE);
						}
					});
					return;
				}


				/*
				 * Get square and make enter dialog
				 */
				position -= 2;
				final Square square = squareListAdapter.getItem(position);
				Bundle bundle = new Bundle();
				SquareEnterDialog enterDialog = new SquareEnterDialog(square, new AhDialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						enterSquare(square, false);
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
						enterSquare(square, true);
					}
				});


				/*
				 * If enough close to enter or it is super user, check code.
				 * Otherwise, cannot enter.
				 */
				if(square.getDistance() <= square.getEntryRange() 
						|| PreferenceHelper.getInstance().getBoolean(AhGlobalVariable.SUDO_KEY)){

					square.setCode("1");
					if(square.getCode().equals("")){
						enterSquare(square, false);
					} else{
						// This square has code
						bundle.putBoolean(SquareEnterDialog.SHOW_CODE, true);
						enterDialog.setArguments(bundle);
						enterDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
					}
				} else{
					// Give preview condition
					bundle.putBoolean(SquareEnterDialog.SHOW_CODE, false);
					enterDialog.setArguments(bundle);
					enterDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
				}
			}
		});
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				updateNearSquares(View.GONE, locationListener);
			}
		});


		/*
		 * Set location helper and listener 
		 */
		locationHelper = new LocationHelper(activity, this, this);
		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location loc) {
				locationHelper.removeLocationUpdates(this);
				getNearSquares(loc);
				locationHelper.getAddress(loc, new AhEntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						addressText.setText(entity);
					}
				});
			}
		};

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		if(locationHelper.isLocationEnabled()){
			locationAccessButton.setVisibility(View.GONE);
			pullToRefreshListView.setVisibility(View.VISIBLE);
		}else{
			locationAccessButton.setVisibility(View.VISIBLE);
			pullToRefreshListView.setVisibility(View.GONE);
		}
		locationHelper.connect();
	}


	@Override
	public void onStop() {
		locationHelper.disconnect();
		super.onStop();
	}


	/*
	 * Enter a square 
	 */
	private void enterSquare(final Square square, final boolean isPreview){
		progressBar.setVisibility(View.VISIBLE);
		progressBar.bringToFront();
		squareListView.setEnabled(false);

		AsyncChainer.asyncChain(thisFragment, new Chainable(){

			@Override
			public void doNext(AhFragment frag) {
				// Get a user object from preference settings
				// Enter a square with the user
				final AhUser user = userHelper.getMyUserInfo();
				userHelper.enterSquareAsync(frag, user, square.getId(), isPreview, new AhPairEntityCallback<String, List<AhUser>>() {

					@Override
					public void onCompleted(String userId, List<AhUser> list) {
						userDBHelper.addAllUsers(list);
					}
				});
			}
		}, new Chainable() {

			@Override
			public void doNext(AhFragment frag) {
				if (isPreview) {
					saveInformationAndEnterSquare(square, isPreview);
					return;
				}

				String enterMessage = getResources().getString(R.string.enter_square_message);
				AhUser user = userHelper.getMyUserInfo();
				AhMessage message = new AhMessage.Builder()
				.setContent(" " + enterMessage)
				.setSender(user.getNickName())
				.setSenderId(user.getId())
				.setReceiverId(square.getId())
				.setType(AhMessage.TYPE.ENTER_SQUARE).build();
				messageHelper.sendMessageAsync(frag, message, new AhEntityCallback<AhMessage>() {

					@Override
					public void onCompleted(AhMessage entity) {
						saveInformationAndEnterSquare(square, isPreview);
					}
				});
			}
		});
	}


	/*
	 * Save this setting and go to next activity
	 */
	private void saveInformationAndEnterSquare(Square square, boolean isPreview) {
		Time time = new Time();
		time.setToNow();

		userHelper.setChatEnable(true);
		squareHelper.setMySquareId(square.getId())
		.setMySquareName(square.getName())
		.setMySquareResetTime(square.getResetTime())
		.setSquareExitTab(SquareTabFragment.CHAT_TAB)
		.setLoggedInSquare(true)
		.setTimeStampAtLoggedInSquare(time.format("%Y:%m:%d:%H"))
		.setPreview(isPreview)
		.setReview(true);

		Intent intent = new Intent(context, SquareActivity.class);
		startActivity(intent);
		activity.finish();
	}


	/*
	 * Get square near from user
	 * Now it just gets all squares cause of location law. (lati and longi is 0)
	 */
	private void getNearSquares(Location loc){
		double latitude = loc.getLatitude();
		double longitude = loc.getLongitude();
		squareHelper.getSquareListAsync(thisFragment, latitude, longitude, new AhListCallback<Square>() {

			@Override
			public void onCompleted(List<Square> list, int count) {
				progressBar.setVisibility(View.GONE);
				pullToRefreshListView.onRefreshComplete();

				// Sort square list by distance and premium
				Collections.sort(list, new Comparator<Square>(){

					@Override
					public int compare(Square lhs, Square rhs) {
						return lhs.getName().compareTo(rhs.getName());
					}
				});
				Collections.sort(list, new Comparator<Square>(){

					@Override
					public int compare(Square lhs, Square rhs) {
						return lhs.getDistance() > rhs.getDistance() ? 1 : 
							lhs.getDistance() < rhs.getDistance() ? -1 : 0;
					}
				});
				Collections.sort(list, new Comparator<Square>(){

					@Override
					public int compare(Square lhs, Square rhs) {
						return lhs.isAdmin() == rhs.isAdmin() ? 0 : 
							!lhs.isAdmin() ? 1 : -1;
					}
				});

				// Add loaded square
				squareListAdapter.clear();
				squareListAdapter.addAll(list);
			}
		});
	}


	private void updateNearSquares(int progressBarVisible, final LocationListener locationListener){
		if(locationHelper.isLocationEnabled()){
			progressBar.setVisibility(progressBarVisible);
			progressBar.bringToFront();
			locationHelper.requestLocationUpdates(locationListener);
		} else{
			locationHelper.getLocationAccess(activity, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					// Do nohting
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
					progressBar.setVisibility(View.GONE);
					pullToRefreshListView.onRefreshComplete();
					locationAccessButton.setVisibility(View.VISIBLE);
					pullToRefreshListView.setVisibility(View.GONE);
				}
			});
		}
	}


	@Override
	public void onConnectionFailed(ConnectionResult result) {
		ExceptionManager.fireException(new AhException(AhException.TYPE.LOCATION_CONNECTION_FAILED));
	}


	@Override
	public void onConnected(Bundle connectionHint) {
		if(locationHelper.isLocationEnabled()){
			progressBar.setVisibility(View.VISIBLE);
			progressBar.bringToFront();

			Location loc = locationHelper.getLastLocation();
			if(loc == null){
				locationHelper.requestLocationUpdates(locationListener);
				return;
			}

			getNearSquares(loc);
			locationHelper.getAddress(loc, new AhEntityCallback<String>() {

				@Override
				public void onCompleted(String entity) {
					addressText.setText(entity);
				}
			});
		} else{
			locationHelper.getLocationAccess(activity, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					// Do nothing
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
					locationHelper.disconnect();
				}
			});
		}
	}


	@Override
	public void onDisconnected() {
	}
}
