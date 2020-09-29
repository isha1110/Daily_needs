package com.skinfotech.dailyneeds.models;

public class Item {
    private String name;
    private int image;
    private final int id;

    public Item(String name, int id,int image) {
        this.name = name;
        this.id = id;
        this.image=image;
    }

    public int getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
