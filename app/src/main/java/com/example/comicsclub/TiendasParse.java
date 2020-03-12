package com.example.comicsclub;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TiendasParse {
    private final String TAG = getClass().getSimpleName();

    public class Tiendas implements Parcelable {
        private String name = null;
        private Double lat = 0.0;
        private Double lng = 0.0;
        private Float distance = null;
        private String icon=null;
        private Double rating=0.0;
        private int user_ratings_total=0;

        public Tiendas(String name, Double lat, Double lng, Float distance, String icon, Double rating, int user_ratings_total) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
            this.distance = distance;
            this.icon = icon;
            this.rating = rating;
            this.user_ratings_total = user_ratings_total;
        }

        public String getName() {
            return name;
        }

        public Double getLat() {
            return lat;
        }

        public Double getLng() {
            return lng;
        }

        public Float getDistance() {
            return distance;
        }

        public String getIcon() {
            return icon;
        }

        public Double getRating() {
            return rating;
        }

        public int getUser_ratings_total() {
            return user_ratings_total;
        }

        public void setDistance(Float distance) {
            this.distance = distance;
        }

        public Tiendas(Parcel in){
            name = in.readString();
            lat = in.readDouble();
            lng = in.readDouble();
            distance = in.readFloat();
            icon = in.readString();
            rating = in.readDouble();
            user_ratings_total = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(name);
            parcel.writeDouble(lat);
            parcel.writeDouble(lng);
            parcel.writeFloat(distance);
            parcel.writeString(icon);
            parcel.writeDouble(rating);
            parcel.writeInt(user_ratings_total);
        }

    public final Parcelable.Creator<Tiendas> CREATOR =
            new Creator<Tiendas>() {
                @Override
                public Tiendas createFromParcel(Parcel parcel) {
                    return new Tiendas(parcel);
                }

                @Override
                public Tiendas[] newArray(int i) {
                    return new Tiendas[i];
                }
            };
    }

    public ArrayList<Tiendas> parsePlaces(String content){
        ArrayList<Tiendas> lplaces = new ArrayList<>();

        JSONArray array;
        JSONObject json = null;

        try {
            json = new JSONObject(content);
            array = json.getJSONArray("results");

            for(int i = 0; i < array.length();i++){
                JSONObject node = array.getJSONObject(i);
                Tiendas pnode = parsePlace (node);

                lplaces.add(pnode);
                Log.d("HOLAPARSE", lplaces.get(i).getName());
            }
            return lplaces;
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    private Tiendas parsePlace(JSONObject jsonData){
        String name = "", urlIcon = "";
        Double latitude = 0.0, longitude= 0.0, rating= 0.0;
        Integer numberUserRating = 0;

        try {
            if(jsonData.has("name"))
                name = jsonData.getString("name");
            if(jsonData.has("icon"))
                urlIcon = jsonData.getString("icon");
            if(jsonData.has("rating"))
                rating = jsonData.getDouble("rating");
            if(jsonData.has("user_ratings_total"))
                numberUserRating = jsonData.getInt("user_ratings_total");
            if(jsonData.has("geometry")){
                JSONObject geo = jsonData.getJSONObject("geometry");
                if(geo.has("location")){
                    JSONObject loc = geo.getJSONObject("location");
                    if(loc.has("lat"))
                        latitude = loc.getDouble("lat");
                    if(loc.has("lng"))
                        longitude = loc.getDouble("lng");
                }
            }
            Tiendas nuevaTienda = new Tiendas(name,latitude,longitude,null,urlIcon,rating,numberUserRating);
            return nuevaTienda;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
