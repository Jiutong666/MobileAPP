package com.example.mobileass2.Item;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileass2.DisplayImageActivity;
import com.example.mobileass2.DisplayTextActivity;
import com.example.mobileass2.DisplayVideoActivity;
import com.example.mobileass2.R;

import java.text.BreakIterator;
import java.util.List;

public class ContentItemAdapter extends RecyclerView.Adapter<ContentItemAdapter.ViewHolder> {

    private List<ContentItem> contentItems;

    private Context context;

    public ContentItemAdapter(Context context, List<ContentItem> contentItems) {
        this.context = context;
        this.contentItems = contentItems;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContentItem item = contentItems.get(position);
        holder.textViewTitle.setText(item.getTitle());
        holder.textViewLikes.setText(String.valueOf(item.getLikes()));
        holder.textViewRank.setText(String.valueOf(position + 1));

        // Determine the drawable ID based on the type
        int drawableId = getDrawableIdByType(item.getType());
        holder.imageViewThumbnail.setImageResource(drawableId);

        holder.buttonSeeDetails.setOnClickListener(v -> onButtonSeeDetailsClicked(item));
    }


    private void onButtonSeeDetailsClicked(ContentItem item) {
        Intent intent;
        String itemId = item.getId(); // Assuming getId() gives the unique ID for the item

        switch (item.getType()) {
            case "texts":
                intent = new Intent(context, DisplayTextActivity.class);
                break;
            case "images":
                intent = new Intent(context, DisplayImageActivity.class);
                break;
            case "videos":
                intent = new Intent(context, DisplayVideoActivity.class);
                break;
            default:
                throw new IllegalArgumentException("Unsupported item type: " + item.getType());
        }

        intent.putExtra("PACKAGE_ID", itemId); // Pass the ID as an extra
        context.startActivity(intent); // Start the activity
    }
    private int getDrawableIdByType(String type) {
        switch (type) {
            case "texts":
                return R.drawable.text;
            case "images":
                return R.drawable.image;
            case "videos":
                return R.drawable.video;
            // Add more cases as needed
            default:
                return R.drawable.text;
        }
    }


    @Override
    public int getItemCount() {
        return contentItems.size();
    }

    public void setContentItems(List<ContentItem> newContentItems) {
        contentItems = newContentItems;
        notifyDataSetChanged();
    }

    // ViewHolder inner class
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRank;
        TextView textViewTitle;
        TextView textViewLikes;
        ImageView imageViewThumbnail;  // Add this line to declare the ImageView
        Button buttonSeeDetails;       // Declare the button for the "See Details" event

        ViewHolder(View itemView) {
            super(itemView);
            textViewRank = itemView.findViewById(R.id.textViewRank);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewLikes = itemView.findViewById(R.id.textViewLikes);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail); // Initialize ImageView
            buttonSeeDetails = itemView.findViewById(R.id.buttonSeeDetails); // Initialize the button
        }
    }

}
