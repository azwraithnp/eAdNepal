package com.azwraithnp.eadnepal.main.Models;

public class Album {
    private String id;
    private String name;
    private int timeCount;
    private String thumbnail;

    public Album() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Album(String id, String name, int timeCount, String thumbnail) {
        this.name = name;
        this.timeCount = timeCount;
        this.thumbnail = thumbnail;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimeCount() {
        return timeCount;
    }

    public void setTimeCount(int timeCount) {
        this.timeCount = timeCount;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
