package com.example.quoraclone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quoraclone.Model.Comment;
import com.example.quoraclone.R;
import com.example.quoraclone.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends  RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mCommentList;
    private String postid;
    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mContext, List<Comment> mCommentList, String postid) {
        this.mContext = mContext;
        this.mCommentList = mCommentList;
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comments_layout,parent,false);


        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mCommentList.get(position);

        holder.commentor_comment.setText(comment.getComment());
        holder.commentDate.setText("Commented on : "+comment.getDate());

        getUserInformation(holder.commentor_profile_image,holder.commentorUserName,comment.getPublisher());
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder
    {

        public CircleImageView commentor_profile_image;
        public TextView commentorUserName,commentDate,commentor_comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            commentor_profile_image=itemView.findViewById(R.id.commentor_profile_image);
            commentorUserName=itemView.findViewById(R.id.commentorUserName);
            commentDate=itemView.findViewById(R.id.commentDate);
        }




    }
    private void getUserInformation(CircleImageView circleImageView,TextView username,String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getProfileimageurl()).into(circleImageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}


