package com.pinthecloud.athere.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.pinthecloud.athere.AhGlobalVariable;

public class BitmapUtil {

	public static Bitmap resize(Context context, Uri imageUri, int reqWidth, int reqHeight) throws FileNotFoundException {
		Bitmap bitmapImage = null;

		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(imageUri), null, options);

			options.inSampleSize = calculateSize(options, reqWidth, reqHeight);
			options.inJustDecodeBounds = false;
			bitmapImage = BitmapFactory.decodeStream(context.getContentResolver()
					.openInputStream(imageUri), null, options);
		} catch (FileNotFoundException e) {
			throw e;
		}
		return bitmapImage;
	}


	public static int calculateSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int size = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				size = Math.round((float) height / (float) reqHeight);
			} else {
				size = Math.round((float) width / (float) reqWidth);
			}
		}
		return size;
	}


	public static int getImageOrientation(File imageFile) throws IOException{
		Log.d(AhGlobalVariable.LOG_TAG, "BitmapHelper getImageOrientation");

		try {
			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				return 90;
			case ExifInterface.ORIENTATION_ROTATE_180:
				return 180;
			case ExifInterface.ORIENTATION_ROTATE_270:
				return 270;
			default:
				return 0;
			}
		} catch (IOException e) {
			throw e;
		}
	}


	public static Bitmap rotate(Bitmap bitmap, int degree) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix mtx = new Matrix();
		mtx.postRotate(degree);

		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}


	public static Bitmap crop(Bitmap bitmap, int xOffset, int yOffset, int width, int height) {
		return Bitmap.createBitmap(bitmap, xOffset, yOffset, width, height);
	}


	public static Bitmap cropOval(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xffff0000;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawOval(rectF, paint);

		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float) 4);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}


	public static Bitmap cropRound(Bitmap bitmap) {
		int targetWidth = 125;
		int targetHeight = 125;
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
				targetHeight, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		path.addCircle(((float) targetWidth - 1) / 2,
				((float) targetHeight - 1) / 2,
				(Math.min(((float) targetWidth), 
						((float) targetHeight)) / 2),
						Path.Direction.CCW);

		canvas.clipPath(path);
		Bitmap sourceBitmap = bitmap;
		canvas.drawBitmap(sourceBitmap, 
				new Rect(0, 0, sourceBitmap.getWidth(),
						sourceBitmap.getHeight()), 
						new Rect(0, 0, targetWidth,
								targetHeight), null);
		return targetBitmap;
	}


	public static Bitmap flip(Bitmap src) {
		// create new matrix for transformation
		Matrix matrix = new Matrix();

		// horizonal
		matrix.preScale(-1.0f, 1.0f);

		// return transformed image
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
	}
	
	public static Bitmap convertToBitmap(String str){
		try{
			byte [] encodeByte = Base64.decode(str, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
			return bitmap;
		 }catch(Exception e){
			 e.printStackTrace();
			 return null;
		 }
		
//		byte[] bytes;
//		try {
//			bytes = str.getBytes("UTF-8");
//			int offset = 0;
//			int length = bytes.length;
//			
//			Bitmap img = BitmapFactory.decodeByteArray(bytes, offset, length);
//			
//			return img;
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
	}
	
	public static String convertToString(Bitmap img){
		ByteArrayOutputStream baos = new  ByteArrayOutputStream();
		img.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
        
//		int bytes = img.getByteCount();
//
//		ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
//		img.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
//
//		byte[] array = buffer.array();
//		String returnStr = null;
//		try {
//			returnStr = new String(array, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			returnStr = null;
//		}
//		return returnStr;
	}
}