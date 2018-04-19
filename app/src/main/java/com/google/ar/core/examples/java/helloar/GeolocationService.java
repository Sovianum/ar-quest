package com.google.ar.core.examples.java.helloar;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.ar.core.examples.java.helloar.core.game.Place;
import com.google.ar.core.examples.java.helloar.model.Quest;

import java.util.Collection;

import javax.inject.Inject;

public class GeolocationService extends Service {
    @Inject
    GameModule gameModule;

    public GeolocationService() {
    }

    private static final String TAG = "GeolocationService";
    private static final String CHANNEL_ID = "channel_01";
    private LocationManager locationManager = null;
    private Location lastLocation;
    private static final double DISTANCE = 50.0;
    private NotificationManager notificationManager;
    private static final int DEFAULT_NOTIFICATION_ID = 101;
    private static final String NOTIFICATION_TITLE = "Найдена точка";
    private static final String NOTIFICATION_SEARCHING_TITLE = "Поиск мест";
    private static final String NOTIFICATION_SEARCHING_CONTENT = "Найдите контрольную точку для продолжения квеста";
    private Place searchedPlace;
    private boolean showedSearchingNotif;

    private Location mDestination;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (lastLocation.distanceTo(location) > DISTANCE/2.0 ) { //если поменялась геопозиция
                lastLocation = location;

                Quest currentQuest = gameModule.getCurrentQuest();
                if (currentQuest == null) {
                    return;
                }
                Collection<Place> currentQuestPlaces = currentQuest.getAvailablePlaces();
                if(isCloseToPoints(currentQuestPlaces, location)) {
                    //showNotification("Найдена точка", String.valueOf(location.getLongitude()) +
                    //        ", " + location.getLatitude());
                    sendNotification(String.valueOf(location.getLongitude()) +
                            ", " + location.getLatitude());
                    gameModule.getCurrentJournal().addNow("Найдена контрольная точка " +
                        searchedPlace.getName());
                    showedSearchingNotif = false;
                } else {
                    //showNotificationSearching(NOTIFICATION_SEARCHING_TITLE, NOTIFICATION_SEARCHING_CONTENT);
                    //sendNotification("Not found");

                }
            } else if(searchedPlace == null) {
                //showNotificationSearching(NOTIFICATION_SEARCHING_TITLE, NOTIFICATION_SEARCHING_CONTENT);
                //sendNotification("Not found");
            } else if (location.distanceTo(searchedPlace.getLocation()) > DISTANCE) {
                //if (!showedSearchingNotif) {
                    //showNotificationSearching(NOTIFICATION_SEARCHING_TITLE, NOTIFICATION_SEARCHING_CONTENT);
                    //showedSearchingNotif = true;
                //}
                //sendNotification("Not found");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        App.getAppComponent().inject(this);
        Log.d("GeoLocationService", "onStart: " + intent);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        initializeLocation();
        mDestination = lastLocation;//intent.getParcelableExtra("destination");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                return START_NOT_STICKY;
            }
        }
        locationManager.removeUpdates(locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                locationListener);
        boolean inForeground = false;
        if (intent.hasExtra(getResources().getString(R.string.foreground))) {
            inForeground = intent.getBooleanExtra("foreground", false);
        }
        if (inForeground) {
            showNotificationSearching(NOTIFICATION_SEARCHING_TITLE, NOTIFICATION_SEARCHING_CONTENT);
        } else {
            stopForeground(true);
        }

        return START_STICKY;
    }



    void showNotification(String title, String content) {
        notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GeolocationService.this, "default")
                .setSmallIcon(R.drawable.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true); // clear notification after click

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(
                        Context.NOTIFICATION_SERVICE);
        //mNotificationManager.notify(1, mBuilder.build());
        startForeground(DEFAULT_NOTIFICATION_ID, mBuilder.build());
    }

    void showNotificationSearching(String title, String content) {
        notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GeolocationService.this, "default")
                .setSmallIcon(R.drawable.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true); // clear notification after click

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(
                        Context.NOTIFICATION_SERVICE);
        //mNotificationManager.notify(1, mBuilder.build());
        startForeground(DEFAULT_NOTIFICATION_ID, mBuilder.build());
    }

    private void initializeLocation() {
        if (lastLocation == null) {
            lastLocation = new Location("");
            lastLocation.setLongitude(-180.0);
            lastLocation.setLatitude(-180.0);
        }
    }

    boolean isCloseToPoints(Collection<Place> places, Location location) {
        for(Place place: places) {
            if (isClose(place.getLocation(), location)) {
                searchedPlace = place;
                return true;
            }
        }
        return false;
    }

    boolean isClose(Location location1, Location location2) {
        if (location2 != null && location1 != null) {
            return location2.distanceTo(location1) <= DISTANCE;
        }
        return false;
    }


    private void sendNotification(String notificationDetails) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            mNotificationManager.createNotificationChannel(mChannel);
        }

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GeolocationService.this, "default")
                .setSmallIcon(R.drawable.ic_launcher) // notification icon
                .setColor(Color.RED)
                .setContentTitle(NOTIFICATION_TITLE) // title for notification
                .setContentText(getString(R.string.geofence_notification_text) + " " + notificationDetails + "?")// message for notification
                .setAutoCancel(true) // clear notification after click
                .setContentIntent(notificationPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setChannelId(CHANNEL_ID); // Channel ID
        }
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
