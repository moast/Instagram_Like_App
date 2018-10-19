package com.company.insta.instagram.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.company.insta.instagram.R;
import com.company.insta.instagram.helper.SharedPrefrenceManger;
import com.company.insta.instagram.helper.URLS;
import com.company.insta.instagram.helper.VolleyHandler;
import com.company.insta.instagram.adapter.StoryListAdapter;
import com.company.insta.instagram.models.Story;
import com.company.insta.instagram.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    ListView feed_lv;
    ArrayList<Story> arrayListStories;
    StoryListAdapter storyListAdapter;
    ProgressDialog mProgrssDialog;
    JSONArray jsonArrayIds;



    public HomeFragment() {
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
        View  view = inflater.inflate(R.layout.fragment_home, container, false);
        feed_lv = view.findViewById(R.id.feed_lv);


        arrayListStories = new ArrayList<Story>();

        mProgrssDialog = new ProgressDialog(getContext());
        mProgrssDialog.setTitle("News Feed");
        mProgrssDialog.setMessage("Updating News Feed....");

        storyListAdapter = new StoryListAdapter(getContext(),R.layout.feed_single_item,arrayListStories);
        feed_lv.setAdapter(storyListAdapter);


        getFollowingIds();


        return view;
    }


    private void getFollowingIds(){


        mProgrssDialog.show();


        User user = SharedPrefrenceManger.getInstance(getContext()).getUserData();
        final int user_id = user.getId();
        Log.i("userId",String.valueOf(user_id));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_following_ids+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            mProgrssDialog.dismiss();

                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                 JSONArray jsonArrayIds =  jsonObject.getJSONArray("ids");

                                Log.i("arrayids",jsonArrayIds.toString());
                                //arrayid : [6,8,10,12]

                              String ids = jsonArrayIds.toString();
                              ids = ids.replace("[","");
                              ids = ids.replace("]","");


                                Log.i("arrayidsNew",ids);
                                 getLatestNewsFeed(ids);

                            }else{

                                Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                                mProgrssDialog.dismiss();
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
                        mProgrssDialog.dismiss();
                    }
                }


        );


        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequetToQueue(stringRequest);


    }





    private void getLatestNewsFeed(String ids){



        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.lasted_news_feed+ids,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                mProgrssDialog.dismiss();

                                JSONArray jsonObjectStories =  jsonObject.getJSONArray("stories");

                                Log.i("arrayStories",jsonObjectStories.toString());

                                for(int i = 0 ; i<jsonObjectStories.length(); i++){
                                    JSONObject jsonObjectSingleStory = jsonObjectStories.getJSONObject(i);
                                    Log.i("jsonsinglestory",jsonObjectSingleStory.toString());
                                    Story story = new Story(jsonObjectSingleStory.getInt("id"),jsonObjectSingleStory.getInt("user_id")
                                                 ,jsonObjectSingleStory.getInt("num_of_likes"),jsonObjectSingleStory.getString("image_url")
                                                 ,jsonObjectSingleStory.getString("title"),jsonObjectSingleStory.getString("time")
                                                , jsonObjectSingleStory.getString("profile_image"),jsonObjectSingleStory.getString("username"));


                                    arrayListStories.add(story);
                                   //storyListAdapter.add(story);
                                }


                                storyListAdapter.notifyDataSetChanged();

                            }else{

                                Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                                mProgrssDialog.dismiss();
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
                        mProgrssDialog.dismiss();
                    }
                }


        );

        VolleyHandler.getInstance(getContext()).addRequetToQueue(stringRequest);
       // Volley.newRequestQueue(getContext().getApplicationContext()).add(stringRequest);

    }








}
