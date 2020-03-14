package com.example.comicsclub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class TiendasCercanas extends AppCompatActivity {

    private ArrayList<TiendasParse.Tiendas> mTiendasCercanas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiendas_cercanas);

        Intent in=getIntent();
        if(in!=null){
            mTiendasCercanas = in.getParcelableArrayListExtra("ARRAY_CERCANO");
            Log.e("MENSAJE", String.valueOf(mTiendasCercanas.size()));
        }
    }
}
