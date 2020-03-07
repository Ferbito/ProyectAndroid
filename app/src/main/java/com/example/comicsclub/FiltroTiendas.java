package com.example.comicsclub;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FiltroTiendas extends AppCompatActivity {
    private int mRadius ;
    private List<TiendasResponse.Tiendas> mFiltros=new ArrayList<>();
    private double mRating;
    private Spinner spDistancia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro_tiendas);

        //Toast.makeText(FiltroTiendas.this, "WELCOME TO FILTERS", Toast.LENGTH_SHORT).show();

        spDistancia = findViewById(R.id.spDistancia);
        spDistancia.setVisibility(View.VISIBLE);
        final ArrayAdapter<CharSequence> adapterDistancia = ArrayAdapter.createFromResource(FiltroTiendas.this, R.array.sp_distancias,
               R.layout.spinner_item);
        adapterDistancia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDistancia.setAdapter(adapterDistancia);
        spDistancia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = adapterView.getItemAtPosition(i).toString();

                Toast.makeText(FiltroTiendas.this, text, Toast.LENGTH_SHORT).show();
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
                String text = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(FiltroTiendas.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
