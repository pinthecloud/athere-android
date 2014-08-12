package com.pinthecloud.athere.util;

import java.util.HashMap;
import java.util.Map;

import com.pinthecloud.athere.AhException;

public class ExceptionManager {
	
	private static final String MAP_KEY = "MAP_KEY";
	private static Map<String, ExceptionHandler> map = new HashMap<String, ExceptionManager.ExceptionHandler>();
	
	public static void setHandler(ExceptionHandler handler) {
		map.put(MAP_KEY, handler);
	}
	
	public static void fireException(AhException ex) {
		ExceptionHandler handler = map.get(MAP_KEY);
		if (handler != null)
			handler.handleException(ex);
	}
	
	public static void fireException(Exception ex) {
		ExceptionHandler handler = map.get(MAP_KEY);
		if (handler != null)
			handler.handleException(ex);
	}
	
	public static interface ExceptionHandler {
		public void handleException(AhException ex);
		public void handleException(Exception ex);
	}
}
