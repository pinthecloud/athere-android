package com.pinthecloud.athere;

import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.exception.ExceptionManager;

public class AhThread extends Thread {

	public AhThread(Runnable runnable) {
		super(runnable);
	}

	@Override
	public void run() {
		try{
			super.run();
		} catch (AhException ex) {
			ExceptionManager.fireException(ex);
		} catch (Exception ex) {
			// Do nothing
		}
	}
}
