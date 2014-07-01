package com.pinthecloud.athere;

import android.app.Application;

import com.pinthecloud.athere.manager.ServiceClient;

public class AhApplication extends Application {
	
	private ServiceClient serviceClient;
		
	@Override
	public void onCreate(){
		super.onCreate();
		
		this.serviceClient = new ServiceClient(this);
	}
	
	public ServiceClient geterviceClient() {
		return serviceClient;
	}
}
