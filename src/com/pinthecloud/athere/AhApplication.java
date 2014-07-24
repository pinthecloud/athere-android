package com.pinthecloud.athere;

import java.net.MalformedURLException;

import android.app.Application;
import android.util.Log;

import com.pinthecloud.athere.helper.ServiceClient;

public class AhApplication extends Application {
	
	private ServiceClient serviceClient;
		
	
	@Override
	public void onCreate(){
		super.onCreate();
		try {
			this.serviceClient = new ServiceClient(this);
		} catch (MalformedURLException e) {
			Log.d(AhGlobalVariable.LOG_TAG, "AhApplication onCreate : " + e.getMessage());
		}
	}
	
	
	public ServiceClient getServiceClient() {
		return serviceClient;
	}
}
