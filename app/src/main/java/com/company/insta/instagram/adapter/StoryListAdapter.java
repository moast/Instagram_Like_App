package com.company.insta.instagram.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.company.insta.instagram.CommentsActivity;
import com.company.insta.instagram.R;
import com.company.insta.instagram.helper.SharedPrefrenceManger;
import com.company.insta.instagram.helper.SquareImageView;
import com.company.insta.instagram.helper.URLS;
import com.company.insta.instagram.helper.VolleyHandler;
import com.company.insta.instagram.models.Story;
import com.company.insta.instagram.models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mostafa on 3/10/2018.
 */

public class StoryListAdapter extends ArrayAdapter<Story> {


    private Context mContext;
    ArrayList<Story> storyArrayList;

    public StoryListAdapter(@NonNull Context context, int resource,ArrayList<Story> list) {
        super(context,resource,list);
        this.mContext = context;

    }



    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Story getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable Story item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if(view == null){

            LayoutInflater li = LayoutInflater.from(mContext);
            view = li.inflate(R.layout.feed_single_item,null);
        }
        Story story = getItem(position);
                if(story != null){
                    CircleImageView profile_photo = (CircleImageView) view.findViewById(R.id.profile_photo);
                    SquareImageView story_image = view.findViewById(R.id.story_image);
                    TextView username = view.findViewById(R.id.username_tv);
                    TextView number_of_likes = view.findViewById(R.id.num_of_likes);
                    TextView tage = view.findViewById(R.id.image_tags);
                    TextView date = view.findViewById(R.id.image_time);
                    TextView view_all_comments = view.findViewById(R.id.view_all_comments);
                    final ImageView redHeart = view.findViewById(R.id.red_heart_like);
                    final ImageView whiteHeart = view.findViewById(R.id.white_heart_like);



                    if(story.getProfile_image().isEmpty()){
                      profile_photo.setImageResource(R.drawable.user);
                    }else{
                        Picasso.get().load(story.getProfile_image()).error(R.drawable.user).into(profile_photo);
                    }
                   if(story.getStory_image().isEmpty()) {
                       profile_photo.setImageResource(R.drawable.user);
                   }else{
                       Picasso.get().load(story.getStory_image()).error(R.drawable.user).into(story_image);
                   }

                    username.setText(story.getUsername());
                    number_of_likes.setText(story.getLike() + " likes");
                    tage.setText(story.getTitle());
                    date.setText(story.getTime());


                    viewAllComments(view_all_comments,story.getId());



                    int story_id = story.getId();
                    didCurrentUserLikeThisStory(story_id,redHeart,whiteHeart);





                }


        return view;
    }



    private void didCurrentUserLikeThisStory(final int story_id, final ImageView redHeart , final ImageView whiteHeart){

        User user = SharedPrefrenceManger.getInstance(getContext()).getUserData();
         final int user_id = user.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.did_user_like_story+story_id+"&user_id="+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                boolean didUserLikeThisStory =  jsonObject.getBoolean("state");


                                //user already liked this story
                                  if(didUserLikeThisStory){
                                      redHeart.setVisibility(View.VISIBLE);
                                      whiteHeart.setVisibility(View.INVISIBLE);


                                      //user did not like this story yet
                                  }else {

                                      redHeart.setVisibility(View.INVISIBLE);
                                      whiteHeart.setVisibility(View.VISIBLE);
                                  }


                                  whiteHeart.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          //1. hide whiteHeart & show redHeart
                                          redHeart.setVisibility(View.VISIBLE);
                                          whiteHeart.setVisibility(View.INVISIBLE);


                                          //2. increase number of likes
                                            increaseLikesOfThisStory(story_id);

                                          //3. add this user to likes db indicating that he/she liked this story_id
                                           addCurrentUserToLikesDB(story_id,user_id);
                                      }
                                  });


                                  redHeart.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          //1. hide redHeart & show whiteHeart
                                          redHeart.setVisibility(View.INVISIBLE);
                                          whiteHeart.setVisibility(View.VISIBLE);

                                          //2. decrease number of likes
                                          decreaseLikesOfThisStory(story_id);


                                          //3. remove user from likes database (assocciated with this specific story)
                                          removeCurrentUserToLikesDB(story_id,user_id);

                                      }
                                  });







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



    private void decreaseLikesOfThisStory(int story_id){



        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.decrease_likes+story_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){


                                String message = jsonObject.getString("message");

                                Log.i("decreaseLikes",message);



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


    private void increaseLikesOfThisStory(int story_id){



        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.increase_likes+story_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){


                                String message = jsonObject.getString("message");

                                Log.i("increasLikes",message);



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


    private void addCurrentUserToLikesDB(int story_id,int user_id){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.add_user_to_likes_db+story_id+"&user_id="+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                String message = jsonObject.getString("message");

                                Log.i("addUserToLikes",message);



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


    private void removeCurrentUserToLikesDB(int story_id,int user_id){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.remove_user_from_likes_db+story_id+"&user_id="+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                String message = jsonObject.getString("message");

                                Log.i("removeUserToLikes",message);



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








    private void viewAllComments(TextView view_all_comments,final int id){



        view_all_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent viewAllCommentsIntent = new Intent(getContext(),CommentsActivity.class);
                viewAllCommentsIntent.putExtra("story_id",id);
                getContext().startActivity(viewAllCommentsIntent);


            }
        });
    }
}
