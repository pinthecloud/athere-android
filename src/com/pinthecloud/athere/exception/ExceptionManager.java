package com.pinthecloud.athere.exception;

import java.util.HashMap;
import java.util.Map;

import com.pinthecloud.athere.fragment.AhFragment;

public class ExceptionManager {
	
//	private static final String MAP_KEY = "MAP_KEY";
	private static Map<String, Handler> map = new HashMap<String, ExceptionManager.Handler>();
	
	public static void setHandler(AhFragment frag) {
		map.put(frag.getClass().getName(), frag);
	}
	
	public static void fireException(AhException ex) {
		Handler handler = map.get(ex.fromWho().getClass().getName());
		if (handler != null)
			handler.handleException(ex);
	}
	
	public static interface Handler {
		public void handleException(AhException ex);
	}
}
