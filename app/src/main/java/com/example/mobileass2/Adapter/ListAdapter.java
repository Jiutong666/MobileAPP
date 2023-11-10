package com.example.mobileass2.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileass2.Data;
import com.example.mobileass2.DisplayTextActivity;
import com.example.mobileass2.DropTextActivity;
import com.example.mobileass2.OnItemDeleteCallback;
import com.example.mobileass2.OnItemDetailCallback;
import com.example.mobileass2.R;

import java.util.List;
import java.util.Locale;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.InnerHolder> {
    private final List<Data> mData;

    private final OnItemDeleteCallback mDeleteCallback;

    private final OnItemDetailCallback mDetailCallback;

    public ListAdapter(List<Data> data, OnItemDeleteCallback deleteCallback, OnItemDetailCallback detailCallback) {
        this.mData = data;
        this.mDeleteCallback = deleteCallback;
        this.mDetailCallback = detailCallback;
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // It's better to use LayoutInflater instead of View.inflate to avoid layout params issues.
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.package_item, parent, false);

        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.setData(mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mSerialNumber;
        private ImageView mDeleteButton;

        private  ImageView mDetailButton;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.re_title);
            mSerialNumber = itemView.findViewById(R.id.tvSerialNumber); // Make sure this ID matches your layout
            mDeleteButton = itemView.findViewById(R.id.btnDelete); // Make sure this ID matches your layout
            mDetailButton = itemView.findViewById(R.id.btnSeeDetails);

            mDetailButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Data data = mData.get(position);
                    mDetailCallback.onItemDetail(data.getType(), data.getId()); // Trigger the callback with the relevant data
                }
            });

            // Setup the delete button if necessary
            mDeleteButton.setOnClickListener(v -> {
                // Implement your delete logic here
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Data data = mData.get(position);
                    mDeleteCallback.onItemDelete(data.getType(), data.getId()); // Assume Data has a method getDocumentId()
                    mData.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mData.size() - position);
                    // This will tell the adapter to update the view holders from the current position to the end of the list
                }
            });
        }

        public void setData(Data data, int position) {
            mTitle.setText(data.re_title);
            mSerialNumber.setText(String.format(Locale.getDefault(), "%02d", position + 1)); // Assuming you want to show a serial number
            // If there are additional fields in Data, set them here as well
        }
    }

}
