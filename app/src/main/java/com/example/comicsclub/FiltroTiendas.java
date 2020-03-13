package com.example.comicsclub;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class FiltroTiendas extends AppCompatActivity {
    private int mRadius ;
    private String mRating;
    private Spinner spDistancia;
    private int mPosDistance = 0;
    private int mPosRating = 0;
    private String mDistance = "500";
    private ObjetcFiltroTienda filtroLeido = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro_tiendas);

        //Toast.makeText(FiltroTiendas.this, "WELCOME TO FILTERS", Toast.LENGTH_SHORT).show();
        leerDatosSP();

        spDistancia = findViewById(R.id.spDistancia);
        spDistancia.setVisibility(View.VISIBLE);
        final ArrayAdapter<CharSequence> adapterDistancia = ArrayAdapter.createFromResource(FiltroTiendas.this, R.array.sp_distancias,
               R.layout.spinner_item);
        adapterDistancia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistancia.setAdapter(adapterDistancia);
        spDistancia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mDistance = adapterView.getItemAtPosition(i).toString();
                mPosDistance = i;
                Toast.makeText(FiltroTiendas.this, mDistance, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Spinner spValoracion = findViewById(R.id.spValoración);
        spValoracion.setVisibility(View.VISIBLE);
        ArrayAdapter<CharSequence> adapterValoracion = ArrayAdapter.createFromResource(FiltroTiendas.this, R.array.sp_valoracion,
                R.layout.spinner_item);
        adapterValoracion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spValoracion.setAdapter(adapterValoracion);
        spValoracion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mRating = adapterView.getItemAtPosition(i).toString();
                mPosRating = i;
                Toast.makeText(FiltroTiendas.this, mRating, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final Switch swCentrocomercial=findViewById(R.id.swcentrocomercial);
        final Switch swLibreria=findViewById(R.id.swlibreria);
        swLibreria.setChecked(true);
        swCentrocomercial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swLibreria.setChecked(false);
                } else {
                    swLibreria.setChecked(true);
                }
            }
        });

        swLibreria.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swCentrocomercial.setChecked(false);
                } else {
                    swCentrocomercial.setChecked(true);
                }
            }
        });

        Button btn_savefiltros=findViewById(R.id.btn_FILTROS);
        btn_savefiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean tienda;
                if(swLibreria.isChecked()){
                    tienda = true;
                }else{
                    tienda=false;
                }
                ObjetcFiltroTienda establecerFiltro = new ObjetcFiltroTienda(mDistance, mPosDistance, mRating, mPosRating, tienda);
                guardarDatoSP(establecerFiltro);
                finish();
            }
        });

        if(filtroLeido!=null){
            if(filtroLeido.getPosDistance()!=0){
                spDistancia.setSelection(filtroLeido.getPosDistance());
            }
            if(filtroLeido.getPosRating()!=0){
                spValoracion.setSelection(filtroLeido.getPosRating());
            }
            if(filtroLeido.isBook_store()){
                swLibreria.setChecked(true);
                swCentrocomercial.setChecked(false);
            }else{
                swLibreria.setChecked(false);
                swCentrocomercial.setChecked(true);
            }
        }


    }

    private void guardarDatoSP(ObjetcFiltroTienda objetcFiltro){
        SharedPreferences mPrefs = getSharedPreferences(HelperGlobal.KEYARRAYFILTROSPREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(objetcFiltro);
        prefsEditor.putString(HelperGlobal.ARRAYTIENDASFILTROS, json);
        prefsEditor.commit();
    }

    private void leerDatosSP(){
        SharedPreferences mPrefs = getSharedPreferences(HelperGlobal.KEYARRAYFILTROSPREFERENCES,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(HelperGlobal.ARRAYTIENDASFILTROS, "");
        //Type founderListType = new TypeToken<ArrayList<TiendasResponse.Tiendas>>(){}.getType();
        //ArrayList<TiendasResponse.Tiendas> restoreArray = gson.fromJson(json, founderListType);
        ObjetcFiltroTienda jsonFiltro= gson.fromJson(json, ObjetcFiltroTienda.class);
        //Log.d("PERSIST", String.valueOf(restoreArray.size()));
        if(jsonFiltro!=null){
            filtroLeido = jsonFiltro;
            Log.d("PERSIST2", String.valueOf(filtroLeido.getDistance()));
        }
    }

}
