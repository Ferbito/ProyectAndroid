package com.example.comicsclub;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    private  ArrayList<ComicParse.comic> mComicsFiltrados;
    private Boolean relleno;
    private ListView mLv;
    private ProgressDialog mPd;
    private final int CODINFILTROCOMIC = 0;
    private ObjectFiltroComic mFiltroComic = null;
    private  BaseAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_comics);

        mPd = new ProgressDialog(ListComics.this);
        mPd.setProgressStyle(Spinner.ACCESSIBILITY_LIVE_REGION_ASSERTIVE);
        mPd.setTitle(HelperGlobal.PROGRESSTITTLE);
        mPd.setMessage(HelperGlobal.PROGRESSMESSAGE);
        mPd.setProgress(100);
        mPd.show();
        mLv = findViewById(R.id.lista);

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
            Toast.makeText(ListComics.this, HelperGlobal.VUELTAATICITY, Toast.LENGTH_SHORT).show();
            actualizar();
        }
    }
    private void actualizar(){
        leerDatosSPFiltro();

        mComicsFiltrados = new ArrayList<>();
        for(int i = 0;i<mComicsRellenos.size();i++){
            mComicsFiltrados.add(mComicsRellenos.get(i));
        }

        if(mFiltroComic!=null){
            String datosPrecio[] = mFiltroComic.getPrice().split(" ");
            for (int i = 0; i < mComicsFiltrados.size(); i++) {
                if(Double.parseDouble(mComicsFiltrados.get(i).getPrice()) >  Double.parseDouble(datosPrecio[0])){
                    mComicsFiltrados.remove(i);
                    i--;
                }
            }
        }

        if(mAdapter==null){
            mAdapter = new ComicsAdapter();
            mLv.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }
    }

    private void leerDatosSPFiltro(){
        SharedPreferences mPrefs = getSharedPreferences(HelperGlobal.KEYARRAYFILTROSPREFERENCESCOMICS,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(HelperGlobal.ARRAYCOMICSFILTROS, "");
        ObjectFiltroComic jsonFiltro= gson.fromJson(json, ObjectFiltroComic.class);
        if(jsonFiltro!=null){
            mFiltroComic = jsonFiltro;

        }
    }
    private void cargarComics(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = HelperGlobal.URLCOMICS;
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
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    public class ComicsAdapter extends BaseAdapter {

        Integer i = 0;

        @Override
        public int getCount() {
            return mComicsFiltrados.size();
        }

        @Override
        public Object getItem(int i) {
            return mComicsFiltrados.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = null;

            if (view == view2) {
                LayoutInflater inflater = (LayoutInflater) ListComics.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view2 = inflater.inflate(R.layout.list_comicsview, null);
            } else {
                view2 = view;
            }

            ImageView img = view2.findViewById(R.id.imgIcono);
            Picasso.get().load(mComicsFiltrados.get(i).getImage() + "." + mComicsFiltrados.get(i).getExtensionImg())
                    .into(img);

            TextView txtTitle = view2.findViewById(R.id.txtTitle);
            txtTitle.setText(mComicsFiltrados.get(i).getTittle());

            TextView txtDescription = view2.findViewById(R.id.txtDescription);
            txtDescription.setText(mComicsFiltrados.get(i).getDescription());

            TextView txtPrice = view2.findViewById(R.id.txtPrice);
            txtPrice.setText(mComicsFiltrados.get(i).getPrice() + "€");

            return view2;
        }
    }
}
