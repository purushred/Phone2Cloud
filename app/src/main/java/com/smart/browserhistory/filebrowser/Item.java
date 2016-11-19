package com.smart.browserhistory.filebrowser;

/**
 * Created by Purushotham on 20-11-2014.
 */
public class Item implements Comparable<Item> {
    private String name;
    private String data;
    private String date;
    private String path;
    private String image;

    public Item(String name, String data, String date, String path, String img) {
        this.name = name;
        this.data = data;
        this.date = date;
        this.path = path;
        this.image = img;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public String getDate() {
        return date;
    }

    public String getPath() {
        return path;
    }

    public String getImage() {
        return image;
    }

    public int compareTo(Item o) {
        if (this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}