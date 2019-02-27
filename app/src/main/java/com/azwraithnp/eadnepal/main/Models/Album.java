package com.azwraithnp.eadnepal.main.Models;

public class Album {
    private String name;
    private int timeCount;
    private int thumbnail;

    public Album() {
    }

    public Album(String name, int timeCount, int thumbnail) {
        this.name = name;
        this.timeCount = timeCount;
        this.thumbnail = thumbnail;
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

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
