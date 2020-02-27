package com.example.gamesclub;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TiendasResponse {

    @SerializedName("results")
    @Expose
        public final List<Tiendas> results=null;

    public static class Tiendas {


        public final String name = null;

        public Float distance = null;
        public final  MyGeometry geometry=null;
        public final  String icon=null;



        public class MyGeometry {

            public final MyLocation location = null;
            public class MyLocation {
                public final Double lat=null;
                public final Double lng=null;
            }
        }
    }
}