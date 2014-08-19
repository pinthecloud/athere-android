//package com.pinthecloud.athere.util;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.util.Base64;
//
//public class ImageFileUtil {
//	
//	public static String saveFile(Context context, String name, Bitmap bitmap) {
//		//File file = new File(filePath);
//		
//		File file = new File(context.getFilesDir(), name);
//		if (file.exists()) return null;
//		
//		FileOutputStream fos = null;
//		try {
//			boolean isSuccess = file.createNewFile();
//			if (!isSuccess) return null;
//			fos = new FileOutputStream(file);
//			ByteArrayOutputStream baos = new  ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
////			byte [] b = baos.toByteArray();
//			baos.writeTo(fos);
////			fos.write(b);
//			fos.flush();
//			fos.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return file.getAbsolutePath();
//	}
//	
//	public static String saveFile(Context context, String name, String bitmapStream) {
//		//File file = new File(filePath);
//		Bitmap bm = BitmapUtil.convertToBitmap(bitmapStream, 0, 0);
//		return null;
//	}
//	
//	public static Bitmap readFile(Context context, String name) {
//		Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir() + "/" + name);
//		return bitmap;
//	}
//}
