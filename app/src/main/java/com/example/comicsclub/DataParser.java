package com.example.comicsclub;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser
{
    private HashMap<String, String> getSingleNearbyPlace(JSONObject googlePlaceJSON)
    {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String NameOfPlace = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try
        {
            if (!googlePlaceJSON.isNull(HelperGlobal.GOOGLEPLACESNAME))
            {
                NameOfPlace = googlePlaceJSON.getString(HelperGlobal.GOOGLEPLACESNAME);
            }
            if (!googlePlaceJSON.isNull(HelperGlobal.GOOGLENEARBYPLACEVICITINY))
            {
                vicinity = googlePlaceJSON.getString(HelperGlobal.GOOGLENEARBYPLACEVICITINY);
            }
            latitude = googlePlaceJSON.getJSONObject(HelperGlobal.GOOGLEPLACESGEOMETRY).getJSONObject(HelperGlobal.GOOGLEPLACESLOCATION).getString(HelperGlobal.GOOGLENEARBYPLACELAT);
            longitude = googlePlaceJSON.getJSONObject(HelperGlobal.GOOGLEPLACESGEOMETRY).getJSONObject(HelperGlobal.GOOGLEPLACESLOCATION).getString(HelperGlobal.GOOGLENEARBYPLACELNG);
            reference = googlePlaceJSON.getString(HelperGlobal.GOOGLEPLACESREFERENCE);

            googlePlaceMap.put(HelperGlobal.GOOGLENEARBYPLACENAME, NameOfPlace);
            googlePlaceMap.put(HelperGlobal.GOOGLENEARBYPLACEVICITINY, vicinity);
            googlePlaceMap.put(HelperGlobal.GOOGLENEARBYPLACELAT, latitude);
            googlePlaceMap.put(HelperGlobal.GOOGLENEARBYPLACELNG, longitude);
            googlePlaceMap.put(HelperGlobal.GOOGLEPLACESREFERENCE, reference);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }



    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray)
    {
        int counter = jsonArray.length();

        List<HashMap<String, String>> NearbyPlacesList = new ArrayList<>();

        HashMap<String, String> NearbyPlaceMap = null;

        for (int i=0; i<counter; i++)
        {
            try
            {
                NearbyPlaceMap = getSingleNearbyPlace( (JSONObject) jsonArray.get(i) );
                NearbyPlacesList.add(NearbyPlaceMap);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return NearbyPlacesList;
    }



    public List<HashMap<String, String>> parse(String jSONdata)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try
        {
            jsonObject = new JSONObject(jSONdata);
            jsonArray = jsonObject.getJSONArray("results");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArray);
    }
}
