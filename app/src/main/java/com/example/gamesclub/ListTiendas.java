package com.example.gamesclub;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListTiendas extends AppCompatActivity implements LocationListener {
    private static final Integer MY_PERMISSIONS_GPS_FINE_LOCATION = 1;
    private LocationManager mLocManager;
    private final String TAG = getClass().getSimpleName();
    private Location mCurrentLocation;


    private List<HashMap<String, String>>  mResults;
    private ListView mLv = null;
    private MyAdapter mAdapter;
    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_tiendas);

        mLv = findViewById(R.id.list);
        mAdapter = new MyAdapter(this);

        LatLng myloc = new LatLng(mLatitude, mLongitude);
        String latitude = String.valueOf(myloc.latitude);
        String longitude = String.valueOf(myloc.longitude);
        String radius = "2000"; // 2 Kilometer
        String name = "hospital";

        /*MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute(latitude, longitude, radius, name);
        Intent i2=getIntent();*/

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(ListTiendas.this, MapsActivity.class);

                startActivity(intent);
            }
        });


    }

    @Override
    public void onLocationChanged(Location location) {

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

    public class MyAdapter extends BaseAdapter {

        private Context mContext;


        public MyAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        /*
                @Override
                public int getCount() {
                    return mResults.size();
                }

                @Override
                public Object getItem(int i) {
                    return mResults.get(i);
                }
        */
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

            TextView tTitle = (TextView) myview.findViewById(R.id.title);
            tTitle.setText(mResults.get(i).size());

            TextView tDistance = (TextView) myview.findViewById(R.id.distance);
           // tDistance.setText("Se encuentra a " + String.valueOf(Math.round(mResults.get(i).clone())) + " metros.");

            return myview;
        }
    }
    /*public class MyAsyncTask extends AsyncTask<String, Void, Boolean> {
        private JSONObject jsonObject;

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            System.out.println(jsonObject); //use jsonObject here
        }

        protected Boolean doInBackground(final String... args) {
            try {
                Looper.prepare();
                String latitude = args[0];
                String longitude = args[1];
                String radius = args[2];
                String name = args[3];
                String key = "YOUR_API_KEY_FOR_BROWSER";

                String uri = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                        + "location=" + latitude + "," + longitude
                        + "&radius=" + radius
                        + "&name=" + name
                        + "&key=AIzaSyAn93plb2763qJNDzPIzNM0hwKJ1fDYvhk"+ key; // you can add more options here

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(uri);
                httpPost.setEntity(new UrlEncodedFormEntity(new ArrayList<NameValuePair>()));
                HttpEntity httpEntity = httpClient.execute(httpPost).getEntity();

                InputStream stream = httpEntity.getContent();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(stream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                stream.close();
                jsonObject = new JSONObject(sBuilder.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }*/

}
