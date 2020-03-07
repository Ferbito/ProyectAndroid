package com.example.comicsclub;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FiltroTiendas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro_tiendas);

        //Toast.makeText(FiltroTiendas.this, "WELCOME TO FILTERS", Toast.LENGTH_SHORT).show();

        Spinner spDistancia = findViewById(R.id.spDistancia);
        spDistancia.setVisibility(View.VISIBLE);
        ArrayAdapter<CharSequence> adapterDistancia = ArrayAdapter.createFromResource(FiltroTiendas.this, R.array.sp_distancias,
                android.R.layout.simple_spinner_item);
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
    }
}
