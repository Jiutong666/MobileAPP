package com.example.mobileass2.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileass2.Data;
import com.example.mobileass2.R;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.InnerHolder> {
    private final List<Data> mData;

    public ListAdapter (List<Data> data){
            this.mData =data;
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.activity_relative, null);

        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if(mData!= null) {
            return mData.size();
        }
        return 0;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.re_title);

        }

        public void setData(Data data) {
            mTitle.setText(data.re_title);
        }
    }
}
