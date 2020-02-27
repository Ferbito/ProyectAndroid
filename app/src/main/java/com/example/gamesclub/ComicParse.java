package com.example.gamesclub;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ComicParse {
    private final String TAG =getClass().getSimpleName();

    public class comic{
        private String tittle;
        private String releaseDate;
        private String description;
        private String image;
        private String extensionImg;

        public comic(String tittle, String releaseDate, String description, String image, String extensionImg) {
            this.tittle = tittle;
            this.releaseDate = releaseDate;
            this.description = description;
            this.image = image;
            this.extensionImg = extensionImg;
        }

        public String getTittle() {
            return tittle;
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

        public String getExtensionImg() {
            return extensionImg;
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
        String tittle = "";
        String releaseDate = "";
        String description = "";
        String image = "";
        String extensionImg = "";

        try{
            if(jsonData.has("title"))
                tittle = jsonData.getString("tittle");
            if(jsonData.has("textObjects")){
                JSONObject tobj =jsonData.getJSONObject("textObjects");
                if(tobj.has("text"))
                    description = tobj.getString("text");
            }
            if(jsonData.has("images")){
                JSONObject images = jsonData.getJSONObject(("images"));
                if(images.has("path")){
                    image = images.getString("path");
                    extensionImg = images.getString("extension");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
