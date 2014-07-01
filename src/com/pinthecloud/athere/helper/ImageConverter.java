package com.pinthecloud.athere.helper;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ImageConverter {

	public static Bitmap convertToImage(String str){
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
