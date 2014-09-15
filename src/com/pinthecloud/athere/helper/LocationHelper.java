package com.pinthecloud.athere.helper;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.AhActivity;
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.interfaces.AhDialogCallback;

public class LocationHelper {

	private final int UPDATE_INTERVAL = 5000;
	private final int FASTEST_INTERVAL = 1000;

	private PreferenceHelper pref;

	private LocationManager manager;
	private LocationClient mLocationClient;
	private LocationRequest locationRequest;


	public LocationHelper(AhActivity activity, 
			ConnectionCallbacks connectionCallbacks, 
			OnConnectionFailedListener onConnectionFailedListener) {
		super();
		pref = AhApplication.getInstance().getPref();
		manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		mLocationClient = new LocationClient(activity, 
				connectionCallbacks, 
				onConnectionFailedListener);
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(UPDATE_INTERVAL);
		locationRequest.setFastestInterval(FASTEST_INTERVAL);
	}


	public void setLocationRequest(LocationRequest locationRequest) {
		this.locationRequest = locationRequest;
	}


	private boolean isLocationEnabled(AhActivity activity){
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) 
				&& !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			return false;
		}
		else{
			return true;
		}
	}


	public void connect(final AhActivity activity){
		/*
		 * Get user consent for location information
		 */
		if(!pref.getBoolean(AhGlobalVariable.LOCATION_CONSENT_KEY)){
			String message = activity.getResources().getString(R.string.location_consent_message);
			AhAlertDialog locConsentDialog = new AhAlertDialog(null, message, true, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					pref.putBoolean(AhGlobalVariable.LOCATION_CONSENT_KEY, true);
					connect(activity);
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
					// do nothing
				}
			});
			locConsentDialog.show(activity.getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			return;
		}


		/*
		 * Set enable location service
		 */
		if(!isLocationEnabled(activity)){
			String message = activity.getResources().getString(R.string.location_setting_message);
			AhAlertDialog locSettingDialog = new AhAlertDialog(null, message, true, new AhDialogCallback() {

				@Override
				public void doPositiveThing(Bundle bundle) {
					Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					activity.startActivity(intent);
				}
				@Override
				public void doNegativeThing(Bundle bundle) {
					// do nothing
				}
			});
			locSettingDialog.show(activity.getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
			return;
		}

		mLocationClient.connect();
	}


	public void disconnect(){
		mLocationClient.disconnect();
	}


	public Location getLastLocation(){
		return mLocationClient.getLastLocation();
	}


	public void requestLocationUpdates(LocationListener listener){
		mLocationClient.requestLocationUpdates(locationRequest, listener);
	}


	public void removeLocationUpdates(LocationListener listener){
		mLocationClient.removeLocationUpdates(listener);
	}
}
