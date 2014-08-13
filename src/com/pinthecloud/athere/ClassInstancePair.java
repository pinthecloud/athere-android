package com.pinthecloud.athere;

import com.pinthecloud.athere.fragment.AhFragment;

public class ClassInstancePair {

	private Class<?> clazz;
	private AhFragment frag;
	
	public ClassInstancePair(Class<?> clazz, AhFragment frag) {
		this.clazz = clazz;
		this.frag = frag;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
	public AhFragment getFrag() {
		return frag;
	}
}
