package com.example.instalarin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instalarin.Fragments.SearchFragment;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.useritem, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);
        holder.followButton.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getName());

        Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);

        isFollowed(user.getId(), holder.followButton);


        System.out.println(user.getId());
        System.out.println(firebaseUser.getUid());
        if(user.getId().equals(firebaseUser.getUid())){
            holder.followButton.setVisibility(View.GONE);
        }

        holder.followButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(holder.followButton.getText().toString().equals(("Follow"))){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);

                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });

    }

    private void isFollowed(final String id, final Button followButton) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(id).exists()){
                    followButton.setText("Following");
                }
                else {
                    followButton.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageProfile;
        public TextView username;
        public TextView fullname;
        public Button followButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.profileImage);
            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            followButton = itemView.findViewById(R.id.followButton);
        }

    }
}
