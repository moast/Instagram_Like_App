package com.company.insta.instagram.models;

/**
 * Created by mostafa on 3/1/2018.
 */

public class User {

    int id,following,followers,posts;
    String email,username,image;


    public User(int id,String email, String username,String image) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.image = image;
    }


    public User(int id,String email, String username,String image,int following,int followers,int posts) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.image = image;
        this.following = following;
        this.followers = followers;
        this.posts = posts;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
