package com.pinthecloud.athere;

import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;

public class AhThread extends Thread {
	
	
	
	public AhThread(Runnable runnable) {
		// TODO Auto-generated constructor stub
		super(runnable);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			super.run();
		} catch (AhException ex) {
			ExceptionManager.fireException(ex);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
}
