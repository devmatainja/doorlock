package com.matainja.door.model;


public class WifiModel {

    private String name;
    private String rss;


    public WifiModel(String name, String rss ) {
        this.name=name;
        this.rss=rss;


    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRss() {
        return rss;
    }

    public void setRss(String rss) {
        this.rss = rss;
    }
}