package com.example.gamesclub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.internal.mc;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListComics extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private ArrayList<ComicParse.comic> mComics = new ArrayList<>();
    private  ArrayList<ComicParse.comic> mComicsRellenos = new ArrayList<>();
    private Boolean relleno;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_comics);

        lv = findViewById(R.id.lista);

        cargarComics();
    }

    private void cargarComics(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://gateway.marvel.com/v1/public/comics?ts=9&apikey=7a18af213a25abcf54a952288670033d&hash=0bd40620c9f9e68a70795b084480daed";
        //final Boolean relleno;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ComicParse comicParse = new ComicParse();
                        mComics = comicParse.parseComics(response);

                        for (int i = 0; i< mComics.size(); i++){
                            relleno = true;
                            if(mComics.get(i).getDescription() == "" || mComics.get(i).getImage() == "" ||
                            mComics.get(i).getPrice().contentEquals("0") || mComics.get(i).getDescription() == "null"){

                            }else{
                                mComicsRellenos.add(mComics.get(i));
                                Log.d("datos", mComics.get(i).getImage() + "." + mComics.get(i).getExtensionImg());
                            }
                        }
                        BaseAdapter adapter = new ComicsAdapter();
                        lv.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    public class ComicsAdapter extends BaseAdapter {

        Integer i = 0;


        @Override
        public int getCount() {
            return mComicsRellenos.size();
        }

        @Override
        public Object getItem(int i) {
            return mComicsRellenos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.d(TAG, "Position " + String.valueOf(i));
            View view2 = null;

            if (view == view2) {
                LayoutInflater inflater = (LayoutInflater) ListComics.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view2 = inflater.inflate(R.layout.list_comicsview, null);
            } else {
                view2 = view;
            }

            ImageView img = view2.findViewById(R.id.imgIcono);
            Picasso.get().load(mComicsRellenos.get(i).getImage() + "/portrait_small." + mComicsRellenos.get(i).getExtensionImg())
                    .into(img);

            TextView txtTitle = view2.findViewById(R.id.txtTitle);
            txtTitle.setText(mComicsRellenos.get(i).getTittle());

            TextView txtDescription = view2.findViewById(R.id.txtDescription);
            txtDescription.setText(mComicsRellenos.get(i).getDescription());

            TextView txtPrice = view2.findViewById(R.id.txtPrice);
            txtPrice.setText(mComicsRellenos.get(i).getPrice() + "€");

            return view2;
        }
    }
}
