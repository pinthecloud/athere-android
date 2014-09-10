package com.pinthecloud.athere.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.view.Surface;

public class CameraUtil {

	/** Check if this device has a camera */
	public static boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}


	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(int mode){
		Camera c = null;
		try {
			switch(mode){
			case Camera.CameraInfo.CAMERA_FACING_BACK:
				c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK); // attempt to get a Camera instance
				break;
			case Camera.CameraInfo.CAMERA_FACING_FRONT:
				c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT); // attempt to get a Camera instance
				break;
			}
		}
		catch (Exception e){
			// Do nothing
		}
		return c; // returns null if camera is unavailable
	}


	public static int getCameraDisplayOrientation(Activity activity, int cameraId) {
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degree = 0;
		switch (rotation) {
		case Surface.ROTATION_0: degree = 0; break;
		case Surface.ROTATION_90: degree = 90; break;
		case Surface.ROTATION_180: degree = 180; break;
		case Surface.ROTATION_270: degree = 270; break;
		}

		int result = 0;
		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degree) % 360;
			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degree + 360) % 360;
		}
		return result;
	}


	public static int findFrontFacingCameraID() {
		// Search for the front facing camera
		int cameraId = -1;
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}


	public static int findBackFacingCameraID() {
		// Search for the front facing camera
		int cameraId = -1;
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}


	public static Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if ((size.width <= width && size.height <= height) || (size.width <= height && size.height <= width)) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;
					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return result;
	}


	public static Camera.Size getBestPictureSize(int width, int height, Camera.Parameters parameters) {
		Camera.Size result = null;
		for(Camera.Size size : parameters.getSupportedPictureSizes()){
			if ((size.width <= width && size.height <= height) || (size.width <= height && size.height <= width)) {
				if (result == null) {
					result = size;
				} else{
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;
					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return result;
	}


	public static Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
		Camera.Size result=null;
		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			if (result == null) {
				result=size;
			} else {
				int resultArea = result.width * result.height;
				int newArea = size.width * size.height;
				if (newArea < resultArea) {
					result = size;
				}
			}
		}
		return result;
	}


	public static int onOrientationChanged(int orientation, int cameraId){
		//		if (orientation == ORIENTATION_UNKNOWN) return;

		// Check whether it is possible to detect or not
		// Get picture rotation orientation
		//		if(oel.canDetectOrientation()){
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		orientation = (orientation + 45) / 90 * 90;
		int rotation = 0;
		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
			rotation = (info.orientation - orientation + 360) % 360;
		} else {  // back-facing camera
			rotation = (info.orientation + orientation) % 360;
		}
		//		}
		return rotation;
	}
}
