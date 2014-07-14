package com.pinthecloud.athere.helper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
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


	public static Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
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
			if (size.width <= height && size.height <= width) {
				if (result == null) {
					result=size;
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


	public static void setAutoFocusArea(Camera camera, int posX, int posY,
			int focusRange, boolean flag, Point point) {
		if (posX < 0 || posY < 0) {
			setArea(camera, null);
			return;
		}

		int touchPointX;
		int touchPointY;
		int endFocusY;
		int startFocusY;

		if (!flag) {
			// Camera.setDisplayOrientation()을 이용해서 영상을 세로로 보고 있는 경우.
			touchPointX = point.y >> 1;
		touchPointY = point.x >> 1;
			startFocusY = posX;
			endFocusY 	= posY;
		} else {
			// Camera.setDisplayOrientation()을 이용해서 영상을 가로로 보고 있는 경우.
			touchPointX = point.x >> 1;
		touchPointY = point.y >> 1;
		startFocusY = posY;
		endFocusY = point.x - posX;
		}

		float startFocusX 	= 1000F / (float) touchPointY;
		float endFocusX 	= 1000F / (float) touchPointX;

		startFocusX = (int) (startFocusX * (float) (startFocusY - touchPointY)) - focusRange;
		startFocusY = (int) (endFocusX * (float) (endFocusY - touchPointX)) - focusRange;
		endFocusX = startFocusX + focusRange;
		endFocusY = startFocusY + focusRange;

		if (startFocusX < -1000)
			startFocusX = -1000;

		if (startFocusY < -1000)
			startFocusY = -1000;

		if (endFocusX > 1000) {
			endFocusX = 1000;
		}

		if (endFocusY > 1000) {
			endFocusY = 1000;
		}

		Rect rect = new Rect((int) startFocusX, (int) startFocusY, (int) endFocusX, (int) endFocusY);
		ArrayList<Camera.Area> arraylist = new ArrayList<Camera.Area>();
		arraylist.add(new Camera.Area(rect, 1000));

		setArea(camera, arraylist);
	}


	public static void setArea(Camera camera, List<Camera.Area> list) {
		Camera.Parameters parameters = camera.getParameters();
		if (parameters.getMaxNumFocusAreas() > 0) {
			parameters.setFocusAreas(list);
		}
		if (parameters.getMaxNumMeteringAreas() > 0) {
			parameters.setMeteringAreas(list);
		}
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
		camera.setParameters(parameters);
	}
}
