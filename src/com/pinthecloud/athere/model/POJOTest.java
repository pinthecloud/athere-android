package com.pinthecloud.athere.model;

import java.util.Random;

public class POJOTest {
	private String name;
	private int age;
	private String describe;
	
	public POJOTest(){
		Random r = new Random();
		this.name = ""+r.nextFloat();
		this.age = r.nextInt();
		this.describe = "" + r.nextDouble();
	}
	
	public POJOTest(String name, int age, String describe) {
		super();
		this.name = name;
		this.age = age;
		this.describe = describe;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
}
