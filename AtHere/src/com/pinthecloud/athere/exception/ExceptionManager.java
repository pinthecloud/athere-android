package com.pinthecloud.athere.exception;

import java.util.HashMap;
import java.util.Map;

import com.pinthecloud.athere.fragment.AhFragment;

public class ExceptionManager {

	private static Map<String, Handler> map = new HashMap<String, ExceptionManager.Handler>();

	public static void setHandler(AhFragment frag) {
		map.put(frag.getClass().getName(), frag);
	}

	public static void fireException(AhException ex) {
		Class<?> clazz = null;
		if (ex.fromWho() == null) {
			clazz = AhFragment.class;
		} else {
			clazz = ex.fromWho().getClass();
		}
		Handler handler = map.get(clazz.getName());
		if (handler != null){
			handler.handleException(ex);
		}
	}

	public static interface Handler {
		public void handleException(AhException ex);
	}
}
