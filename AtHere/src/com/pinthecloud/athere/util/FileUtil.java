package com.pinthecloud.athere.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.exception.AhException;

public class FileUtil {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;


	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}


	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), AhGlobalVariable.APP_NAME);

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()){
			if (!mediaStorageDir.mkdirs()){
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		File mediaFile = null;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".jpg");
		} else if(type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"VID_"+ timeStamp + ".mp4");
		} else {
			return null;
		}
		return mediaFile;
	}


	public static void clearFile(Context context, String name) {
		context.deleteFile(name);
	}


	public static void clearAllFiles(Context context) {
		File dir = context.getFilesDir();
		boolean result = false;
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					result = file.delete();
					if (!result) {
						throw new AhException("File remove Error");
					}
				} else {
					throw new AhException("File remove Error");
				}
			}
		} else {
			throw new AhException("File remove Error");
		}
	}


	// 특정  Uri 경로에 Bitmap 저장.
	public static File saveBitmapToFile(Context context, Uri uri, Bitmap bitmap){
		File file = null;
		try {
			file = new File(uri.getPath());
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			throw new AhException("FileNotFoundException");
		} catch (IOException e) {
			throw new AhException("IOException");
		}
		return file;
	}


	public static void saveBitmapToInternalStorage(Context context, String name, Bitmap image) {
		try {
			// Use the compress method on the Bitmap object to write image to the OutputStream
			// Writing the bitmap to the output stream
			FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
			image.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			throw new AhException("FileNotFoundException");
		} catch (IOException e) {
			throw new AhException("IOException");
		}
	}


	public static Uri getLastCaptureBitmapUri(Context context){
		Uri uri =null;
		String[] IMAGE_PROJECTION = {
				MediaStore.Images.ImageColumns.DATA, 
				MediaStore.Images.ImageColumns._ID,
		};
		Cursor cursorImages = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				IMAGE_PROJECTION, null, null,null);
		if (cursorImages != null && cursorImages.moveToLast()) {
			uri = Uri.parse(cursorImages.getString(0)); //경로
			cursorImages.close(); // 커서 사용이 끝나면 꼭 닫아준다.
		}
		return uri;  
	}


	/*
	 * look in internal storage
	 */
	public static Bitmap getBitmapFromInternalStorage(Context context, String fileName) {
		if (context == null) {
			return null;
		}
		return BitmapFactory.decodeFile(context.getFilesDir() + "/" + fileName);
		//		try {
		//			File filePath = context.getFileStreamPath(fileName);
		//			FileInputStream fi = new FileInputStream(filePath);
		//			return BitmapFactory.decodeStream(fi);
		//		} catch (FileNotFoundException e) {
		//			Log.d(AhGlobalVariable.LOG_TAG, "FileNotFoundException");
		//			return null;
		//		}
	}


	//	public static Bitmap getImageFromInternalStorage(Context context, String fileName, int reqWidth, int reqHeight) {
	//		if (context == null) {
	//			return null;
	//		}
	//
	//		// First decode with inJustDecodeBounds=true to check dimensions
	//		final BitmapFactory.Options options = new BitmapFactory.Options();
	//		options.inJustDecodeBounds = true;
	//		BitmapFactory.decodeFile(context.getFilesDir() + "/" + fileName, options);
	//
	//		// Calculate inSampleSize
	//		options.inSampleSize = BitmapUtil.calculateSize(options, reqWidth, reqHeight);
	//
	//		// Decode bitmap with inSampleSize set
	//		options.inJustDecodeBounds = false;
	//		return BitmapFactory.decodeFile(context.getFilesDir() + "/" + fileName, options);
	//	}
}
