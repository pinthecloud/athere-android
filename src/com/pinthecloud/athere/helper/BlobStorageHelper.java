package com.pinthecloud.athere.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.interfaces.AhEntityCallback;

public class BlobStorageHelper {

	
	private static final String storageConnectionString = 
			"DefaultEndpointsProtocol=http;AccountName=athere;AccountKey=ldhgydlWndSIl7XfiaAQ+sibsNtVZ1Psebba1RpBKxMbyFVYUCMvvuQir0Ty7f0+8TnNLfFKc9yFlYpP6ZSuQQ==";
	private CloudBlobClient blobClient;
	
	public BlobStorageHelper() {
		CloudStorageAccount account = null;
		try {
			account = CloudStorageAccount.parse(storageConnectionString);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AhException(AhException.TYPE.BLOB_STORAGE_ERROR);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AhException(AhException.TYPE.BLOB_STORAGE_ERROR);
		}

		// Create a blob service client
		blobClient = account.createCloudBlobClient();
	}
	
	public String uploadBitmapSync(String id, Bitmap bitmap) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		try {
			container = blobClient.getContainerReference("chupaprofile");
			 blob = container.getBlockBlobReference(id);
			 ByteArrayOutputStream baos = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			 blob.upload(new ByteArrayInputStream(baos.toByteArray()), baos.size());
			 baos.close();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return id;
	}
	
	public Bitmap downloadBitmapSync(String id) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		
		try {
			container = blobClient.getContainerReference("chupaprofile");
			blob = container.getBlockBlobReference(id);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			blob.download(baos);
			return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String downloadToFileSync(String id, Context context, String path) {
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;
		
		try {
			container = blobClient.getContainerReference("chupaprofile");
			blob = container.getBlockBlobReference(id);
			blob.downloadToFile(context.getFilesDir() + "/" + path);
			return context.getFilesDir() + "/" + path;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void uploadBitmapAsync(String id, final Bitmap bitmap, final AhEntityCallback<String> callback) {
		
		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				String id = params[0];
				return uploadBitmapSync(id, bitmap);
			}
			
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				
				callback.onCompleted(result);
			}
		}).execute(id);
	}
	
	public void downloadBitmapAsync(String id, final AhEntityCallback<Bitmap> callback) {
		(new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				// TODO Auto-generated method stub
				String id = params[0];
				return downloadBitmapSync(id);
			}
			
			@Override
			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				
				callback.onCompleted(result);
			}
		}).execute(id);
	}
	
	public void downloadToFileAsync(String id, final Context context, final String path, final AhEntityCallback<String> callback) {
		(new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				String id = params[0];
				return downloadToFileSync(id, context, path);
			}
			
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				
				callback.onCompleted(result);
			}
		}).execute(id);
	}
}
