package com.example.mymeds.util;

import com.example.mymeds.R;
import com.example.mymeds.activites.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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
        int id = intent.getIntExtra("id", 0);
        String time = intent.getStringExtra("time");
        String dosage = intent.getStringExtra("dosage");
        String units = intent.getStringExtra("units");
        String name = intent.getStringExtra("name");
        //Log.v("INFO", Integer.toString(value));
        showNotification(getID(id, time), time, dosage, units, name);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Builds & Displays a notification.
     */
    private void showNotification(int id, String time, String dosage, String units, String name) {
        //Initialise notificationManager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // The PendingIntent to launch a activity if the user selects the notification.
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        //Create the notification with a icon and text.
        Notification notification = new Notification.Builder(this)
        		.setContentTitle("Pop dem pills Muthafucker")
                .setContentText(name +": "+dosage+" "+units).setSmallIcon(R.drawable.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(contentIntent).build();

        // Send the notification.
        notificationManager.notify(id, notification);
        Log.v("HERE", String.valueOf(id) +" "+time);
    }
    
    /**
     * Get the id based on the id & time of a medication
     */
    public int getID(int id, String time) {
		StringBuilder idValue = new StringBuilder();
		idValue.append(id);
		idValue.append(time);
    	return Integer.valueOf(idValue.toString());
    }
    
    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}