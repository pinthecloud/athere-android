package com.pinthecloud.athere.fragment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.BasicProfileActivity;
import com.pinthecloud.athere.activity.SquareProfileActivity;
import com.pinthecloud.athere.adapter.SquareListAdapter;
import com.pinthecloud.athere.dialog.SquareCodeDialog;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.helper.LocationHelper;
import com.pinthecloud.athere.helper.PreferenceHelper;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.model.Square;

public class SquareListFragment extends AhFragment implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{

	private ProgressBar progressBar;
	private Button locationAccessButton;
	private Button refreshButton;
	private LinearLayout listLayout;
	private PullToRefreshListView pullToRefreshListView;
	private ListView squareListView;
	private SquareListAdapter squareListAdapter;

	private LocationHelper locationHelper;
	private LocationListener locationListener;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_square_list, container, false);

		/*
		 * Set UI component
		 */
		listLayout = (LinearLayout)view.findViewById(R.id.square_list_frag_list_layout);
		locationAccessButton = (Button)view.findViewById(R.id.square_list_frag_location_access_button);
		refreshButton = (Button)view.findViewById(R.id.square_list_frag_refresh_button);
		progressBar = (ProgressBar)view.findViewById(R.id.square_list_frag_progress_bar);
		pullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.square_list_frag_list);
		squareListView = pullToRefreshListView.getRefreshableView();
		registerForContextMenu(squareListView);


		/*
		 * For easy developing, make back button to super user
		 * DO NOT REMOVE THIS SCRIPT!
		 */
		if (PreferenceHelper.getInstance().getBoolean(AhGlobalVariable.SUDO_KEY)) {
			Button b = new Button(activity);
			b.setText("Go to BasicProfile");
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Session session = Session.getActiveSession();
					if (session != null) {
						session.close();
					}

					Intent intent = new Intent(context, BasicProfileActivity.class);
					startActivity(intent);
					activity.finish();
				}
			});
			activity.addContentView(b, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}


		/*
		 * Set button
		 */
		locationAccessButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				locationHelper.connect();
			}
		});
		refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateNearSquares(View.VISIBLE, locationListener);
			}
		});


		/*
		 * Set square list view
		 */
		squareListAdapter = new SquareListAdapter(context, thisFragment);
		squareListView.setAdapter(squareListAdapter);
		squareListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(!locationHelper.isLocationAccess()){
					locationHelper.getLocationAccess(activity, new AhDialogCallback() {

						@Override
						public void doPositiveThing(Bundle bundle) {
							progressBar.setVisibility(View.VISIBLE);
							progressBar.bringToFront();
							locationHelper.requestLocationUpdates(locationListener);
						}
						@Override
						public void doNegativeThing(Bundle bundle) {
							locationAccessButton.setVisibility(View.VISIBLE);
							listLayout.setVisibility(View.GONE);
						}
					});
					return;
				}


				/*
				 * If enough close to enter or it is super user, check code.
				 * Otherwise, cannot enter.
				 */
				final Square square = squareListAdapter.getItem(--position);
				if(square.getDistance() <= square.getEntryRange() 
						|| PreferenceHelper.getInstance().getBoolean(AhGlobalVariable.SUDO_KEY)){

					if(square.getCode().equals("")){

						// This square doesn't have code
						Intent intent = new Intent(context, SquareProfileActivity.class);
						intent.putExtra(AhGlobalVariable.SQUARE_KEY, square);
						startActivity(intent);
					} else{

						// This square has code
						SquareCodeDialog codeDialog = new SquareCodeDialog(square, new AhDialogCallback() {

							@Override
							public void doPositiveThing(Bundle bundle) {
								String code = bundle.getString(AhGlobalVariable.CODE_VALUE_KEY);
								if(code.equals(square.getCode())){
									Intent intent = new Intent(context, SquareProfileActivity.class);
									intent.putExtra(AhGlobalVariable.SQUARE_KEY, square);
									startActivity(intent);
								}else{
									String message = getResources().getString(R.string.bad_square_code_message);
									Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
									toast.show();
								}
							}
							@Override
							public void doNegativeThing(Bundle bundle) {
								// do nothing		
							}
						});
						codeDialog.show(getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
					}
				} else{
					String message = getResources().getString(R.string.far_message);
					Toast.makeText(activity, message, Toast.LENGTH_LONG)
					.show();;
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
						refreshButton.setText(entity);
					}
				});
			}
		};

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		if(locationHelper.isLocationAccess()){
			locationAccessButton.setVisibility(View.GONE);
			listLayout.setVisibility(View.VISIBLE);
		}else{
			locationAccessButton.setVisibility(View.VISIBLE);
			listLayout.setVisibility(View.GONE);
		}
		locationHelper.connect();
	}


	@Override
	public void onStop() {
		locationHelper.disconnect();
		super.onStop();
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
		if(locationHelper.isLocationAccess()){
			progressBar.setVisibility(progressBarVisible);
			progressBar.bringToFront();
			locationHelper.requestLocationUpdates(locationListener);
		} else{
			locationHelper.getLocationAccess(activity, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					locationHelper.requestLocationUpdates(locationListener);
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
					progressBar.setVisibility(View.GONE);
					pullToRefreshListView.onRefreshComplete();
					locationAccessButton.setVisibility(View.VISIBLE);
					listLayout.setVisibility(View.GONE);
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
		if(locationHelper.isLocationAccess()){
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
					refreshButton.setText(entity);
				}
			});
		} else{
			locationHelper.getLocationAccess(activity, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					locationAccessButton.setVisibility(View.GONE);
					listLayout.setVisibility(View.VISIBLE);
					locationHelper.connect();
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
