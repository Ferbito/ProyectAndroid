package com.example.comicsclub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class FiltrosComics extends AppCompatActivity {

    private String mPrice;
    private Spinner spPrecio;
    private int mPostPrice=0;
    private ObjectFiltroComic filtroleidocomic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filro_comics);

        leerDatosSP();

        spPrecio = findViewById(R.id.spPrecio);
        spPrecio.setVisibility(View.VISIBLE);
        final ArrayAdapter<CharSequence> adapterPrecios = ArrayAdapter.createFromResource(FiltrosComics.this, R.array.sp_precio,
                R.layout.spinner_item);
        adapterPrecios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrecio.setAdapter(adapterPrecios);
        spPrecio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPrice = adapterView.getItemAtPosition(i).toString();
                mPostPrice=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Button btn_savefiltros=findViewById(R.id.btn_FILTROSCOMICS);
        btn_savefiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectFiltroComic establecerFiltro = new ObjectFiltroComic(mPrice,mPostPrice);
                guardarDatoSP(establecerFiltro);
                finish();
            }
        });
        if(filtroleidocomic !=null){
            if(filtroleidocomic.getPost()!=0){
                spPrecio.setSelection(filtroleidocomic.getPost());
            }
        }

    }

    private void guardarDatoSP(ObjectFiltroComic objetcFiltro){
        SharedPreferences mPrefs = getSharedPreferences(HelperGlobal.KEYARRAYFILTROSPREFERENCESCOMICS,MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(objetcFiltro);
        prefsEditor.putString(HelperGlobal.ARRAYCOMICSFILTROS, json);
        prefsEditor.commit();
    }

    private void leerDatosSP(){
        SharedPreferences mPrefs = getSharedPreferences(HelperGlobal.KEYARRAYFILTROSPREFERENCESCOMICS,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(HelperGlobal.ARRAYCOMICSFILTROS, "");
        ObjectFiltroComic jsonFiltro= gson.fromJson(json, ObjectFiltroComic.class);
        if(jsonFiltro!=null){
            filtroleidocomic = jsonFiltro;
        }
    }

}

