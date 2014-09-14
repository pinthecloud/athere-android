package com.pinthecloud.athere.fragment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareProfileActivity;
import com.pinthecloud.athere.adapter.SquareListAdapter;
import com.pinthecloud.athere.dialog.SquareCodeDialog;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhListCallback;
import com.pinthecloud.athere.model.Square;

public class SquareListFragment extends AhFragment implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{

	private ActionBar mActionBar;
	private ProgressBar mProgressBar;

	private ListView squareListView;
	private SquareListAdapter squareListAdapter;

	private LocationClient mLocationClient;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_square_list, container, false);

		/*
		 * Set UI component
		 */
		mActionBar = activity.getActionBar();
		squareListView = (ListView)view.findViewById(R.id.square_list_frag_list);
		mProgressBar = (ProgressBar)view.findViewById(R.id.square_list_frag_progress_bar);

		/*
		 * Set Action Bar
		 */
		mActionBar.setTitle(pref.getString(AhGlobalVariable.NICK_NAME_KEY));


		/*
		 * Set square list view
		 */
		squareListAdapter = new SquareListAdapter(context, thisFragment);
		squareListView.setAdapter(squareListAdapter);
		squareListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Square square = squareListAdapter.getItem(position);
				if(square.getCode().equals("")){
					Intent intent = new Intent(context, SquareProfileActivity.class);
					intent.putExtra(AhGlobalVariable.SQUARE_KEY, square);
					startActivity(intent);
				} else{
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
			}
		});
		mLocationClient = new LocationClient(this.getActivity(), this, this);

		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	public void onStop() {
		mLocationClient.disconnect();
		super.onStop();
	}


	/*
	 * Get square near from user
	 * Now it just gets all squares cause of location law. (lati and longi is 0)
	 */
	private void getNearSquares(){
		mProgressBar.setVisibility(View.VISIBLE);

		Location loc = mLocationClient.getLastLocation();
		double latitude = loc.getLatitude();
		double longitude = loc.getLongitude();
		if(pref.getBoolean(AhGlobalVariable.SUDO_KEY)){
			latitude = -1;
			longitude = -1;
		}

		squareHelper.getSquareListAsync(thisFragment, latitude, longitude, new AhListCallback<Square>() {

			@Override
			public void onCompleted(List<Square> list, int count) {
				mProgressBar.setVisibility(View.GONE);

				// Sort square list by distance and premium
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


	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		ExceptionManager.fireException(new AhException(AhException.TYPE.CONNECTION_FAILED));
	}


	@Override
	public void onConnected(Bundle arg0) {
		getNearSquares();
	}


	@Override
	public void onDisconnected() {
	}
}
