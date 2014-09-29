package com.pinthecloud.athere.helper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;

import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.dialog.AhAlertDialog;
import com.pinthecloud.athere.interfaces.AhDialogCallback;
import com.pinthecloud.athere.interfaces.AhEntityCallback;

public class LocationHelper {

	private final int UPDATE_INTERVAL = 5000;
	private final int FASTEST_INTERVAL = 1000;

	private Context context;
	private LocationManager manager;
	private LocationClient mLocationClient;
	private LocationRequest locationRequest;


	public LocationHelper(Context context, 
			ConnectionCallbacks connectionCallbacks, 
			OnConnectionFailedListener onConnectionFailedListener) {
		super();
		this.context = context;
		manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		mLocationClient = new LocationClient(context, 
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


	public boolean isConnected(){
		return mLocationClient.isConnected();
	}


	public void connect(){
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


	public boolean isLocationEnabled(){
		return (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) 
				|| manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
	}


	public void getLocationAccess(final Activity activity, final AhDialogCallback callback){
		/*
		 * Set enable location service
		 */
		String message = activity.getResources().getString(R.string.location_setting_message);
		AhAlertDialog locSettingDialog = new AhAlertDialog(null, message, true, new AhDialogCallback() {

			@Override
			public void doPositiveThing(Bundle bundle) {
				Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				activity.startActivity(intent);
			}
			@Override
			public void doNegativeThing(Bundle bundle) {
				callback.doNegativeThing(bundle);
			}
		});
		locSettingDialog.show(activity.getFragmentManager(), AhGlobalVariable.DIALOG_KEY);
	}


	/**
	 * The "Get Address" button in the UI is defined with
	 * android:onClick="getAddress". The method is invoked whenever the
	 * user clicks the button.
	 *
	 * @param v The view object associated with this method,
	 * in this case a Button.
	 */
	public void getAddress(Location loc, AhEntityCallback<String> callback) {
		/*
		 * Reverse geocoding is long-running and synchronous.
		 * Run it on a background thread.
		 * Pass the current location to the background task.
		 * When the task finishes,
		 * onPostExecute() displays the address.
		 */
		(new GetAddressTask(callback)).execute(loc);
	}


	/**
	 * A subclass of AsyncTask that calls getFromLocation() in the
	 * background. The class definition has these generic types:
	 * Location - A Location object containing
	 * the current location.
	 * Void     - indicates that progress units are not used
	 * String   - An address passed to onPostExecute()
	 */
	private class GetAddressTask extends AsyncTask<Location, Void, String> {

		private AhEntityCallback<String> callback;

		public GetAddressTask(AhEntityCallback<String> callback) {
			super();
			this.callback = callback;
		}

		/**
		 * Get a Geocoder instance, get the latitude and longitude
		 * look up the address, and return it
		 *
		 * @params params One or more Location objects
		 * @return A string containing the address of the current
		 * location, or an empty string if no address can be found,
		 * or an error message
		 */
		@Override
		protected String doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());

			// Get the current location from the input parameter list
			Location loc = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;
			try {
				// Return 1 address.
				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			} catch (IOException e) {
				// Do nothing
			} catch (IllegalArgumentException e) {
				// Do nothing
			}

			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				Address address = addresses.get(0);

				// Format the first line of address (if available), city, and country name.
				String addressText = address.getLocality() + " " + address.getThoroughfare();

				// Return the text
				return addressText;
			} else {
				return null;
			}
		}


		/**
		 * A method that's called once doInBackground() completes. Turn
		 * off the indeterminate activity indicator and set
		 * the text of the UI element that shows the address. If the
		 * lookup failed, display the error message.
		 */
		@Override
		protected void onPostExecute(String address) {
			callback.onCompleted(address);
		}
	}
}
