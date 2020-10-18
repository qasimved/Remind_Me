package com.example.qasim1793.remindme.Backend;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.qasim1793.remindme.Frontend.DialogActivity;
import com.example.qasim1793.remindme.Frontend.LocationAlarmEditDelete;
import com.example.qasim1793.remindme.Frontend.ReminderEditActivity;
import com.example.qasim1793.remindme.R;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.List;

public class BackgroundService extends Service {
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private final String TAG = "BackgroundService";
    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private NotificationManager notificationManager;

    private Context context;

    private final int LOCATION_INTERVAL = 1500;
    private final int LOCATION_DISTANCE = 0;
    private List<Reminder> reminderList;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private class LocationListener implements android.location.LocationListener
    {
        private Location lastLocation = null;
        private final String TAG = "LocationListener";
        private Location mLastLocation;

        public LocationListener(String provider)
        {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {

            Toast.makeText(getApplicationContext(),"Location is cahnged",Toast.LENGTH_SHORT).show();
            mLastLocation = location;

            ReminderDatabase DB = new ReminderDatabase(getApplicationContext());
            reminderList = DB.getAllReminderForBGService();

            for(Reminder obj : reminderList){
                LatLng alarmLatLng = new LatLng(Double.valueOf(obj.getLat()),Double.valueOf(obj.getLng()));
                LatLng currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());

                if(CalculationByDistance(alarmLatLng,currentLatLng)<=1){
                    DB.updateLocationReminderForServicesWithStatus(obj);
                    Intent intentForDailog = new Intent(context, DialogActivity.class);
                    intentForDailog.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intentForDailog.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(obj.getID()));
                    intentForDailog.putExtra("name", obj.getTitle());
                    context.getApplicationContext().startActivity(intentForDailog);
                }
            }


            Log.i(TAG, "LocationChanged: "+location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + status);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.i(TAG, "onCreate");
        startForeground(12345678, getNotification());
        startTracking();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
    }

    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void startTracking() {
        initializeLocationManager();
        mLocationListener = new LocationListener(LocationManager.GPS_PROVIDER);

        try {
            mLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener );

        } catch (java.lang.SecurityException ex) {
            // Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            // Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    public void stopTracking() {
        this.onDestroy();
    }

    private Notification getNotification() {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),"notify_001");

        NotificationManager nManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            nManager.createNotificationChannel(channel);
        }

//        nManager.notify(0, mBuilder.build());
        return mBuilder.build();

    }

    
    public class LocationServiceBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }


    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    public void notificationForlocationReminder(LocationReminder obj){
        String mTitle = obj.getTitle();

        int mReceivedID = 1793;
        // Create intent to open ReminderEditActivity on notification click
        Intent editIntent = new Intent(getApplicationContext(), LocationAlarmEditDelete.class);
        editIntent.putExtra("id",obj.getId());
        PendingIntent mClick = PendingIntent.getActivity(getApplicationContext(), mReceivedID, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),"notify_001")
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_alarm_on_red_24dp)
                .setContentTitle(getApplicationContext().getResources().getString(R.string.app_name))
                .setTicker(mTitle)
                .setContentText("You are near to add location click here to check")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(mClick)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

        NotificationManager nManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            nManager.createNotificationChannel(channel);
        }

        nManager.notify(0, mBuilder.build());
        nManager.notify(mReceivedID, mBuilder.build());
    }




}
