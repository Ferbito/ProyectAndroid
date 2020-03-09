package com.example.comicsclub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavoritosTiendas extends AppCompatActivity {
    private List<TiendasResponse.Tiendas> mTiendasFavorito=new ArrayList<>();
    private ListView mLv = null;
    private MyAdapter mAdapter;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos_tiendas);
        Toast.makeText(FavoritosTiendas.this, "WELCOME TO FAVORITES", Toast.LENGTH_SHORT).show();

        Intent getIntent = getIntent();
        mCurrentLocation.setLatitude(getIntent.getDoubleExtra("locationLat", 0.0));
        mCurrentLocation.setLongitude(getIntent.getDoubleExtra("locationLong", 0.0));

        mLv = findViewById(R.id.list_fav);
        leerDatosSP();

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i3=new Intent(FavoritosTiendas.this,ListComics.class);
                startActivity(i3);
            }
        });

        mLv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu,
                                            View view,
                                            ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0, 1, 0, "MAPS");
                contextMenu.add(0, 2, 0, "ELIMINAR");
            }
        });
    }

    private void guardarDatoSP(){
        SharedPreferences mPrefs = getSharedPreferences(HelperGlobal.KEYARRAYFAVSPREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mTiendasFavorito);
        prefsEditor.putString(HelperGlobal.ARRAYTIENDASFAV, json);
        prefsEditor.commit();
    }

    private void leerDatosSP(){
        SharedPreferences mPrefs = getSharedPreferences(HelperGlobal.KEYARRAYFAVSPREFERENCES,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(HelperGlobal.ARRAYTIENDASFAV, "");
        Type founderListType = new TypeToken<ArrayList<TiendasResponse.Tiendas>>(){}.getType();
        ArrayList<TiendasResponse.Tiendas> restoreArray = gson.fromJson(json, founderListType);
        //Log.d("PERSIST", String.valueOf(restoreArray.size()));
        if(restoreArray!=null){
            mTiendasFavorito=restoreArray;

            for (int i =0; i<mTiendasFavorito.size(); i++) {
                Location location = new Location("");
                location.setLatitude(mTiendasFavorito.get(i).getGeometry().getLocation().getLat());
                location.setLongitude(mTiendasFavorito.get(i).getGeometry().getLocation().getLng());

                float distance = mCurrentLocation.distanceTo( location );
                mTiendasFavorito.get(i).setDistance(distance);
            }

            Collections.sort(mTiendasFavorito, new Comparator<TiendasResponse.Tiendas>(){
                public int compare(TiendasResponse.Tiendas obj1,
                                   TiendasResponse.Tiendas obj2) {

                    return obj1.getDistance().compareTo(obj2.getDistance());
                }
            });

            mAdapter = new MyAdapter();
            mLv.setAdapter(mAdapter);
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTiendasFavorito.size();
        }

        @Override
        public Object getItem(int i) {
            return mTiendasFavorito.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View myview = null;

            if (myview == null) {

                LayoutInflater inflater = (LayoutInflater) FavoritosTiendas.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                myview = inflater.inflate(R.layout.list_fav, null);
            } else
                myview = view;

            ImageView iv = myview.findViewById(R.id.imageIcon);
            Picasso.get().load(mTiendasFavorito.get(i).getIcon()).into(iv);

            TextView tTitle = myview.findViewById(R.id.title);
            tTitle.setText(mTiendasFavorito.get(i).getName());

            TextView tRating = myview.findViewById(R.id.rating);
            tRating.setText("Valoración: "+String.valueOf(mTiendasFavorito.get(i).getRating())+" "+"["+
                    String.valueOf(mTiendasFavorito.get(i).getUser_ratings_total())+"]");

            TextView tDistance = myview.findViewById(R.id.distance);
            tDistance.setText("Se encuentra a " + String.valueOf(Math.round(mTiendasFavorito.get(i).getDistance())) + " metros.");

            return myview;
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 1:
                Toast.makeText(FavoritosTiendas.this,
                        "MAPS", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(FavoritosTiendas.this, MapsActivity.class);
                intent.putExtra("TITLE", mTiendasFavorito.get(info.position).getName());
                intent.putExtra("LAT", mTiendasFavorito.get(info.position).getGeometry().getLocation().getLat());
                intent.putExtra("LON", mTiendasFavorito.get(info.position).getGeometry().getLocation().getLng());
                startActivity(intent);

                mAdapter.notifyDataSetChanged();
                break;
            case 2:
                mTiendasFavorito.remove(info.position);
                Toast.makeText(FavoritosTiendas.this,"ELIMINADO DE FAVORITOS", Toast.LENGTH_SHORT).show();
                guardarDatoSP();
                mAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }





}
