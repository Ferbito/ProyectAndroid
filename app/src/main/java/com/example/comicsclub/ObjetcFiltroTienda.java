package com.example.comicsclub;

public class ObjetcFiltroTienda {
    private String distance;
    private int posDistance;
    private String rating;
    private int posRating;
    private boolean book_store;

    public ObjetcFiltroTienda(String distance, int posDistance, String rating, int posRating, boolean book_store) {
        this.distance = distance;
        this.posDistance = posDistance;
        this.rating = rating;
        this.posRating = posRating;
        this.book_store = book_store;
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

}
