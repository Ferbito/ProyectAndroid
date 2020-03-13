package com.example.comicsclub;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import java.util.List;

public class ListTiendas extends AppCompatActivity implements LocationListener {
    private static final Integer MY_PERMISSIONS_GPS_FINE_LOCATION = 1;
    private LocationManager mLocManager;
    private final String TAG = getClass().getSimpleName();
    private Location mCurrentLocation;
    private ArrayList<TiendasParse.Tiendas> mResultsTiendas;
    private ArrayList<TiendasParse.Tiendas> mTiendasFinal;
    private ArrayList<TiendasParse.Tiendas> mResultsCentros;
    private List<TiendasResponse.Tiendas> mTiendasFavorito=new ArrayList<>();

    private ObjetcFiltroTienda mFiltroLeido = null;
    private Intent mServiceIntent;

    private ListView mLv = null;
    private MyAdapter mAdapter = null;
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mCurrentLocation = intent.getParcelableExtra(HelperGlobal.KEY_MESSAGE);
        }
    };

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

    private void actualizar() {
        leerDatosSPFiltro();
            if (mFiltroLeido != null) {
                //TIPO
                if (mFiltroLeido.isBook_store()) {
                    mTiendasFinal = mResultsTiendas;
                } else {
                    mTiendasFinal = mResultsCentros;
                }
                //DISTANCIA
                String datosDistance[] = mFiltroLeido.getDistance().split(" ");
                if (datosDistance[1].contains("km")) {
                    datosDistance[0] = String.valueOf(Integer.parseInt(datosDistance[0]) * 1000);
                }
                mRadiusBusqueda=Integer.parseInt(datosDistance[0]);
                for(int dist=0; dist<mTiendasFinal.size();dist++){
                    if(mTiendasFinal.get(dist).getDistance()>Float.parseFloat(datosDistance[0])){
                        mTiendasFinal.remove(dist);
                    }
                }
                //RATING
                for(int rat = 0; rat<mTiendasFinal.size(); rat++){
                    if(Double.parseDouble(mFiltroLeido.getRating()) > mTiendasFinal.get(rat).getRating()){
                        mTiendasFinal.remove(rat);
                    }
                }
            } else {
                Log.d("VACIO", "VACIO");
                mTiendasFinal = mResultsTiendas;
            }
            if(mAdapter==null){
                mAdapter = new MyAdapter(ListTiendas.this);
                mLv.setAdapter(mAdapter);
            }else
                mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODINTFILTROTIENDA) {
            Toast.makeText(ListTiendas.this, "VUELTA A CASA", Toast.LENGTH_SHORT).show();
            //actualizar();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
       AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {

            case 1:
                        Toast.makeText(ListTiendas.this,
                        "MAPS", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ListTiendas.this, MapsActivity.class);
                        intent.putExtra("TITLE", mTiendasFinal.get(info.position).getName());
                        intent.putExtra("LAT", mTiendasFinal.get(info.position).getLat());
                        intent.putExtra("LON", mTiendasFinal.get(info.position).getLng());
                        intent.putExtra("RADIUS",mRadiusBusqueda);
                        startActivity(intent);

                mAdapter.notifyDataSetChanged();
                break;
            case 2:
                boolean encontrado = false;
                TiendasParse.Tiendas tiendasfav = mTiendasFinal.get(info.position);
                for (int x=0 ; x<mTiendasFinal.size();x++){
                    if(mTiendasFinal.get(x).getName().equalsIgnoreCase(tiendasfav.getName())
                            && (mTiendasFinal.get(x).getIcon().equalsIgnoreCase(tiendasfav.getIcon()))){
                        encontrado = true;
                        break;
                    }
                }
                if(encontrado){
                    Toast.makeText(ListTiendas.this,
                            "TIENDA YA EN FAVORITOS", Toast.LENGTH_LONG).show();
                }else{
                    mTiendasFinal.add(tiendasfav);
                    Toast.makeText(ListTiendas.this,
                            "AÑADIDO A FAVORITOS", Toast.LENGTH_LONG).show();
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
            return mResultsTiendas.size();
        }

        @Override
        public Object getItem(int i) {
            return mResultsTiendas.get(i);
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
            Picasso.get().load(mResultsTiendas.get(i).getIcon()).into(iv);

            TextView tTitle = myview.findViewById(R.id.title);
            tTitle.setText(mResultsTiendas.get(i).getName());

            TextView tRating = myview.findViewById(R.id.rating);
            tRating.setText("Valoración: "+String.valueOf(mResultsTiendas.get(i).getRating())+" "+"["+String.valueOf(mResultsTiendas.get(i).getUser_ratings_total())+"]");


            TextView tDistance = myview.findViewById(R.id.distance);
            tDistance.setText("Se encuentra a " + String.valueOf(Math.round(mResultsTiendas.get(i).getDistance())) + " metros.");

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
        Log.d("HOLAPARSE", "url");
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ lat + "," + lng
                + "&radius=50000&type=book_store&key=AIzaSyAn93plb2763qJNDzPIzNM0hwKJ1fDYvhk";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("HOLAPARSE", url);
                    TiendasParse tiendasParse = new TiendasParse();
                    mResultsTiendas = tiendasParse.parsePlaces(response);

                    for (int i = 0; i < mResultsTiendas.size(); i++) {
                        Location location = new Location("");
                        location.setLatitude(mResultsTiendas.get(i).getLat());
                        location.setLongitude(mResultsTiendas.get(i).getLng());

                        float distance = mCurrentLocation.distanceTo(location);
                        mResultsTiendas.get(i).setDistance(distance);
                    }
                    // Order Array
                    Collections.sort(mResultsTiendas, new Comparator<TiendasParse.Tiendas>() {
                        @Override
                        public int compare(TiendasParse.Tiendas obj1, TiendasParse.Tiendas obj2) {
                            return obj1.getDistance().compareTo(obj2.getDistance());

                        }
                    });

                  /*  mPd.dismiss();
                    Log.d("SIZE", String.valueOf(mResultsTiendas.size()));*/

                }
            }, new Response.ErrorListener() {
                @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);



        final String url2 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ lat + "," + lng
                + "&radius=50000&type=shopping_mall&key=AIzaSyAn93plb2763qJNDzPIzNM0hwKJ1fDYvhk";
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("HOLAPARSE", url2);
                        TiendasParse tiendasParse = new TiendasParse();
                        mResultsCentros = tiendasParse.parsePlaces(response);

                        for (int i = 0; i < mResultsCentros.size(); i++) {
                            Location location = new Location("");
                            location.setLatitude(mResultsCentros.get(i).getLat());
                            location.setLongitude(mResultsCentros.get(i).getLng());

                            float distance = mCurrentLocation.distanceTo(location);
                            mResultsCentros.get(i).setDistance(distance);
                        }
                        // Order Array
                        Collections.sort(mResultsCentros, new Comparator<TiendasParse.Tiendas>() {
                            @Override
                            public int compare(TiendasParse.Tiendas obj1, TiendasParse.Tiendas obj2) {
                                return obj1.getDistance().compareTo(obj2.getDistance());

                            }
                        });

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

        Log.d(TAG, "New Location: " +
                location.getLatitude() + ", " +
                location.getLongitude() + "," +
                location.getAltitude());

        mCurrentLocation = location;
        getTiendas(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        //startService();
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
