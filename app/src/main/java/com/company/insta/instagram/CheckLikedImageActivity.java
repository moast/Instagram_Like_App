package com.company.insta.instagram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.insta.instagram.helper.SquareImageView;
import com.squareup.picasso.Picasso;

public class CheckLikedImageActivity extends AppCompatActivity {

    TextView story_username_tv;
    SquareImageView story_image;
    ImageView back_arrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_liked_image);


        story_username_tv = (TextView) findViewById(R.id.story_username_tv);
        story_image = (SquareImageView) findViewById(R.id.story_image);
        back_arrow = (ImageView) findViewById(R.id.back_arrow);

        String image_url = getIntent().getStringExtra("image_url");
        String publisher_username = getIntent().getStringExtra("image_username");


        if(image_url != null && !image_url.isEmpty()){
            Picasso.get().load(image_url).error(R.drawable.user).into(story_image);
        }
        if(publisher_username != null && !publisher_username.isEmpty()){
            story_username_tv.setText("By " + publisher_username);
        }





    }



}
