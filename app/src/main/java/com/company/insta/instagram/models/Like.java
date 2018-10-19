package com.company.insta.instagram.models;

/**
 * Created by mostafa on 3/25/2018.
 */

public class Like {

    int story_id;
    String story_image,story_username;

    public Like(int story_id, String story_image, String story_username) {
        this.story_id = story_id;
        this.story_image = story_image;
        this.story_username = story_username;
    }


    public int getStory_id() {
        return story_id;
    }

    public void setStory_id(int story_id) {
        this.story_id = story_id;
    }

    public String getStory_image() {
        return story_image;
    }

    public void setStory_image(String story_image) {
        this.story_image = story_image;
    }

    public String getStory_username() {
        return story_username;
    }

    public void setStory_username(String story_username) {
        this.story_username = story_username;
    }
}
