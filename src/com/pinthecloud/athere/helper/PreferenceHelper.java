package com.pinthecloud.athere.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceHelper {
	
	private SharedPreferences pref;
	
	private final String DEFAULT_STRING = null;
	private final int DEFAULT_INT = 0;
	private final boolean DEFAULT_BOOLEAN = false;
	
	public PreferenceHelper(Context context){
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
	
	public String getString(String key){
		return pref.getString(key, DEFAULT_STRING);
	}
	
	public int getInt(String key){
		return pref.getInt(key, DEFAULT_INT);
	}
	
	public boolean getBoolean(String key){
		return pref.getBoolean(key, DEFAULT_BOOLEAN);
	}
	
	
	

	
	// @Not Using Method
//	public <T> boolean putPreference(String id, T obj) {
//		
//		//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
//		SharedPreferences pref = context.getSharedPreferences(id, Context.MODE_PRIVATE);
//		Editor editor = pref.edit();
//		Class<? extends Object> _class = obj.getClass();
//		Method[] method = _class.getMethods();
//		
//		try{
//			for(int i = 0 ; i < method.length ; i++){
//				Class<? extends Object> returnType = method[i].getReturnType();
//				String className = returnType.getName();
//				Log.e("ERROR", "returnType className : "+className);
//				if ("String".equals(className)) {
//					Log.e("ERROR", method[i].getName());
//					editor.putString(method[i].getName(), (String)method[i].invoke(obj));
//				} else if ("int".equals(className)) {
//					Log.e("ERROR", method[i].getName());
//					editor.putInt(method[i].getName(), (Integer)method[i].invoke(obj));
//				} else if ("float".equals(className)) {
//					Log.e("ERROR", method[i].getName());
//					editor.putFloat(method[i].getName(), (Float)method[i].invoke(obj));
//				} else if ("boolean".equals(className)) {
//					Log.e("ERROR", method[i].getName());
//					editor.putBoolean(method[i].getName(), (Boolean)method[i].invoke(obj));
//				} else if ("long".equals(className)) {
//					Log.e("ERROR", method[i].getName());
//					editor.putLong(method[i].getName(), (Long)method[i].invoke(obj));
//				} else {
//					Log.e("ERROR", ">>className>> "+className);
//				}
//			}
//		} catch (IllegalArgumentException ex) {
//			
//			return false;
//		} catch (IllegalAccessException ex) {
//			return false;
//		} catch (InvocationTargetException ex) {
//			return false;
//		}
//		
//		return editor.commit();
//	}
//			
//	public <T> T getPreference(String id, Class<? extends Object> _class) {
//		
//		SharedPreferences pref = context.getSharedPreferences(id, Context.MODE_PRIVATE);
//			
//		Method[] method = _class.getMethods();
//		T obj = null;
//		try {
//			obj = (T)_class.newInstance();
//		
//			for(int i = 0 ; i < method.length ; i++){
//				Class<? extends Object> returnType = method[i].getReturnType();
//				String className = returnType.getName();
//				
//				if ("String".equals(className)) {
//					String str = pref.getString(method[i].getName(), "DEFAULT");
//					Log.e("ERROR", "str : " + str);
//				} else if ("int".equals(className)) {
//					int intt = pref.getInt(method[i].getName(), 0);
//					Log.e("ERROR", "int : " + intt);
//				} else if ("float".equals(className)) {
//					pref.getFloat(method[i].getName(), 0);
//				} else if ("boolean".equals(className)) {
//					pref.getBoolean(method[i].getName(), false);
//				} else if ("long".equals(className)) {
//					pref.getLong(method[i].getName(), 0);
//				}
//			}
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//			return null;
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return obj;
//	}
}
