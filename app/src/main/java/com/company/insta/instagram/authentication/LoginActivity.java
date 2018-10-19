package com.company.insta.instagram.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.company.insta.instagram.MainActivity;
import com.company.insta.instagram.R;
import com.company.insta.instagram.helper.SharedPrefrenceManger;
import com.company.insta.instagram.helper.URLS;
import com.company.insta.instagram.helper.VolleyHandler;
import com.company.insta.instagram.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    LinearLayout mLoginContainer;
    AnimationDrawable mAnimationDrawable;

    EditText username_et,password_et;
    Button login_btn;
    TextView sign_up_btn, forgot_pass_btn;
    ProgressDialog mProgrssDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //animation drawable variable & config
        mLoginContainer = (LinearLayout) findViewById(R.id.login_container);
        mAnimationDrawable = (AnimationDrawable) mLoginContainer.getBackground();
        mAnimationDrawable.setEnterFadeDuration(5000);
        mAnimationDrawable.setExitFadeDuration(2000);


        //login variables
        username_et = (EditText) findViewById(R.id.user_name);
        password_et = (EditText) findViewById(R.id.user_password);
        sign_up_btn =  (TextView) findViewById(R.id.sign_up_btn);
        login_btn = (Button) findViewById(R.id.login_btn);
        forgot_pass_btn =  (TextView) findViewById(R.id.forgot_pass_btn);
        mProgrssDialog = new ProgressDialog(this);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent signUpIntent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        forgot_pass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }


    private void logIn(){

        mProgrssDialog.setTitle("Log In");
        mProgrssDialog.setMessage("Please wait....");
        mProgrssDialog.show();

        final String username = username_et.getText().toString();
        final String password = password_et.getText().toString();

        if(TextUtils.isEmpty(username)){
            username_et.setError("Please fill in this field");
            username_et.requestFocus();
            mProgrssDialog.dismiss();
            return;
        }

        if(TextUtils.isEmpty(password)){
            password_et.setError("Please fill in this field");
            password_et.requestFocus();
            mProgrssDialog.dismiss();
            return;
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.login_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                mProgrssDialog.dismiss();

                               JSONObject jsonObjectUser =  jsonObject.getJSONObject("user");

                               User user = new User(jsonObjectUser.getInt("id"),jsonObjectUser.getString("email")
                                       ,jsonObjectUser.getString("username"),jsonObjectUser.getString("image"));


                                //store user data inside sharedprefrences
                                SharedPrefrenceManger.getInstance(getApplicationContext()).storeUserData(user);



                                //let user in
                                finish();
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));

                            }else{

                                Toast.makeText(LoginActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                            }


                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                        mProgrssDialog.dismiss();
                    }
                }


        ){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> authenticationVariables = new HashMap<>();
                authenticationVariables.put("username",username);
                authenticationVariables.put("password",password);
                return  authenticationVariables;
            }
        };//end of string Request

        VolleyHandler.getInstance(getApplicationContext()).addRequetToQueue(stringRequest);


    }


    @Override
    protected void onResume() {
        super.onResume();

        if(mAnimationDrawable != null && !mAnimationDrawable.isRunning()){
            mAnimationDrawable.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mAnimationDrawable != null && mAnimationDrawable.isRunning()){
            mAnimationDrawable.stop();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        boolean isUserLoggedIn = SharedPrefrenceManger.getInstance(getApplicationContext()).isUserLogggedIn();

        if(isUserLoggedIn){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }else{

        }
    }





}
