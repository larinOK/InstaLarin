package com.example.instalarin.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instalarin.Adapter.TagAdapter;
import com.example.instalarin.Adapter.UserAdapter;
import com.example.instalarin.Model.User;
import com.example.instalarin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SocialAutoCompleteTextView searchBar;
    private List<User> mUsers;
    private UserAdapter userAdapter;

    private RecyclerView recyclerViewTags;
    private List<String> mHashtags;
    private List<String> mHashtagsCount;
    private TagAdapter tagAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        recyclerViewTags = view.findViewById(R.id.recyclerViewTags);
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));

        mHashtags = new ArrayList<>();
        mHashtagsCount = new ArrayList<>();
        tagAdapter = new TagAdapter(getContext(), mHashtags, mHashtagsCount);
        recyclerViewTags.setAdapter(tagAdapter);


        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userAdapter);

        searchBar  = view.findViewById(R.id.searchBar);

        readUsers();
        readTags();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    searchUser(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

                filter(editable.toString());

            }
        });

        return view;
    }



    private void readTags() {

        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHashtags.clear();
                mHashtagsCount.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    mHashtags.add(dataSnapshot.getKey());
                    mHashtagsCount.add(dataSnapshot.getChildrenCount()+"");
                }
                tagAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(TextUtils.isEmpty(searchBar.getText().toString())){
                    mUsers.clear();
                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                        User user = snapshot1.getValue(User.class);
                        mUsers.add(user);
                    }

                    userAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void searchUser(String s){

        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").startAt(s).endAt(s+ "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void filter(String text) {
        List<String> mSearchTags = new ArrayList<>();
        List<String> mSearchTagsCount = new ArrayList<>();

        for(String s: mHashtags){
            if(s.toLowerCase().contains(text.toLowerCase())){
                mSearchTags.add(s);
                mSearchTagsCount.add(mHashtagsCount.get(mHashtags.indexOf(s)));
            }
        }
        tagAdapter.filter(mSearchTags, mSearchTagsCount);
    }

}