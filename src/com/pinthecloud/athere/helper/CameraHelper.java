package com.pinthecloud.athere.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.Surface;

import com.pinthecloud.athere.AhGlobalVariable;

public class CameraHelper {

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
			Log.d(AhGlobalVariable.LOG_TAG, "Error Camera is not available(in use or does not exist): " + e.getMessage());
		}
		return c; // returns null if camera is unavailable
	}


	public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
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
		camera.setDisplayOrientation(result);
	}


	public static int findFrontFacingCameraID(int facing) {
		// Search for the front facing camera
		int cameraId = -1;
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == facing) {
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}
}
