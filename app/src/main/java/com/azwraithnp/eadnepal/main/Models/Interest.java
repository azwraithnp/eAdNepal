package com.azwraithnp.eadnepal.main.Models;

public class Interest {

    String id, title, image;

    public String getId() {
        return id;
    }

    public Interest(String id, String title, String image) {
        this.id = id;
        this.title = title;
        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
