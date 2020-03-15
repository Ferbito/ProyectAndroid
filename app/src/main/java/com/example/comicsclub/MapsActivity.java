package com.example.comicsclub;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code=99;
    private static String mTitle;
    private static double  mLat;
    private static double mLon;
    private static final Integer MY_PERMISSIONS_GPS_FINE_LOCATION = 1;
    private LocationManager mLocManager;
    private int mProximityRadius = 1000;
    private final String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        if (intent != null)
        {
            mTitle = intent.getStringExtra(HelperGlobal.TITLEINPUTTIENDASCERCANAS);
            mLat = intent.getDoubleExtra(HelperGlobal.LATINPUTTIENDASCERCANAS, 0.0);
            mLon = intent.getDoubleExtra(HelperGlobal.LONINPUTTIENDASCERCANAS, 0.0);
            mProximityRadius=intent.getIntExtra(HelperGlobal.RADIUSINPUTTIENDASCERCANAS,1000);

        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            checkUserLocationPermision();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

            buildGoogleApiClient();
            LatLng myloc = new LatLng(mLat, mLon);
            mMap.addMarker(new MarkerOptions().position(myloc).title(mTitle));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(myloc, 17);
            mMap.moveCamera(location);
            mMap.setMyLocationEnabled(true);

        }

        mMap.getUiSettings().setZoomControlsEnabled(true);


    }

    public void onClick(View v)
    {
       String comic = "book_store" ;
       String centro_comercial="shopping_mall";
       Object transferData[] = new Object[2];
       GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();


        switch (v.getId())
        {
            case R.id.search_adrress:
                EditText addressField = (EditText) findViewById(R.id.location_search);
                String address = addressField.getText().toString();


                List<Address> addressList ;
                MarkerOptions userMarkerOptions = new MarkerOptions();

                if (!TextUtils.isEmpty(address))
                {
                    Geocoder geocoder = new Geocoder(this);

                    try
                    {
                        addressList = geocoder.getFromLocationName(address, 6);

                        if (addressList != null)
                        {
                            for (int i=0; i<addressList.size(); i++)
                            {
                                Address userAddress = addressList.get(i);
                                LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

                                userMarkerOptions.position(latLng);
                                userMarkerOptions.title(address);
                                userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                mMap.addMarker(userMarkerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            }
                        }
                        else
                        {
                            Toast.makeText(this, HelperGlobal.LOCATIONNOTFOUNDTOAST, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(this, HelperGlobal.PLEASEWRITETOAST, Toast.LENGTH_SHORT).show();
                }
                break;


           case R.id.libreria:
                    mMap.clear();
                    String url = getUrl(mLat, mLon, comic);
                    transferData[0] = mMap;
                    transferData[1] = url;

                    getNearbyPlaces.execute(transferData);
                    Toast.makeText(this, HelperGlobal.SHEARCHINGNEARBYSHOP, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this,HelperGlobal.SHOWINGNEARBYSHOP , Toast.LENGTH_SHORT).show();
                    break;
            case R.id.centrocomercial:
                mMap.clear();
                String url2 = getUrl(mLat, mLon, centro_comercial);
                transferData[0] = mMap;
                transferData[1] = url2;

                getNearbyPlaces.execute(transferData);
                Toast.makeText(this, HelperGlobal.SHEARCHINGNEARBYSHOP, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, HelperGlobal.SHOWINGNEARBYSHOP, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private String getUrl(double latitide, double longitude, String nearbyPlace)
    {
        StringBuilder googleURL = new StringBuilder(HelperGlobal.GOOGLEURL);
        googleURL.append("location=" + latitide + "," + longitude);
        googleURL.append("&radius=" + mProximityRadius);
        googleURL.append("&type=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" +HelperGlobal.KEY);
        return googleURL.toString();
    }

    public boolean checkUserLocationPermision(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);

            } return false;

        }else{
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Request_User_Location_Code:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if(googleApiClient==null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(this,HelperGlobal.PERMISIONDENIED,Toast.LENGTH_SHORT).show();

                }
                return;

                case 1: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // Permission granted by user
                        Toast.makeText(getApplicationContext(), HelperGlobal.GPSPERMISEDGARANTED,
                                Toast.LENGTH_SHORT).show();
                        startLocation();

                    } else {
                        // permission denied
                        Toast.makeText(getApplicationContext(),
                                HelperGlobal.PERMISSIONDENIEDUSER, Toast.LENGTH_SHORT).show();
                    }
                    return;

            }
        }
    }

    protected  synchronized void buildGoogleApiClient(){
        googleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {

        mLat=location.getLatitude();
        mLon=location.getLongitude();

        lastLocation=location;
        if (currentUserLocationMarker!=null){

            currentUserLocationMarker.remove();

        }

        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());

        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(HelperGlobal.MARKEROPTIONSTITLE);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker=mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(13));

        if (googleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }
    }


    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    public void onProviderEnabled(String provider) {

    }


    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @SuppressWarnings({"MissingPermission"})
    private void startLocation() {

        mLocManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (! mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            Intent callGPSSettingIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);
        }


    }
    @Override
    protected void onStart() {
        super.onStart();

        // Ask user permission for location.
        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(MapsActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_GPS_FINE_LOCATION);

        } else {
            Toast.makeText(getApplicationContext(),
                    HelperGlobal.PERMISSIONGRANTEDPAST,
                    Toast.LENGTH_SHORT).show();

        }
    }


}
