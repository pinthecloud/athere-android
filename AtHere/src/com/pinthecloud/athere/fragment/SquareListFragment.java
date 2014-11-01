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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.pinthecloud.athere.activity.SquarePreviewActivity;
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
	private RelativeLayout addressLayout;
	private TextView addressText;

	private PullToRefreshListView pullToRefreshListView;
	private ListView squareListView;
	private SquareListAdapter squareListAdapter;


	private LocationHelper locationHelper;
	private LocationListener locationListener;


	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_square_list, container, false);

		findComponent(view);
		setButtonEvent();
		setSquareList();
		setLocationListener();

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

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		if(locationHelper.isLocationEnabled()){
			addressText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.groundlist_location_ico, 0, 0, 0);
			addressText.setText("");
		}else{
			addressText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			addressText.setText(getResources().getString(R.string.location_turn_on_message));
		}
		locationHelper.connect();
	}


	@Override
	public void onStop() {
		locationHelper.disconnect();
		super.onStop();
	}


	private void findComponent(View view){
		progressBar = (ProgressBar)view.findViewById(R.id.square_list_frag_progress_bar);
		addressLayout = (RelativeLayout)view.findViewById(R.id.square_list_frag_address_layout);
		addressText = (TextView)view.findViewById(R.id.square_list_frag_address);
		pullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.square_list_frag_list);
		squareListView = pullToRefreshListView.getRefreshableView();
		registerForContextMenu(squareListView);
	}


	private void setButtonEvent(){
		addressLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateSquareList(View.VISIBLE, locationListener);
			}
		});
	}


	private void setSquareList(){
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
					locationHelper.getLocationService(activity, new AhDialogCallback() {

						@Override
						public void doPositiveThing(Bundle bundle) {
							// Do nothing
						}
						@Override
						public void doNegativeThing(Bundle bundle) {
							addressText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
							addressText.setText(getResources().getString(R.string.location_turn_on_message));
						}
					});
					return;
				}


				/*
				 * Get square and make enter dialog
				 */
				final Square square = squareListAdapter.getItem(--position);
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
				if(!square.isFar()
						|| PreferenceHelper.getInstance().getBoolean(AhGlobalVariable.SUDO_KEY)){

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
				updateSquareList(View.GONE, locationListener);
			}
		});
	}


	private void setLocationListener(){
		locationHelper = new LocationHelper(activity, this, this);
		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location loc) {
				locationHelper.removeLocationUpdates(this);
				getSquareList(loc);
				locationHelper.getAddress(loc, new AhEntityCallback<String>() {

					@Override
					public void onCompleted(String entity) {
						addressText.setText(entity);
					}
				});
			}
		};
	}


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
						progressBar.setVisibility(View.GONE);
						squareListView.setEnabled(true);
						userDBHelper.addAllUsers(list);
					}
				});
			}
		}, new Chainable() {

			@Override
			public void doNext(AhFragment frag) {
				if(!isPreview){
					String enterMessage = getResources().getString(R.string.enter_square_message);
					String greetingMessage = getResources().getString(R.string.greeting_sentence);
					AhUser user = userHelper.getMyUserInfo();
					AhMessage message = new AhMessage.Builder()
					.setContent(" " + enterMessage + "\n" + greetingMessage)
					.setSender(user.getNickName())
					.setSenderId(user.getId())
					.setReceiverId(square.getId())
					.setType(AhMessage.TYPE.ENTER_SQUARE).build();
					messageHelper.sendMessageAsync(frag, message, null);
				} else{
					AsyncChainer.notifyNext(frag);
				}
			}
		}, new Chainable(){

			@Override
			public void doNext(AhFragment frag) {
				Intent intent = new Intent();
				if(!isPreview){
					Time time = new Time();
					time.setToNow();

					squareHelper.setMySquareId(square.getId())
					.setMySquareName(square.getName())
					.setMySquareResetTime(square.getResetTime())
					.setSquareExitTab(SquareTabFragment.CHAT_TAB)
					.setLoggedInSquare(true)
					.setTimeStampAtLoggedInSquare(time.format("%Y:%m:%d:%H"))
					.setReview(true)
					.setPreview(isPreview);

					intent.setClass(context, SquareActivity.class);
					startActivity(intent);
					activity.finish();
				}else{
					squareHelper.setPreview(isPreview);
					intent.setClass(context, SquarePreviewActivity.class);
					intent.putExtra(AhGlobalVariable.SQUARE_KEY, square);
					startActivity(intent);
				}
			}
		});
	}


	/*
	 * Get square near from user
	 * Now it just gets all squares cause of location law. (lati and longi is 0)
	 */
	private void getSquareList(Location loc){
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
				Collections.sort(list, new Comparator<Square>(){

					@Override
					public int compare(Square lhs, Square rhs) {
						return lhs.isFar() == rhs.isFar() ? 0 : 
							lhs.isFar() ? 1 : -1;
					}
				});

				// Add loaded square
				squareListAdapter.clear();
				squareListAdapter.addAll(list);
			}
		});
	}


	private void updateSquareList(int progressBarVisible, final LocationListener locationListener){
		if(locationHelper.isLocationEnabled()){
			progressBar.setVisibility(progressBarVisible);
			progressBar.bringToFront();
			locationHelper.requestLocationUpdates(locationListener);
		} else{
			locationHelper.getLocationService(activity, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					// Do nohting
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
					progressBar.setVisibility(View.GONE);
					pullToRefreshListView.onRefreshComplete();

					addressText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
					addressText.setText(getResources().getString(R.string.location_turn_on_message));
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

			getSquareList(loc);
			locationHelper.getAddress(loc, new AhEntityCallback<String>() {

				@Override
				public void onCompleted(String entity) {
					addressText.setText(entity);
				}
			});
		} else{
			locationHelper.getLocationService(activity, new AhDialogCallback() {

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
