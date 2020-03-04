package com.example.gamesclub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class FiltroTiendas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro_tiendas);

        Toast.makeText(FiltroTiendas.this, "WELCOME", Toast.LENGTH_SHORT);
    }
}
