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
import android.widget.TextView;

import com.company.insta.instagram.ProfileActivity;
import com.company.insta.instagram.R;
import com.company.insta.instagram.models.User;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mostafa on 3/18/2018.
 */

public class SearchListAdapter extends ArrayAdapter<User> {

    Context mContext;
    ArrayList<User> arrayListUsers;

    public SearchListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource,objects);
        this.mContext = context;
        arrayListUsers = new ArrayList<>();


    }



    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable User item) {
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
            view = li.inflate(R.layout.user_single_item,null);
        }
        User user = getItem(position);
        if(user != null){
            CircleImageView profile_photo = (CircleImageView) view.findViewById(R.id.profile_photo);
            TextView username = view.findViewById(R.id.username_tv);


            if(!user.getImage().isEmpty()) {
                Picasso.get().load(user.getImage()).error(R.drawable.user).into(profile_photo);
            }

            username.setText(user.getUsername());



            visitProfile(view,user.getId(),user);

        }


        return view;
    }




    private void visitProfile(View view,final int id,final User user){

        String array[] = new String[5];
        array[0] = "followers: "+ String.valueOf(user.getFollowers());
        array[1] = "following: "+ String.valueOf(user.getFollowing());
        array[2] = "posts: " + String.valueOf(user.getPosts());
        Log.i("userChosen",array[0]+ array[1]+array[2]);

        final  String username = user.getUsername();
        final String image = user.getImage();
        final String email = user.getEmail();
        final int following = user.getFollowing();
        final int followers = user.getFollowers();
        final int posts = user.getPosts();


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getContext(),ProfileActivity.class);



                profileIntent.putExtra("user_id",id);
                profileIntent.putExtra("username",username);
                profileIntent.putExtra("email",email);
                profileIntent.putExtra("image",image);
                profileIntent.putExtra("following",following);
                profileIntent.putExtra("followers",followers);
                profileIntent.putExtra("posts",posts);

                getContext().startActivity(profileIntent);
            }
        });



    }
}
