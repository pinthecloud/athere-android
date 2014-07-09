package com.pinthecloud.athere.interfaces;

import java.io.IOException;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pinthecloud.athere.AhGlobalVariable;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder mHolder;
	private Camera mCamera;

	
	public CameraPreview(Context context, Camera camera) {
		super(context);
		Log.d(AhGlobalVariable.LOG_TAG, "CameraPreview constructer");

		this.mCamera = camera;
		this.mHolder = getHolder();
		
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder.addCallback(this);
	}


	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(AhGlobalVariable.LOG_TAG, "CameraPreview surfaceCreated");

		// The Surface has been created, now tell the camera where to draw the preview.
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.d(AhGlobalVariable.LOG_TAG, "Error of CameraPreview surfaceCreated : " + e.getMessage());
		}
	}


	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(AhGlobalVariable.LOG_TAG, "CameraPreview surfaceDestroyed");
		mHolder.removeCallback(this);
	}


	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Log.d(AhGlobalVariable.LOG_TAG, "CameraPreview surfaceChanged");

		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null){
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		mCamera.stopPreview();

		// set preview size and make any resize, rotate or
		// reformatting changes here
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewSize(w, h);
		parameters.setPictureFormat(PixelFormat.JPEG);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		mCamera.setParameters(parameters);
		mCamera.setDisplayOrientation(AhGlobalVariable.RIGHT_ANGLE);

		try {
			// start preview with new settings
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.d(AhGlobalVariable.LOG_TAG, "Error of CameraPreview surfaceChanged : " + e.getMessage());
		}
	}
}
