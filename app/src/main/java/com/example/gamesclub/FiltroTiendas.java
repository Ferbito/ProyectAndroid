package com.example.gamesclub;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FiltroTiendas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro_tiendas);

        Toast.makeText(FiltroTiendas.this, "WELCOME TO FILTERS", Toast.LENGTH_SHORT).show();
    }
}
