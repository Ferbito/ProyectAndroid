package com.example.comicsclub;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListComics extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private ArrayList<ComicParse.comic> mComics = new ArrayList<>();
    private  ArrayList<ComicParse.comic> mComicsRellenos = new ArrayList<>();
    private Boolean relleno;
    private ListView lv;
    private ProgressDialog mPd;
    private final int CODINFILTROCOMIC = 0;
    private ObjectFiltroComic mFiltroComic = null;
    private  BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_comics);

        mPd = new ProgressDialog(ListComics.this);
        mPd.setProgressStyle(Spinner.ACCESSIBILITY_LIVE_REGION_ASSERTIVE);
        mPd.setTitle("COMICS");
        mPd.setMessage("SEARCHING... WAIT A SECOND");
        mPd.setProgress(100);
        mPd.show();
        lv = findViewById(R.id.lista);

        cargarComics();

        ImageButton filtroButton = findViewById(R.id.filtros);
        filtroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent filtroComic = new Intent(ListComics.this, FiltrosComics.class);
                startActivityForResult(filtroComic, CODINFILTROCOMIC);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODINFILTROCOMIC) {
            Toast.makeText(ListComics.this, "VUELTA A CASA", Toast.LENGTH_SHORT).show();
            actualizar();
        }
    }
    private void actualizar(){
        leerDatosSPFiltro();

        String datosPrecio[] = mFiltroComic.getPrice().split(" ");
        Log.d("PERSIST24", datosPrecio[0]);
            for (int i = 0; i < mComicsRellenos.size(); i++) {
                 if(Double.parseDouble(mComicsRellenos.get(i).getPrice()) >  Double.parseDouble(datosPrecio[0])){
                     mComicsRellenos.remove(i);
                }
            }
            adapter.notifyDataSetChanged();
        }

    private void leerDatosSPFiltro(){
        SharedPreferences mPrefs = getSharedPreferences(Variables.KEYARRAYFILTROSPREFERENCESCOMICS,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(Variables.ARRAYCOMICSFILTROS, "");
        //Type founderListType = new TypeToken<ArrayList<TiendasResponse.Tiendas>>(){}.getType();
        //ArrayList<TiendasResponse.Tiendas> restoreArray = gson.fromJson(json, founderListType);
        ObjectFiltroComic jsonFiltro= gson.fromJson(json, ObjectFiltroComic.class);
        //Log.d("PERSIST", String.valueOf(restoreArray.size()));
        if(jsonFiltro!=null){
            mFiltroComic = jsonFiltro;
            Log.d("PERSIST24", String.valueOf(mFiltroComic.getPrice()));
        }
    }
    private void cargarComics(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://gateway.marvel.com/v1/public/comics?ts=9&apikey=7a18af213a25abcf54a952288670033d&hash=0bd40620c9f9e68a70795b084480daed";
        //final Boolean relleno;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ComicParse comicParse = new ComicParse();
                        mComics = comicParse.parseComics(response);
                        
                        for (int i = 0; i< mComics.size(); i++){
                            relleno = true;
                            if(mComics.get(i).getDescription() == "" || mComics.get(i).getImage() == "" ||
                            mComics.get(i).getPrice().contentEquals("0") || mComics.get(i).getDescription() == "null"){

                            }else{
                                mComicsRellenos.add(mComics.get(i));
                                Log.d("datos", mComics.get(i).getImage() + "." + mComics.get(i).getExtensionImg());
                            }
                        }
                        mPd.dismiss();
                         adapter = new ComicsAdapter();
                        lv.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    public class ComicsAdapter extends BaseAdapter {

        Integer i = 0;


        @Override
        public int getCount() {
            return mComicsRellenos.size();
        }

        @Override
        public Object getItem(int i) {
            return mComicsRellenos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.d(TAG, "Position " + String.valueOf(i));
            View view2 = null;

            if (view == view2) {
                LayoutInflater inflater = (LayoutInflater) ListComics.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view2 = inflater.inflate(R.layout.list_comicsview, null);
            } else {
                view2 = view;
            }

            ImageView img = view2.findViewById(R.id.imgIcono);
            Picasso.get().load(mComicsRellenos.get(i).getImage() + "." + mComicsRellenos.get(i).getExtensionImg())
                    .into(img);

            TextView txtTitle = view2.findViewById(R.id.txtTitle);
            txtTitle.setText(mComicsRellenos.get(i).getTittle());

            TextView txtDescription = view2.findViewById(R.id.txtDescription);
            txtDescription.setText(mComicsRellenos.get(i).getDescription());

            TextView txtPrice = view2.findViewById(R.id.txtPrice);
            txtPrice.setText(mComicsRellenos.get(i).getPrice() + "€");

            return view2;
        }
    }
}
