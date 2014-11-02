package com.pinthecloud.athere.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.pinthecloud.athere.AhApplication;

public class PreferenceHelper {

	public static final String DEFAULT_STRING = "DEFAULT_STRING";
	public static final int DEFAULT_INT = 0;
	public static final boolean DEFAULT_BOOLEAN = false;
	public static final float DEFAULT_FLOAT = 0;
	
	private SharedPreferences pref;
	
	private static PreferenceHelper prefHelper = null;
	public static PreferenceHelper getInstance() {
		if (prefHelper == null) {
			prefHelper = new PreferenceHelper(AhApplication.getInstance());
		}
		return prefHelper;
	}

	private PreferenceHelper(Context context){
		this.pref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public boolean putString(String key, String value){
		Editor editor = pref.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	public boolean putInt(String key, int value){
		Editor editor = pref.edit();
		editor.putInt(key, value);
		return editor.commit();
	}

	public boolean putBoolean(String key, boolean value){
		Editor editor = pref.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}
	
	public boolean putFloat(String key, float value) {
		Editor editor = pref.edit();
		editor.putFloat(key, value);
		return editor.commit();
	}

	public String getString(String key){
		return pref.getString(key, DEFAULT_STRING);
	}

	public int getInt(String key){
		return pref.getInt(key, DEFAULT_INT);
	}

	public boolean getBoolean(String key){
		return pref.getBoolean(key, DEFAULT_BOOLEAN);
	}
	
	public float getFloat(String key) {
		return pref.getFloat(key, DEFAULT_FLOAT);
	}

	public boolean removePref(String key){
		Editor editor = pref.edit();
		editor.remove(key);
		return editor.commit();
	}
}
