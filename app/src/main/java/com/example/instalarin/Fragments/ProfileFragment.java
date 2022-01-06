package com.example.instalarin.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instalarin.Adapter.PhotoAdapter;
import com.example.instalarin.Model.Post;
import com.example.instalarin.Model.User;
import com.example.instalarin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> listOfPhotos;


    private CircleImageView profileImage;
    private ImageView options;
    private TextView postsCount;
    private TextView followerCount;
    private TextView followingCount;
    private TextView bio;
    private TextView fullname;
    private TextView username;


    private Button editProfile;
    private ImageView myPictures;
    private ImageView savedPictures;

    private FirebaseUser fUSer;
    String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fUSer = FirebaseAuth.getInstance().getCurrentUser();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");
        System.out.println(getContext());

        if (data.equals("none")) {
            System.out.println("KMT KMA");
            profileId = fUSer.getUid();
        } else {
            profileId = data;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }
        //profileId = fUSer.getUid();

        profileImage = view.findViewById(R.id.imageProfile);
        options = view.findViewById(R.id.options);
        followerCount = view.findViewById(R.id.followers);
        followingCount = view.findViewById(R.id.following);
        postsCount = view.findViewById(R.id.posts);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        myPictures = view.findViewById(R.id.myPictures);
        savedPictures = view.findViewById(R.id.savedPictures);
        editProfile = view.findViewById(R.id.editProfile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = editProfile.getText().toString();

                if (btnText.equals("Edit profile")) {
                    //startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else {
                    if (btnText.equals("Follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUSer.getUid())
                                .child("following").child(profileId).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(fUSer.getUid()).setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUSer.getUid())
                                .child("following").child(profileId).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                                .child("followers").child(fUSer.getUid()).removeValue();
                    }
                }
            }
        });

        recyclerView = view.findViewById(R.id.recyclerViewPictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        listOfPhotos = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), listOfPhotos);
        recyclerView.setAdapter(photoAdapter);



        userInfo();
        getFollowingCount();
        getFollowerCount();
        getPostCount();
        getPhotos();

        if(profileId.equals(fUSer.getUid())){
            editProfile.setText(editProfile.getText());
        }
        else{
            checkFollowingStatus();
        }

        return view;
    }

    private void getPhotos() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOfPhotos.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);

                    assert post != null;
                    if(profileId.equals(post.getPublisher())){
                        listOfPhotos.add(post);
                    }
                }

                Collections.reverse(listOfPhotos);
                photoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    public void setEditProfileButton(View v){
//
//        Button editButton = (Button) v;
//
//        if(editButton.getText().toString().equals("EDIT PROFILE")){
//            //edit profile
//        }
//        else if(editButton.getText().toString().equals("Follow")){
//
//            FirebaseDatabase.getInstance().getReference().child("Follow").child(fUSer.getUid()).child("following").child(profileId).setValue(true);
//
//            FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(fUSer.getUid()).setValue(true);
//        }
//        else{
//            FirebaseDatabase.getInstance().getReference().child("Follow").child(fUSer.getUid()).child("following").child(profileId).removeValue();
//
//            FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(fUSer.getUid()).removeValue();
//        }
//
//    }

    private void checkFollowingStatus() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUSer.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileId).exists()){
                    editProfile.setText("Following");
                }
                else{
                    editProfile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getPostCount() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);

                    if(post.getPublisher().equals(profileId)) counter++;

                }

                postsCount.setText(String.valueOf(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFollowerCount() {

        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference().child("follow").child(profileId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followerCount.setText(""+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFollowingCount() {

        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);
        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingCount.setText(""+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void userInfo() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user  = snapshot.getValue(User.class);

                Picasso.get().load(R.mipmap.ic_launcher).into(profileImage);
                username.setText(user.getUsername());
                fullname.setText(user.getName());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}