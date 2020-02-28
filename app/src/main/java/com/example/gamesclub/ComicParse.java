package com.example.gamesclub;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ComicParse {
    private final String TAG =getClass().getSimpleName();

    public class comic{
        private String tittle;
        private String description;
        private String image;
        private String extensionImg;
        private String price;

        public comic(String tittle, String description, String image, String extensionImg, String price) {
            this.tittle = tittle;
            this.description = description;
            this.image = image;
            this.extensionImg = extensionImg;
            this.price = price;
        }

        public String getTittle() {
            return tittle;
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

        public String getPrice() {
            return price;
        }
    }

    public ArrayList<comic> parseComics (String content){
        ArrayList<comic> lComic = new ArrayList<comic>();

        JSONArray array;
        JSONObject json = null;
        JSONObject data = null;

        try {
            json = new JSONObject(content);
            data = json.getJSONObject("data");
            array = data.getJSONArray("results");

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

    private comic parseComic(JSONObject jsonData) throws JSONException {
        String tittle = "";
        String description = "";
        String image = "";
        String extensionImg = "";
        String price = "";

        try{
            if(jsonData.has("title"))
                tittle = jsonData.getString("title");
            if(jsonData.has("description"))
                description = jsonData.getString("description");
            if(jsonData.has("images")){
                JSONArray images = jsonData.getJSONArray("images");
                for(int i = 0; i < images.length();i++) {
                    JSONObject node = images.getJSONObject(i);
                    if(node.has("path"))
                        image = node.getString("path");
                    if(node.has("extension"))
                        extensionImg = node.getString("extension");
                }
            }
            if(jsonData.has("prices")){
                JSONArray images = jsonData.getJSONArray("prices");
                for(int i = 0; i < images.length();i++) {
                    JSONObject node = images.getJSONObject(i);
                    if(node.has("price"))
                        price = node.getString("price");
                }
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }
        comic comic = new comic(tittle,description,image,extensionImg,price);
        return comic;
    }
}
