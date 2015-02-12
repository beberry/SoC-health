package com.example.mymeds.util;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mymeds.R;
import com.example.mymeds.activites.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class NotificationsService extends Service {
	private final IBinder mBinder = new LocalBinder();
	private NotificationManager mNM;
	private int NOTIFICATION = R.string.hello_world;
	
	@Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		try {
			// read file from assets
			AssetManager assetManager = this.getAssets();
			InputStream is = assetManager.open("meds.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String bufferString = new String(buffer);	

			JSONObject jsonObject = new JSONObject(bufferString);
			JSONArray medIndex = jsonObject.getJSONArray("medication");

			for(int k=0;k<medIndex.length();k++){
				Medication med = new Medication();

				JSONObject tempCheck = medIndex.getJSONObject(k);
				int itemID = tempCheck.getInt("index");
				String itemName = tempCheck.getString("name");
//				int dosage = tempCheck.getInt("dosage");
				String description = tempCheck.getString("description");
//				int time = tempCheck.getInt("time");
				System.out.println(description);
			}
		} catch (IOException e) {
			Log.e("IOException","Error loading file");
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e("JSONException","JSON exception");
			e.printStackTrace();			
		}
	    return START_STICKY;
	}
	
	 private void showNotification() {
	        // In this sample, we'll use the same text for the ticker and the expanded notification
	        CharSequence text = getText(R.string.hello_world);
	        
	        // The PendingIntent to launch our activity if the user selects this notification
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	                new Intent(this, MainActivity.class), 0);

	        // Set the icon, scrolling text and timestamp
	        Notification notification = new Notification.Builder(this)
	        .setContentText(text)
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(contentIntent)
	        .build();

	        // Send the notification.
	        mNM.notify(NOTIFICATION, notification);
	    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
    public class LocalBinder extends Binder {
        NotificationsService getService() {
            return NotificationsService.this;
        }
    }
}
