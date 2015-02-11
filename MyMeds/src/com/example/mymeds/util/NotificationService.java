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
import android.os.IBinder;
import android.util.Log;

public class NotificationService extends Service {
    private NotificationManager notificationManager;

    /**
     * Returning null, as the service will not be communication
     * with any other components.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called by the system when the service is first created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Called every time the service is started by calling startService(Intent).
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int value = intent.getIntExtra("id", 0);
        Log.v("INFO", Integer.toString(value));
        showNotification(value);
        setAlarms();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Builds & Displays a notification.
     */
    private void showNotification(int id) {
        //Initialise notificationManager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // The PendingIntent to launch a activity if the user selects the notification.
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        //Create the notification with a icon and text.
        Notification notification = new Notification.Builder(this)
                .setContentText("Alarm!!!!!!!").setSmallIcon(R.drawable.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(contentIntent).build();

        // Send the notification.
        notificationManager.notify(id, notification);
    }
    
    /**
     * Go through the mymeds json file and set an alarm for each piece of medication to be
     * taken during the day
     */
    public void setAlarms(){
    	
		String jsonTime = null;
		String dosage = null;
		String units = null;
		String name = null;
		
    	try {
			// read file from assets
			AssetManager assetManager = this.getAssets();
			InputStream is = assetManager.open("allmeds.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String bufferString = new String(buffer);

			JSONObject jsonObject = new JSONObject(bufferString);
			JSONArray medIndex = jsonObject.getJSONArray("medication");
			JSONObject record;

			for (int k = 0; k < medIndex.length(); k++) {
				record = medIndex.getJSONObject(k);
				//work to get into frequency to get time
				name = record.getString("displayName");
				JSONArray freqIndex = record.getJSONArray("frequency");
				JSONObject freq;
				for(int t = 0; t<freqIndex.length(); t++){
					freq = freqIndex.getJSONObject(t);
					jsonTime = freq.getString("time");
					dosage = freq.getString("dosage");
					units = freq.getString("units");
					Log.v("Time", jsonTime);
					Log.v("Dosage", dosage);
					Log.v("Units", units);
					Log.v("Name", name);
					
				}
				
			}
		} catch (IOException e) {
			Log.e("IOException", "Error loading file");
			e.printStackTrace();
		} catch (JSONException e) {
			Log.e("JSONException", "JSON exception");
			e.printStackTrace();
		}
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}