package com.example.comicsclub;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity   {
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter mSliderAdapter;
    private TextView[] mDots;
    private Button mButonGame;
    private Button mButonBuscar;
    private Typeface script;
    private String []imageUrls=new String[]{HelperGlobal.IMAGEURL1,HelperGlobal.IMAGEURL2,HelperGlobal.IMAGEURL3, HelperGlobal.IMAGEURL4};

    private LocationManager mLocManager;
    private final String TAG = getClass().getSimpleName();
    private Location mCurrentLocation;
    final static String LAT="VALOR_RETURN3";
    final static String LONG="VALOR_RETURN2";
    final static String TITLE="VALOR_RETURN1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlideViewPager=(ViewPager)findViewById(R.id.slideViewPager);


        mSliderAdapter=new SliderAdapter(this,imageUrls);

        mSlideViewPager.setAdapter(mSliderAdapter);


        mSlideViewPager.addOnPageChangeListener(viewListener);


        Button btnMaps=findViewById(R.id.btn_MAPS);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(MainActivity.this, ListTiendas.class);
                startActivity(intent);
            }
        });

    }

    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
           // addDotsIndicator(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };


}
