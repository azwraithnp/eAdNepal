package com.azwraithnp.eadnepal.main.Models;

public class Album {
    private String id;
    private String name;
    private String desc;
    private String email, phone, url;
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

    public Album(String id, String name, String desc, int timeCount, String thumbnail, String email, String phone, String url) {
        this.name = name;
        this.timeCount = timeCount;
        this.thumbnail = thumbnail;
        this.desc = desc;
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
