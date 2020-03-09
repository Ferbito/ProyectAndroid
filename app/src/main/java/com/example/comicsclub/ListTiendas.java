package com.example.comicsclub;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListTiendas extends AppCompatActivity implements LocationListener {
    private static final Integer MY_PERMISSIONS_GPS_FINE_LOCATION = 1;
    private LocationManager mLocManager;
    private final String TAG = getClass().getSimpleName();
    private Location mCurrentLocation;
    private List<TiendasResponse.Tiendas> mResults;
    private List<TiendasResponse.Tiendas> mTiendasFavorito=new ArrayList<>();
    private ObjetcFiltroTienda mFiltroLeido = null;
    private Intent mServiceIntent;

    private ListView mLv = null;
    private MyAdapter mAdapter;
    private boolean mListSimple=false;
    private static final int CODINTFILTROTIENDA = 0;
    private int mRadiusBusqueda = 1000;
    private String mSitioPref = "book_store";
    private Double mRating=  0.0;
    private int mUserRating=0;
    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_tiendas);

        pedirPermisos();

        mPd = new ProgressDialog(ListTiendas.this);
        mPd.setProgressStyle(Spinner.ACCESSIBILITY_LIVE_REGION_ASSERTIVE);
        mPd.setTitle("LIBRARIES");
        mPd.setMessage("SEARCHING... WAIT A SECOND");
        mPd.setProgress(100);
        mPd.show();

        mLv = findViewById(R.id.list);
        mAdapter = new MyAdapter(this);
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
                contextMenu.add(0, 1, 0, "MAPS");
                contextMenu.add(0, 2, 0, "FAVORITOS");
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
                favoritosLista.putExtra("locationLat", mCurrentLocation.getLatitude());
                favoritosLista.putExtra("locationLong", mCurrentLocation.getLongitude());
                startActivity(favoritosLista);
            }
        });
        leerDatosSPFavs();
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(HelperGlobal.INTENT_LOCALIZATION_ACTION));

        startService();
    }

    public void startService() {

        mServiceIntent = new Intent(getApplicationContext(), MyService.class);
        startService(mServiceIntent);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "Activity onDestroy!");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private void actualizar(){
        leerDatosSPFiltro();
        if(mFiltroLeido!=null){
            String datosDistance[] = mFiltroLeido.getDistance().split(" ");

            if(datosDistance[1].contains("km")){
                datosDistance[0] = String.valueOf(Integer.parseInt(datosDistance[0])*1000);
            }

            if (mRadiusBusqueda > Integer.parseInt(datosDistance[0])) {
                for (int i = 0; i < mResults.size(); i++) {
                    if (mResults.get(i).getDistance() > Integer.parseInt(datosDistance[0])) {
                        mResults.remove(i);
                    }else if(mResults.get(i).getRating() < Double.parseDouble(mFiltroLeido.getRating())){
                        mResults.remove(i);
                    }
                }
                mAdapter.notifyDataSetChanged();
                mRadiusBusqueda = Integer.parseInt(datosDistance[0]);
            }else{
                mRadiusBusqueda = Integer.parseInt(datosDistance[0]);
                mRating = Double.parseDouble(mFiltroLeido.getRating());
                mSitioPref="book_store";
                getTiendas(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            }

            String sitioLeido;
            if(mFiltroLeido.isBook_store()){
                sitioLeido = "book_store";
            }else{
                sitioLeido = "shopping_mall";
            }
            Log.d("MISITIO",sitioLeido);
            if(mSitioPref.equals(sitioLeido)){
                //NO HACE NADA
            }else{
                mSitioPref = sitioLeido;
                Log.d("MISITIO2",mSitioPref);
                getTiendas(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            }
        }


    }
    // Este receiver gestiona mensajes recibidos con el intent 'location-event-position'
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra(HelperGlobal.KEY_MESSAGE);
            Log.d(TAG, "BroadcastReceiver::Got message: " + message);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODINTFILTROTIENDA) {
            Toast.makeText(ListTiendas.this, "VUELTA A CASA", Toast.LENGTH_SHORT).show();
            actualizar();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {

        //import android.widget.AdapterView.AdapterContextMenuInfo;

       AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {

            case 1:

                        Toast.makeText(ListTiendas.this,
                        "MAPS", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ListTiendas.this, MapsActivity.class);
                        intent.putExtra("TITLE", mResults.get(info.position).getName());
                        intent.putExtra("LAT", mResults.get(info.position).getGeometry().getLocation().getLat());
                        intent.putExtra("LON", mResults.get(info.position).getGeometry().getLocation().getLng());
                        intent.putExtra("RADIUS",mRadiusBusqueda);
                        startActivity(intent);

                mAdapter.notifyDataSetChanged();
                break;
            case 2:
                boolean encontrado = false;
                TiendasResponse.Tiendas tiendasfav = mResults.get(info.position);
                for (int x=0 ; x<mTiendasFavorito.size();x++){
                    if(mTiendasFavorito.get(x).getName().equalsIgnoreCase(tiendasfav.getName())
                            && (mTiendasFavorito.get(x).getIcon().equalsIgnoreCase(tiendasfav.getIcon()))){
                        encontrado = true;
                        break;
                    }
                }
                if(encontrado){
                    Toast.makeText(ListTiendas.this,
                            "TIENDA YA EN FAVORITOS", Toast.LENGTH_LONG).show();
                }else{
                    mTiendasFavorito.add(tiendasfav);
                    Toast.makeText(ListTiendas.this,
                            "AÑADIDO A FAVORITOS", Toast.LENGTH_LONG).show();
                    guardarDatoSPFavs();
                }

                /*for (int i=0;i<mTiendasFavorito.size();i++){
                    Log.d("FAV",mTiendasFavorito.get(i).getName());
                }*/
                break;
        }

        return true;
    }

    private void leerDatosSPFiltro(){
        SharedPreferences mPrefs = getSharedPreferences(HelperGlobal.KEYARRAYFILTROSPREFERENCES,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(HelperGlobal.ARRAYTIENDASFILTROS, "");
        //Type founderListType = new TypeToken<ArrayList<TiendasResponse.Tiendas>>(){}.getType();
        //ArrayList<TiendasResponse.Tiendas> restoreArray = gson.fromJson(json, founderListType);
        ObjetcFiltroTienda jsonFiltro= gson.fromJson(json, ObjetcFiltroTienda.class);
        //Log.d("PERSIST", String.valueOf(restoreArray.size()));
        if(jsonFiltro!=null){
            mFiltroLeido = jsonFiltro;
            Log.d("PERSIST2", String.valueOf(mFiltroLeido.getDistance()));
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
        Type founderListType = new TypeToken<ArrayList<TiendasResponse.Tiendas>>(){}.getType();
        ArrayList<TiendasResponse.Tiendas> restoreArray = gson.fromJson(json, founderListType);

        if(restoreArray!=null){
            mTiendasFavorito=restoreArray;
            for (int i =0; i<restoreArray.size(); i++) {
                Log.d("Leido", restoreArray.get(i).getName());
            }
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
            Toast.makeText(getApplicationContext(),
                    "[LOCATION] Permission granted in the past!",
                    Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(getApplicationContext(), "GPS Permission granted!",
                            Toast.LENGTH_SHORT).show();
                    startLocation();
                } else {
                    // permission denied
                    Toast.makeText(getApplicationContext(),
                            "Permission denied by user!", Toast.LENGTH_SHORT).show();
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
            return mResults.size();
        }

        @Override
        public Object getItem(int i) {
            return mResults.get(i);
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
            Picasso.get().load(mResults.get(i).getIcon()).into(iv);

            TextView tTitle = myview.findViewById(R.id.title);
            tTitle.setText(mResults.get(i).getName());

            TextView tRating = myview.findViewById(R.id.rating);
            tRating.setText("Valoración: "+String.valueOf(mResults.get(i).getRating())+" "+"["+String.valueOf(mResults.get(i).getUser_ratings_total())+"]");


            TextView tDistance = myview.findViewById(R.id.distance);
            tDistance.setText("Se encuentra a " + String.valueOf(Math.round(mResults.get(i).getDistance())) + " metros.");

            return myview;
        }
    }

    @Override
    protected void onStop() {
        mLocManager.removeUpdates(this);
        super.onStop();
    }

    private void getTiendas (double lat, double lng)
    {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit=
                new Retrofit.Builder()
                        .baseUrl("https://maps.googleapis.com/maps/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

        TiendasInterface  d = retrofit.create(TiendasInterface.class);
        
        d.getTiendas(mSitioPref, lat + "," + lng, mRadiusBusqueda,mRating,mUserRating).enqueue(
                new Callback<TiendasResponse>() {
                    @Override
                    public void onResponse(Call<TiendasResponse> call,
                                           Response<TiendasResponse> response) {

                        mResults = response.body().results;
                        if(mPd.isShowing()){
                            mPd.dismiss();
                        }

                        Log.d(TAG, String.valueOf(response.code()));
                        if (response.body() != null && mResults != null) {

                            Log.d(TAG, "Response: " + mResults.size());

                            // Print
                            for (int i=0; i<mResults.size(); i++) {
                                Log.d(TAG, mResults.get(i).getName());

                                Log.d(TAG, String.valueOf(mResults.get(i).getGeometry().getLocation().getLat()));
                                Log.d(TAG, String.valueOf(mResults.get(i).getGeometry().getLocation().getLng()));

                                Location location = new Location("");
                                location.setLatitude(mResults.get(i).getGeometry().getLocation().getLat());
                                location.setLongitude(mResults.get(i).getGeometry().getLocation().getLng());

                                float distance = mCurrentLocation.distanceTo( location );
                                mResults.get(i).setDistance(distance);
                                Log.d(TAG, String.valueOf(distance) + " metros.");
                                Log.d(TAG, "==================");


                            }

                            // Order Array
                            Collections.sort(mResults, new Comparator<TiendasResponse.Tiendas>(){
                                public int compare(TiendasResponse.Tiendas obj1,
                                                   TiendasResponse.Tiendas obj2) {

                                    return obj1.getDistance().compareTo(obj2.getDistance());
                                }
                            });


                            // Print
                            /*for (int i=0; i<mResults.size(); i++) {
                                Log.d(TAG, mResults.get(i).getName());

                                Log.d(TAG, String.valueOf(mResults.get(i).geometry.location.lat));
                                Log.d(TAG, String.valueOf(mResults.get(i).geometry.location.lng));

                                Location location = new Location("");
                                location.setLatitude(mResults.get(i).geometry.location.lat);
                                location.setLongitude(mResults.get(i).geometry.location.lng);

                                Log.d(TAG, mResults.get(i).distance.toString() + " metros.");
                                Log.d(TAG, "********************");
                            }*/
                            mLv.setAdapter(mAdapter);
                        } else
                            Log.e(TAG, "Response: empty array");
                    }

                    @Override
                    public void onFailure(Call<TiendasResponse> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                    }
                });

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

        Log.d(TAG, "New Location: " +
                location.getLatitude() + ", " +
                location.getLongitude() + "," +
                location.getAltitude());

        mCurrentLocation = location;
        getTiendas(location.getLatitude(), location.getLongitude() );
        actualizar();

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
