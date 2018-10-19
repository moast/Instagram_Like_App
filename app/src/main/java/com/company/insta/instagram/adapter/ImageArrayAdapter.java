package com.company.insta.instagram.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.company.insta.instagram.R;
import com.company.insta.instagram.SingleStoryActivity;
import com.company.insta.instagram.models.Image;
import com.company.insta.instagram.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mostafa on 3/20/2018.
 */

public class ImageArrayAdapter extends ArrayAdapter<Image> {

    Context mContext;
    User user;

    public ImageArrayAdapter(@NonNull Context context, int resource, @NonNull List<Image> objects) {
        super(context, resource, objects);
        this.mContext = context;

    }


    public void setUser(User user){
        this.user = user;
    }


    public User getUser(){
        return user;
    }


    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Image getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable Image item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

       ImageView imageView;

        if(convertView == null) {

            imageView = new ImageView(getContext());
            imageView.setLayoutParams(new GridView.LayoutParams(200,200));
            imageView.setPadding(1,1,1,1);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        }else{

            imageView = (ImageView) convertView;
        }

        Image image = getItem(position);
        Log.i("image",image.getImage_url());


        if(image != null){

            if(!image.getImage_url().isEmpty()) {
                Picasso.get().load(image.getImage_url()).error(R.drawable.user).into(imageView);
            }



        }


      showSingleImage(imageView,image);


        return imageView;
    }


    private void showSingleImage(ImageView imageView,Image image){

        final String image_url = image.getImage_url();
        final String image_name = image.getImage_name();
        final int user_id = image.getUser_id();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singleImageIntent = new Intent(getContext(),SingleStoryActivity.class);
                singleImageIntent.putExtra("image_url",image_url);
                singleImageIntent.putExtra("image_name",image_name);
                singleImageIntent.putExtra("user_id",user_id);
                if(user != null) {
                    singleImageIntent.putExtra("username", user.getUsername());
                    singleImageIntent.putExtra("followers", user.getFollowers());
                    singleImageIntent.putExtra("following", user.getFollowing());
                    singleImageIntent.putExtra("posts", user.getPosts());
                    singleImageIntent.putExtra("email", user.getEmail());
                    singleImageIntent.putExtra("image", user.getImage());
                }



                getContext().startActivity(singleImageIntent);


            }
        });


    }




}
