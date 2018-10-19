package com.company.insta.instagram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.company.insta.instagram.adapter.ImageArrayAdapter;
import com.company.insta.instagram.helper.SharedPrefrenceManger;
import com.company.insta.instagram.helper.URLS;
import com.company.insta.instagram.helper.VolleyHandler;
import com.company.insta.instagram.models.Image;
import com.company.insta.instagram.models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;



/*This Activity runs when you're attempting to browser another user's profile*/
/*It takes user id passed with the intent to this activity */


public class ProfileActivity extends AppCompatActivity {

    TextView follow_this_profile,posts_num_tv,following_num_tv,followers_num_tv,display_name_tv,description;
    CircleImageView other_user_profile_image;
    int other_user_id,user_id;
    GridView images_grid_layout;
    ArrayList<Image> arrayListImages;
    ImageArrayAdapter imageArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        follow_this_profile =  (TextView) findViewById(R.id.follow_this_profile);
        posts_num_tv =  (TextView) findViewById(R.id.posts_num_tv);
        following_num_tv =  (TextView) findViewById(R.id.following_num_tv);
        followers_num_tv =  (TextView) findViewById(R.id.followers_num_tv);
        display_name_tv =  (TextView) findViewById(R.id.display_name_tv);
        description =  (TextView) findViewById(R.id.description);
        other_user_profile_image = (CircleImageView) findViewById(R.id.profile_image);
        images_grid_layout = (GridView) findViewById(R.id.images_grid_layout);

        follow_this_profile.setEnabled(false);

         other_user_id = getIntent().getIntExtra("user_id",0);
         User user = SharedPrefrenceManger.getInstance(getApplicationContext()).getUserData();
         user_id = user.getId();


        String username = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");
        String image = getIntent().getStringExtra("image");
        int following =  getIntent().getIntExtra("following",0);
        int followers = getIntent().getIntExtra("followers",0);
        int posts =  getIntent().getIntExtra("posts",0);

        User userObject = new User(user_id,email,username,image,following,followers,posts);

         arrayListImages = new ArrayList<>();



        IsCurrentUserFollowingThisUser();
        getOtherUsersProfileData(userObject);


        getAllImages();
        imageArrayAdapter = new ImageArrayAdapter(ProfileActivity.this,0,arrayListImages);
        imageArrayAdapter.setUser(userObject);
        images_grid_layout.setAdapter(imageArrayAdapter);


    }

    private void getAllImages(){


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_all_images+other_user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){


                                JSONArray jsonObjectImages =  jsonObject.getJSONArray("images");

                                Log.i("arrayImages",jsonObjectImages.toString());

                                for(int i = 0 ; i<jsonObjectImages.length(); i++){
                                    JSONObject jsonObjectSingleImage = jsonObjectImages.getJSONObject(i);
                                    Log.i("jsonsingleImage",jsonObjectSingleImage.toString());

                                    Image image = new Image(jsonObjectSingleImage.getInt("id"),
                                            jsonObjectSingleImage.getString("image_url"),
                                            jsonObjectSingleImage.getString("image_name")
                                            ,jsonObjectSingleImage.getInt("user_id"));;

                                    arrayListImages.add(image);


                                }


                                imageArrayAdapter.notifyDataSetChanged();

                            }else{

                                Toast.makeText(ProfileActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();

                            }


                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                }


        );

        VolleyHandler.getInstance(getApplicationContext()).addRequetToQueue(stringRequest);




    }


    private void getOtherUsersProfileData(User user){

        if(!user.getImage().isEmpty()){

            Picasso.get().load(user.getImage()).error(R.drawable.user).into(other_user_profile_image);
        }


        posts_num_tv.setText(String.valueOf(user.getPosts()));
        following_num_tv.setText(String.valueOf(user.getFollowing()));
        followers_num_tv.setText(String.valueOf(user.getFollowers()));
        display_name_tv.setText(user.getUsername());
        description.setText(user.getEmail());



    }


    private void IsCurrentUserFollowingThisUser(){


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.check_following_state+other_user_id+"&user_id="+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("isfollowing",jsonObject.toString());

                            if(!jsonObject.getBoolean("error")){

                                boolean isFollowing =  jsonObject.getBoolean("state");

                                follow_this_profile.setEnabled(true);

                                if(isFollowing){

                                    follow_this_profile.setText("Unfollow");
                                    unfollowThisUser();


                                }else{
                                    follow_this_profile.setText("Follow");
                                    followThisUser();

                                }



                            }else{

                                Toast.makeText(ProfileActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();

                            }


                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                }


        );


        VolleyHandler.getInstance(getApplicationContext()).addRequetToQueue(stringRequest);

    }



    private void unfollowThisUser(){

        follow_this_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.unfollow_this_person+other_user_id+"&user_id="+user_id,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    Log.i("unfollowMethod",jsonObject.toString());

                                    if(!jsonObject.getBoolean("error")){

                                        boolean state =  jsonObject.getBoolean("state");

                                        follow_this_profile.setEnabled(false);
                                        follow_this_profile.setText("Follow");


                                    }else{

                                        Toast.makeText(ProfileActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();

                                    }


                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ProfileActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();

                            }
                        }


                );


                VolleyHandler.getInstance(ProfileActivity.this.getApplicationContext()).addRequetToQueue(stringRequest);



            }
        });
    }



    private void followThisUser(){

        follow_this_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.follow_this_person+other_user_id+"&user_id="+user_id,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    Log.i("followMethod",jsonObject.toString());

                                    if(!jsonObject.getBoolean("error")){


                                        boolean state =  jsonObject.getBoolean("state");

                                        follow_this_profile.setEnabled(false);
                                        follow_this_profile.setText("Unfollow");



                                    }else{

                                        Toast.makeText(ProfileActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();

                                    }


                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ProfileActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();

                            }
                        }


                );


                VolleyHandler.getInstance(ProfileActivity.this.getApplicationContext()).addRequetToQueue(stringRequest);


            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        int user_id = getIntent().getIntExtra("user_id",0);

        if(user_id == 0){
            startActivity(new Intent(this,SearchActivity.class));
        }
    }
}
