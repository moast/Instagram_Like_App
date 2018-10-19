package com.company.insta.instagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.company.insta.instagram.R;
import com.company.insta.instagram.SettingsActivity;
import com.company.insta.instagram.helper.SharedPrefrenceManger;
import com.company.insta.instagram.helper.URLS;
import com.company.insta.instagram.helper.VolleyHandler;
import com.company.insta.instagram.adapter.ImageArrayAdapter;
import com.company.insta.instagram.models.Image;
import com.company.insta.instagram.models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfieFragment extends Fragment {

    TextView edit_profile,posts_num_tv,following_num_tv,followers_num_tv,display_name_tv,description;
    CircleImageView user_profile_image;
    GridView images_grid_layout;
    ArrayList<Image> arrayListImages;
    ImageArrayAdapter imageArrayAdapter;
    User user;
    int user_id,posts,following,followers;
    String email,image;


    public ProfieFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profie, container, false);

        edit_profile = view.findViewById(R.id.follow_this_profile);
        posts_num_tv =  (TextView) view.findViewById(R.id.posts_num_tv);
        following_num_tv =  (TextView) view.findViewById(R.id.following_num_tv);
        followers_num_tv =  (TextView) view.findViewById(R.id.followers_num_tv);
        display_name_tv =  (TextView) view.findViewById(R.id.display_name_tv);
        description =  (TextView) view.findViewById(R.id.description);
        user_profile_image = (CircleImageView) view.findViewById(R.id.profile_image);
        images_grid_layout = (GridView) view.findViewById(R.id.images_grid_layout);

        edit_profile.setText("Edit Profile");


         user = SharedPrefrenceManger.getInstance(getContext()).getUserData();
         user_id = user.getId();

        arrayListImages = new ArrayList<>();


        getUserData();

         getAllImages();

        imageArrayAdapter = new ImageArrayAdapter(getContext(),0,arrayListImages);
        images_grid_layout.setAdapter(imageArrayAdapter);



        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });
    }

    private void goToSettings(){
        Intent settingsIntenet = new Intent(getContext(),SettingsActivity.class);

        settingsIntenet.putExtra("user_id",user.getId());
        settingsIntenet.putExtra("username",user.getUsername());
        settingsIntenet.putExtra("email",email);
        settingsIntenet.putExtra("imageProfile",image);
        settingsIntenet.putExtra("following",following);
        settingsIntenet.putExtra("followers",followers);
        settingsIntenet.putExtra("posts",posts);

        getContext().startActivity(settingsIntenet);

    }



    private void getAllImages(){


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_all_images+user_id,
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

                                    Image image = new Image(jsonObjectSingleImage.getInt("id")
                                            ,jsonObjectSingleImage.getString("image_url"), jsonObjectSingleImage.getString("image_name")
                                            ,jsonObjectSingleImage.getInt("user_id"));;

                                    arrayListImages.add(image);


                                }


                                imageArrayAdapter.notifyDataSetChanged();

                            }else{

                                Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();

                            }


                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                }


        );

        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequetToQueue(stringRequest);




    }



    private void getUserData(){


        String username = user.getUsername();

        display_name_tv.setText(username);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_user_data+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("userObjectProfile",jsonObject.toString());

                            if(!jsonObject.getBoolean("error")){


                                JSONObject jsonObjectUser =  jsonObject.getJSONObject("user");

                                posts = jsonObjectUser.getInt("posts");
                                following = jsonObjectUser.getInt("following");
                                followers = jsonObjectUser.getInt("followers");
                                 email  = jsonObjectUser.getString("email");
                                 image = jsonObjectUser.getString("image");

                                description.setText(email);
                                posts_num_tv.setText(String.valueOf(jsonObjectUser.getInt("posts")));
                                following_num_tv.setText(String.valueOf(jsonObjectUser.getInt("following")));
                                followers_num_tv.setText(String.valueOf(jsonObjectUser.getInt("followers")));

                                if(!image.isEmpty()){
                                    Picasso.get().load(image).error(R.drawable.user).into(user_profile_image);}

                            }else{

                                Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();

                            }


                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                }


        );

        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequetToQueue(stringRequest);



    }

}
