package com.example.comicsclub;

import android.os.Parcelable;

public class ObjectFiltroComic  {

        private String price;
        private int post;

    public ObjectFiltroComic(String price, int post) {
        this.price = price;
        this.post = post;
    }

    public String getPrice() {
        return price;
    }

    public int getPost() {
        return post;
    }
}
