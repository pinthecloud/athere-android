package com.pinthecloud.athere.helper;

public class AhCarrier <E> {
	private E item;
	
	public AhCarrier(){
		
	}
	
	public AhCarrier(E item){
		this.item = item;
	}
	
	public void load(E item){
		this.item = item;
	}
	
	public E getItem() {
		return this.item;
	}
}
