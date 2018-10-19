package com.company.insta.instagram.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.company.insta.instagram.R;
import com.company.insta.instagram.helper.SharedPrefrenceManger;
import com.company.insta.instagram.helper.URLS;
import com.company.insta.instagram.helper.VolleyHandler;
import com.company.insta.instagram.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class CameraFragment extends Fragment{


    Button upload_btn,capture_btn;
    ImageView captured_iv;
    Uri mImageUri;
    final int CAPTURE_IMAGE = 1,GALLARY_PICK = 2;
    Bitmap bitmap;
    String mStoryTitle,imageToString,mProfileImage;
    boolean OkToUpload;



    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_camera, container, false);

        upload_btn = (Button) view.findViewById(R.id.upload_btn);
        capture_btn = (Button) view.findViewById(R.id.capture_btn);
        captured_iv = (ImageView) view.findViewById(R.id.captured_iv);

        OkToUpload = false;



      return view;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getProfileImage();

        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String[] options = {"Choose From Gallery","Take Photo"};
                AlertDialog.Builder build = new AlertDialog.Builder(v.getContext());
                build.setTitle("Choose Image");
                build.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        switch (which) {
                            //choose from gallery
                            case 0:
                                Intent galleryIntent = new Intent();
                                galleryIntent.setType("image/*");
                                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLARY_PICK);

                                break;

                            //take a photo using camera
                            case 1:

                                capturePhoto();

                                break;


                        }


                    }
                });

                build.show();





            }
        });


        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                storyAndImageTitle();

            }
        });


    }



    private void capturePhoto(){

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String imageName = "image.jpg";
             mImageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),imageName));
             cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,mImageUri);
             startActivityForResult(cameraIntent,CAPTURE_IMAGE);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAPTURE_IMAGE){

            if(resultCode == RESULT_OK){

                if(mImageUri != null){

                    //convert uri to bitmap
                    try {
                         bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),mImageUri);

                         if(bitmap != null){

                             OkToUpload = true;
                            captured_iv.setImageBitmap(bitmap);

                            Log.i("bitmap",bitmap.toString());
                         }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }

        }


        if(requestCode == GALLARY_PICK){

            if(resultCode == RESULT_OK){


                Uri uri = data.getData();
                if(uri != null) {

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);

                            if (bitmap != null) {

                                OkToUpload = true;
                                captured_iv.setImageBitmap(bitmap);

                                Log.i("bitmap", bitmap.toString());
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                }

            }


        }

    }

    private void storyAndImageTitle(){

        final EditText editText = new EditText(getContext());
        editText.setTextColor(Color.BLACK);
        editText.setHint("Set Title/Tags for story");
        editText.setHintTextColor(Color.GRAY);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Story Title");
        builder.setCancelable(false);
        builder.setView(editText);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if(OkToUpload) {
                    mStoryTitle = editText.getText().toString();
                    imageToString = convertImageToString();
                    uploadStory();
                }else{
                    Toast.makeText(getContext(),"Please take a photo first!",Toast.LENGTH_LONG).show();
                }



            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();





    }

    private String convertImageToString(){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] imageByteArray = baos.toByteArray();
        String result =  Base64.encodeToString(imageByteArray,Base64.DEFAULT);

        return result;


    }



    private void getProfileImage(){


        User user = SharedPrefrenceManger.getInstance(getContext()).getUserData();
        int user_id = user.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLS.get_user_data+user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){

                                JSONObject jsonObjectUser =  jsonObject.getJSONObject("user");

                               mProfileImage =  jsonObjectUser.getString("image");



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

    private void uploadStory(){


        if(!OkToUpload){
            Toast.makeText(getContext(),"There is no image to upload!",Toast.LENGTH_LONG).show();

            return;
        }


        final String dateOfImage = dateOfImage();
        final String currentTime = currentReadableTime();
        User user = SharedPrefrenceManger.getInstance(getContext()).getUserData();
        final String username = user.getUsername();
        final int user_id = user.getId();
        final String profile_image = mProfileImage;


        final ProgressDialog mProgrssDialog =  new ProgressDialog(getContext());
        mProgrssDialog.setTitle("Uploading Story");
        mProgrssDialog.setMessage("Please wait....");
        mProgrssDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.upload_story_image,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(!jsonObject.getBoolean("error")){
                                mProgrssDialog.dismiss();



                                Toast.makeText(getContext(),"story uploaded successfully!",Toast.LENGTH_LONG).show();

                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.main_fragment_content,new HomeFragment());
                                ft.commit();


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
                        mProgrssDialog.dismiss();
                    }
                }


        ){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> imageMap = new HashMap<>();
                imageMap.put("image_name",dateOfImage);
                imageMap.put("image_encoded",imageToString);
                imageMap.put("title",mStoryTitle);
                imageMap.put("time",currentTime);
                imageMap.put("username",username);
                imageMap.put("user_id",String.valueOf(user_id));
                imageMap.put("profile_image", profile_image);
                return  imageMap;
            }
        };//end of string Request

        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequetToQueue(stringRequest);

        OkToUpload = false;


    }



    private String dateOfImage(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.toString();
    }



    private String currentReadableTime(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date(timestamp.getTime());
        return date.toString();


    }





























    //
//    Button capture_btn,upload_btn;
//    ImageView captured_iv;
//    String  mStoryTitle, imageToString;
//    Bitmap bitmap;
//    Uri mImageUri;
//    private final int CAPTURE_IMAGE = 1;
//    boolean upload;
//
//
//
//
//    public CameraFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_camera, container, false);;
//
//
//        capture_btn = view.findViewById(R.id.capture_btn);
//        captured_iv = view.findViewById(R.id.captured_iv);
//        upload_btn = view.findViewById(R.id.upload_btn);
//
//         upload = false;
//
//        return view;
//    }
//
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        capture_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                    capturePhoto();
//
//            }
//        });
//
//
//        upload_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //set title for story (post)
//                StoryAndImageTitle();
//
//            }
//        });
//    }
//
//
//    private void StoryAndImageTitle(){
//
//        final EditText editText = new EditText(getContext());
//        editText.setTextColor(Color.BLACK);
//        editText.setHint("Set Title for Story");
//        editText.setHintTextColor(Color.GRAY);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Story Title");
//        builder.setCancelable(false);
//        builder.setView(editText);
//
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//             mStoryTitle = editText.getText().toString();
//             imageToString = convertImageToString();
//             //upload image
//             uploadImage();
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//      builder.show();
//
//    }
//
//
//
//
//
//    private void uploadImage(){
//
//        if(!upload){
//            Toast.makeText(getContext(), "upload is false", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//
//        final String dateOfImage = dateOfImage();
//        final String currentRedableTime = currentReadbleTime();
//        User user = SharedPrefrenceManger.getInstance(getContext()).getUserData();
//        final String username = user.getUsername();
//        final int user_id = user.getId();
//        final String profile_image = user.getImage();
//
//
//        final ProgressDialog  mProgrssDialog = new ProgressDialog(getContext());
//        mProgrssDialog.setTitle("Upload Story");
//        mProgrssDialog.setMessage("Please wait....");
//        mProgrssDialog.show();
//
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLS.upload_story_image,  new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    if(!jsonObject.getBoolean("error")){
//                        mProgrssDialog.dismiss();
//
//                        JSONObject jsonObjectImage =  jsonObject.getJSONObject("image");
//
//                        Log.i("imageRespose",jsonObjectImage.toString());
//                        Toast.makeText(getContext(),"we have no error",Toast.LENGTH_LONG).show();
//
//                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                        ft.replace(R.id.main_fragment_content,new HomeFragment());
//                        ft.commit();
//
//
//                    }else{
//
//                        Toast.makeText(getContext(),jsonObject.getString("message"),Toast.LENGTH_LONG).show();
//                    }
//
//
//                }catch (JSONException e){
//                    e.printStackTrace();
//                }
//            }
//        },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getContext(),"volley error",Toast.LENGTH_LONG).show();
//                        mProgrssDialog.dismiss();
//                    }
//                }
//
//
//        ){
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map imageMap = new HashMap<>();
//                Log.i("imagetostring",imageToString);
//
//                imageMap.put("image_name",dateOfImage);
//                imageMap.put("image_encoded",imageToString);
//                imageMap.put("title",mStoryTitle);
//                imageMap.put("time",currentRedableTime);
//                imageMap.put("username",username);
//                imageMap.put("user_id",String.valueOf(user_id));
//                imageMap.put("profile_image",profile_image);
//
//
//                return  imageMap;
//            }
//        };//end of string Request
//
//        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequetToQueue(stringRequest);
//
//
//    }
//
//
//    private String dateOfImage(){
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        return timestamp.toString();
//
//    }
//
//    private String currentReadbleTime(){
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        Date date = new Date(timestamp.getTime());
//        return date.toString();
//
//    }
//
//
//    private String convertImageToString(){
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
//        byte[] imageByteArray = baos.toByteArray();
//        String result =  Base64.encodeToString(imageByteArray,Base64.DEFAULT);
//        return result;
//    }
//
//
//
//
//
//    private void capturePhoto()  {
//
//        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        String imageName = "image.jpg";
//        mImageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),imageName));
//        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
//        startActivityForResult(intentCamera, CAPTURE_IMAGE);
//
//
//    }
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//      super.onActivityResult(requestCode, resultCode, data);
//
//
//
//        if(requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK){
//
//            if(mImageUri != null) {
//                Log.i("imageuri",mImageUri.getPath());
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),mImageUri);
//                    if(bitmap != null){
//                        upload = true;
//                        captured_iv.setImageBitmap(bitmap);
//                    }
//                    Log.i("bitmap",bitmap.toString());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//        }
//
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//    }


}
