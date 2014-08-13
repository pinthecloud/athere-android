package com.pinthecloud.athere.exception;

import java.util.HashMap;
import java.util.Map;

public class ExceptionManager {
	
	private static final String MAP_KEY = "MAP_KEY";
	private static Map<String, Handler> map = new HashMap<String, ExceptionManager.Handler>();
	
	public static void setHandler(Handler handler) {
		map.put(MAP_KEY, handler);
	}
	
	public static void fireException(AhException ex) {
		Handler handler = map.get(MAP_KEY);
		if (handler != null)
			handler.handleException(ex);
	}
	
	public static void fireException(Exception ex) {
		Handler handler = map.get(MAP_KEY);
		if (handler != null)
			handler.handleException(ex);
	}
	
	public static interface Handler {
		public void handleException(AhException ex);
		public void handleException(Exception ex);
	}
}
