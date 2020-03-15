package com.example.comicsclub;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListTiendas extends AppCompatActivity implements LocationListener {
    private static final Integer MY_PERMISSIONS_GPS_FINE_LOCATION = 1;
    private LocationManager mLocManager;
    private final String TAG = getClass().getSimpleName();
    private Location mCurrentLocation;
    private ArrayList<TiendasParse.Tiendas> mResultsTiendas;
    public static ArrayList<TiendasParse.Tiendas> mTiendasFinal = new ArrayList<>();
    private ArrayList<TiendasParse.Tiendas> mResultsCentros;
    public static ArrayList<TiendasParse.Tiendas> mTiendasFavorito = new ArrayList<>();

    private ObjetcFiltroTienda mFiltroLeido = null;
    private Intent mServiceIntent;

    private ListView mLv = null;
    private MyAdapter mAdapter = null;
    private boolean mListSimple=false;
    private static final int CODINTFILTROTIENDA = 0;
    private static final int CODINTFAVORITOTIENDA = 1;
    private double mRadiusBusqueda = 1000.00;
    private String mSitioPref = "book_store";
    private Double mRating=  0.0;
    private int mUserRating=0;
    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_tiendas);

        pedirPermisos();

        mLv = findViewById(R.id.list);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(HelperGlobal.INTENT_LOCALIZATION_ACTION));

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i3=new Intent(ListTiendas.this,ListComics.class);
                startActivity(i3);
            }
        });
        mLv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu,
                                            View view,
                                            ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0, 1, 0, HelperGlobal.ABRIRMAPSCONTEXTMENU);
                contextMenu.add(0, 2, 0, HelperGlobal.AÑADIRFAV);
            }
        });

        ImageButton filtroButton = findViewById(R.id.imgBtnFiltros);
        filtroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent filtroTienda = new Intent(ListTiendas.this, FiltroTiendas.class);
                startActivityForResult(filtroTienda, CODINTFILTROTIENDA);
            }
        });

        final ImageButton favoritosButton = findViewById(R.id.imgBtnFavoritos);
        favoritosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent favoritosLista = new Intent(ListTiendas.this, FavoritosTiendas.class);
                favoritosLista.putParcelableArrayListExtra(HelperGlobal.PARCELABLEKEYARRAY, mTiendasFavorito);
                favoritosLista.putExtra(HelperGlobal.LOCATIONLAT, mCurrentLocation.getLatitude());
                favoritosLista.putExtra(HelperGlobal.LOCATIONLONG, mCurrentLocation.getLongitude());
                startActivityForResult(favoritosLista, CODINTFAVORITOTIENDA);
            }
        });
        leerDatosSPFavs();
    }

    public void startService() {
        mServiceIntent = new Intent(getApplicationContext(), MyService.class);
        startService(mServiceIntent);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location posicion = intent.getParcelableExtra(HelperGlobal.KEY_MESSAGE);
            ArrayList<TiendasParse.Tiendas> tiendasCercanas = new ArrayList<>();
            leerDatosSPFavs();
            int cont = 0;
            for(int i = 0; i<mTiendasFavorito.size();i++){
                Location location = new Location("");
                location.setLatitude(mTiendasFavorito.get(i).getLat());
                location.setLongitude(mTiendasFavorito.get(i).getLng());

                double distance = posicion.distanceTo(location);
                mTiendasFavorito.get(i).setDistance(distance);
                if(distance<1000){
                    cont++;
                    tiendasCercanas.add(mTiendasFavorito.get(i));
                }
            }

            NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String CHANNEL_ID="my_channel_01";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                CharSequence name="my_channel";
                String Description="This is my channel";
                int importance=NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel=new NotificationChannel(CHANNEL_ID,name,importance);
                mChannel.setDescription(Description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
                mChannel.setShowBadge(false);
                notificationManager.createNotificationChannel(mChannel);
            }

            Intent cercanas = new Intent(ListTiendas.this, TiendasCercanas.class);
            cercanas.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            cercanas.putParcelableArrayListExtra(HelperGlobal.PARCELABLEARRAYNEARBY,tiendasCercanas);
            PendingIntent pendingIntent=PendingIntent.getActivity(ListTiendas.this,0,cercanas,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(ListTiendas.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.marv_serv)
                    .setContentTitle(HelperGlobal.NOTIFICATIONNEARBYTITLE)
                    .setContentText("Tienes " + cont + " tiendas favoritas cerca de ti.")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            notificationManager.notify(2,builder.build());
        }
    };

    @Override
    protected void onDestroy() {
        if (mLocManager != null) {
            mLocManager.removeUpdates(this);
        }
        super.onDestroy();
    }

    private void actualizar() {
        leerDatosSPFiltro();
        mTiendasFinal = new ArrayList<>();
            if (mFiltroLeido != null) {

                //TIPO
                if (mFiltroLeido.isBook_store()) {
                    for(int i = 0; i<mResultsTiendas.size();i++){
                        mTiendasFinal.add(mResultsTiendas.get(i));
                    }

                } else {
                    for(int i = 0; i<mResultsCentros.size();i++){
                        mTiendasFinal.add(mResultsCentros.get(i));
                    }

                }


                //DISTANCIA
                String datosDistance[] = mFiltroLeido.getDistance().split(" ");
                datosDistance[0].replace(",", "");
                if (datosDistance[1].contains("km")) {
                    datosDistance[0] = String.valueOf(Integer.parseInt(datosDistance[0]) * 1000);
                }
                mRadiusBusqueda=Integer.parseInt(datosDistance[0]);

                for(int dist=0; dist<mTiendasFinal.size();dist++){

                    if(mTiendasFinal.get(dist).getDistance()>mRadiusBusqueda){
                        mTiendasFinal.remove(dist);
                        dist--;

                    }
                }
                //RATING
                for(int rat = 0; rat<mTiendasFinal.size(); rat++){
                    if(Double.parseDouble(mFiltroLeido.getRating()) > mTiendasFinal.get(rat).getRating()){
                        mTiendasFinal.remove(rat);
                        rat--;

                    }
                }
            } else {
                mTiendasFinal = mResultsTiendas;
            }
            if(mAdapter==null) {
                mAdapter = new MyAdapter(ListTiendas.this);
                mLv.setAdapter(mAdapter);
            }else{
                mAdapter.notifyDataSetChanged();
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODINTFILTROTIENDA) {
            actualizar();
        }else if(requestCode == CODINTFAVORITOTIENDA){
            leerDatosSPFavs();
        }

    }


    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
       AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {

            case 1:
                        Intent intent = new Intent(ListTiendas.this, MapsActivity.class);
                        intent.putExtra(HelperGlobal.TITLEINPUTTIENDASCERCANAS, mTiendasFinal.get(info.position).getName());
                        intent.putExtra(HelperGlobal.LATINPUTTIENDASCERCANAS, mTiendasFinal.get(info.position).getLat());
                        intent.putExtra(HelperGlobal.LONINPUTTIENDASCERCANAS, mTiendasFinal.get(info.position).getLng());
                        intent.putExtra(HelperGlobal.RADIUSINPUTTIENDASCERCANAS,mRadiusBusqueda);
                        startActivity(intent);

                mAdapter.notifyDataSetChanged();
                break;
            case 2:
                boolean encontrado = false;
                TiendasParse.Tiendas tiendasfav = mTiendasFinal.get(info.position);
                if(mTiendasFavorito.size()!=0) {
                    for (int x = 0; x < mTiendasFavorito.size(); x++) {
                        if (mTiendasFavorito.get(x).getName().equalsIgnoreCase(tiendasfav.getName())
                                && (mTiendasFavorito.get(x).getIcon().equalsIgnoreCase(tiendasfav.getIcon()))) {

                            encontrado = true;
                            break;
                        }
                    }
                }

                if(encontrado){
                    Toast.makeText(ListTiendas.this,
                            HelperGlobal.TIENDAYAFAV, Toast.LENGTH_LONG).show();
                }else{
                    mTiendasFavorito.add(tiendasfav);
                    Toast.makeText(ListTiendas.this,
                            HelperGlobal.AÑADIDOFAV, Toast.LENGTH_LONG).show();
                    guardarDatoSPFavs();
                }
                break;
        }
        return true;
    }

    private void leerDatosSPFiltro(){
        SharedPreferences mPrefs = getSharedPreferences(HelperGlobal.KEYARRAYFILTROSPREFERENCES,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(HelperGlobal.ARRAYTIENDASFILTROS, "");
        ObjetcFiltroTienda jsonFiltro= gson.fromJson(json, ObjetcFiltroTienda.class);
        if(jsonFiltro!=null){
            mFiltroLeido = jsonFiltro;
        }
    }

    private void guardarDatoSPFavs(){
        SharedPreferences mPrefs = getSharedPreferences(HelperGlobal.KEYARRAYFAVSPREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mTiendasFavorito);
        prefsEditor.putString(HelperGlobal.ARRAYTIENDASFAV, json);
        prefsEditor.commit();
    }

    private void leerDatosSPFavs(){
        SharedPreferences mPrefs = getSharedPreferences(HelperGlobal.KEYARRAYFAVSPREFERENCES,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(HelperGlobal.ARRAYTIENDASFAV, "");
        Type founderListType = new TypeToken<ArrayList<TiendasParse.Tiendas>>(){}.getType();
        ArrayList<TiendasParse.Tiendas> restoreArray = gson.fromJson(json, founderListType);

        if(restoreArray!=null){
            mTiendasFavorito=restoreArray;

        }
    }

    private void pedirPermisos(){
        // Ask user permission for location.
        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(ListTiendas.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

            ActivityCompat.requestPermissions(ListTiendas.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_GPS_FINE_LOCATION);

        } else {
            mPd = new ProgressDialog(ListTiendas.this);
            mPd.setProgressStyle(Spinner.ACCESSIBILITY_LIVE_REGION_ASSERTIVE);
            mPd.setTitle(HelperGlobal.PROGRESSTITTLELIBRARIES);
            mPd.setMessage(HelperGlobal.PROGRESSMESSAGE);
            mPd.setProgress(100);
            mPd.show();

            startLocation();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission granted by user
                    Toast.makeText(getApplicationContext(), HelperGlobal.GPSPERMISEDGARANTED,
                            Toast.LENGTH_SHORT).show();
                    mPd = new ProgressDialog(ListTiendas.this);
                    mPd.setProgressStyle(Spinner.ACCESSIBILITY_LIVE_REGION_ASSERTIVE);
                    mPd.setTitle(HelperGlobal.PROGRESSTITTLELIBRARIES);
                    mPd.setMessage(HelperGlobal.PROGRESSMESSAGE);
                    mPd.setProgress(100);
                    mPd.show();
                    startLocation();
                } else {
                    // permission denied
                    Toast.makeText(getApplicationContext(),
                            HelperGlobal.PERMISIONDENIED, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    public class MyAdapter extends BaseAdapter {

        private Context mContext;


        public MyAdapter(Context context) {
            this.mContext = context;

        }

        @Override
        public int getCount() {
            return mTiendasFinal.size();
        }

        @Override
        public Object getItem(int i) {
            return mTiendasFinal.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View myview = null;

            if (myview == null) {

                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                myview = inflater.inflate(R.layout.final_listtiendas, null);
            } else
                myview = view;

            ImageView iv = myview.findViewById(R.id.imageIcon);
            Picasso.get().load(mTiendasFinal.get(i).getIcon()).into(iv);

            TextView tTitle = myview.findViewById(R.id.title);
            tTitle.setText(mTiendasFinal.get(i).getName());

            TextView tRating = myview.findViewById(R.id.rating);
            tRating.setText("Valoración: "+String.valueOf(mTiendasFinal.get(i).getRating())+" "+"["+String.valueOf(mTiendasFinal.get(i).getUser_ratings_total())+"]");


            TextView tDistance = myview.findViewById(R.id.distance);
            tDistance.setText("Se encuentra a " + String.valueOf(Math.round(mTiendasFinal.get(i).getDistance())) + " metros.");

            return myview;
        }
    }

    private void getTiendas (final double lat, final double lng)
    {

        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ lat + "," + lng
                + "&rankby=distance&type=book_store&key=AIzaSyAn93plb2763qJNDzPIzNM0hwKJ1fDYvhk";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    TiendasParse tiendasParse = new TiendasParse();
                    mResultsTiendas = tiendasParse.parsePlaces(response);



                    for (int i = 0; i < mResultsTiendas.size(); i++) {
                        Location location = new Location("");
                        location.setLatitude(mResultsTiendas.get(i).getLat());
                        location.setLongitude(mResultsTiendas.get(i).getLng());

                        double distance = mCurrentLocation.distanceTo(location);
                        mResultsTiendas.get(i).setDistance(distance);

                    }
                    // Order Array
                    Collections.sort(mResultsTiendas, new Comparator<TiendasParse.Tiendas>() {
                        @Override
                        public int compare(TiendasParse.Tiendas obj1, TiendasParse.Tiendas obj2) {
                            return obj1.getDistance().compareTo(obj2.getDistance());
                        }
                    });


                    while(true){
                        if(mResultsTiendas!=null){
                            if (mResultsTiendas.size()==20){
                                break;
                            }
                        }
                    }
                    getComerciales(lat, lng);
                }
            }, new Response.ErrorListener() {
                @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);

    }

    private void getComerciales(double lat, double lng){
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url2 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ lat + "," + lng
                + "&rankby=distance&type=shopping_mall&key=AIzaSyAn93plb2763qJNDzPIzNM0hwKJ1fDYvhk";
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        TiendasParse tiendasParse = new TiendasParse();
                        mResultsCentros = tiendasParse.parsePlaces(response);

                        while (mResultsCentros==null){

                        }

                        for (int i = 0; i < mResultsCentros.size(); i++) {
                            Location location = new Location("");
                            location.setLatitude(mResultsCentros.get(i).getLat());
                            location.setLongitude(mResultsCentros.get(i).getLng());

                            double distance = mCurrentLocation.distanceTo(location);
                            mResultsCentros.get(i).setDistance(distance);
                        }
                        // Order Array
                        Collections.sort(mResultsCentros, new Comparator<TiendasParse.Tiendas>() {
                            @Override
                            public int compare(TiendasParse.Tiendas obj1, TiendasParse.Tiendas obj2) {
                                return obj1.getDistance().compareTo(obj2.getDistance());
                            }
                        });

                        while(true){
                            if(mResultsCentros!=null){
                                if (mResultsCentros.size()==20){
                                    break;
                                }
                            }
                        }

                        mPd.dismiss();
                        actualizar();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest2.setShouldCache(false);
        queue.add(stringRequest2);
    }

    // Methods to implement due to GPS Listener.

    @SuppressWarnings({"MissingPermission"})
    private void startLocation() {

        mLocManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (! mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            Intent callGPSSettingIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);
        }
        else {
            mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1, 300,
                    this);
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
        getTiendas(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        startService();
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
