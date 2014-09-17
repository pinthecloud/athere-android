package com.pinthecloud.athere.exception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pinthecloud.athere.fragment.AhFragment;


/**
 * 
 * NOT USING METHOD
 * BUT NEED FOR REFERENCE!!
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionHandler {
	public Class<?> target() default AhFragment.class;
}
