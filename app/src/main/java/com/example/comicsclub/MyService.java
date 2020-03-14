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
    private Location mCurrentLocation;
    private ObjetcFiltroTienda mFiltroLeido = null;
    private List<TiendasParse.Tiendas> mTiendasFavorito = new ArrayList<>();


    public MyService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startLocation();
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
                .setContentTitle("hi Service")
                .setContentText("Bucando favoritos cerca de ti.")
                .setSmallIcon(R.drawable.marv_serv)
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
        Type founderListType = new TypeToken<ArrayList<TiendasParse.Tiendas>>() {
        }.getType();
        ArrayList<TiendasParse.Tiendas> restoreArray = gson.fromJson(json, founderListType);
        //Log.d("PERSIST", String.valueOf(restoreArray.size()));
        if (restoreArray != null) {
            mTiendasFavorito = restoreArray;
            for (int i = 0; i < mTiendasFavorito.size(); i++) {
                Location location = new Location("");
                location.setLatitude(mTiendasFavorito.get(i).getLat());
                location.setLongitude(mTiendasFavorito.get(i).getLng());

                float distance = mCurrentLocation.distanceTo(location);
                mTiendasFavorito.get(i).setDistance((double) distance);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLocManager != null) {
            mLocManager.removeUpdates(this);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void startLocation() {

        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent callGPSSettingIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1, 300,
                    this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "new location");
        Toast.makeText(this, "New Location", Toast.LENGTH_SHORT).show();
        mCurrentLocation = location;
        Intent intent = new Intent(HelperGlobal.INTENT_LOCALIZATION_ACTION);
        intent.putExtra(HelperGlobal.KEY_MESSAGE, mCurrentLocation);
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
