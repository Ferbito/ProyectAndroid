package com.example.gamesclub;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    String[] imageUrls;
    public SliderAdapter(Context context,String[] imageUrls){

        this.context=context;
        this.imageUrls=imageUrls;
    }
    public String[] slider_headings={
            "IRON MAN",
            "HULK",
            "CAPITAN AMERICA"
    };

    //final String[] urls = {"https://img.icons8.com/bubbles/2x/iron-man.png","https://synth.agency/wp-content/uploads/2019/07/Marvel-Hulk-1024x819.png","https://i.pinimg.com/736x/06/51/1d/06511d08fcfa420f568c9ddfca2cee53.jpg" };

   /* public int[] slide_images{
        int 1=Picasso.get().load(urls[0]).error(R.drawable.ic_launcher_background).into(imageView1);
                Picasso.get().load(urls[1]).error(R.drawable.ic_launcher_background).into(imageView1);
                Picasso.get().load(urls[2]).error(R.drawable.ic_launcher_background).into(imageView1);}
    */
   public String[]slide_descs={"Iron Man es un superhéroe ficticio que aparece en los cómics estadounidenses publicados por Marvel Comics. El personaje fue cocreado por el escritor y editor Stan Lee, desarrollado por el guionista Larry Lieber y diseñado por los artistas Don Heck y Jack Kirby.","Hulk es un personaje ficticio, que aparece en los cómics estadounidenses publicados por la editorial Marvel Comics. El personaje fue creado por los escritores Stan Lee y Jack Kirby siendo su primera aparición en The Incredible Hulk #1 publicado en mayo de 1962","Capitán América cuyo nombre real es Steven \"Steve\" Rogers, es un superhéroe ficticio que aparece en los cómics estadounidenses publicados por Marvel Comics."};
    @Override
    public int getCount() {
       return slider_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==(View) object;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view =layoutInflater.inflate(R.layout.slide_layout,container,false);
        ImageView slideImageView=(ImageView)view.findViewById(R.id.slide_image);
        TextView slideHeading=(TextView)view.findViewById(R.id.slide_heading);
        TextView slideDescription=(TextView)view.findViewById(R.id.slide_desc);
        Picasso.get().load(imageUrls[position]).error(R.drawable.ic_launcher_background).into(slideImageView);

        slideHeading.setText(slider_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);

     return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);
    }
}
