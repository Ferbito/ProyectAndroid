package com.example.gamesclub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
private ViewPager mSlideViewPager;
private LinearLayout mDotLayout;
private SliderAdapter mSliderAdapter;
private TextView[] mDots;
private Button mButonGame;
private Button mButonBuscar;
private Typeface script;
private String []imageUrls=new String[]{"https://img.icons8.com/bubbles/2x/iron-man.png","https://synth.agency/wp-content/uploads/2019/07/Marvel-Hulk-1024x819.png","https://cdn4.iconfinder.com/data/icons/superhero-3/500/Superhero-01-512.png" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    }
    public void addDotsIndicator(int position){

        mDots=new TextView[3];
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
