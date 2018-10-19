package com.company.insta.instagram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.company.insta.instagram.adapter.SearchListAdapter;
import com.company.insta.instagram.helper.SharedPrefrenceManger;
import com.company.insta.instagram.helper.URLS;
import com.company.insta.instagram.helper.VolleyHandler;
import com.company.insta.instagram.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {


    EditText search_et;
    ListView search_results_lv;
    ArrayList<User> arrayListUsers;
    SearchListAdapter searchListAdapter;
    int user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_et = (EditText) findViewById(R.id.search_et);
        search_results_lv = (ListView) findViewById(R.id.search_results_lv);

        arrayListUsers = new ArrayList<User>();
        searchListAdapter = new SearchListAdapter(SearchActivity.this,R.layout.user_single_item,arrayListUsers);
        search_results_lv.setAdapter(searchListAdapter);


        User user = SharedPrefrenceManger.getInstance(getApplicationContext()).getUserData();
        user_id = user.getId();


        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().length() > 0){
                    getSimilarUsers(s.toString());
                }else{

                }


            }
        });

    }



    private void getSimilarUsers(String text){
        searchListAdapter.clear();
        //arrayListUsers.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_similar_users+text,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){



                                JSONArray jsonArrayUsers =  jsonObject.getJSONArray("users");

                                Log.i("arrayUsers",jsonArrayUsers.toString());

                                for(int i = 0 ; i<jsonArrayUsers.length(); i++){
                                    JSONObject jsonObjectSingleUser = jsonArrayUsers.getJSONObject(i);
                                    Log.i("jsonsingleUser",jsonObjectSingleUser.toString());


                                    if(user_id != jsonObjectSingleUser.getInt("id")) {

                                        User user = new User(jsonObjectSingleUser.getInt("id"), jsonObjectSingleUser.getString("email")
                                                , jsonObjectSingleUser.getString("username"), jsonObjectSingleUser.getString("image")
                                                , jsonObjectSingleUser.getInt("following"), jsonObjectSingleUser.getInt("followers")
                                                , jsonObjectSingleUser.getInt("posts"));


                                        //  arrayListUsers.add(user);

                                        searchListAdapter.add(user);
                                    }

                                }


                               // searchListAdapter.notifyDataSetChanged();

                            }else{

                                Toast.makeText(SearchActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();

                            }


                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();

                    }
                }


        );

        VolleyHandler.getInstance(getApplicationContext()).addRequetToQueue(stringRequest);



    }
}
