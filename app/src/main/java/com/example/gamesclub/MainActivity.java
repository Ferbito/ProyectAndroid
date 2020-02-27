package com.example.gamesclub;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
    private String []imageUrls=new String[]{"https://img.icons8.com/bubbles/2x/iron-man.png","https://synth.agency/wp-content/uploads/2019/07/Marvel-Hulk-1024x819.png","https://cdn4.iconfinder.com/data/icons/superhero-3/500/Superhero-01-512.png","https://icons-for-free.com/iconfiles/png/512/super+thor+wings+icon-1320166699905266736.png" };

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
        //AQUI EMPIEZA TODO
       // https://api.rawg.io/api/games?
        mSlideViewPager=(ViewPager)findViewById(R.id.slideViewPager);
        mDotLayout=(LinearLayout) findViewById(R.id.dotsLayout);

        mSliderAdapter=new SliderAdapter(this,imageUrls);

        mSlideViewPager.setAdapter(mSliderAdapter);
        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        Button btnComics =findViewById(R.id.btn_COMIC);
        btnComics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("PRUEBA", "PULSADO");

            }
        });
        Button btnMaps=findViewById(R.id.btn_MAPS);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PRUEBA", "PULSADO");
                Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });

    }
    public void addDotsIndicator(int position){

        mDots=new TextView[4];
        mDotLayout.removeAllViews();

        for (int i=0;i<mDots.length;i++){
            mDots[i]=new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(50);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            mDots[i].setTypeface(script);
            mDotLayout.addView(mDots[i]);

        }
        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.red));

        }

    }
    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };






}
