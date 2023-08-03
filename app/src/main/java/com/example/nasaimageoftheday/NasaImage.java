package com.example.nasaimageoftheday;

import android.graphics.Bitmap;

public class NasaImage {
    private String date;
    private String url;
    private String hdurl;
    private Bitmap bitmap;

    private int id;

    public  NasaImage() {

    }
    public NasaImage(String date, String url, String hdurl) {
        this.date = date;
        this.url = url;
        this.hdurl = hdurl;
    }

    public String getDate() {
        return date;
    }

    public String getHdurl() {
        return hdurl;
    }

    public String getUrl() {
        return url;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHdurl(String hdurl) {
        this.hdurl = hdurl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

