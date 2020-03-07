package com.example.comicsclub;

public class ObjetcFiltro {
    private String distance;
    private int posDistance;
    private String rating;
    private int posRating;
    private boolean book_store;
    private boolean shopping_mall;

    public ObjetcFiltro(String distance, int posDistance, String rating, int posRating, boolean book_store, boolean shopping_mall) {
        this.distance = distance;
        this.posDistance = posDistance;
        this.rating = rating;
        this.posRating = posRating;
        this.book_store = book_store;
        this.shopping_mall = shopping_mall;
    }

    public String getDistance() {
        return distance;
    }

    public int getPosDistance() {
        return posDistance;
    }

    public String getRating() {
        return rating;
    }

    public int getPosRating() {
        return posRating;
    }

    public boolean isBook_store() {
        return book_store;
    }

    public boolean isShopping_mall() {
        return shopping_mall;
    }
}
