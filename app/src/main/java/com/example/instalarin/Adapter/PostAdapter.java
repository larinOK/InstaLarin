package com.example.instalarin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instalarin.HomeActivity;
import com.example.instalarin.MainActivity;
import com.example.instalarin.Model.Post;
import com.example.instalarin.Model.User;
import com.example.instalarin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.postitem, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPosts.get(position);

        Picasso.get().load(post.getImageurl()).into(holder.postImage);

        holder.description.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                if(user.getImageUrl().equals("default")) {
                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                }
                else{
                    Picasso.get().load(user.getImageUrl()).into(holder.imageProfile);
                }
                holder.username.setText(user.getUsername());
                holder.author.setText(user.getName());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("publisherId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("publisherId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HomeActivity.class);
                intent.putExtra("publisherId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageProfile;
        public ImageView postImage;
        public ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more;

        public TextView username;
        public TextView likeCount;
        public TextView author;
        public TextView commentsCount;
        SocialTextView description;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.profileImage);
            postImage = itemView.findViewById(R.id.postImage);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            more = itemView.findViewById(R.id.more);

            username = itemView.findViewById(R.id.username);
            likeCount = itemView.findViewById(R.id.likesCount);
            author = itemView.findViewById(R.id.postAuthor);
            commentsCount = itemView.findViewById(R.id.commentCount);
            description = itemView.findViewById(R.id.postDescription);
        }
    }
}
