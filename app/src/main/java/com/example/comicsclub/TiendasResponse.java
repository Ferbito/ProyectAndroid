package com.example.comicsclub;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TiendasResponse {

    @SerializedName("results")
    @Expose
        public  List<Tiendas> results=null;

    public static class Tiendas {


        private   String name = null;
        private Float distance = null;
        private   MyGeometry geometry=null;
        private   String icon=null;
        private  Double rating=null;

        public Double getRating() {
            return rating;
        }

        public void setDistance(Float distance) {
            this.distance = distance;
        }

        public String getName() {
            return name;
        }
        public Float getDistance() {
            return distance;
        }

        public MyGeometry getGeometry() {
            return geometry;
        }

        public String getIcon() {
            return icon;
        }

        public class MyGeometry {

            private  MyLocation location = null;

            public MyLocation getLocation() {
                return location;
            }

            public class MyLocation {
                private  Double lat=null;
                private  Double lng=null;

                public Double getLat() {
                    return lat;
                }

                public Double getLng() {
                    return lng;
                }
            }
        }
    }
}