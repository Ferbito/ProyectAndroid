package com.example.gamesclub;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

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
    private List<TiendasResponse.Tiendas> mTiendasFavorito;

    private ListView mLv = null;
    private MyAdapter mAdapter;
    private boolean mListSimple=false;
    private static final int CODINTFILTROTIENDA = 0;

    private ProgressDialog mPd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_tiendas);

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
                contextMenu.add(1, 2, 0, "FAVORITOS");

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

        ImageButton favoritosButton = findViewById(R.id.imgBtnFavoritos);
        favoritosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent favoritosLista = new Intent(ListTiendas.this, FavoritosTiendas.class);
                startActivity(favoritosLista);
            }
        });
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
                        intent.putExtra("TITLE", mResults.get(info.position).name);
                        intent.putExtra("LAT", mResults.get(info.position).geometry.location.lat);
                        intent.putExtra("LON", mResults.get(info.position).geometry.location.lng);
                        startActivity(intent);

                mAdapter.notifyDataSetChanged();
                break;
            case 2:
                mTiendasFavorito.add(mResults.get(info.position));

                Toast.makeText(ListTiendas.this,
                        "AÑADIDO A FAVORITOS", Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();

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

            ImageView iv = (ImageView) myview.findViewById(R.id.imageIcon);
            Picasso.get().load(mResults.get(i).getIcon()).into(iv);

            TextView tTitle = (TextView) myview.findViewById(R.id.title);
            tTitle.setText(mResults.get(i).getName());

            TextView tDistance = (TextView) myview.findViewById(R.id.distance);
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
        
        d.getTiendas("book_store", lat + "," + lng, 1000).enqueue(
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
                                Log.d(TAG, mResults.get(i).name);
                                Log.d(TAG, String.valueOf(mResults.get(i).geometry.location.lat));
                                Log.d(TAG, String.valueOf(mResults.get(i).geometry.location.lng));

                                Location location = new Location("");
                                location.setLatitude(mResults.get(i).geometry.location.lat);
                                location.setLongitude(mResults.get(i).geometry.location.lng);

                                float distance = mCurrentLocation.distanceTo( location );
                                mResults.get(i).distance = distance;
                                Log.d(TAG, String.valueOf(distance) + " metros.");
                                Log.d(TAG, "==================");
                            }

                            // Order Array
                            Collections.sort(mResults, new Comparator<TiendasResponse.Tiendas>(){
                                public int compare(TiendasResponse.Tiendas obj1,
                                                   TiendasResponse.Tiendas obj2) {

                                    return obj1.distance.compareTo(obj2.distance);
                                }
                            });

                            // Print
                            for (int i=0; i<mResults.size(); i++) {
                                Log.d(TAG, mResults.get(i).name);

                                Log.d(TAG, String.valueOf(mResults.get(i).geometry.location.lat));
                                Log.d(TAG, String.valueOf(mResults.get(i).geometry.location.lng));

                                Location location = new Location("");
                                location.setLatitude(mResults.get(i).geometry.location.lat);
                                location.setLongitude(mResults.get(i).geometry.location.lng);

                                Log.d(TAG, mResults.get(i).distance.toString() + " metros.");
                                Log.d(TAG, "********************");
                            }
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
