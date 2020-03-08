package com.example.comicsclub;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;



public interface TiendasInterface {


        @Headers("Accept: application/geo+json")
        @GET("api/place/nearbysearch/json?key=AIzaSyAn93plb2763qJNDzPIzNM0hwKJ1fDYvhk")
        Call<TiendasResponse> getTiendas(@Query("type") String type, @Query("location") String location, @Query("radius") int radius, @Query("rating") double rating,@Query("user_ratings_total") int user_ratings_total


        );
    }

