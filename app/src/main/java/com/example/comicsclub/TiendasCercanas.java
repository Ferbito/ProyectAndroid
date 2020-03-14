package com.example.comicsclub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TiendasCercanas extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private ArrayList<TiendasParse.Tiendas> mTiendasCercanas;
    private MyAdapter mAdapter = null;
    private ListView mLv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiendas_cercanas);
        mLv = findViewById(R.id.list_notify);
        Intent in=getIntent();
        if(in!=null){
            mTiendasCercanas = in.getParcelableArrayListExtra(HelperGlobal.NAMEPARCELABLEARRAY);

            if(mAdapter==null) {
                mAdapter = new MyAdapter(TiendasCercanas.this);
                mLv.setAdapter(mAdapter);
            }else{
                mAdapter.notifyDataSetChanged();
            }


        }
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i3=new Intent(TiendasCercanas.this,ListComics.class);
                startActivity(i3);
            }
        });
        mLv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu,
                                            View view,
                                            ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0, 1, 0, HelperGlobal.LISTVIEWCONTEXT);

            }
        });

    }
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {

            case 1:
                Toast.makeText(TiendasCercanas.this,
                        HelperGlobal.MAPSTOAST, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(TiendasCercanas.this, MapsActivity.class);
                intent.putExtra(HelperGlobal.TITLEINPUTTIENDASCERCANAS, mTiendasCercanas.get(info.position).getName());
                intent.putExtra(HelperGlobal.LATINPUTTIENDASCERCANAS, mTiendasCercanas.get(info.position).getLat());
                intent.putExtra(HelperGlobal.LONINPUTTIENDASCERCANAS, mTiendasCercanas.get(info.position).getLng());
                startActivity(intent);

                mAdapter.notifyDataSetChanged();
                break;

        }
        return true;
    }

    public class MyAdapter extends BaseAdapter {

        private Context mContext;


        public MyAdapter(Context context) {
            this.mContext = context;

        }

        @Override
        public int getCount() {
            return mTiendasCercanas.size();
        }

        @Override
        public Object getItem(int i) {
            return mTiendasCercanas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View myview = null;

            if (myview == null) {

                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                myview = inflater.inflate(R.layout.final_listtiendas, null);
            } else
                myview = view;

            ImageView iv = myview.findViewById(R.id.imageIcon);
            Picasso.get().load(mTiendasCercanas.get(i).getIcon()).into(iv);

            TextView tTitle = myview.findViewById(R.id.title);
            tTitle.setText(mTiendasCercanas.get(i).getName());

            TextView tRating = myview.findViewById(R.id.rating);
            tRating.setText("Valoración: "+String.valueOf(mTiendasCercanas.get(i).getRating())+" "+"["+String.valueOf(mTiendasCercanas.get(i).getUser_ratings_total())+"]");


            TextView tDistance = myview.findViewById(R.id.distance);
            tDistance.setText("Se encuentra a " + String.valueOf(Math.round(mTiendasCercanas.get(i).getDistance())) + " metros.");

            return myview;
        }
    }
}
