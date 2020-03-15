package com.example.comicsclub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoritosTiendas extends AppCompatActivity {
    public static ArrayList<TiendasParse.Tiendas> mTiendasFavorito;
    private ListView mLv = null;
    private MyAdapter mAdapter;
    private Location mCurrentLocation = new Location("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos_tiendas);
        Intent getIntent = getIntent();
        mCurrentLocation.setLatitude(getIntent.getDoubleExtra(HelperGlobal.LOCATIONLAT, 0.0));
        mCurrentLocation.setLongitude(getIntent.getDoubleExtra(HelperGlobal.LOCATIONLONG, 0.0));
        ArrayList<TiendasParse.Tiendas> tiendasIntent = getIntent.getParcelableArrayListExtra(HelperGlobal.PARCELABLEKEYARRAY);
        mLv = findViewById(R.id.list_fav);
        mTiendasFavorito = new ArrayList<>();
        for(int i = 0; i<tiendasIntent.size();i++){
            mTiendasFavorito.add(tiendasIntent.get(i));
        }
        mAdapter = new MyAdapter();
        mLv.setAdapter(mAdapter);

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
                contextMenu.add(0, 1, 0, HelperGlobal.ABRIRMAPSCONTEXTMENU);
                contextMenu.add(0, 2, 0, HelperGlobal.ELIMINARFAVCONTEXTMENU);
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
                        HelperGlobal.MAPSTOAST, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(FavoritosTiendas.this, MapsActivity.class);
                intent.putExtra(HelperGlobal.TITLEINPUTTIENDASCERCANAS, mTiendasFavorito.get(info.position).getName());
                intent.putExtra(HelperGlobal.LATINPUTTIENDASCERCANAS, mTiendasFavorito.get(info.position).getLat());
                intent.putExtra(HelperGlobal.LONINPUTTIENDASCERCANAS, mTiendasFavorito.get(info.position).getLng());
                startActivity(intent);

                mAdapter.notifyDataSetChanged();
                break;
            case 2:
                mTiendasFavorito.remove(info.position);
                Toast.makeText(FavoritosTiendas.this,HelperGlobal.ELIMINADOFAV, Toast.LENGTH_SHORT).show();
                guardarDatoSP();
                mAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }
}
