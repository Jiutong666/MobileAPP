package com.example.mobileass2;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Comment> commentsList;
    private Context context;

    public CommentsAdapter(Context context, List<Comment> commentsList) {
        this.commentsList = commentsList;
        this.context = context;
    }


    @NonNull
    @Override
    public CommentViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentsList.get(position);

        holder.commentUsername.setText(comment.getUsername());
        holder.commentTimestamp.setText(formatTimestamp(comment.getTimestamp())); // You can create a function to format the timestamp
        holder.commentText.setText(comment.getText());
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentUsername, commentTimestamp, commentText;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            commentUsername = itemView.findViewById(R.id.commentUsername);
            commentTimestamp = itemView.findViewById(R.id.commentTimestamp);
            commentText = itemView.findViewById(R.id.CommentText);
        }
    }

    // Example timestamp formatting method
    private String formatTimestamp(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return format.format(date);
    }
}

