package com.example.comicsclub;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyService extends Service implements LocationListener {

    private final String TAG = getClass().getSimpleName();
    private LocationManager mLocManager = null;
    private Location mCurrentLocation = new Location("");
    private ObjetcFiltroTienda mFiltroLeido = null;
    private List<TiendasResponse.Tiendas> mTiendasFavorito = new ArrayList<>();


    public MyService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Servicio creado");

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "1",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    @SuppressWarnings({"MissingPermission"})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Set Foreground service

        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, "1")
                .setContentTitle("Foreground Service")
                .setContentText(tiendasCercanas() + " tiendas favoritas cerca de ti")
                .setSmallIcon(R.mipmap.logomarvel)
                .build();

        startForeground(1, notification);


        // Set GPS Listener
        mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1, 300,
                this);

        Log.d(TAG, "Listener set");

        return START_STICKY;
    }

    public int tiendasCercanas() {
        leerDatosSPFavs();
        int contTiendas = 0;
        if (mTiendasFavorito != null) {
            for (int i = 0; i < mTiendasFavorito.size(); i++) {
                if (mTiendasFavorito.get(i).getDistance() < 500) {
                    contTiendas++;
                }
            }
        }
        return contTiendas;
    }


    private void leerDatosSPFavs() {
        SharedPreferences mPrefs = getSharedPreferences(HelperGlobal.KEYARRAYFAVSPREFERENCES, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(HelperGlobal.ARRAYTIENDASFAV, "");
        Type founderListType = new TypeToken<ArrayList<TiendasResponse.Tiendas>>() {
        }.getType();
        ArrayList<TiendasResponse.Tiendas> restoreArray = gson.fromJson(json, founderListType);
        //Log.d("PERSIST", String.valueOf(restoreArray.size()));
        if (restoreArray != null) {
            mTiendasFavorito = restoreArray;
            for (int i = 0; i < mTiendasFavorito.size(); i++) {
                Location location = new Location("");
                location.setLatitude(mTiendasFavorito.get(i).getGeometry().getLocation().getLat());
                location.setLongitude(mTiendasFavorito.get(i).getGeometry().getLocation().getLng());
                Log.d("LOCATION9999", String.valueOf(location.getLatitude()));
                Log.d("LOCATION9999", String.valueOf(location.getLongitude()));
                Log.d("LOCATION9999", String.valueOf(mCurrentLocation.getLatitude()));
                Log.d("LOCATION9999", String.valueOf(mCurrentLocation.getLongitude()));
                float distance = mCurrentLocation.distanceTo(location);
                mTiendasFavorito.get(i).setDistance(distance);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        if (mLocManager != null) {
            mLocManager.removeUpdates(this);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "new location");
        Toast.makeText(this, "New Location", Toast.LENGTH_SHORT).show();
        mCurrentLocation = location;
        Log.d("POSICIONGUARDADA", String.valueOf(mCurrentLocation.getLongitude()));
        Log.d("POSICIONGUARDADA", String.valueOf(mCurrentLocation.getLatitude()));

        Intent intent = new Intent(HelperGlobal.INTENT_LOCALIZATION_ACTION);
        intent.putExtra(HelperGlobal.KEY_MESSAGE, "New Location");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
