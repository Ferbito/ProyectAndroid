package com.example.gamesclub;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HelperParser {
    private final String TAG =getClass().getSimpleName();

    public class comic{
        private String name;
        private String releaseDate;
        private String description;
        private String image;

        public comic(String name, String releaseDate, String description, String image) {
            this.name = name;
            this.releaseDate = releaseDate;
            this.description = description;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public String getDescription() {
            return description;
        }

        public String getImage() {
            return image;
        }
    }

    public ArrayList<comic> parseComics (String content){
        ArrayList<comic> lComic = new ArrayList<comic>();

        JSONArray array;
        JSONObject json = null;

        try {
            json = new JSONObject(content);
            array = json.getJSONArray("results");

            for(int i = 0; i < array.length();i++){
                JSONObject node = array.getJSONObject(i);
                comic pnode = parseComic (node);

                lComic.add(pnode);
            }
            return lComic;
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    private comic parseComic(JSONObject jsonData){
        return null;
    }
}
