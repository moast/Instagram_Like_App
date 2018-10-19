package com.company.insta.instagram.models;

/**
 * Created by mostafa on 3/20/2018.
 */

public class Image {

    int id,user_id;
    String image_url,image_name;


    public Image(int id, String image_url, String image_name,int user_id) {
        this.id = id;
        this.image_url = image_url;
        this.image_name = image_name;
        this.user_id = user_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
