package com.company.insta.instagram.models;

/**
 * Created by mostafa on 3/10/2018.
 */

public class Story {

    private int id,publisher_id,like;
    private String story_image,title,time,profile_image,username;


    public Story(int id, int publisher_id, int like, String story_image, String title, String time, String profile_image, String username) {
        this.id = id;
        this.publisher_id = publisher_id;
        this.like = like;
        this.story_image = story_image;
        this.title = title;
        this.time = time;
        this.profile_image = profile_image;
        this.username = username;
    }

    public String getStory_image() {
        return story_image;
    }

    public void setStory_image(String story_image) {
        this.story_image = story_image;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPublisher_id() {
        return publisher_id;
    }

    public void setPublisher_id(int publisher_id) {
        this.publisher_id = publisher_id;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
