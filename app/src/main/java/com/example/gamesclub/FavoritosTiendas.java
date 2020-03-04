package com.example.gamesclub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class FavoritosTiendas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos_tiendas);

        Toast.makeText(FavoritosTiendas.this, "WELCOME TO FAVORITES", Toast.LENGTH_SHORT);
    }
}
