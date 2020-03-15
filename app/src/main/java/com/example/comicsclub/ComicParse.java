package com.example.comicsclub;

import android.util.Log;

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
            data = json.getJSONObject(HelperGlobal.JSONOBJECTDATA);
            array = data.getJSONArray(HelperGlobal.JSONARRAY);

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
            if(jsonData.has(HelperGlobal.JSONDATATITLE))
                tittle = jsonData.getString(HelperGlobal.JSONDATATITLE);
            if(jsonData.has(HelperGlobal.JSONDATADESCRIPTION))
                description = jsonData.getString(HelperGlobal.JSONDATADESCRIPTION);
            if(jsonData.has(HelperGlobal.JSONDATAIMAGES)){
                JSONArray images = jsonData.getJSONArray(HelperGlobal.JSONDATAIMAGES);
                for(int i = 0; i < images.length();i++) {
                    JSONObject node = images.getJSONObject(i);
                    if(node.has(HelperGlobal.JSONOBJECTPATH))
                        image = node.getString(HelperGlobal.JSONOBJECTPATH);
                    if(node.has(HelperGlobal.JSONOBJECTEXTENSION))
                        extensionImg = node.getString(HelperGlobal.JSONOBJECTEXTENSION);
                }
            }
            if(jsonData.has(HelperGlobal.JSONDATAPRICES)){
                JSONArray images = jsonData.getJSONArray(HelperGlobal.JSONDATAPRICES);
                for(int i = 0; i < images.length();i++) {
                    JSONObject node = images.getJSONObject(i);
                    if(node.has(HelperGlobal.JSONOBJECTPRICE))
                        price = node.getString(HelperGlobal.JSONOBJECTPRICE);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        comic comic = new comic(tittle,description,image,extensionImg,price);
        return comic;
    }
}
